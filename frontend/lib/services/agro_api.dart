import '../core/api_client.dart';
import '../core/models.dart';

/// Role-aware service layer over [ApiClient].
/// One method per backend endpoint the portal uses.
class AgroTraceApi {
  AgroTraceApi(this.client);

  final ApiClient client;

  // ─────────────────────────── AUTH ───────────────────────────

  Future<AuthSession> login(String identity, String pin) async {
    final data = await client.post(
      '/auth/login',
      body: {'identity': identity, 'pin': pin},
      auth: false,
    );
    return AuthSession.fromMap(data as Map<String, dynamic>);
  }

  Future<AuthSession> registerFarmer({
    required String aadhaarReference,
    required String otp,
    required String pin,
  }) async {
    final data = await client.post(
      '/auth/farmer/register',
      body: {'aadhaarReference': aadhaarReference, 'otp': otp, 'pin': pin},
      auth: false,
    );
    return AuthSession.fromMap(data as Map<String, dynamic>);
  }

  Future<AuthSession> registerPf({
    required String pfReference,
    required String aadhaarReference,
    required String otp,
    required String pin,
    required String userType,
  }) async {
    final data = await client.post(
      '/auth/pf/register',
      body: {
        'pfReference': pfReference,
        'aadhaarReference': aadhaarReference,
        'otp': otp,
        'pin': pin,
        'userType': userType,
      },
      auth: false,
    );
    return AuthSession.fromMap(data as Map<String, dynamic>);
  }

  Future<AuthSession> registerRetailer({
    required String gstNumber,
    required String aadhaarReference,
    required String otp,
    required String pin,
  }) async {
    final data = await client.post(
      '/auth/retailer/register',
      body: {
        'gstNumber': gstNumber,
        'aadhaarReference': aadhaarReference,
        'otp': otp,
        'pin': pin,
      },
      auth: false,
    );
    return AuthSession.fromMap(data as Map<String, dynamic>);
  }

  Future<void> logout() async {
    await client.post('/auth/logout');
  }

  // ─────────────────────── PRODUCT CATALOG ───────────────────────

  Future<List<Product>> products() async {
    final data = await client.get('/products', auth: false);
    if (data is! List) return <Product>[];
    return data
        .whereType<Map<String, dynamic>>()
        .map(Product.fromMap)
        .toList();
  }

  Future<List<ProductVariety>> varieties(int productId) async {
    final data = await client.get('/products/$productId/varieties', auth: false);
    if (data is! List) return <ProductVariety>[];
    return data
        .whereType<Map<String, dynamic>>()
        .map(ProductVariety.fromMap)
        .toList();
  }

  // ─────────────────────────── FARMER ───────────────────────────

  Future<Lot> farmerCreateLot({
    required int productId,
    int? varietyId,
    required double quantity,
    String? unit,
    double? latitude,
    double? longitude,
    String? originAddress,
    double? estimatedValue,
    String? notes,
  }) async {
    final body = <String, dynamic>{
      'productId': productId,
      'quantity': quantity,
    };
    if (varietyId != null) body['varietyId'] = varietyId;
    if (unit != null && unit.isNotEmpty) body['unit'] = unit;
    if (latitude != null) body['latitude'] = latitude;
    if (longitude != null) body['longitude'] = longitude;
    if (originAddress != null && originAddress.isNotEmpty) {
      body['originAddress'] = originAddress;
    }
    if (estimatedValue != null) body['estimatedValue'] = estimatedValue;
    if (notes != null && notes.isNotEmpty) body['notes'] = notes;
    final data = await client.post('/farmer/lots', body: body);
    return Lot.fromMap(data as Map<String, dynamic>);
  }

  Future<Paged<Lot>> farmerLots({int page = 0, int size = 50}) async {
    final data = await client
        .get('/farmer/lots', query: {'page': '$page', 'size': '$size'});
    return Paged.parse<Lot>(data, Lot.fromMap);
  }

  Future<void> farmerDeleteLot(String lotId) =>
      client.delete('/farmer/lots/$lotId');

  Future<Complaint> farmerFileComplaint({
    required String category,
    required String description,
  }) async {
    final data = await client.post('/farmer/complaints',
        body: {'category': category, 'description': description});
    return Complaint.fromMap(data as Map<String, dynamic>);
  }

  Future<Paged<Complaint>> farmerComplaints() async {
    final data = await client.get('/farmer/complaints');
    return Paged.parse<Complaint>(data, Complaint.fromMap);
  }

  // ─────────────────────── AGENT / INTAKE ───────────────────────

  Future<Paged<Lot>> agentAvailableLots() async {
    final data = await client.get('/agents/lots/available');
    return Paged.parse<Lot>(data, Lot.fromMap);
  }

  Future<Lot> lotDetails(String lotId) async {
    final data = await client.get('/lots/$lotId');
    return Lot.fromMap(data as Map<String, dynamic>);
  }

  Future<List<TraceEvent>> lotTrace(String lotId) async {
    final data = await client.get('/lots/$lotId/trace');
    if (data is! List) return <TraceEvent>[];
    return data
        .whereType<Map<String, dynamic>>()
        .map(TraceEvent.fromMap)
        .toList();
  }

  Future<Lot> acceptLot(
    String lotId, {
    double? latitude,
    double? longitude,
    String? qrId,
  }) async {
    final q = <String, String>{};
    if (latitude != null) q['latitude'] = '$latitude';
    if (longitude != null) q['longitude'] = '$longitude';
    if (qrId != null && qrId.isNotEmpty) q['qrId'] = qrId;
    final data = await client.post('/agents/lots/$lotId/accept', query: q);
    return Lot.fromMap(data as Map<String, dynamic>);
  }

  // ───────────────────────── SUPPLIER ─────────────────────────

  Future<Paged<Lot>> supplierAssignments() async {
    final data = await client.get('/suppliers/assignments');
    return Paged.parse<Lot>(data, Lot.fromMap);
  }

  Future<List<Package>> lotPackages(String lotId) async {
    final data = await client.get('/suppliers/lots/$lotId/packages');
    if (data is! List) return <Package>[];
    return data
        .whereType<Map<String, dynamic>>()
        .map(Package.fromMap)
        .toList();
  }

  Future<Package> packageDetails(String packageId) async {
    final data = await client.get('/suppliers/packages/$packageId');
    return Package.fromMap(data as Map<String, dynamic>);
  }

  Future<Package> supplierReceivePackage(
    String packageId, {
    double? latitude,
    double? longitude,
    String? qrId,
  }) async {
    final q = <String, String>{};
    if (latitude != null) q['latitude'] = '$latitude';
    if (longitude != null) q['longitude'] = '$longitude';
    if (qrId != null && qrId.isNotEmpty) q['qrId'] = qrId;
    final data =
        await client.post('/suppliers/packages/$packageId/receive', query: q);
    return Package.fromMap(data as Map<String, dynamic>);
  }

  // ───────────────────────── RETAILER ─────────────────────────

  Future<List<Bundle>> retailerBundles() async {
    final data = await client.get('/retailers/bundles');
    if (data is! List) return <Bundle>[];
    return data
        .whereType<Map<String, dynamic>>()
        .map(Bundle.fromMap)
        .toList();
  }

  Future<Bundle> bundleDetails(String bundleId) async {
    final data = await client.get('/retailers/bundles/$bundleId');
    return Bundle.fromMap(data as Map<String, dynamic>);
  }

  Future<Bundle> retailerReceiveBundle(
    String bundleId, {
    double? latitude,
    double? longitude,
    String? qrId,
  }) async {
    final q = <String, String>{};
    if (latitude != null) q['latitude'] = '$latitude';
    if (longitude != null) q['longitude'] = '$longitude';
    if (qrId != null && qrId.isNotEmpty) q['qrId'] = qrId;
    final data =
        await client.post('/retailers/bundles/$bundleId/receive', query: q);
    return Bundle.fromMap(data as Map<String, dynamic>);
  }

  // ─────────────────────── GOVERNMENT ───────────────────────

  Future<Paged<Flag>> governmentFlags({int page = 0, int size = 50}) async {
    final data = await client
        .get('/government/flags', query: {'page': '$page', 'size': '$size'});
    return Paged.parse<Flag>(data, Flag.fromMap);
  }

  Future<Flag> resolveFlag(int flagId, String resolution) async {
    final data = await client.post('/government/flags/$flagId/resolve',
        query: {'resolution': resolution});
    return Flag.fromMap(data as Map<String, dynamic>);
  }

  Future<Flag> assignFlag(int flagId, String investigatorUuid) async {
    final data = await client.post('/government/flags/$flagId/assign',
        query: {'investigatorUuid': investigatorUuid});
    return Flag.fromMap(data as Map<String, dynamic>);
  }

  Future<FullHistory> fullLotHistory(String lotId) async {
    final data = await client.get('/government/lots/$lotId/full-history');
    return FullHistory.fromMap(data as Map<String, dynamic>);
  }

  Future<Paged<Complaint>> governmentComplaints() async {
    final data = await client.get('/government/complaints');
    return Paged.parse<Complaint>(data, Complaint.fromMap);
  }

  // ─────────────────── CONSUMER / PUBLIC ───────────────────

  Future<Verification> publicVerify(String qrToken) async {
    final data = await client.get('/public/products/$qrToken', auth: false);
    return Verification.fromMap(data as Map<String, dynamic>);
  }

  Future<List<TraceEvent>> publicTrace(String qrToken) async {
    final data =
        await client.get('/public/products/$qrToken/trace', auth: false);
    if (data is! List) return <TraceEvent>[];
    return data
        .whereType<Map<String, dynamic>>()
        .map(TraceEvent.fromMap)
        .toList();
  }
}
