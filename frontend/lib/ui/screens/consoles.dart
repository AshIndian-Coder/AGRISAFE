import 'package:flutter/material.dart';

import '../../core/app_theme.dart';
import '../../core/models.dart';
import '../../core/roles.dart';
import '../../core/deps.dart';
import '../components/chrome.dart';
import '../components/forms.dart';
import '../components/ledger.dart';

/// Opens a console sheet with the portal's framing.
Future<T?> showConsole<T>(
  BuildContext context, {
  required String title,
  required String caption,
  required Widget child,
}) {
  return showModalBottomSheet<T>(
    context: context,
    isScrollControlled: true,
    backgroundColor: Colors.transparent,
    builder: (ctx) => DraggableScrollableSheet(
      initialChildSize: 0.86,
      minChildSize: 0.5,
      maxChildSize: 0.96,
      expand: false,
      builder: (_, scroll) => Container(
        decoration: const BoxDecoration(
          color: AT.slateBg,
          borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
        ),
        child: Column(
          children: [
            const SizedBox(height: 10),
            Container(
              width: 40,
              height: 4,
              decoration: BoxDecoration(
                color: AT.line,
                borderRadius: BorderRadius.circular(4),
              ),
            ),
            Padding(
              padding: const EdgeInsets.fromLTRB(20, 14, 20, 10),
              child: Row(
                children: [
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(title,
                            style:
                                AT.body(size: 16, weight: FontWeight.w800)),
                        Text(caption,
                            style: AT.mono(size: 9.5, color: AT.faint)),
                      ],
                    ),
                  ),
                  IconButton(
                    icon: const Icon(Icons.close, size: 18, color: AT.sub),
                    onPressed: () => Navigator.of(ctx).pop(),
                  ),
                ],
              ),
            ),
            const TricolorHairline(),
            Expanded(child: ListView(
              controller: scroll,
              padding: const EdgeInsets.fromLTRB(20, 16, 20, 34),
              children: [child],
            )),
          ],
        ),
      ),
    ),
  );
}

/// Latitude / longitude pair — every operational scan on the backend
/// accepts GPS coordinates. Defaults to Mumbai so demos run instantly.
class GpsFields {
  final lat = TextEditingController(text: '19.0760');
  final lon = TextEditingController(text: '72.8777');

  double? get latValue => double.tryParse(lat.text.trim());
  double? get lonValue => double.tryParse(lon.text.trim());

  Widget build() => Row(
        children: [
          Expanded(
            child: GovField(
              label: 'Latitude',
              controller: lat,
              mono: true,
              keyboardType:
                  const TextInputType.numberWithOptions(decimal: true),
              helper: 'GPS required for scans',
            ),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: GovField(
              label: 'Longitude',
              controller: lon,
              mono: true,
              keyboardType:
                  const TextInputType.numberWithOptions(decimal: true),
            ),
          ),
        ],
      );

  void dispose() {
    lat.dispose();
    lon.dispose();
  }
}

/// Shared receipt view shown after a successful ledger write.
Widget receiptView(BuildContext context, ReceiptCard receipt, String note) {
  return Column(
    children: [
      receipt,
      const SizedBox(height: 12),
      Text(note, style: AT.body(size: 11.5, color: AT.sub)),
      const SizedBox(height: 16),
      GovButton(
          label: 'RETURN TO BOARD',
          secondary: true,
          onPressed: () => Navigator.of(context).pop(true)),
    ],
  );
}

// ═══════════════════════════ FARMER ═══════════════════════════

/// ACTION CONSOLE — Create Farm Batch → `POST /farmer/lots`
class FarmerBatchConsole extends StatefulWidget {
  const FarmerBatchConsole({super.key, required this.products});

  final List<Product> products;

  @override
  State<FarmerBatchConsole> createState() => _FarmerBatchConsoleState();
}

class _FarmerBatchConsoleState extends State<FarmerBatchConsole> {
  Product? _product;
  final _qty = TextEditingController();
  final _unit = TextEditingController(text: 'Kg');
  final _value = TextEditingController();
  final _location = TextEditingController();
  final _gps = GpsFields();
  bool _busy = false;
  Lot? _receipt;

  @override
  void dispose() {
    _qty.dispose();
    _unit.dispose();
    _value.dispose();
    _location.dispose();
    _gps.dispose();
    super.dispose();
  }

  Future<void> _submit() async {
    if (_product == null) {
      showError(context, 'Select the crop / produce type.');
      return;
    }
    final qty = double.tryParse(_qty.text.trim());
    if (qty == null || qty <= 0) {
      showError(context, 'Batch weight must be numeric, greater than 0.');
      return;
    }
    setState(() => _busy = true);
    try {
      final lot = await deps.api.farmerCreateLot(
        productId: _product!.id,
        quantity: qty,
        unit: _unit.text.trim().isEmpty ? 'Kg' : _unit.text.trim(),
        latitude: _gps.latValue,
        longitude: _gps.lonValue,
        originAddress: _location.text.trim().isEmpty
            ? null
            : _location.text.trim(),
        estimatedValue: double.tryParse(_value.text.trim()),
      );
      if (!mounted) return;
      setState(() {
        _busy = false;
        _receipt = lot;
      });
    } catch (e) {
      if (!mounted) return;
      setState(() => _busy = false);
      showError(context, e);
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_receipt != null) {
      return receiptView(
        context,
        ReceiptCard(
          stamp: 'BATCH REGISTERED',
          qrToken: _receipt!.qrId,
          rows: [
            ('Batch ID', _receipt!.lotId),
            ('Produce', _product?.name ?? '—'),
            ('Net weight',
                '${fmtNum(_receipt!.quantity, decimals: 2)} ${_receipt!.unit ?? 'Kg'}'),
            ('Status', _receipt!.status),
            ('Field GPS',
                '${_gps.lat.text}, ${_gps.lon.text}'),
          ],
        ),
        'Origin record minted on the traceability ledger.',
      );
    }
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text('Mint a new origin record on the traceability ledger.',
            style: AT.body(size: 12.5, color: AT.sub)),
        const SizedBox(height: 16),
        GovDropdown<Product>(
          label: 'Crop / produce type',
          value: _product,
          items: [
            for (final p in widget.products)
              DropdownMenuItem(
                value: p,
                child: Text(p.category == null
                    ? p.name
                    : '${p.name}  ·  ${p.category}'),
              ),
          ],
          onChanged: (p) => setState(() => _product = p),
          helper: widget.products.isEmpty
              ? 'Catalog empty — check backend seed data'
              : 'Live from GET /products',
        ),
        const SizedBox(height: 16),
        Row(
          children: [
            Expanded(
              flex: 3,
              child: GovField(
                label: 'Batch weight',
                controller: _qty,
                mono: true,
                keyboardType:
                    const TextInputType.numberWithOptions(decimal: true),
                hint: '500',
                helper: 'Numeric only, greater than 0',
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              flex: 2,
              child: GovField(
                label: 'Unit',
                controller: _unit,
                mono: true,
                hint: 'Kg',
              ),
            ),
          ],
        ),
        const SizedBox(height: 16),
        Row(
          children: [
            Expanded(
              child: GovField(
                label: 'Declared farm-gate value (₹)',
                controller: _value,
                mono: true,
                keyboardType:
                    const TextInputType.numberWithOptions(decimal: true),
                hint: '12500',
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: GovField(
                label: 'Field location (village / block)',
                controller: _location,
                hint: 'Kheda Block, Anand',
              ),
            ),
          ],
        ),
        const SizedBox(height: 16),
        _gps.build(),
        const SizedBox(height: 20),
        GovButton(
          label: 'REGISTER BATCH ON LEDGER',
          icon: Icons.add_box_outlined,
          loading: _busy,
          onPressed: _submit,
        ),
      ],
    );
  }
}

/// Farmer complaint console → `POST /farmer/complaints`
class FarmerComplaintConsole extends StatefulWidget {
  const FarmerComplaintConsole({super.key});

  @override
  State<FarmerComplaintConsole> createState() => _FarmerComplaintConsoleState();
}

class _FarmerComplaintConsoleState extends State<FarmerComplaintConsole> {
  static const _categories = [
    'PAYMENT_DELAY',
    'QUALITY_DISPUTE',
    'WEIGHT_DISCREPANCY',
    'LOGISTICS',
    'OTHER',
  ];
  String _category = _categories[0];
  final _desc = TextEditingController();
  bool _busy = false;
  Complaint? _receipt;

  @override
  void dispose() {
    _desc.dispose();
    super.dispose();
  }

  Future<void> _submit() async {
    if (_desc.text.trim().length < 10) {
      showError(context, 'Describe the issue in at least 10 characters.');
      return;
    }
    setState(() => _busy = true);
    try {
      final c = await deps.api
          .farmerFileComplaint(category: _category, description: _desc.text.trim());
      if (!mounted) return;
      setState(() {
        _busy = false;
        _receipt = c;
      });
    } catch (e) {
      if (!mounted) return;
      setState(() => _busy = false);
      showError(context, e);
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_receipt != null) {
      return receiptView(
        context,
        ReceiptCard(
          stamp: 'COMPLAINT LOGGED',
          rows: [
            ('Reference', _receipt!.complaintId),
            ('Category', _receipt!.category ?? _category),
            ('Status', _receipt!.status),
          ],
        ),
        'Escalation routed to the grievance cell.',
      );
    }
    return Column(
      children: [
        GovDropdown<String>(
          label: 'Category',
          value: _category,
          items: [
            for (final c in _categories)
              DropdownMenuItem(value: c, child: Text(c)),
          ],
          onChanged: (v) => setState(() => _category = v ?? _category),
        ),
        const SizedBox(height: 16),
        GovField(
          label: 'Description',
          controller: _desc,
          maxLines: 4,
          hint: 'Describe the dispute, dates and parties involved…',
        ),
        const SizedBox(height: 20),
        GovButton(
          label: 'FILE COMPLAINT',
          icon: Icons.report_gmailerrorred_outlined,
          loading: _busy,
          onPressed: _submit,
        ),
      ],
    );
  }
}

// ═══════════════════════════ AGENT ═══════════════════════════

/// ACTION CONSOLE — Scan & Receive Batch → `POST /agents/lots/{id}/accept`
class AgentIntakeConsole extends StatefulWidget {
  const AgentIntakeConsole({super.key, required this.lots});

  final List<Lot> lots;

  @override
  State<AgentIntakeConsole> createState() => _AgentIntakeConsoleState();
}

class _AgentIntakeConsoleState extends State<AgentIntakeConsole> {
  Lot? _lot;
  final _gps = GpsFields();
  bool _busy = false;
  Lot? _receipt;

  @override
  void dispose() {
    _gps.dispose();
    super.dispose();
  }

  Future<void> _submit() async {
    if (_lot == null) {
      showError(context, 'Select the batch to receive.');
      return;
    }
    setState(() => _busy = true);
    try {
      final lot = await deps.api.acceptLot(
        _lot!.lotId,
        latitude: _gps.latValue,
        longitude: _gps.lonValue,
        qrId: _lot!.qrId,
      );
      if (!mounted) return;
      setState(() {
        _busy = false;
        _receipt = lot;
      });
    } catch (e) {
      if (!mounted) return;
      setState(() => _busy = false);
      showError(context, e);
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_receipt != null) {
      return receiptView(
        context,
        ReceiptCard(
          stamp: 'ACCEPTED INTO STORAGE',
          qrToken: _receipt!.qrId,
          rows: [
            ('Source batch', _receipt!.lotId),
            ('Net weight',
                '${fmtNum(_receipt!.quantity, decimals: 2)} ${_receipt!.unit ?? 'Kg'}'),
            ('Status', _receipt!.status),
            ('Intake GPS', '${_gps.lat.text}, ${_gps.lon.text}'),
            ('Cold chain', '2.4 °C · NOMINAL (SIMULATED)'),
          ],
        ),
        'Intake telemetry attached to the farm batch.',
      );
    }
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text('Attach intake telemetry to an existing farm batch.',
            style: AT.body(size: 12.5, color: AT.sub)),
        const SizedBox(height: 16),
        GovDropdown<Lot>(
          label: 'Batch ID (from farmer)',
          value: _lot,
          items: [
            for (final l in widget.lots)
              DropdownMenuItem(
                value: l,
                child: Text(
                  '${l.lotId} · ${fmtNum(l.quantity)} ${l.unit ?? 'Kg'} · ${l.status}',
                  overflow: TextOverflow.ellipsis,
                ),
              ),
          ],
          onChanged: (l) => setState(() => _lot = l),
          helper: widget.lots.isEmpty
              ? 'No batches assigned yet — zero-state active'
              : 'Live from GET /agents/lots/available',
        ),
        const SizedBox(height: 16),
        _gps.build(),
        const SizedBox(height: 20),
        GovButton(
          label: 'COMMIT INTAKE RECORD',
          icon: Icons.qr_code_scanner,
          loading: _busy,
          onPressed: _submit,
        ),
      ],
    );
  }
}

// ═══════════════════════════ SUPPLIER ═══════════════════════════

/// ACTION CONSOLE — Receive consignment package
/// → `POST /suppliers/packages/{id}/receive`
class SupplierReceiveConsole extends StatefulWidget {
  const SupplierReceiveConsole({super.key, required this.lots});

  final List<Lot> lots;

  @override
  State<SupplierReceiveConsole> createState() => _SupplierReceiveConsoleState();
}

class _SupplierReceiveConsoleState extends State<SupplierReceiveConsole> {
  final _packageId = TextEditingController();
  Lot? _lot;
  List<Package> _packages = [];
  bool _loadingPackages = false;
  final _gps = GpsFields();
  bool _busy = false;
  Package? _receipt;

  @override
  void dispose() {
    _packageId.dispose();
    _gps.dispose();
    super.dispose();
  }

  Future<void> _loadPackages() async {
    if (_lot == null) return;
    setState(() => _loadingPackages = true);
    try {
      final pkgs = await deps.api.lotPackages(_lot!.lotId);
      setState(() {
        _packages = pkgs;
        _loadingPackages = false;
        if (pkgs.length == 1) _packageId.text = pkgs.first.packageId;
      });
    } catch (e) {
      if (!mounted) return;
      setState(() => _loadingPackages = false);
      showError(context, e);
    }
  }

  Future<void> _submit() async {
    final id = _packageId.text.trim();
    if (id.isEmpty) {
      showError(context, 'Enter a package ID (or pick a lot to list its packages).');
      return;
    }
    setState(() => _busy = true);
    try {
      final pkg = await deps.api.supplierReceivePackage(
        id,
        latitude: _gps.latValue,
        longitude: _gps.lonValue,
      );
      if (!mounted) return;
      setState(() {
        _busy = false;
        _receipt = pkg;
      });
    } catch (e) {
      if (!mounted) return;
      setState(() => _busy = false);
      showError(context, e);
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_receipt != null) {
      return receiptView(
        context,
        ReceiptCard(
          stamp: 'CONSIGNMENT IN-TRANSIT',
          qrToken: _receipt!.qrId,
          rows: [
            ('Package', _receipt!.packageId),
            ('Source lot', _receipt!.lotId ?? '—'),
            ('Load',
                '${fmtNum(_receipt!.quantity, decimals: 2)} ${_receipt!.unit ?? 'Kg'}'),
            ('Testing', _receipt!.testingStatus ?? 'PENDING'),
            ('Handover GPS', '${_gps.lat.text}, ${_gps.lon.text}'),
          ],
        ),
        'Consignment bound to this vehicle for hub transfer.',
      );
    }
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
            'Bind a consignment package to this vehicle and destination hub.',
            style: AT.body(size: 12.5, color: AT.sub)),
        const SizedBox(height: 16),
        GovDropdown<Lot>(
          label: 'Source lot (optional lookup)',
          value: _lot,
          items: [
            for (final l in widget.lots)
              DropdownMenuItem(
                value: l,
                child: Text('${l.lotId} · ${l.status}',
                    overflow: TextOverflow.ellipsis),
              ),
          ],
          onChanged: (l) {
            setState(() => _lot = l);
            _loadPackages();
          },
          helper: 'Loads the lot\'s split packages from the nodal center',
        ),
        if (_loadingPackages)
          const Padding(
            padding: EdgeInsets.only(top: 12),
            child: LoadingVeil(message: 'LOADING PACKAGES…'),
          ),
        if (_packages.isNotEmpty) ...[
          const SizedBox(height: 12),
          Wrap(
            spacing: 8,
            runSpacing: 8,
            children: [
              for (final p in _packages)
                GestureDetector(
                  onTap: () => _packageId.text = p.packageId,
                  child: MonoChip(
                    text: p.packageId,
                    bg: _packageId.text == p.packageId
                        ? AT.gov
                        : AT.slateBg,
                    fg: _packageId.text == p.packageId
                        ? Colors.white
                        : AT.govMid,
                  ),
                ),
            ],
          ),
        ],
        const SizedBox(height: 16),
        GovField(
          label: 'Package ID',
          controller: _packageId,
          mono: true,
          hint: 'PKG-ABC123',
          helper: 'Expected format: PKG-XXXXXX',
        ),
        const SizedBox(height: 16),
        _gps.build(),
        const SizedBox(height: 20),
        GovButton(
          label: 'RAISE DISPATCH ORDER',
          icon: Icons.local_shipping_outlined,
          loading: _busy,
          onPressed: _submit,
        ),
      ],
    );
  }
}

// ═══════════════════════════ RETAILER ═══════════════════════════

/// ACTION CONSOLE — Stock bundle & publish retail QR
/// → `POST /retailers/bundles/{id}/receive`
class RetailerStockConsole extends StatefulWidget {
  const RetailerStockConsole({super.key, required this.bundles});

  final List<Bundle> bundles;

  @override
  State<RetailerStockConsole> createState() => _RetailerStockConsoleState();
}

class _RetailerStockConsoleState extends State<RetailerStockConsole> {
  Bundle? _bundle;
  final _shelf = TextEditingController();
  final _gps = GpsFields();
  bool _busy = false;
  Bundle? _receipt;

  @override
  void dispose() {
    _shelf.dispose();
    _gps.dispose();
    super.dispose();
  }

  Future<void> _submit() async {
    if (_bundle == null) {
      showError(context, 'Select the bundle to stock.');
      return;
    }
    setState(() => _busy = true);
    try {
      final b = await deps.api.retailerReceiveBundle(
        _bundle!.bundleId,
        latitude: _gps.latValue,
        longitude: _gps.lonValue,
        qrId: _bundle!.qrId,
      );
      if (!mounted) return;
      setState(() {
        _busy = false;
        _receipt = b;
      });
    } catch (e) {
      if (!mounted) return;
      setState(() => _busy = false);
      showError(context, e);
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_receipt != null) {
      return receiptView(
        context,
        ReceiptCard(
          stamp: 'QR PUBLISHED',
          qrToken: _receipt!.qrId,
          rows: [
            ('Bundle', _receipt!.bundleId),
            ('Mfg lot', _receipt!.manufacturerLotId ?? '—'),
            ('Units',
                '${fmtNum(_receipt!.quantity, decimals: 2)} ${_receipt!.unit ?? ''}'),
            ('Shelf', _shelf.text.trim().isEmpty
                ? 'AISLE-04 / RACK-B'
                : _shelf.text.trim()),
            ('Status', _receipt!.status),
          ],
        ),
        'Consumer-scannable trace label is now live for this SKU.',
      );
    }
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
            'Price the lot and mint a consumer-scannable trace label for the shelf.',
            style: AT.body(size: 12.5, color: AT.sub)),
        const SizedBox(height: 16),
        GovDropdown<Bundle>(
          label: 'Source bundle reference',
          value: _bundle,
          items: [
            for (final b in widget.bundles)
              DropdownMenuItem(
                value: b,
                child: Text(
                  '${b.bundleId} · ${b.bundleType ?? 'CARTON'} · ${fmtNum(b.quantity)} ${b.unit ?? ''}',
                  overflow: TextOverflow.ellipsis,
                ),
              ),
          ],
          onChanged: (b) => setState(() => _bundle = b),
          helper: widget.bundles.isEmpty
              ? 'No bundles dispatched to this outlet yet'
              : 'Live from GET /retailers/bundles',
        ),
        const SizedBox(height: 16),
        GovField(
          label: 'Shelf location',
          controller: _shelf,
          mono: true,
          hint: 'AISLE-04 / RACK-B',
        ),
        const SizedBox(height: 16),
        _gps.build(),
        const SizedBox(height: 20),
        GovButton(
          label: 'STOCK ITEM & MINT QR',
          icon: Icons.qr_code_2,
          loading: _busy,
          onPressed: _submit,
        ),
      ],
    );
  }
}

// ═══════════════════════════ INSPECTOR ═══════════════════════════

/// ACTION CONSOLE — Investigate lot
/// → `GET /government/lots/{id}/full-history`
class InspectorInvestigateConsole extends StatefulWidget {
  const InspectorInvestigateConsole({super.key});

  @override
  State<InspectorInvestigateConsole> createState() =>
      _InspectorInvestigateConsoleState();
}

class _InspectorInvestigateConsoleState
    extends State<InspectorInvestigateConsole> {
  final _lotId = TextEditingController();
  bool _busy = false;
  FullHistory? _history;

  @override
  void dispose() {
    _lotId.dispose();
    super.dispose();
  }

  Future<void> _submit() async {
    if (_lotId.text.trim().isEmpty) {
      showError(context, 'Enter the lot ID to investigate.');
      return;
    }
    setState(() => _busy = true);
    try {
      final h = await deps.api.fullLotHistory(_lotId.text.trim());
      if (!mounted) return;
      setState(() {
        _busy = false;
        _history = h;
      });
    } catch (e) {
      if (!mounted) return;
      setState(() => _busy = false);
      showError(context, e);
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_history != null) {
      final lot = _history!.lot;
      return Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          ReceiptCard(
            stamp: 'INVESTIGATION DOSSIER',
            stampColor: AT.govMid,
            qrToken: lot?.qrId,
            rows: [
              ('Lot', lot?.lotId ?? _lotId.text.trim()),
              ('Status', lot?.status ?? '—'),
              ('Quantity',
                  '${fmtNum(lot?.quantity, decimals: 2)} ${lot?.unit ?? ''}'),
              ('Custodian', lot?.currentCustodianRole ?? '—'),
              ('Recalled', lot == null ? '—' : (lot.recalled ? 'YES' : 'NO')),
              ('Trace events', '${_history!.traceEvents.length}'),
              ('Flags', '${_history!.flags.length}'),
            ],
          ),
          const SizedBox(height: 16),
          const SectionCaption('Hash-chained trace ledger'),
          const SizedBox(height: 10),
          TraceTimeline(
            events: [
              for (final e in _history!.traceEvents)
                (
                  e.eventType ?? 'EVENT',
                  e.actorRole ?? e.actorUuid ?? 'system',
                  fmtDate(e.createdAt),
                ),
            ],
          ),
          if (_history!.flags.isNotEmpty) ...[
            const SizedBox(height: 10),
            const SectionCaption('Fraud flags', color: Color(0xFFE11D48)),
            const SizedBox(height: 10),
            for (final f in _history!.flags)
              Padding(
                padding: const EdgeInsets.only(bottom: 8),
                child: LedgerRow(
                  monoId: 'FLAG-${f.id}',
                  title: f.description ?? f.flagType ?? 'Flagged event',
                  status: f.status,
                ),
              ),
          ],
        ],
      );
    }
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
            'Pull the complete investigation data — lot, trace chain and fraud flags.',
            style: AT.body(size: 12.5, color: AT.sub)),
        const SizedBox(height: 16),
        GovField(
          label: 'Lot ID',
          controller: _lotId,
          mono: true,
          hint: 'LOT-26-0001',
          helper: 'Backend: GET /government/lots/{lotId}/full-history',
        ),
        const SizedBox(height: 20),
        GovButton(
          label: 'OPEN INVESTIGATION',
          icon: Icons.search_outlined,
          loading: _busy,
          onPressed: _submit,
        ),
      ],
    );
  }
}

/// ACTION CONSOLE — Resolve flag → `POST /government/flags/{id}/resolve`
class InspectorResolveConsole extends StatefulWidget {
  const InspectorResolveConsole({super.key, required this.flags});

  final List<Flag> flags;

  @override
  State<InspectorResolveConsole> createState() =>
      _InspectorResolveConsoleState();
}

class _InspectorResolveConsoleState extends State<InspectorResolveConsole> {
  Flag? _flag;
  final _resolution = TextEditingController();
  bool _busy = false;
  Flag? _receipt;

  @override
  void dispose() {
    _resolution.dispose();
    super.dispose();
  }

  Future<void> _submit() async {
    if (_flag == null) {
      showError(context, 'Select the flag to resolve.');
      return;
    }
    if (_resolution.text.trim().length < 5) {
      showError(context, 'Record a resolution note (min 5 characters).');
      return;
    }
    setState(() => _busy = true);
    try {
      final f = await deps.api
          .resolveFlag(_flag!.id, _resolution.text.trim());
      if (!mounted) return;
      setState(() {
        _busy = false;
        _receipt = f;
      });
    } catch (e) {
      if (!mounted) return;
      setState(() => _busy = false);
      showError(context, e);
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_receipt != null) {
      return receiptView(
        context,
        ReceiptCard(
          stamp: 'FLAG RESOLVED',
          rows: [
            ('Flag', 'FLAG-${_receipt!.id}'),
            ('Type', _receipt!.flagType ?? '—'),
            ('Entity',
                '${_receipt!.entityType ?? '—'} ${_receipt!.entityId ?? ''}'),
            ('Status', _receipt!.status),
            ('Resolution', _receipt!.resolution ?? _resolution.text.trim()),
          ],
        ),
        'Enforcement action recorded on the audit ledger.',
      );
    }
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text('Clear or reject a flagged anomaly with a recorded resolution.',
            style: AT.body(size: 12.5, color: AT.sub)),
        const SizedBox(height: 16),
        GovDropdown<Flag>(
          label: 'Flag',
          value: _flag,
          items: [
            for (final f in widget.flags)
              DropdownMenuItem(
                value: f,
                child: Text(
                  'FLAG-${f.id} · ${f.flagType ?? f.entityType ?? '—'} · ${f.entityId ?? ''}',
                  overflow: TextOverflow.ellipsis,
                ),
              ),
          ],
          onChanged: (f) => setState(() => _flag = f),
          helper: widget.flags.isEmpty
              ? 'No flags raised — grid is clean'
              : 'Live from GET /government/flags',
        ),
        const SizedBox(height: 16),
        GovField(
          label: 'Resolution note',
          controller: _resolution,
          maxLines: 3,
          hint: 'Cleared after on-site verification…',
        ),
        const SizedBox(height: 20),
        GovButton(
          label: 'RECORD RESOLUTION',
          icon: Icons.gavel_outlined,
          loading: _busy,
          onPressed: _submit,
        ),
      ],
    );
  }
}

/// Bottom-sheet trace viewer for any lot (used by agents/suppliers).
Future<void> showLotTraceSheet(BuildContext context, Lot lot) async {
  List<TraceEvent>? events;
  String? error;
  try {
    events = await deps.api.lotTrace(lot.lotId);
  } catch (e) {
    error = e.toString();
  }
  if (!context.mounted) return;
  await showConsole(
    context,
    title: lot.lotId,
    caption: 'LOT TRACE · HASH-CHAINED LEDGER',
    child: Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            MonoChip(text: lot.status, fg: statusColor(lot.status)),
            const SizedBox(width: 8),
            Text('${fmtNum(lot.quantity, decimals: 2)} ${lot.unit ?? 'Kg'}',
                style: AT.mono(size: 12, weight: FontWeight.w700)),
          ],
        ),
        const SizedBox(height: 16),
        if (error != null) ErrorBanner(message: error),
        if (events != null)
          TraceTimeline(
            events: [
              for (final e in events)
                (
                  e.eventType ?? 'EVENT',
                  e.actorRole ?? e.actorUuid ?? 'system',
                  fmtDate(e.createdAt),
                ),
            ],
          ),
      ],
    ),
  );
}

/// Helper to parse role-specific hint labels for console headers.
String consoleCaption(NodeRole role) =>
    '${role.meta.nodeCode} · ACTION CONSOLE';


