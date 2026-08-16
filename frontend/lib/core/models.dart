/// Defensive JSON models for every Agro Trace backend payload.
///
/// Every model parses from a loose `Map<String, dynamic>` so that small
/// backend schema drifts never crash the app — unknown keys are ignored and
/// missing keys fall back to safe defaults.
library;

double? _num(dynamic v) {
  if (v == null) return null;
  if (v is num) return v.toDouble();
  return double.tryParse('$v');
}

int? _int(dynamic v) {
  if (v == null) return null;
  if (v is int) return v;
  if (v is num) return v.toInt();
  return int.tryParse('$v');
}

String? _str(dynamic v) => v == null ? null : '$v';

bool _bool(dynamic v) => v == true || '$v' == 'true';

DateTime? _date(dynamic v) {
  if (v == null) return null;
  return DateTime.tryParse('$v');
}

String fmtDate(DateTime? d) {
  if (d == null) return '—';
  final dd = d.toLocal();
  String two(int n) => n.toString().padLeft(2, '0');
  return '${two(dd.day)}-${two(dd.month)}-${dd.year} ${two(dd.hour)}:${two(dd.minute)}';
}

String fmtNum(num? n, {int decimals = 0}) {
  if (n == null) return '—';
  final s = n.toStringAsFixed(decimals);
  return s;
}

/// Parsed `ApiResponse` envelope.
class ApiEnvelope {
  final bool success;
  final int status;
  final String? code;
  final String? message;
  final dynamic data;
  final String? traceId;

  ApiEnvelope({
    required this.success,
    required this.status,
    this.code,
    this.message,
    this.data,
    this.traceId,
  });

  factory ApiEnvelope.fromMap(Map<String, dynamic> m) => ApiEnvelope(
        success: _bool(m['success']),
        status: _int(m['status']) ?? 0,
        code: _str(m['code']),
        message: _str(m['message']),
        data: m['data'],
        traceId: _str(m['traceId']),
      );
}

/// Exception raised for backend error envelopes / transport failures.
class ApiException implements Exception {
  final int httpStatus;
  final String code;
  final String message;
  final String? traceId;

  ApiException({
    required this.httpStatus,
    required this.code,
    required this.message,
    this.traceId,
  });

  @override
  String toString() => message;
}

/// `/auth/*` response — snake_case per backend `AuthResponse`.
class AuthSession {
  final String accessToken;
  final String refreshToken;
  final String tokenType;
  final int expiresIn;
  final String userUuid;
  final String userName;
  final String userType;
  final String role;

  AuthSession({
    required this.accessToken,
    required this.refreshToken,
    required this.tokenType,
    required this.expiresIn,
    required this.userUuid,
    required this.userName,
    required this.userType,
    required this.role,
  });

  factory AuthSession.fromMap(Map<String, dynamic> m) => AuthSession(
        accessToken: _str(m['access_token']) ?? '',
        refreshToken: _str(m['refresh_token']) ?? '',
        tokenType: _str(m['token_type']) ?? 'Bearer',
        expiresIn: _int(m['expires_in']) ?? 900,
        userUuid: _str(m['user_uuid']) ?? '',
        userName: _str(m['user_name']) ?? 'Operator',
        userType: _str(m['user_type']) ?? '',
        role: _str(m['role']) ?? '',
      );
}

/// `PagedResponse<T>` envelope.
class Paged<T> {
  final List<T> content;
  final int page;
  final int size;
  final int totalElements;
  final int totalPages;
  final bool empty;

  Paged({
    required this.content,
    required this.page,
    required this.size,
    required this.totalElements,
    required this.totalPages,
    required this.empty,
  });

  /// Accepts either a `PagedResponse` map or a bare JSON list.
  static Paged<T> parse<T>(dynamic raw, T Function(Map<String, dynamic>) f) {
    if (raw is List) {
      final items = raw.whereType<Map<String, dynamic>>().map(f).toList();
      return Paged(
        content: items,
        page: 0,
        size: items.length,
        totalElements: items.length,
        totalPages: items.isEmpty ? 0 : 1,
        empty: items.isEmpty,
      );
    }
    final m = (raw is Map<String, dynamic> ? raw : <String, dynamic>{}) ;
    final list = (m['content'] is List) ? m['content'] as List : <dynamic>[];
    final items = list.whereType<Map<String, dynamic>>().map(f).toList();
    return Paged(
      content: items,
      page: _int(m['page']) ?? 0,
      size: _int(m['size']) ?? items.length,
      totalElements: _int(m['totalElements']) ?? items.length,
      totalPages: _int(m['totalPages']) ?? (items.isEmpty ? 0 : 1),
      empty: _bool(m['empty']) || items.isEmpty,
    );
  }
}

class Lot {
  final String lotId;
  final String? farmerUuid;
  final int? productId;
  final int? varietyId;
  final double? quantity;
  final String? unit;
  final String status;
  final String? originAddress;
  final double? estimatedValue;
  final String? currentCustodianRole;
  final String? qrId;
  final bool recalled;
  final DateTime? createdAt;
  final DateTime? acceptedAt;
  final String? notes;

  Lot({
    required this.lotId,
    this.farmerUuid,
    this.productId,
    this.varietyId,
    this.quantity,
    this.unit,
    required this.status,
    this.originAddress,
    this.estimatedValue,
    this.currentCustodianRole,
    this.qrId,
    required this.recalled,
    this.createdAt,
    this.acceptedAt,
    this.notes,
  });

  factory Lot.fromMap(Map<String, dynamic> m) => Lot(
        lotId: _str(m['lotId']) ?? '—',
        farmerUuid: _str(m['farmerUuid']),
        productId: _int(m['productId']),
        varietyId: _int(m['varietyId']),
        quantity: _num(m['quantity']),
        unit: _str(m['unit']),
        status: _str(m['status']) ?? 'UNKNOWN',
        originAddress: _str(m['originAddress']),
        estimatedValue: _num(m['estimatedValue']),
        currentCustodianRole: _str(m['currentCustodianRole']),
        qrId: _str(m['qrId']),
        recalled: _bool(m['recalled']),
        createdAt: _date(m['createdAt']),
        acceptedAt: _date(m['acceptedAt']),
        notes: _str(m['notes']),
      );
}

class Package {
  final String packageId;
  final String? lotId;
  final double? quantity;
  final String? unit;
  final String? packageType;
  final String status;
  final String? testingStatus;
  final String? qrId;
  final bool quarantined;
  final DateTime? createdAt;

  Package({
    required this.packageId,
    this.lotId,
    this.quantity,
    this.unit,
    this.packageType,
    required this.status,
    this.testingStatus,
    this.qrId,
    required this.quarantined,
    this.createdAt,
  });

  factory Package.fromMap(Map<String, dynamic> m) => Package(
        packageId: _str(m['packageId']) ?? '—',
        lotId: _str(m['lotId']),
        quantity: _num(m['quantity']),
        unit: _str(m['unit']),
        packageType: _str(m['packageType']),
        status: _str(m['status']) ?? 'UNKNOWN',
        testingStatus: _str(m['testingStatus']),
        qrId: _str(m['qrId']),
        quarantined: _bool(m['quarantined']),
        createdAt: _date(m['createdAt']),
      );
}

class Bundle {
  final String bundleId;
  final String? manufacturerLotId;
  final String? bundleType;
  final double? quantity;
  final String? unit;
  final String status;
  final String? qrId;
  final bool recalled;
  final bool quarantined;
  final bool retailerReceived;
  final bool distributorVerified;
  final DateTime? createdAt;

  Bundle({
    required this.bundleId,
    this.manufacturerLotId,
    this.bundleType,
    this.quantity,
    this.unit,
    required this.status,
    this.qrId,
    required this.recalled,
    required this.quarantined,
    required this.retailerReceived,
    required this.distributorVerified,
    this.createdAt,
  });

  factory Bundle.fromMap(Map<String, dynamic> m) => Bundle(
        bundleId: _str(m['bundleId']) ?? '—',
        manufacturerLotId: _str(m['manufacturerLotId']),
        bundleType: _str(m['bundleType']),
        quantity: _num(m['quantity']),
        unit: _str(m['unit']),
        status: _str(m['status']) ?? 'UNKNOWN',
        qrId: _str(m['qrId']),
        recalled: _bool(m['recalled']),
        quarantined: _bool(m['quarantined']),
        retailerReceived: _bool(m['retailerReceived']),
        distributorVerified: _bool(m['distributorVerified']),
        createdAt: _date(m['createdAt']),
      );
}

class Product {
  final int id;
  final String name;
  final String? category;
  final String? baseUnit;
  final String? description;

  Product({
    required this.id,
    required this.name,
    this.category,
    this.baseUnit,
    this.description,
  });

  factory Product.fromMap(Map<String, dynamic> m) => Product(
        id: _int(m['id']) ?? 0,
        name: _str(m['name']) ?? 'Unknown produce',
        category: _str(m['category']),
        baseUnit: _str(m['baseUnit']) ?? _str(m['unit']),
        description: _str(m['description']),
      );
}

class ProductVariety {
  final int id;
  final String name;

  ProductVariety({required this.id, required this.name});

  factory ProductVariety.fromMap(Map<String, dynamic> m) => ProductVariety(
        id: _int(m['id']) ?? 0,
        name: _str(m['name']) ?? '—',
      );
}

class Flag {
  final int id;
  final String? flagType;
  final String? severity;
  final String? entityType;
  final String? entityId;
  final String status;
  final String? description;
  final String? resolution;
  final String? assignedInvestigator;
  final DateTime? createdAt;

  Flag({
    required this.id,
    this.flagType,
    this.severity,
    this.entityType,
    this.entityId,
    required this.status,
    this.description,
    this.resolution,
    this.assignedInvestigator,
    this.createdAt,
  });

  factory Flag.fromMap(Map<String, dynamic> m) => Flag(
        id: _int(m['id']) ?? 0,
        flagType: _str(m['flagType']) ?? _str(m['type']),
        severity: _str(m['severity']),
        entityType: _str(m['entityType']),
        entityId: _str(m['entityId']),
        status: _str(m['status']) ?? 'OPEN',
        description: _str(m['description']) ?? _str(m['reason']),
        resolution: _str(m['resolution']),
        assignedInvestigator: _str(m['assignedInvestigator']) ?? _str(m['investigatorUuid']),
        createdAt: _date(m['createdAt']),
      );
}

class Complaint {
  final String complaintId;
  final String? category;
  final String? description;
  final String status;
  final String? resolution;
  final DateTime? createdAt;

  Complaint({
    required this.complaintId,
    this.category,
    this.description,
    required this.status,
    this.resolution,
    this.createdAt,
  });

  factory Complaint.fromMap(Map<String, dynamic> m) => Complaint(
        complaintId: _str(m['complaintId']) ?? _str(m['id']) ?? '—',
        category: _str(m['category']),
        description: _str(m['description']),
        status: _str(m['status']) ?? 'OPEN',
        resolution: _str(m['resolution']),
        createdAt: _date(m['createdAt']),
      );
}

class TraceEvent {
  final String? eventType;
  final String? actorUuid;
  final String? actorRole;
  final String? details;
  final double? latitude;
  final double? longitude;
  final DateTime? createdAt;

  TraceEvent({
    this.eventType,
    this.actorUuid,
    this.actorRole,
    this.details,
    this.latitude,
    this.longitude,
    this.createdAt,
  });

  factory TraceEvent.fromMap(Map<String, dynamic> m) => TraceEvent(
        eventType: _str(m['eventType']) ?? _str(m['event']) ?? _str(m['type']),
        actorUuid: _str(m['actorUuid']) ?? _str(m['actor']),
        actorRole: _str(m['actorRole']) ?? _str(m['role']),
        details: _str(m['details']) ?? _str(m['metadata']),
        latitude: _num(m['latitude']),
        longitude: _num(m['longitude']),
        createdAt: _date(m['createdAt']) ?? _date(m['occurredAt']),
      );
}

class Verification {
  final String verificationStatus;
  final String? productName;
  final String? manufacturer;
  final DateTime? manufacturedAt;
  final String? qualityStatus;
  final bool traceabilityComplete;
  final bool retailerReceived;
  final bool recalled;
  final String? reason;
  final int traceEventCount;

  Verification({
    required this.verificationStatus,
    this.productName,
    this.manufacturer,
    this.manufacturedAt,
    this.qualityStatus,
    required this.traceabilityComplete,
    required this.retailerReceived,
    required this.recalled,
    this.reason,
    required this.traceEventCount,
  });

  factory Verification.fromMap(Map<String, dynamic> m) => Verification(
        verificationStatus: _str(m['verificationStatus']) ?? 'NOT_VERIFIED',
        productName: _str(m['productName']),
        manufacturer: _str(m['manufacturer']),
        manufacturedAt: _date(m['manufacturedAt']),
        qualityStatus: _str(m['qualityStatus']),
        traceabilityComplete: _bool(m['traceabilityComplete']),
        retailerReceived: _bool(m['retailerReceived']),
        recalled: _bool(m['recalled']),
        reason: _str(m['reason']),
        traceEventCount: _int(m['traceEventCount']) ?? 0,
      );
}

/// Full-investigation payload for `/government/lots/{id}/full-history`.
class FullHistory {
  final Lot? lot;
  final List<TraceEvent> traceEvents;
  final List<Flag> flags;

  FullHistory({this.lot, required this.traceEvents, required this.flags});

  factory FullHistory.fromMap(Map<String, dynamic> m) => FullHistory(
        lot: m['lot'] is Map<String, dynamic> ? Lot.fromMap(m['lot']) : null,
        traceEvents: (m['traceEvents'] is List)
            ? (m['traceEvents'] as List)
                .whereType<Map<String, dynamic>>()
                .map(TraceEvent.fromMap)
                .toList()
            : <TraceEvent>[],
        flags: (m['flags'] is List)
            ? (m['flags'] as List)
                .whereType<Map<String, dynamic>>()
                .map(Flag.fromMap)
                .toList()
            : <Flag>[],
      );
}
