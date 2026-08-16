import 'package:flutter/material.dart';

void main() => runApp(const AgroTraceApp());

enum PortalRole { farmer, transporter, supplier, manufacturer, distributor, retailer, government, consumer }

class AgroTraceApp extends StatelessWidget {
  const AgroTraceApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'AGRO TRACE National Food Traceability & Quality Inspection Portal',
      theme: ThemeData(
        useMaterial3: true,
        fontFamily: 'Noto Sans',
        colorScheme: ColorScheme.fromSeed(seedColor: const Color(0xFF1B5E20)),
        scaffoldBackgroundColor: const Color(0xFFC8E6C9),
      ),
      home: const AgroTraceShell(),
    );
  }
}

class AgroTraceShell extends StatefulWidget {
  const AgroTraceShell({super.key});

  @override
  State<AgroTraceShell> createState() => _AgroTraceShellState();
}

class _AgroTraceShellState extends State<AgroTraceShell> {
  static const green = Color(0xFF1B5E20);
  static const darkGreen = Color(0xFF004D40);
  static const mint = Color(0xFFC8E6C9);
  static const borderGreen = Color(0xFFA5D6A7);
  static const cream = Color(0xFFFFFDE7);
  static const saffron = Color(0xFFFF9933);
  static const flagGreen = Color(0xFF138808);
  static const brown = Color(0xFF4A2E10);

  PortalRole? activeRole;
  PortalRole selectedRole = PortalRole.farmer;
  bool locked = false;
  bool hindi = false;
  String pinBuffer = '';
  int pinFailures = 0;
  String? pinError;
  String identityRef = 'FARM-MP-7821';
  String otp = '999888';
  String authPin = '123456';
  String toast = '';

  String crop = 'Wheat (Gehun)';
  String qty = '500';
  String lat = '23.2599';
  String lng = '77.4126';
  String splitLotId = 'LOT-2026-4401';
  String splitWeights = '[100, 50, 50]';
  String productId = 'PRD-ATTA-ORG-5KG';
  String inputLotIds = '[LOT-2026-8942, LOT-2026-4401]';
  String productionQty = '20';
  String retailerUuid = 'RET-NEWDELHI-901';
  String investigatorUuid = 'INS-GOV-DELHI-402';
  String packageId = 'PKG-3112-A';
  String measuredValue = '99.4';
  String testProfile = 'FSSAI Standard 2026 (Heavy Metals & Pesticides)';
  String qrToken = 'QR-WHEAT-2026';

  final lots = <Map<String, String>>[
    {'lotId': 'LOT-2026-8942', 'farmer': 'Ramesh Kumar Patel', 'crop': 'Wheat (Gehun)', 'qty': '500 KG', 'date': '2026-03-28', 'status': 'CREATED', 'location': '23.2599, 77.4126'},
    {'lotId': 'LOT-2026-4401', 'farmer': 'Suresh Chandra Sharma', 'crop': 'Organic Rice (Chawal)', 'qty': '800 KG', 'date': '2026-03-27', 'status': 'ACCEPTED_BY_AGENT', 'location': '30.9010, 75.8573'},
    {'lotId': 'LOT-2026-3112', 'farmer': 'Gurpreet Singh', 'crop': 'Mustard (Sarson)', 'qty': '350 KG', 'date': '2026-03-26', 'status': 'IN_TESTING', 'location': '31.6340, 74.8723'},
  ];
  final packages = <Map<String, String>>[
    {'packageId': 'PKG-3112-A', 'weight': '200 KG', 'lotId': 'LOT-2026-3112', 'grade': 'Grade A+ Agmark', 'status': 'SPLIT'},
    {'packageId': 'PKG-3112-B', 'weight': '150 KG', 'lotId': 'LOT-2026-3112', 'grade': 'Grade A Standard', 'status': 'SPLIT'},
  ];
  final bundles = <Map<String, String>>[
    {'bundleId': 'BND-7710-X', 'count': '20', 'manufacturer': 'MFG-DELHI-AGRO-01', 'description': 'Packaged Organic Atta 5kg x 20', 'expiry': '2027-03-28', 'status': 'BUNDLED'},
    {'bundleId': 'BND-8821-Y', 'count': '10', 'manufacturer': 'MFG-DELHI-AGRO-01', 'description': 'Royal Basmati Rice 10kg x 10', 'expiry': '2027-09-15', 'status': 'IN_DISTRIBUTION'},
  ];
  final flags = <Map<String, String>>[
    {'id': 'FLG-1049', 'code': '409 QR_ALREADY_CONSUMED', 'time': '2026-03-28 14:22 UTC', 'lot': 'LOT-2026-8942', 'status': 'OPEN'},
    {'id': 'FLG-1052', 'code': '502 PESTICIDE_THRESHOLD_EXCEEDED', 'time': '2026-03-28 10:15 UTC', 'lot': 'LOT-2026-3112', 'status': 'UNDER_INVESTIGATION'},
  ];

  final roleMeta = const {
    PortalRole.farmer: ['FARMER', 'Kisan Farmer Portal'],
    PortalRole.transporter: ['TRANSPORTER', 'Nodal Agent / Transporter'],
    PortalRole.supplier: ['SUPPLIER', 'Supplier & Mandi Processing'],
    PortalRole.manufacturer: ['MANUFACTURER', 'Manufacturer Processing'],
    PortalRole.distributor: ['DISTRIBUTOR', 'Distributor Logistics'],
    PortalRole.retailer: ['RETAILER', 'Retailer Shelf Inventory'],
    PortalRole.government: ['GOVERNMENT', 'Government Inspector'],
    PortalRole.consumer: ['CONSUMER', 'Public Consumer Transparency'],
  };

  void showToast(String message) {
    setState(() => toast = message);
    Future.delayed(const Duration(seconds: 3), () {
      if (mounted) setState(() => toast = '');
    });
  }

  Future<void> requirePin(String title, String endpoint, VoidCallback onSuccess) async {
    String value = '';
    String? error;
    await showDialog<void>(
      context: context,
      barrierDismissible: false,
      builder: (context) => StatefulBuilder(
        builder: (context, setDialogState) => AlertDialog(
          titlePadding: EdgeInsets.zero,
          title: Container(
            padding: const EdgeInsets.all(14),
            color: green,
            child: const Row(children: [Icon(Icons.lock, color: Colors.amber), SizedBox(width: 8), Text('RailOne 6-Digit Operation Authorization', style: TextStyle(color: Colors.white, fontSize: 15, fontWeight: FontWeight.w800))]),
          ),
          content: SizedBox(
            width: 420,
            child: Column(mainAxisSize: MainAxisSize.min, crossAxisAlignment: CrossAxisAlignment.start, children: [
              Text(title, style: const TextStyle(fontWeight: FontWeight.w800)),
              const SizedBox(height: 8),
              _apiBadge(endpoint),
              const SizedBox(height: 14),
              TextField(
                autofocus: true,
                obscureText: true,
                maxLength: 6,
                keyboardType: TextInputType.number,
                decoration: InputDecoration(labelText: 'Enter 6-Digit PIN', counterText: '', errorText: error, border: const OutlineInputBorder()),
                onChanged: (v) => value = v,
              ),
              TextButton(onPressed: () => setDialogState(() => value = '123456'), child: const Text('Fill demo PIN 123456')),
            ]),
          ),
          actions: [
            TextButton(onPressed: () => Navigator.pop(context), child: const Text('CANCEL')),
            FilledButton(
              style: FilledButton.styleFrom(backgroundColor: green),
              onPressed: () {
                if (value == '123456') {
                  Navigator.pop(context);
                  onSuccess();
                  showToast('Authorized: $endpoint');
                } else {
                  setDialogState(() => error = 'PIN_INVALID: use 123456 in demo mode');
                }
              },
              child: const Text('VERIFY & EXECUTE'),
            ),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    if (activeRole == null) return _roleSelectionAuthScreen();
    if (locked) return _pinUnlockScreen();
    return Scaffold(
      body: Stack(children: [
        Column(children: [
          _officialHeader(),
          Expanded(child: Row(children: [_sidebar(), Expanded(child: Container(color: mint, padding: const EdgeInsets.all(22), child: SingleChildScrollView(child: _workspace())))])),
        ]),
        if (toast.isNotEmpty) Positioned(right: 20, bottom: 20, child: Material(elevation: 8, color: green, borderRadius: BorderRadius.circular(6), child: Padding(padding: const EdgeInsets.all(14), child: Text(toast, style: const TextStyle(color: Colors.white, fontWeight: FontWeight.w700))))),
      ]),
    );
  }

  Widget _officialHeader() => Material(
        elevation: 4,
        child: Column(children: [
          Container(
            color: Colors.white,
            padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
            child: Row(children: [
              _assetOrIcon('assets/national emblem of India.png', Icons.account_balance, 60),
              const SizedBox(width: 14),
              const Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                Text('कृषि एवं किसान कल्याण विभाग', style: TextStyle(color: darkGreen, fontSize: 18, fontWeight: FontWeight.w900)),
                Text('DEPARTMENT OF AGRICULTURE & FARMERS WELFARE', style: TextStyle(color: green, fontSize: 16, fontWeight: FontWeight.w900, letterSpacing: .4)),
                Text('भारत सरकार | GOVERNMENT OF INDIA | कृषि एवं किसान कल्याण मंत्रालय | MINISTRY OF AGRICULTURE & FARMERS WELFARE', style: TextStyle(color: Colors.black87, fontSize: 11, fontWeight: FontWeight.w600)),
              ])),
              _helpline(), const SizedBox(width: 10),
              _campaign('assets/150_logo.jpeg', '150'), _campaignText('JAL\nSHAKTI'), _campaign('assets/75_logo.jpeg', '75'), _campaign('assets/G20_logo.jpeg', 'G20'), _campaignText('MILLETS\nSEEDS'),
            ]),
          ),
          _menuBar(),
          Container(color: cream, padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 6), child: const Row(children: [Icon(Icons.warning_amber, color: Colors.orange, size: 18), SizedBox(width: 8), Text('Alert: CAUTION AGAINST FRAUDULENT RECRUITMENT - Click here to read official advisory.', style: TextStyle(color: brown, fontSize: 12, fontWeight: FontWeight.w800))])),
          Row(children: const [Expanded(child: ColoredBox(color: saffron, child: SizedBox(height: 4))), Expanded(child: ColoredBox(color: Colors.white, child: SizedBox(height: 4))), Expanded(child: ColoredBox(color: flagGreen, child: SizedBox(height: 4)))]),
        ]),
      );

  Widget _menuBar() => Container(
        color: green,
        padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 7),
        child: Row(children: [
          ...['ABOUT US', 'DIVISION', 'ACT & RULES', 'DOCUMENTS', 'STATISTICS', 'SCHEMES', 'RECRUITMENTS', 'MEDIA'].map((e) => Padding(padding: const EdgeInsets.only(right: 18), child: Text(e, style: const TextStyle(color: Colors.white, fontSize: 11, fontWeight: FontWeight.w800)))),
          const Spacer(),
          SizedBox(width: 190, height: 30, child: TextField(decoration: InputDecoration(filled: true, fillColor: Colors.white, hintText: 'Search', prefixIcon: const Icon(Icons.search, size: 16), contentPadding: EdgeInsets.zero, border: OutlineInputBorder(borderRadius: BorderRadius.circular(2))))),
          const SizedBox(width: 8),
          OutlinedButton(style: OutlinedButton.styleFrom(foregroundColor: Colors.white, side: const BorderSide(color: Colors.white)), onPressed: () => setState(() => hindi = !hindi), child: Text(hindi ? 'English' : 'हिंदी')),
        ]),
      );

  Widget _helpline() => Container(padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 8), decoration: BoxDecoration(border: Border.all(color: const Color(0xFFE0E0E0))), child: const Row(children: [Icon(Icons.phone, color: green), SizedBox(width: 7), Column(crossAxisAlignment: CrossAxisAlignment.start, children: [Text('KISAN HELPLINE CENTRE', style: TextStyle(color: green, fontWeight: FontWeight.w900, fontSize: 10)), Text('1800 180 1551', style: TextStyle(color: green, fontWeight: FontWeight.w900, fontSize: 16))])])));
  Widget _assetOrIcon(String asset, IconData icon, double size) => Image.asset(asset, width: size, height: size, fit: BoxFit.contain, errorBuilder: (_, __, ___) => Icon(icon, color: brown, size: size));
  Widget _campaign(String asset, String fallback) => Container(width: 58, height: 42, margin: const EdgeInsets.only(left: 6), decoration: BoxDecoration(border: Border.all(color: const Color(0xFFE0E0E0))), child: Image.asset(asset, fit: BoxFit.contain, errorBuilder: (_, __, ___) => Center(child: Text(fallback, style: const TextStyle(fontWeight: FontWeight.w900, color: green)))));
  Widget _campaignText(String text) => Container(width: 58, height: 42, margin: const EdgeInsets.only(left: 6), alignment: Alignment.center, decoration: BoxDecoration(color: const Color(0xFFE8F5E9), border: Border.all(color: borderGreen)), child: Text(text, textAlign: TextAlign.center, style: const TextStyle(fontSize: 9, color: green, fontWeight: FontWeight.w900)));

  Widget _sidebar() {
    final meta = roleMeta[activeRole]!;
    return Container(
      width: 270,
      color: Colors.white,
      child: Column(crossAxisAlignment: CrossAxisAlignment.stretch, children: [
        Container(padding: const EdgeInsets.all(14), color: const Color(0xFFF7F7F7), child: Text('${meta[0]} AUTHENTICATED\n$identityRef', style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w800))),
        Container(padding: const EdgeInsets.all(12), color: green, child: Row(children: [Icon(_roleIcon(activeRole!), color: Colors.white), const SizedBox(width: 10), Expanded(child: Text(meta[1], style: const TextStyle(color: Colors.white, fontWeight: FontWeight.w900)))])),
        const Padding(padding: EdgeInsets.all(14), child: Text('RBAC STRICT ISOLATION ACTIVE\nOnly this authorized role workspace is rendered. Central dashboard overview tab is excluded.', style: TextStyle(fontSize: 11, color: Colors.black54, fontWeight: FontWeight.w700))),
        const Spacer(),
        Padding(padding: const EdgeInsets.all(12), child: FilledButton.icon(style: FilledButton.styleFrom(backgroundColor: Colors.amber.shade800), onPressed: () => setState(() => locked = true), icon: const Icon(Icons.lock), label: const Text('LOCK APP'))),
        Padding(padding: const EdgeInsets.fromLTRB(12, 0, 12, 12), child: OutlinedButton.icon(onPressed: () => setState(() => activeRole = null), icon: const Icon(Icons.logout), label: const Text('SWITCH ROLE'))),
      ]),
    );
  }

  Widget _roleSelectionAuthScreen() => Scaffold(
        backgroundColor: mint,
        body: Center(
          child: ConstrainedBox(
            constraints: const BoxConstraints(maxWidth: 1120),
            child: SingleChildScrollView(
              padding: const EdgeInsets.all(24),
              child: Column(crossAxisAlignment: CrossAxisAlignment.stretch, children: [
                _officialHeader(), const SizedBox(height: 18),
                _contentCard(Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                  const Text('Step 1: Select Your Official E-Gov Role', style: TextStyle(fontSize: 18, fontWeight: FontWeight.w900)),
                  const SizedBox(height: 14),
                  Wrap(spacing: 10, runSpacing: 10, children: PortalRole.values.map((r) => SizedBox(width: 250, child: InkWell(onTap: () => setState(() { selectedRole = r; identityRef = '${roleMeta[r]![0]}-DEMO-101'; }), child: _roleCard(r)))).toList()),
                ])),
                const SizedBox(height: 16),
                _contentCard(Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                  const Text('Step 2: Sign-Up / Login Form', style: TextStyle(fontSize: 18, fontWeight: FontWeight.w900)),
                  const SizedBox(height: 12),
                  _textField('Identity Reference', identityRef, (v) => identityRef = v),
                  _textField('6-Digit PIN', authPin, (v) => authPin = v, obscure: true),
                  _textField('OTP Code', otp, (v) => otp = v),
                  const SizedBox(height: 10),
                  FilledButton.icon(style: FilledButton.styleFrom(backgroundColor: green), onPressed: () => setState(() { activeRole = selectedRole; locked = false; }), icon: const Icon(Icons.security), label: const Text('LOGIN TO ISOLATED WORKSPACE - /api/v1/auth/login')),
                ])),
              ]),
            ),
          ),
        ),
      );

  Widget _roleCard(PortalRole role) => Card(
        color: selectedRole == role ? const Color(0xFFE8F5E9) : Colors.white,
        shape: RoundedRectangleBorder(side: BorderSide(color: selectedRole == role ? green : const Color(0xFFE0E0E0)), borderRadius: BorderRadius.circular(4)),
        child: Padding(padding: const EdgeInsets.all(14), child: Row(children: [Icon(_roleIcon(role), color: green), const SizedBox(width: 10), Expanded(child: Text(roleMeta[role]![1], style: const TextStyle(fontWeight: FontWeight.w900)))])),
      );

  Widget _pinUnlockScreen() => Scaffold(
        backgroundColor: const Color(0xFF10251B),
        body: Center(
          child: Container(
            width: 430,
            padding: const EdgeInsets.all(24),
            decoration: BoxDecoration(color: Colors.white, borderRadius: BorderRadius.circular(12)),
            child: Column(mainAxisSize: MainAxisSize.min, children: [
              _assetOrIcon('assets/national emblem of India.png', Icons.account_balance, 72),
              Text(roleMeta[activeRole]![1], style: const TextStyle(fontWeight: FontWeight.w900)),
              Text(identityRef, style: const TextStyle(fontFamily: 'monospace')),
              const SizedBox(height: 18),
              Row(mainAxisAlignment: MainAxisAlignment.center, children: List.generate(6, (i) => Container(margin: const EdgeInsets.all(5), width: 16, height: 16, decoration: BoxDecoration(color: i < pinBuffer.length ? green : Colors.grey.shade200, shape: BoxShape.circle, border: Border.all(color: Colors.grey.shade400))))),
              if (pinError != null) Container(margin: const EdgeInsets.only(top: 10), padding: const EdgeInsets.all(10), color: Colors.red.shade50, child: Text(pinError!, style: TextStyle(color: Colors.red.shade800, fontWeight: FontWeight.w800))),
              const SizedBox(height: 12),
              SizedBox(width: 260, child: GridView.count(shrinkWrap: true, crossAxisCount: 3, mainAxisSpacing: 8, crossAxisSpacing: 8, children: ['1','2','3','4','5','6','7','8','9','SWITCH','0','DEL'].map(_pinKey).toList())),
            ]),
          ),
        ),
      );

  Widget _pinKey(String label) => OutlinedButton(onPressed: () {
    if (label == 'SWITCH') { setState(() { activeRole = null; locked = false; pinBuffer = ''; }); return; }
    if (label == 'DEL') { setState(() => pinBuffer = pinBuffer.isEmpty ? '' : pinBuffer.substring(0, pinBuffer.length - 1)); return; }
    if (pinBuffer.length < 6) setState(() => pinBuffer += label);
    if (pinBuffer.length == 6) {
      if (pinBuffer == '123456') setState(() { locked = false; pinError = null; pinBuffer = ''; });
      else setState(() { pinFailures++; pinError = pinFailures >= 3 ? 'PIN_LOCKED: maximum attempts exceeded' : 'PIN_INVALID: incorrect 6-digit PIN'; pinBuffer = ''; });
    }
  }, child: Text(label, style: const TextStyle(fontWeight: FontWeight.w900)));

  Widget _workspace() {
    switch (activeRole!) {
      case PortalRole.farmer: return _farmer();
      case PortalRole.transporter: return _transporter();
      case PortalRole.supplier: return _supplier();
      case PortalRole.manufacturer: return _manufacturer();
      case PortalRole.distributor: return _distributor();
      case PortalRole.retailer: return _retailer();
      case PortalRole.government: return _government();
      case PortalRole.consumer: return _consumer();
    }
  }

  Widget _farmer() => _section(Icons.agriculture, 'Kisan Lot Management & Crop Registration Portal', 'POST /api/v1/farmer/lots', [
    _contentCard(Column(crossAxisAlignment: CrossAxisAlignment.start, children: [_formTitle('Register New Harvest Lot'), _dropdown('Select Commodity / Crop', crop, ['Wheat (Gehun)', 'Organic Rice (Chawal)', 'Mustard (Sarson)', 'Sugarcane (Ganna)'], (v) => setState(() => crop = v!)), _textField('Harvested Weight (quantity) KG', qty, (v) => qty = v), Row(children: [Expanded(child: _textField('latitude', lat, (v) => lat = v)), const SizedBox(width: 8), Expanded(child: _textField('longitude', lng, (v) => lng = v)), OutlinedButton(onPressed: () => setState(() { lat = '23.2599'; lng = '77.4126'; }), child: const Text('Fetch GPS'))]), FilledButton(style: FilledButton.styleFrom(backgroundColor: green), onPressed: () => requirePin('Submit harvest lot', 'POST /api/v1/farmer/lots', () => setState(() => lots.insert(0, {'lotId': 'LOT-2026-${9000 + lots.length}', 'farmer': 'Demo Kisan', 'crop': crop, 'qty': '$qty KG', 'date': '2026-08-16', 'status': 'CREATED', 'location': '$lat, $lng'}))), child: const Text('GENERATE BLOCKCHAIN LOT (REQUIRES 6-DIGIT PIN)'))])),
    _contentCard(_dataTable('My Registered Crop Batches', 'GET /api/v1/farmer/lots?page=0&size=20', ['lotId','crop','qty','date','status'], lots, deleteEndpoint: 'DELETE /api/v1/farmer/lots/{lotId}')),
  ]);

  Widget _transporter() => _section(Icons.local_shipping, 'Agent & Nodal Center Package Pick-up Portal', 'POST /api/v1/agents/lots/{lotId}/accept', [
    _contentCard(_actionList('Available Lots', 'GET /api/v1/agents/lots/available', lots, ['lotId','farmer','location','crop','qty'], 'ACCEPT LOT (GPS+PIN)', (row) => requirePin('Accept ${row['lotId']}', 'POST /api/v1/agents/lots/${row['lotId']}/accept?latitude=23.25&longitude=77.41', () => setState(() => row['status'] = 'ACCEPTED_BY_AGENT')))),
    _contentCard(Column(crossAxisAlignment: CrossAxisAlignment.start, children: [_formTitle('Package Splitter Section'), _textField('lotId', splitLotId, (v) => splitLotId = v), _textField('Package weight splits e.g. [100, 50, 50]', splitWeights, (v) => splitWeights = v), FilledButton(style: FilledButton.styleFrom(backgroundColor: green), onPressed: () => requirePin('Split lot', 'POST /api/v1/nodal-centers/lots/$splitLotId/split', () => setState(() => packages.add({'packageId': 'PKG-${packages.length + 100}', 'weight': '100 KG', 'lotId': splitLotId, 'grade': 'Grade A+', 'status': 'SPLIT'}))), child: const Text('SPLIT LOT INTO PACKAGES (PIN)'))])),
  ]);

  Widget _supplier() => _section(Icons.store, 'Supplier & Mandi Processing Center', 'POST /api/v1/suppliers/packages/{packageId}/receive', [_contentCard(_actionList('Assigned Nodal Packages', 'GET /api/v1/suppliers/assignments', packages, ['packageId','weight','lotId','grade'], 'RECEIVE (PIN)', (row) => requirePin('Receive ${row['packageId']}', 'POST /api/v1/suppliers/packages/${row['packageId']}/receive', () => setState(() => row['status'] = 'RECEIVED'))))]);
  Widget _manufacturer() => _section(Icons.factory, 'Manufacturer Processing & Bundle Packaging', 'POST /api/v1/manufacturers/lots', [_contentCard(Column(crossAxisAlignment: CrossAxisAlignment.start, children: [_formTitle('Manufacturing Card'), _textField('productId', productId, (v) => productId = v), _textField('inputLotIds: [...]', inputLotIds, (v) => inputLotIds = v), _textField('Production Quantity', productionQty, (v) => productionQty = v), FilledButton(style: FilledButton.styleFrom(backgroundColor: green), onPressed: () => requirePin('Generate carton bundles', 'POST /api/v1/manufacturers/lots/{id}/bundles?bundleType=CARTON&bundleCount=$productionQty', () => setState(() => bundles.add({'bundleId': 'BND-${bundles.length + 8800}', 'count': productionQty, 'manufacturer': 'MFG-DELHI-AGRO-01', 'description': productId, 'expiry': '2027-12-31', 'status': 'BUNDLED'}))), child: const Text('GENERATE CARTON BUNDLES (PIN)'))]))]);
  Widget _distributor() => _section(Icons.inventory_2, 'Distributor Bundle Transit & Verification', 'POST /api/v1/distributors/bundles/{bundleId}/dispatch', [_contentCard(Column(crossAxisAlignment: CrossAxisAlignment.start, children: [_formTitle('Available Bundles Card'), _textField('target retailerUuid', retailerUuid, (v) => retailerUuid = v), _actionList('Bundles', 'GET /api/v1/distributors/bundles/available', bundles, ['bundleId','count','manufacturer'], 'DISPATCH (PIN)', (row) => requirePin('Dispatch ${row['bundleId']}', 'POST /api/v1/distributors/bundles/${row['bundleId']}/dispatch/$retailerUuid', () => setState(() => row['status'] = 'DISPATCHED')))]))]);
  Widget _retailer() => _section(Icons.shopping_bag, 'Retailer Shelf Inventory & QR Receive', 'POST /api/v1/retailers/bundles/{bundleId}/receive', [_contentCard(_actionList('Shelf Stock Card', 'GET /api/v1/retailers/bundles', bundles, ['bundleId','description','manufacturer','expiry'], 'RECEIVE (PIN)', (row) => requirePin('Receive ${row['bundleId']}', 'POST /api/v1/retailers/bundles/${row['bundleId']}/receive', () => setState(() => row['status'] = 'SHELF_READY'))))]);
  Widget _government() => _section(Icons.verified_user, 'Government Investigation & Quality Flags Portal', 'GET /api/v1/government/flags', [_contentCard(Column(crossAxisAlignment: CrossAxisAlignment.start, children: [_dataTable('Incident Flag Center', 'GET /api/v1/government/flags', ['id','code','time','lot','status'], flags), _textField('investigatorUuid', investigatorUuid, (v) => investigatorUuid = v), Wrap(spacing: 8, children: [FilledButton(style: FilledButton.styleFrom(backgroundColor: green), onPressed: () => requirePin('Assign investigator', 'POST /api/v1/government/flags/{id}/assign', () => setState(() => flags.first['status'] = 'UNDER_INVESTIGATION')), child: const Text('ASSIGN INVESTIGATOR (PIN)')), FilledButton(style: FilledButton.styleFrom(backgroundColor: Colors.red.shade800), onPressed: () => requirePin('Resolve flag', 'POST /api/v1/government/flags/{id}/resolve', () => setState(() => flags.first['status'] = 'RESOLVED')), child: const Text('RESOLVE FLAG / INITIATE RECALL'))])])), _contentCard(Column(crossAxisAlignment: CrossAxisAlignment.start, children: [_formTitle('Quality Control Sub-Card (FSSAI Lab Submit)'), _textField('packageId', packageId, (v) => packageId = v), _textField('measuredValue (%)', measuredValue, (v) => measuredValue = v), _dropdown('test profile', testProfile, ['FSSAI Standard 2026 (Heavy Metals & Pesticides)', 'Agmark Gold Grade'], (v) => setState(() => testProfile = v!)), Wrap(spacing: 8, children: [FilledButton(style: FilledButton.styleFrom(backgroundColor: green), onPressed: () => requirePin('Pass and certify', 'POST /api/v1/testing/submit', () {}), child: const Text('PASS & CERTIFY')), FilledButton(style: FilledButton.styleFrom(backgroundColor: Colors.red.shade800), onPressed: () => requirePin('Flag and recall', 'POST /api/v1/testing/submit', () {}), child: const Text('FLAG & RECALL'))])]))]);
  Widget _consumer() => _section(Icons.qr_code, 'Public Consumer Food Traceability & Transparency', 'GET /api/v1/public/products/{qrToken}', [_contentCard(Column(crossAxisAlignment: CrossAxisAlignment.start, children: [_textField('qrToken', qrToken, (v) => qrToken = v), const SizedBox(height: 10), Container(width: double.infinity, padding: const EdgeInsets.all(16), decoration: BoxDecoration(color: const Color(0xFFE8F5E9), border: Border.all(color: green, width: 2)), child: const Row(children: [Icon(Icons.check_circle, color: green, size: 32), SizedBox(width: 12), Text('verificationStatus: VERIFIED', style: TextStyle(color: green, fontSize: 18, fontWeight: FontWeight.w900))])), const SizedBox(height: 12), _summary(), const SizedBox(height: 12), _apiBadge('GET /api/v1/public/products/{qrToken}/trace'), const Text('12 events logged on-chain', style: TextStyle(fontWeight: FontWeight.w900, fontSize: 16))]))]);

  Widget _section(IconData icon, String title, String api, List<Widget> children) => Column(crossAxisAlignment: CrossAxisAlignment.stretch, children: [_roleHeader(icon, title, api), const SizedBox(height: 16), ...children.map((w) => Padding(padding: const EdgeInsets.only(bottom: 16), child: w))]);
  Widget _roleHeader(IconData icon, String title, String api) => _contentCard(Row(children: [CircleAvatar(backgroundColor: green, foregroundColor: Colors.white, child: Icon(icon)), const SizedBox(width: 12), Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [Text(title, style: const TextStyle(fontSize: 18, fontWeight: FontWeight.w900)), _apiBadge(api)]))]));
  Widget _contentCard(Widget child) => Container(padding: const EdgeInsets.all(16), decoration: BoxDecoration(color: Colors.white, border: Border.all(color: borderGreen), boxShadow: const [BoxShadow(color: Color(0x1A000000), blurRadius: 8, offset: Offset(0, 3))]), child: child);
  Widget _formTitle(String text) => Padding(padding: const EdgeInsets.only(bottom: 10), child: Text(text, style: const TextStyle(fontSize: 15, fontWeight: FontWeight.w900)));
  Widget _apiBadge(String text) => Container(margin: const EdgeInsets.only(top: 4), padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4), color: green, child: Text(text, style: const TextStyle(color: Colors.white, fontSize: 11, fontFamily: 'monospace', fontWeight: FontWeight.w800)));
  Widget _textField(String label, String value, ValueChanged<String> onChanged, {bool obscure = false}) => Padding(padding: const EdgeInsets.only(bottom: 10), child: TextFormField(initialValue: value, obscureText: obscure, onChanged: onChanged, decoration: InputDecoration(labelText: label, border: const OutlineInputBorder(), isDense: true)));
  Widget _dropdown(String label, String value, List<String> values, ValueChanged<String?> onChanged) => Padding(padding: const EdgeInsets.only(bottom: 10), child: DropdownButtonFormField<String>(value: value, items: values.map((v) => DropdownMenuItem(value: v, child: Text(v))).toList(), onChanged: onChanged, decoration: InputDecoration(labelText: label, border: const OutlineInputBorder(), isDense: true)));
  Widget _dataTable(String title, String api, List<String> keys, List<Map<String, String>> rows, {String? deleteEndpoint}) => Column(crossAxisAlignment: CrossAxisAlignment.start, children: [_formTitle(title), Row(children: [_apiBadge(api), const Spacer(), OutlinedButton.icon(onPressed: () {}, icon: const Icon(Icons.refresh), label: const Text('Refresh API'))]), const SizedBox(height: 10), SingleChildScrollView(scrollDirection: Axis.horizontal, child: DataTable(columns: [...keys.map((k) => DataColumn(label: Text(k, style: const TextStyle(fontWeight: FontWeight.w900)))), if (deleteEndpoint != null) const DataColumn(label: Text('Action'))], rows: rows.map((r) => DataRow(cells: [...keys.map((k) => DataCell(k == 'status' ? Chip(label: Text(r[k] ?? '')) : Text(r[k] ?? ''))), if (deleteEndpoint != null) DataCell(IconButton(icon: const Icon(Icons.delete, color: Colors.red), onPressed: () => requirePin('Delete lot', deleteEndpoint, () => setState(() => rows.remove(r)))))] )).toList()))]);
  Widget _actionList(String title, String api, List<Map<String, String>> rows, List<String> keys, String action, ValueChanged<Map<String, String>> onAction) => Column(crossAxisAlignment: CrossAxisAlignment.start, children: [_formTitle(title), _apiBadge(api), const SizedBox(height: 10), ...rows.map((r) => Container(margin: const EdgeInsets.only(bottom: 8), padding: const EdgeInsets.all(12), decoration: BoxDecoration(border: Border.all(color: const Color(0xFFE0E0E0))), child: Row(children: [Expanded(child: Wrap(spacing: 16, runSpacing: 6, children: keys.map((k) => Text('$k: ${r[k] ?? ''}', style: const TextStyle(fontWeight: FontWeight.w700))).toList())), FilledButton(style: FilledButton.styleFrom(backgroundColor: green), onPressed: () => onAction(r), child: Text(action))])))]);
  Widget _summary() => Wrap(spacing: 10, runSpacing: 10, children: const [InfoBox('productName', 'Sharbati Organic Wheat Atta 5kg'), InfoBox('manufacturer', 'Bharat Agro Foods Ltd'), InfoBox('qualityStatus', 'PASSED'), InfoBox('traceabilityComplete', 'true'), InfoBox('retailerReceived', 'true')]);
  IconData _roleIcon(PortalRole role) => {PortalRole.farmer: Icons.agriculture, PortalRole.transporter: Icons.local_shipping, PortalRole.supplier: Icons.store, PortalRole.manufacturer: Icons.factory, PortalRole.distributor: Icons.inventory_2, PortalRole.retailer: Icons.shopping_bag, PortalRole.government: Icons.verified_user, PortalRole.consumer: Icons.qr_code}[role]!;
}

class InfoBox extends StatelessWidget {
  const InfoBox(this.label, this.value, {super.key});
  final String label;
  final String value;
  @override
  Widget build(BuildContext context) => Container(width: 210, padding: const EdgeInsets.all(12), decoration: BoxDecoration(color: const Color(0xFFF8FAF8), border: Border.all(color: const Color(0xFFE0E0E0))), child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [Text(label, style: const TextStyle(fontSize: 10, fontWeight: FontWeight.w900, color: Colors.black54)), Text(value, style: const TextStyle(fontSize: 13, fontWeight: FontWeight.w900))]));
}