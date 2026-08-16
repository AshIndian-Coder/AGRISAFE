import 'dart:convert';

import 'package:agrotrace_portal/core/api_client.dart';
import 'package:agrotrace_portal/core/models.dart';
import 'package:agrotrace_portal/services/agro_api.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:http/http.dart' as http;
import 'package:http/testing.dart';
import 'package:shared_preferences/shared_preferences.dart';

void main() {
  setUp(() async {
    SharedPreferences.setMockInitialValues({});
    await AppConfig.init();
  });

  Map<String, dynamic> envelope(dynamic data, {bool success = true}) => {
        'success': success,
        'status': success ? 200 : 400,
        'code': success ? 'SUCCESS' : 'ERROR',
        'message': success ? 'Success' : 'Failed',
        'data': data,
      };

  test('unwraps the ApiResponse envelope and returns data', () async {
    final mock = MockClient((req) async {
      expect(req.url.path, '/api/v1/products');
      return http.Response(
        jsonEncode(envelope([
          {'id': 1, 'name': 'Wheat (Sharbati)', 'category': 'CEREAL'}
        ])),
        200,
        headers: {'content-type': 'application/json'},
      );
    });
    final api = ApiClient(Session(), client: mock);
    final data = await api.get('/products', auth: false);
    expect(data, isList);
    expect((data as List).first['name'], 'Wheat (Sharbati)');
  });

  test('maps backend error envelopes to ApiException with code', () async {
    final mock = MockClient((req) async => http.Response(
          jsonEncode({
            'success': false,
            'status': 409,
            'code': 'QR_ALREADY_CONSUMED',
            'message': 'QR has already been consumed.',
            'traceId': 'abc-123',
          }),
          409,
          headers: {'content-type': 'application/json'},
        ));
    final api = ApiClient(Session(), client: mock);
    try {
      await api.get('/lots/X', auth: false);
      fail('should have thrown');
    } on ApiException catch (e) {
      expect(e.code, 'QR_ALREADY_CONSUMED');
      expect(e.httpStatus, 409);
      expect(e.traceId, 'abc-123');
    }
  });

  test('sends Bearer token and rotates it on 401 via /auth/refresh',
      () async {
    final session = Session()
      ..auth = AuthSession.fromMap({
        'access_token': 'STALE',
        'refresh_token': 'RT-1',
        'token_type': 'Bearer',
        'expires_in': 900,
        'user_uuid': 'u',
        'user_name': 'Demo Farmer',
        'user_type': 'FARMER',
        'role': 'ROLE_FARMER',
      });

    var refreshed = false;
    final mock = MockClient((req) async {
      if (req.url.path == '/api/v1/auth/refresh') {
        expect(req.url.queryParameters['refreshToken'], 'RT-1');
        refreshed = true;
        return http.Response(
          jsonEncode(envelope({
            'access_token': 'FRESH',
            'refresh_token': 'RT-2',
            'token_type': 'Bearer',
            'expires_in': 900,
            'user_uuid': 'u',
            'user_name': 'Demo Farmer',
            'user_type': 'FARMER',
            'role': 'ROLE_FARMER',
          })),
          200,
        );
      }
      // Protected endpoint: reject STALE, accept FRESH.
      final auth = req.headers['Authorization'];
      if (auth == 'Bearer STALE') {
        return http.Response('{"success":false,"status":401}', 401);
      }
      expect(auth, 'Bearer FRESH');
      return http.Response(
        jsonEncode(envelope({
          'content': <dynamic>[],
          'page': 0,
          'size': 20,
          'totalElements': 0,
          'totalPages': 0,
          'first': true,
          'last': true,
          'empty': true,
        })),
        200,
      );
    });

    final api = ApiClient(session, client: mock);
    final data = await api.get('/farmer/lots');
    expect(refreshed, isTrue);
    expect(session.auth!.accessToken, 'FRESH');
    expect(data['totalElements'], 0);
  });

  test('login posts identity + pin and parses snake_case session', () async {
    final mock = MockClient((req) async {
      expect(req.url.path, '/api/v1/auth/login');
      final body = jsonDecode(req.body);
      expect(body['identity'], 'AADHAR-DEMO-FARMER');
      expect(body['pin'], '123456');
      return http.Response(
        jsonEncode(envelope({
          'access_token': 'AT',
          'refresh_token': 'RT',
          'token_type': 'Bearer',
          'expires_in': 900,
          'user_uuid': 'uuid-1',
          'user_name': 'Demo Farmer',
          'user_type': 'FARMER',
          'role': 'ROLE_FARMER',
        })),
        200,
      );
    });

    final api = AgroTraceApi(ApiClient(Session(), client: mock));
    final s = await api.login('AADHAR-DEMO-FARMER', '123456');
    expect(s.accessToken, 'AT');
    expect(s.userType, 'FARMER');
    expect(s.role, 'ROLE_FARMER');
  });

  test('acceptLot sends GPS + qrId as query parameters', () async {
    final mock = MockClient((req) async {
      expect(req.url.path, '/api/v1/agents/lots/LOT-1/accept');
      expect(req.url.queryParameters['latitude'], '19.076');
      expect(req.url.queryParameters['longitude'], '72.877');
      expect(req.url.queryParameters['qrId'], 'QR-1');
      return http.Response(
        jsonEncode(envelope({
          'lotId': 'LOT-1',
          'status': 'ACCEPTED',
          'recalled': false,
        })),
        200,
      );
    });

    final session = Session()
      ..auth = AuthSession.fromMap({
        'access_token': 'AT',
        'refresh_token': '',
        'token_type': 'Bearer',
        'expires_in': 900,
        'user_uuid': 'u',
        'user_name': 'Agent',
        'user_type': 'COLLECTION_AGENT',
        'role': 'ROLE_COLLECTING_AGENT',
      });
    final api = AgroTraceApi(ApiClient(session, client: mock));
    final lot = await api.acceptLot('LOT-1',
        latitude: 19.076, longitude: 72.877, qrId: 'QR-1');
    expect(lot.status, 'ACCEPTED');
  });
}
