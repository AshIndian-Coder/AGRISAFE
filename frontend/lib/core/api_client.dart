import 'dart:async';
import 'dart:convert';

import 'package:flutter/foundation.dart' show TargetPlatform, defaultTargetPlatform, kIsWeb;
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';

import 'models.dart';

/// Persistent settings: API base URL + last-used identity (for the
/// "PIN re-entry on every resume" gate). Raw Aadhaar numbers are NEVER
/// stored — only registry reference IDs, matching the portal's
/// zero raw-identifier charter.
class AppConfig {
  AppConfig._();
  static AppConfig? _instance;
  static AppConfig get I => _instance!;

  static const String androidEmulatorBaseUrl =
      'http://10.0.2.2:8080/api/v1';
  static const String webBaseUrl = 'http://localhost:8080/api/v1';

  /// Web/desktop point at localhost; Android emulator at 10.0.2.2.
  static String get defaultBaseUrl {
    if (kIsWeb) return webBaseUrl;
    if (defaultTargetPlatform == TargetPlatform.android) {
      return androidEmulatorBaseUrl;
    }
    return webBaseUrl; // Windows / macOS / Linux / iOS
  }

  late String baseUrl;
  String? lastIdentity;
  String? lastRoleName;

  static Future<AppConfig> init() async {
    final prefs = await SharedPreferences.getInstance();
    final c = AppConfig._();
    c.baseUrl = prefs.getString('at.base_url') ?? defaultBaseUrl;
    c.lastIdentity = prefs.getString('at.last_identity');
    c.lastRoleName = prefs.getString('at.last_role');
    _instance = c;
    return c;
  }

  Future<void> setLastRole(String? roleName) async {
    lastRoleName = roleName;
    final prefs = await SharedPreferences.getInstance();
    if (roleName == null || roleName.isEmpty) {
      await prefs.remove('at.last_role');
    } else {
      await prefs.setString('at.last_role', roleName);
    }
  }

  Future<void> setBaseUrl(String url) async {
    var u = url.trim();
    while (u.endsWith('/')) {
      u = u.substring(0, u.length - 1);
    }
    baseUrl = u.isEmpty ? defaultBaseUrl : u;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('at.base_url', baseUrl);
  }

  Future<void> setLastIdentity(String? identity) async {
    lastIdentity = identity;
    final prefs = await SharedPreferences.getInstance();
    if (identity == null || identity.isEmpty) {
      await prefs.remove('at.last_identity');
    } else {
      await prefs.setString('at.last_identity', identity);
    }
  }
}

/// Holds the live JWT session for the signed-in operator.
class Session {
  AuthSession? auth;
  String? identity;

  bool get signedIn => auth != null && auth!.accessToken.isNotEmpty;
  String get displayName => auth?.userName ?? 'Operator';
}

/// Thin HTTP layer over the Agro Trace backend.
///
/// * Unwraps the `ApiResponse` envelope automatically.
/// * Attaches `Authorization: Bearer <token>` when a session exists.
/// * On a 401, silently rotates the token once via `/auth/refresh`.
/// * Converts error envelopes + transport failures into [ApiException].
class ApiClient {
  ApiClient(this.session, {http.Client? client})
      : _client = client ?? http.Client();

  final Session session;
  final http.Client _client;

  String get _base => AppConfig.I.baseUrl;

  Map<String, String> _headers({bool auth = true, bool json = true}) {
    final h = <String, String>{'Accept': 'application/json'};
    if (json) h['Content-Type'] = 'application/json';
    if (auth && session.signedIn) {
      h['Authorization'] = 'Bearer ${session.auth!.accessToken}';
    }
    return h;
  }

  /// GET request → unwrapped `data` payload.
  Future<dynamic> get(
    String path, {
    Map<String, String>? query,
    bool auth = true,
  }) =>
      _send('GET', path, query: query, auth: auth);

  /// POST request → unwrapped `data` payload.
  Future<dynamic> post(
    String path, {
    Object? body,
    Map<String, String>? query,
    bool auth = true,
  }) =>
      _send('POST', path, body: body, query: query, auth: auth);

  /// DELETE request → unwrapped `data` payload.
  Future<dynamic> delete(String path, {bool auth = true}) =>
      _send('DELETE', path, auth: auth);

  Future<dynamic> _send(
    String method,
    String path, {
    Object? body,
    Map<String, String>? query,
    bool auth = true,
    bool isRetry = false,
  }) async {
    final uri = Uri.parse('$_base$path').replace(queryParameters:
        (query == null || query.isEmpty) ? null : query);

    http.Response res;
    try {
      final req = http.Request(method, uri);
      req.headers.addAll(_headers(auth: auth, json: body != null));
      if (body != null) req.body = jsonEncode(body);
      final streamed = await _client.send(req).timeout(
            const Duration(seconds: 30),
          );
      res = await http.Response.fromStream(streamed);
    } on TimeoutException {
      throw ApiException(
        httpStatus: 0,
        code: 'TIMEOUT',
        message:
            'The backend did not respond within 30 seconds. Check that the Agro Trace server is running and the base URL is correct.',
      );
    } catch (e) {
      throw ApiException(
        httpStatus: 0,
        code: 'NETWORK',
        message:
            'Could not reach the Agro Trace backend at $_base. Verify the server is up (Android emulator should use http://10.0.2.2:8080/api/v1). $e',
      );
    }

    // ---- Token rotation -------------------------------------------------
    if (res.statusCode == 401 &&
        auth &&
        !isRetry &&
        (session.auth?.refreshToken ?? '').isNotEmpty) {
      final refreshed = await _tryRefresh();
      if (refreshed) {
        return _send(method, path,
            body: body, query: query, auth: auth, isRetry: true);
      }
    }

    // ---- Parse envelope ---------------------------------------------------
    dynamic decoded;
    try {
      decoded = res.body.isEmpty ? null : jsonDecode(res.body);
    } catch (_) {
      decoded = null;
    }

    if (decoded is Map<String, dynamic> && decoded.containsKey('success')) {
      final env = ApiEnvelope.fromMap(decoded);
      if (env.success) return env.data;
      throw ApiException(
        httpStatus: res.statusCode,
        code: env.code ?? 'ERROR',
        message: env.message ?? 'Request failed',
        traceId: env.traceId,
      );
    }

    if (res.statusCode >= 200 && res.statusCode < 300) return decoded;

    throw ApiException(
      httpStatus: res.statusCode,
      code: res.statusCode == 401
          ? 'UNAUTHORIZED'
          : res.statusCode == 403
              ? 'FORBIDDEN'
              : 'HTTP_${res.statusCode}',
      message: res.statusCode == 401
          ? 'Session rejected. Sign in again with your registry reference and PIN.'
          : res.statusCode == 403
              ? 'Your role is not permitted to perform this operation.'
              : 'Backend returned HTTP ${res.statusCode}.',
    );
  }

  Future<bool> _tryRefresh() async {
    try {
      final data = await _send(
        'POST',
        '/auth/refresh',
        query: {'refreshToken': session.auth!.refreshToken},
        auth: false,
        isRetry: true,
      );
      if (data is Map<String, dynamic>) {
        session.auth = AuthSession.fromMap(data);
        return true;
      }
    } catch (_) {}
    return false;
  }

  /// `/actuator/health` ping — raw, no envelope expected.
  Future<String> healthCheck() async {
    try {
      final res = await _client
          .get(Uri.parse('$_base/actuator/health'))
          .timeout(const Duration(seconds: 6));
      final body = res.body;
      if (res.statusCode == 200 && body.contains('UP')) return 'UP';
      return 'DEGRADED';
    } catch (_) {
      return 'DOWN';
    }
  }
}
