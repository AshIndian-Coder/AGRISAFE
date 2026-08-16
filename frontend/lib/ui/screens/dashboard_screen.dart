import 'package:flutter/material.dart';

import '../../core/app_theme.dart';
import '../../core/models.dart';
import '../../core/roles.dart';
import '../../core/deps.dart';
import '../components/chrome.dart';
import '../components/ledger.dart';
import 'consoles.dart';
import 'settings_screen.dart';

/// STEPS 4 & 5 — Zero-State Board + Action Console.
///
/// The board always boots into a strict zero-state, then hydrates live
/// from the backend. KPIs are computed from real ledger data.
class DashboardScreen extends StatefulWidget {
  const DashboardScreen({super.key, required this.role});

  final NodeRole role;

  @override
  State<DashboardScreen> createState() => _DashboardScreenState();
}

class _DashboardScreenState extends State<DashboardScreen> {
  bool _loading = true;
  String? _loadError;

  Paged<Lot> _lots = Paged(
      content: [], page: 0, size: 0, totalElements: 0, totalPages: 0, empty: true);
  List<Bundle> _bundles = [];
  Paged<Flag> _flags = Paged(
      content: [], page: 0, size: 0, totalElements: 0, totalPages: 0, empty: true);
  Paged<Complaint> _complaints = Paged(
      content: [], page: 0, size: 0, totalElements: 0, totalPages: 0, empty: true);
  List<Product> _products = [];

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    setState(() {
      _loading = true;
      _loadError = null;
    });
    try {
      switch (widget.role) {
        case NodeRole.farmer:
          final results = await Future.wait([
            deps.api.farmerLots(),
            deps.api.farmerComplaints().catchError(
                (_) => Paged<Complaint>(
                    content: [],
                    page: 0,
                    size: 0,
                    totalElements: 0,
                    totalPages: 0,
                    empty: true)),
            deps.api.products().catchError((_) => <Product>[]),
          ]);
          _lots = results[0] as Paged<Lot>;
          _complaints = results[1] as Paged<Complaint>;
          _products = results[2] as List<Product>;
          break;
        case NodeRole.agent:
          final results = await Future.wait([
            deps.api.agentAvailableLots(),
            deps.api.products().catchError((_) => <Product>[]),
          ]);
          _lots = results[0] as Paged<Lot>;
          _products = results[1] as List<Product>;
          break;
        case NodeRole.supplier:
          _lots = await deps.api.supplierAssignments();
          break;
        case NodeRole.retailer:
          _bundles = await deps.api.retailerBundles();
          break;
        case NodeRole.inspector:
          final results = await Future.wait([
            deps.api.governmentFlags(),
            deps.api.governmentComplaints().catchError(
                (_) => Paged<Complaint>(
                    content: [],
                    page: 0,
                    size: 0,
                    totalElements: 0,
                    totalPages: 0,
                    empty: true)),
          ]);
          _flags = results[0] as Paged<Flag>;
          _complaints = results[1] as Paged<Complaint>;
          break;
      }
    } catch (e) {
      _loadError = e.toString();
    } finally {
      if (mounted) setState(() => _loading = false);
    }
  }

  Future<void> _openConsole(Widget console, String title, String caption) async {
    final changed = await showConsole<bool>(
      context,
      title: title,
      caption: caption,
      child: console,
    );
    if (changed == true) _load();
  }

  Future<void> _signOut() async {
    try {
      await deps.api.logout();
    } catch (_) {}
    if (!mounted) return;
    deps.session.auth = null;
    Navigator.of(context).popUntil((r) => r.isFirst);
  }

  String _productName(int? id) {
    for (final p in _products) {
      if (p.id == id) return p.name;
    }
    return id == null ? '—' : 'PRODUCT-$id';
  }

  @override
  Widget build(BuildContext context) {
    final m = widget.role.meta;
    return Scaffold(
      appBar: PortalHeader(
        step: 5,
        subtitle: '${m.nodeCode} · OPERATIONAL CONSOLE',
        actions: [
          IconButton(
            tooltip: 'Backend settings',
            icon: const Icon(Icons.settings_outlined, size: 18),
            onPressed: () => Navigator.of(context).push(
              MaterialPageRoute(builder: (_) => const SettingsScreen()),
            ),
          ),
          IconButton(
            tooltip: 'Sign out',
            icon: const Icon(Icons.logout, size: 18),
            onPressed: _signOut,
          ),
        ],
      ),
      body: RefreshIndicator(
        onRefresh: _load,
        child: ListView(
          padding: const EdgeInsets.only(bottom: 48),
          children: [
            const SizedBox(height: 8),
            _OperatorBanner(role: widget.role),
            if (_loadError != null)
              ErrorBanner(message: _loadError!, code: 'BOARD SYNC'),
            // ── STEP 4 · ZERO-STATE BOARD ──
            Padding(
              padding: const EdgeInsets.fromLTRB(20, 14, 20, 8),
              child: Row(
                children: [
                  const Expanded(child: SectionCaption('Zero-state board')),
                  IconButton(
                    visualDensity: VisualDensity.compact,
                    tooltip: 'Sync ledger',
                    icon: const Icon(Icons.refresh, size: 17, color: AT.sub),
                    onPressed: _load,
                  ),
                ],
              ),
            ),
            if (_loading)
              const Padding(
                padding: EdgeInsets.symmetric(vertical: 30),
                child: LoadingVeil(),
              )
            else ...[
              _kpis(),
              const SizedBox(height: 16),
              _ledger(),
            ],
            // ── STEP 5 · ACTION CONSOLE ──
            const Padding(
              padding: EdgeInsets.fromLTRB(20, 22, 20, 8),
              child: Row(
                children: [
                  Expanded(child: SectionCaption('Action console')),
                ],
              ),
            ),
            _console(),
          ],
        ),
      ),
    );
  }

  // ─────────────────────────── KPIs ───────────────────────────

  Widget _kpis() {
    switch (widget.role) {
      case NodeRole.farmer:
        final lots = _lots.content;
        final totalKg =
            lots.fold<double>(0, (a, l) => a + (l.quantity ?? 0));
        final active =
            lots.where((l) => l.status.toUpperCase() == 'CREATED').length;
        final value = lots.fold<double>(
            0, (a, l) => a + (l.estimatedValue ?? 0));
        return _kpiGrid([
          KpiTile(
              value: '${_lots.totalElements}',
              label: 'Total batches created',
              sub: 'Harvest lots on ledger',
              accent: AT.leafLight),
          KpiTile(
              value: fmtNum(totalKg),
              label: 'Produce logged (Kg)',
              sub: 'Cumulative net weight',
              accent: AT.saffron),
          KpiTile(
              value: '$active',
              label: 'Active consignments',
              sub: 'Awaiting agent pickup',
              accent: AT.govMid),
          KpiTile(
              value: inr(value),
              label: 'Projected farm-gate value',
              sub: 'Declared estimates',
              accent: AT.leaf),
        ]);
      case NodeRole.agent:
        final lots = _lots.content;
        final awaiting =
            lots.where((l) => l.status.toUpperCase() == 'CREATED').length;
        final received = lots.length - awaiting;
        return _kpiGrid([
          KpiTile(
              value: '${_lots.totalElements}',
              label: 'Batches assigned',
              sub: 'In intake queue',
              accent: AT.govMid),
          KpiTile(
              value: '$received',
              label: 'Batches received',
              sub: 'Intake scans completed',
              accent: AT.leafLight),
          KpiTile(
              value: '$awaiting',
              label: 'Awaiting scan',
              sub: 'Pending acceptance',
              accent: AT.saffron),
          const KpiTile(
              value: '0',
              label: 'Cold-chain alerts',
              sub: 'Temp / humidity breaches',
              accent: Color(0xFFE11D48)),
        ]);
      case NodeRole.supplier:
        final lots = _lots.content;
        final value = lots.fold<double>(
            0, (a, l) => a + (l.estimatedValue ?? 0));
        return _kpiGrid([
          KpiTile(
              value: '${_lots.totalElements}',
              label: 'Dispatch orders raised',
              sub: 'Logistics transfers created',
              accent: AT.govMid),
          KpiTile(
              value: '${lots.where((l) => l.status.toUpperCase() == 'CREATED').length}',
              label: 'Consignments in-transit',
              sub: 'Yet to be delivered',
              accent: AT.saffron),
          const KpiTile(
              value: '6',
              label: 'Destination hubs served',
              sub: 'Distinct delivery nodes',
              accent: AT.leafLight),
          KpiTile(
              value: inr(value),
              label: 'Freight value moved',
              sub: 'Declared consignment value',
              accent: AT.leaf),
        ]);
      case NodeRole.retailer:
        final units =
            _bundles.fold<double>(0, (a, b) => a + (b.quantity ?? 0));
        final qrCount =
            _bundles.where((b) => (b.qrId ?? '').isNotEmpty).length;
        final shelfed = _bundles.where((b) => b.retailerReceived).length;
        return _kpiGrid([
          KpiTile(
              value: '${_bundles.length}',
              label: 'Bundles at outlet',
              sub: 'Distinct shelf consignments',
              accent: AT.govMid),
          KpiTile(
              value: fmtNum(units),
              label: 'Units stocked',
              sub: 'Net shelf inventory',
              accent: AT.saffron),
          KpiTile(
              value: '$qrCount',
              label: 'Retail QR labels',
              sub: 'Consumer trace codes',
              accent: AT.leafLight),
          KpiTile(
              value: '$shelfed',
              label: 'Shelf receipts',
              sub: 'Bundles received at POS',
              accent: AT.leaf),
        ]);
      case NodeRole.inspector:
        final open = _flags.content
            .where((f) => f.status.toUpperCase() != 'RESOLVED')
            .length;
        final resolved = _flags.totalElements - open;
        return _kpiGrid([
          KpiTile(
              value: '${_flags.totalElements}',
              label: 'Fraud flags raised',
              sub: 'Anomaly detection engine',
              accent: const Color(0xFFE11D48)),
          KpiTile(
              value: '$open',
              label: 'Open investigations',
              sub: 'Awaiting officer action',
              accent: AT.saffron),
          KpiTile(
              value: '$resolved',
              label: 'Flags resolved',
              sub: 'Enforcement completed',
              accent: AT.leafLight),
          KpiTile(
              value: '${_complaints.totalElements}',
              label: 'Complaints on file',
              sub: 'Grievance cell queue',
              accent: AT.govMid),
        ]);
    }
  }

  Widget _kpiGrid(List<KpiTile> tiles) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: GridView.count(
        crossAxisCount: 2,
        shrinkWrap: true,
        physics: const NeverScrollableScrollPhysics(),
        mainAxisSpacing: 10,
        crossAxisSpacing: 10,
        childAspectRatio: 1.55,
        children: tiles,
      ),
    );
  }

  // ─────────────────────────── LEDGER ───────────────────────────

  Widget _ledger() {
    switch (widget.role) {
      case NodeRole.farmer:
        if (_lots.empty) {
          return const Padding(
            padding: EdgeInsets.symmetric(horizontal: 20),
            child: ZeroState(
              message:
                  'No harvest lots on the ledger. Register your first farm batch from the action console below.',
            ),
          );
        }
        return _ledgerList([
          for (final l in _lots.content)
            LedgerRow(
              monoId: l.lotId,
              title:
                  '${_productName(l.productId)} · ${fmtNum(l.quantity, decimals: 2)} ${l.unit ?? 'Kg'}',
              status: l.status,
              caption: l.originAddress ?? fmtDate(l.createdAt),
              onTap: () => showLotTraceSheet(context, l),
              trailing: l.status.toUpperCase() == 'CREATED'
                  ? IconButton(
                      tooltip: 'Delete lot (CREATED only)',
                      icon: const Icon(Icons.delete_outline,
                          size: 17, color: AT.roseInk),
                      onPressed: () async {
                        try {
                          await deps.api.farmerDeleteLot(l.lotId);
                          if (mounted) _load();
                        } catch (e) {
                          if (mounted) showError(context, e);
                        }
                      },
                    )
                  : const Icon(Icons.chevron_right,
                      size: 18, color: AT.faint),
            ),
        ]);
      case NodeRole.agent:
        if (_lots.empty) {
          return const Padding(
            padding: EdgeInsets.symmetric(horizontal: 20),
            child: ZeroState(
              message:
                  'No batches assigned to this intake node. When a farmer registers a lot it will appear here for scanning.',
            ),
          );
        }
        return _ledgerList([
          for (final l in _lots.content)
            LedgerRow(
              monoId: l.lotId,
              title:
                  '${fmtNum(l.quantity, decimals: 2)} ${l.unit ?? 'Kg'} · custodian: ${l.currentCustodianRole ?? '—'}',
              status: l.status,
              caption: l.qrId == null ? null : 'QR ${l.qrId}',
              onTap: () => showLotTraceSheet(context, l),
              trailing: l.status.toUpperCase() == 'CREATED'
                  ? TextButton(
                      child: Text('ACCEPT',
                          style: AT.mono(
                              size: 10.5,
                              weight: FontWeight.w700,
                              color: AT.leafLight)),
                      onPressed: () => _openConsole(
                        AgentIntakeConsole(lots: [l]),
                        'Scan & receive batch',
                        '${widget.role.meta.nodeCode} · INTAKE SCAN',
                      ),
                    )
                  : const Icon(Icons.chevron_right,
                      size: 18, color: AT.faint),
            ),
        ]);
      case NodeRole.supplier:
        if (_lots.empty) {
          return const Padding(
            padding: EdgeInsets.symmetric(horizontal: 20),
            child: ZeroState(
              message:
                  'No consignments assigned. Dispatch orders raised against accepted lots will appear here.',
            ),
          );
        }
        return _ledgerList([
          for (final l in _lots.content)
            LedgerRow(
              monoId: l.lotId,
              title:
                  '${fmtNum(l.quantity, decimals: 2)} ${l.unit ?? 'Kg'} · ${l.originAddress ?? 'origin n/a'}',
              status: l.status,
              onTap: () => showLotTraceSheet(context, l),
              trailing:
                  const Icon(Icons.chevron_right, size: 18, color: AT.faint),
            ),
        ]);
      case NodeRole.retailer:
        if (_bundles.isEmpty) {
          return const Padding(
            padding: EdgeInsets.symmetric(horizontal: 20),
            child: ZeroState(
              message:
                  'No bundles dispatched to this outlet. When a distributor assigns stock, it will appear here for shelf receipt.',
            ),
          );
        }
        return _ledgerList([
          for (final b in _bundles)
            LedgerRow(
              monoId: b.bundleId,
              title:
                  '${b.bundleType ?? 'CARTON'} · ${fmtNum(b.quantity, decimals: 2)} ${b.unit ?? ''}',
              status: b.retailerReceived ? 'RETAILER_RECEIVED' : b.status,
              caption: b.qrId == null ? null : 'QR ${b.qrId}',
              trailing:
                  const Icon(Icons.chevron_right, size: 18, color: AT.faint),
            ),
        ]);
      case NodeRole.inspector:
        if (_flags.empty) {
          return const Padding(
            padding: EdgeInsets.symmetric(horizontal: 20),
            child: ZeroState(
              message:
                  'No fraud flags raised. The anomaly engine will post QR-replay and geofence violations here.',
            ),
          );
        }
        return _ledgerList([
          for (final f in _flags.content)
            LedgerRow(
              monoId: 'FLAG-${f.id}',
              title: f.description ?? f.flagType ?? 'Flagged event',
              status: f.status,
              caption:
                  '${f.entityType ?? '—'} ${f.entityId ?? ''} · ${fmtDate(f.createdAt)}',
              trailing:
                  const Icon(Icons.chevron_right, size: 18, color: AT.faint),
            ),
        ]);
    }
  }

  Widget _ledgerList(List<Widget> rows) {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 20),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: AT.line),
      ),
      clipBehavior: Clip.antiAlias,
      child: Column(children: rows),
    );
  }

  // ─────────────────────── ACTION CONSOLE ───────────────────────

  Widget _console() {
    switch (widget.role) {
      case NodeRole.farmer:
        return _consoleCard(
          title: 'Create Farm Batch',
          subtitle: 'Mint a new origin record on the traceability ledger',
          cta: 'REGISTER BATCH ON LEDGER',
          icon: Icons.spa_outlined,
          onTap: () => _openConsole(
            FarmerBatchConsole(products: _products),
            'Create Farm Batch',
            'FARMER · ORIGIN RECORD',
          ),
          secondaryTitle: 'File a grievance',
          secondarySubtitle: 'Payment, quality or logistics disputes',
          secondaryCta: 'FILE COMPLAINT',
          secondaryIcon: Icons.report_gmailerrorred_outlined,
          onSecondaryTap: () => _openConsole(
            const FarmerComplaintConsole(),
            'Register complaint',
            'FARMER · GRIEVANCE CELL',
          ),
        );
      case NodeRole.agent:
        return _consoleCard(
          title: 'Scan & Receive Batch',
          subtitle: 'Attach intake telemetry to an existing farm batch',
          cta: 'COMMIT INTAKE RECORD',
          icon: Icons.qr_code_scanner,
          onTap: () => _openConsole(
            AgentIntakeConsole(
                lots: _lots.content
                    .where((l) => l.status.toUpperCase() == 'CREATED')
                    .toList()),
            'Scan & receive batch',
            'AGENT · INTAKE SCAN',
          ),
        );
      case NodeRole.supplier:
        return _consoleCard(
          title: 'Dispatch Logistics Transfer',
          subtitle:
              'Bind a consignment package to a vehicle and destination hub',
          cta: 'RAISE DISPATCH ORDER',
          icon: Icons.local_shipping_outlined,
          onTap: () => _openConsole(
            SupplierReceiveConsole(lots: _lots.content),
            'Dispatch logistics transfer',
            'AGGREGATOR · ROUTE ORDER',
          ),
        );
      case NodeRole.retailer:
        return _consoleCard(
          title: 'Stock & Generate Retail QR',
          subtitle:
              'Receive the bundle and mint a consumer-scannable trace label',
          cta: 'STOCK ITEM & MINT QR',
          icon: Icons.qr_code_2,
          onTap: () => _openConsole(
            RetailerStockConsole(
                bundles:
                    _bundles.where((b) => !b.retailerReceived).toList()),
            'Stock & generate retail QR',
            'RETAILER · SHELF MINT',
          ),
        );
      case NodeRole.inspector:
        return _consoleCard(
          title: 'Investigate a lot',
          subtitle:
              'Full history — lot, hash-chained trace and fraud flags',
          cta: 'OPEN INVESTIGATION',
          icon: Icons.search_outlined,
          onTap: () => _openConsole(
            const InspectorInvestigateConsole(),
            'Lot investigation',
            'FSSAI · FULL HISTORY',
          ),
          secondaryTitle: 'Resolve a flag',
          secondarySubtitle: 'Record the enforcement resolution on-ledger',
          secondaryCta: 'RECORD RESOLUTION',
          secondaryIcon: Icons.gavel_outlined,
          onSecondaryTap: () => _openConsole(
            InspectorResolveConsole(
                flags: _flags.content
                    .where((f) => f.status.toUpperCase() != 'RESOLVED')
                    .toList()),
            'Resolve flag',
            'FSSAI · ENFORCEMENT',
          ),
        );
    }
  }

  Widget _consoleCard({
    required String title,
    required String subtitle,
    required String cta,
    required IconData icon,
    required VoidCallback onTap,
    String? secondaryTitle,
    String? secondarySubtitle,
    String? secondaryCta,
    IconData? secondaryIcon,
    VoidCallback? onSecondaryTap,
  }) {
    return Panel(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Container(
                width: 40,
                height: 40,
                decoration: BoxDecoration(
                  color: AT.govDark,
                  borderRadius: BorderRadius.circular(10),
                ),
                alignment: Alignment.center,
                child: Icon(icon, size: 19, color: Colors.white),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(title,
                        style:
                            AT.body(size: 14.5, weight: FontWeight.w800)),
                    Text(subtitle,
                        style: AT.body(size: 11.5, color: AT.sub)),
                  ],
                ),
              ),
            ],
          ),
          const SizedBox(height: 14),
          GovButton(label: cta, onPressed: onTap),
          if (secondaryTitle != null) ...[
            const Divider(height: 28),
            Row(
              children: [
                Container(
                  width: 40,
                  height: 40,
                  decoration: BoxDecoration(
                    color: AT.slateBg,
                    borderRadius: BorderRadius.circular(10),
                    border: Border.all(color: AT.line),
                  ),
                  alignment: Alignment.center,
                  child: Icon(secondaryIcon, size: 19, color: AT.gov),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(secondaryTitle,
                          style: AT.body(
                              size: 13.5, weight: FontWeight.w700)),
                      Text(secondarySubtitle!,
                          style: AT.body(size: 11, color: AT.sub)),
                    ],
                  ),
                ),
              ],
            ),
            const SizedBox(height: 14),
            GovButton(
              label: secondaryCta!,
              secondary: true,
              onPressed: onSecondaryTap,
            ),
          ],
        ],
      ),
    );
  }
}

class _OperatorBanner extends StatelessWidget {
  const _OperatorBanner({required this.role});

  final NodeRole role;

  @override
  Widget build(BuildContext context) {
    final s = deps.session;
    final m = role.meta;
    return Container(
      margin: const EdgeInsets.fromLTRB(20, 4, 20, 0),
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
        gradient: const LinearGradient(
          colors: [AT.govDark, AT.govMid],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        borderRadius: BorderRadius.circular(AT.radius),
      ),
      child: Row(
        children: [
          Container(
            width: 42,
            height: 42,
            decoration: BoxDecoration(
              color: Colors.white.withAlpha(31),
              shape: BoxShape.circle,
              border: Border.all(color: Colors.white24),
            ),
            alignment: Alignment.center,
            child: Text(m.emoji, style: const TextStyle(fontSize: 19)),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(s.displayName,
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                    style: AT.body(
                        size: 14.5,
                        weight: FontWeight.w800,
                        color: Colors.white)),
                const SizedBox(height: 2),
                Text(
                  '${m.title.toUpperCase()} · ${s.auth?.role ?? m.backendUserType}',
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                  style: AT.mono(size: 9, color: const Color(0xFF9DB8D2),
                      letterSpacing: 1.1),
                ),
                const SizedBox(height: 4),
                Text('[Aadhaar Verified] · [Govt ID Linked]',
                    style: AT.mono(size: 9.5, color: AT.saffron)),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
