import 'package:flutter/material.dart';

import '../../core/app_theme.dart';
import '../../core/models.dart';
import '../../core/deps.dart';
import '../components/chrome.dart';
import '../components/forms.dart';
import '../components/ledger.dart';

/// PUBLIC — Consumer product verification.
/// No token required: `GET /public/products/{qrToken}`.
class ConsumerVerifyScreen extends StatefulWidget {
  const ConsumerVerifyScreen({super.key});

  @override
  State<ConsumerVerifyScreen> createState() => _ConsumerVerifyScreenState();
}

class _ConsumerVerifyScreenState extends State<ConsumerVerifyScreen> {
  final _token = TextEditingController();
  bool _busy = false;
  Verification? _result;
  List<TraceEvent> _trace = [];
  String? _error;

  @override
  void dispose() {
    _token.dispose();
    super.dispose();
  }

  Future<void> _verify() async {
    final t = _token.text.trim();
    if (t.isEmpty) {
      showError(context, 'Enter or scan the QR token from the retail label.');
      return;
    }
    setState(() {
      _busy = true;
      _result = null;
      _trace = [];
      _error = null;
    });
    try {
      final v = await deps.api.publicVerify(t);
      List<TraceEvent> trace = [];
      try {
        trace = await deps.api.publicTrace(t);
      } catch (_) {}
      setState(() {
        _busy = false;
        _result = v;
        _trace = trace;
      });
    } on ApiException catch (e) {
      setState(() {
        _busy = false;
        _error = '${e.code}: ${e.message}';
      });
    } catch (e) {
      setState(() {
        _busy = false;
        _error = e.toString();
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const PortalHeader(
        subtitle: 'PUBLIC · CONSUMER VERIFICATION GATE',
      ),
      body: ListView(
        padding: const EdgeInsets.only(bottom: 40),
        children: [
          const SizedBox(height: 8),
          Panel(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const SectionCaption('Trace QR lookup', color: AT.leafLight),
                const SizedBox(height: 12),
                Text(
                  'Scan the QR printed on the retail label. The ledger returns the verification verdict without requiring any login.',
                  style: AT.body(size: 12.5, color: AT.sub),
                ),
                const SizedBox(height: 14),
                GovField(
                  label: 'QR token',
                  controller: _token,
                  mono: true,
                  hint: 'QR-XXXX-XXXX',
                  helper: 'Backend: GET /public/products/{qrToken}',
                  prefixIcon:
                      const Icon(Icons.qr_code_scanner, size: 18, color: AT.gov),
                ),
                const SizedBox(height: 16),
                GovButton(
                  label: 'VERIFY PRODUCT',
                  icon: Icons.verified_outlined,
                  loading: _busy,
                  onPressed: _verify,
                ),
              ],
            ),
          ),
          if (_error != null) ErrorBanner(message: _error!, code: 'VERIFY'),
          if (_result != null) _verdict(),
        ],
      ),
    );
  }

  Widget _verdict() {
    final v = _result!;
    final verified = v.verificationStatus == 'VERIFIED';
    final recalled = v.recalled || v.verificationStatus == 'RECALLED';
    final stampColor =
        recalled ? const Color(0xFFE11D48) : verified ? AT.leafLight : AT.saffron;
    final stamp = recalled
        ? 'RECALLED'
        : verified
            ? 'VERIFIED'
            : 'NOT VERIFIED';
    return Panel(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Container(
            width: double.infinity,
            padding: const EdgeInsets.symmetric(vertical: 14),
            decoration: BoxDecoration(
              color: stampColor.withAlpha(20),
              borderRadius: BorderRadius.circular(10),
              border: Border.all(color: stampColor),
            ),
            alignment: Alignment.center,
            child: Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(
                  recalled
                      ? Icons.block_outlined
                      : verified
                          ? Icons.verified
                          : Icons.warning_amber_rounded,
                  size: 20,
                  color: stampColor,
                ),
                const SizedBox(width: 8),
                Text(stamp,
                    style: AT.mono(
                        size: 15,
                        weight: FontWeight.w800,
                        color: stampColor,
                        letterSpacing: 2)),
              ],
            ),
          ),
          const SizedBox(height: 16),
          ReceiptCard(
            stamp: 'PUBLIC TRACE SUMMARY',
            stampColor: AT.govMid,
            rows: [
              ('Product', v.productName ?? '—'),
              ('Manufacturer', v.manufacturer ?? '—'),
              ('Manufactured', fmtDate(v.manufacturedAt)),
              ('Quality status', v.qualityStatus ?? '—'),
              ('Traceability', v.traceabilityComplete ? 'COMPLETE' : 'PARTIAL'),
              ('Retailer receipt', v.retailerReceived ? 'YES' : 'PENDING'),
              ('Trace events', '${v.traceEventCount}'),
              if (v.reason != null) ('Reason', v.reason!),
            ],
          ),
          if (_trace.isNotEmpty) ...[
            const SizedBox(height: 18),
            const SectionCaption('Farm-to-shelf journey'),
            const SizedBox(height: 10),
            TraceTimeline(
              events: [
                for (final e in _trace)
                  (
                    e.eventType ?? 'EVENT',
                    e.actorRole ?? e.actorUuid ?? 'system',
                    fmtDate(e.createdAt),
                  ),
              ],
            ),
          ],
        ],
      ),
    );
  }
}
