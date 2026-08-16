import 'dart:math' as math;

import 'package:flutter/material.dart';

import '../../core/app_theme.dart';
import 'chrome.dart';

/// KPI statistic tile for the Zero-State Board.
class KpiTile extends StatelessWidget {
  const KpiTile({
    super.key,
    required this.value,
    required this.label,
    this.sub,
    this.accent = AT.gov,
  });

  final String value;
  final String label;
  final String? sub;
  final Color accent;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(13),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: AT.line),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Container(width: 22, height: 3, color: accent),
          const SizedBox(height: 10),
          Text(value,
              maxLines: 1,
              overflow: TextOverflow.ellipsis,
              style: AT.mono(size: 19, weight: FontWeight.w700)),
          const SizedBox(height: 4),
          Text(label,
              style: AT.body(size: 11.5, weight: FontWeight.w700)),
          if (sub != null)
            Text(sub!, style: AT.body(size: 10, color: AT.faint)),
        ],
      ),
    );
  }
}

/// Strict zero-state shown before the first ledger record exists.
class ZeroState extends StatelessWidget {
  const ZeroState({
    super.key,
    required this.message,
    this.icon = Icons.grid_off_outlined,
  });

  final String message;
  final IconData icon;

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.symmetric(vertical: 30, horizontal: 20),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: AT.line),
      ),
      child: Column(
        children: [
          Container(
            width: 52,
            height: 52,
            decoration: BoxDecoration(
              color: AT.slateBg,
              shape: BoxShape.circle,
              border: Border.all(color: AT.line),
            ),
            alignment: Alignment.center,
            child: Icon(icon, size: 24, color: AT.faint),
          ),
          const SizedBox(height: 14),
          Text('STRICT ZERO-STATE', style: AT.label(size: 10, color: AT.faint)),
          const SizedBox(height: 8),
          Text(
            message,
            textAlign: TextAlign.center,
            style: AT.body(size: 12.5, color: AT.sub),
          ),
        ],
      ),
    );
  }
}

/// Deterministic pseudo-QR glyph painted from the hash of a token —
/// used to render lot / package / bundle QR identities without any
/// external dependency.
class QrGlyph extends StatelessWidget {
  const QrGlyph({super.key, required this.token, this.size = 120});

  final String token;
  final double size;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(8),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(10),
        border: Border.all(color: AT.ink, width: 1.4),
      ),
      child: CustomPaint(
        size: Size(size, size),
        painter: _QrPainter(token),
      ),
    );
  }
}

class _QrPainter extends CustomPainter {
  _QrPainter(this.token);

  final String token;

  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()..color = AT.ink;
    const n = 17;
    final cell = size.width / n;
    var seed = 0x811C9DC5;
    for (final c in token.codeUnits) {
      seed ^= c;
      seed = (seed * 0x01000193) & 0x7FFFFFFF;
    }
    bool cellOn(int r, int c) {
      seed = (seed * 1103515245 + 12345) & 0x7FFFFFFF;
      return seed % 5 < 2;
    }

    void finder(double x, double y) {
      canvas.drawRect(
          Rect.fromLTWH(x, y, cell * 5, cell * 5), paint);
      final white = Paint()..color = Colors.white;
      canvas.drawRect(
          Rect.fromLTWH(x + cell, y + cell, cell * 3, cell * 3), white);
      canvas.drawRect(
          Rect.fromLTWH(x + cell * 2, y + cell * 2, cell, cell), paint);
    }

    for (var r = 0; r < n; r++) {
      for (var c = 0; c < n; c++) {
        final inFinder = (r < 6 && c < 6) ||
            (r < 6 && c > n - 7) ||
            (r > n - 7 && c < 6);
        if (inFinder) continue;
        if (cellOn(r, c)) {
          canvas.drawRect(
              Rect.fromLTWH(c * cell, r * cell, cell * 0.92, cell * 0.92),
              paint);
        }
      }
    }
    finder(0, 0);
    finder(size.width - cell * 5, 0);
    finder(0, size.height - cell * 5);
  }

  @override
  bool shouldRepaint(_QrPainter old) => old.token != token;
}

/// Tamper-evident receipt card issued after every ledger write.
class ReceiptCard extends StatelessWidget {
  const ReceiptCard({
    super.key,
    required this.stamp,
    required this.rows,
    this.qrToken,
    this.stampColor = AT.leafLight,
  });

  final String stamp;
  final List<(String, String)> rows;
  final String? qrToken;
  final Color stampColor;

  String get _signature {
    var h = 0x9E3779B9;
    for (final r in rows) {
      for (final c in '${r.$1}${r.$2}'.codeUnits) {
        h = (h ^ c) & 0xFFFFFFFF;
        h = ((h << 5) + h) & 0xFFFFFFFF;
      }
    }
    return '0x${h.toRadixString(16).padLeft(8, '0').toUpperCase()}';
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(18),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(14),
        border: Border.all(color: stampColor, width: 1.4),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Icon(Icons.verified_outlined, size: 18, color: stampColor),
              const SizedBox(width: 8),
              Expanded(
                child: Text(
                  stamp,
                  style: AT.mono(
                      size: 13, weight: FontWeight.w800, color: stampColor,
                      letterSpacing: 1.2),
                ),
              ),
            ],
          ),
          const Divider(height: 22),
          if (qrToken != null) ...[
            Center(child: QrGlyph(token: qrToken!, size: 118)),
            const SizedBox(height: 6),
            Center(
              child: Text(qrToken!,
                  style: AT.mono(size: 10, color: AT.sub)),
            ),
            const Divider(height: 22),
          ],
          for (final r in rows) ...[
            Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                SizedBox(
                  width: 118,
                  child: Text(r.$1.toUpperCase(),
                      style: AT.label(size: 9.5, color: AT.faint)),
                ),
                Expanded(
                  child: Text(r.$2,
                      style: AT.mono(size: 12, weight: FontWeight.w600)),
                ),
              ],
            ),
            const SizedBox(height: 8),
          ],
          const Divider(height: 18),
          Row(
            children: [
              const Icon(Icons.fingerprint, size: 14, color: AT.faint),
              const SizedBox(width: 6),
              Text('LEDGER SIGNATURE',
                  style: AT.label(size: 9, color: AT.faint)),
              const Spacer(),
              Text(_signature,
                  style: AT.mono(size: 10.5, color: AT.sub)),
            ],
          ),
        ],
      ),
    );
  }
}

/// One row of the hash-chained activity ledger.
class LedgerRow extends StatelessWidget {
  const LedgerRow({
    super.key,
    required this.title,
    required this.monoId,
    this.trailing,
    this.caption,
    this.onTap,
    this.status,
  });

  final String title;
  final String monoId;
  final Widget? trailing;
  final String? caption;
  final VoidCallback? onTap;
  final String? status;

  @override
  Widget build(BuildContext context) {
    return Material(
      color: Colors.white,
      child: InkWell(
        onTap: onTap,
        child: Container(
          padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
          decoration: const BoxDecoration(
            border: Border(bottom: BorderSide(color: AT.line)),
          ),
          child: Row(
            children: [
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: [
                        Flexible(
                          child: Text(monoId,
                              overflow: TextOverflow.ellipsis,
                              style: AT.mono(
                                  size: 12.5, weight: FontWeight.w700)),
                        ),
                        if (status != null) ...[
                          const SizedBox(width: 8),
                          MonoChip(
                            text: status!,
                            bg: AT.slateBg,
                            fg: statusColor(status!),
                          ),
                        ],
                      ],
                    ),
                    const SizedBox(height: 3),
                    Text(title,
                        style: AT.body(size: 12, color: AT.sub)),
                    if (caption != null)
                      Text(caption!,
                          style: AT.mono(size: 9.5, color: AT.faint)),
                  ],
                ),
              ),
              if (trailing != null) trailing!,
            ],
          ),
        ),
      ),
    );
  }
}

/// Vertical timeline of trace events (used by investigations & consumer
/// trace views).
class TraceTimeline extends StatelessWidget {
  const TraceTimeline({super.key, required this.events});

  final List<(String, String, String)> events; // (title, actor, time)

  @override
  Widget build(BuildContext context) {
    if (events.isEmpty) {
      return Text('No trace events recorded yet.',
          style: AT.body(size: 12.5, color: AT.faint));
    }
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        for (var i = 0; i < events.length; i++)
          Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Column(
                children: [
                  Container(
                    width: 11,
                    height: 11,
                    decoration: BoxDecoration(
                      shape: BoxShape.circle,
                      color: i == 0 ? AT.saffron : AT.gov,
                      border: Border.all(color: Colors.white, width: 2),
                      boxShadow: [
                        BoxShadow(
                            color: Colors.black.withAlpha(20),
                            blurRadius: 3),
                      ],
                    ),
                  ),
                  if (i < events.length - 1)
                    Container(
                      width: 1.6,
                      height: 34,
                      color: AT.line,
                    ),
                ],
              ),
              const SizedBox(width: 12),
              Expanded(
                child: Padding(
                  padding: const EdgeInsets.only(bottom: 10),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(events[i].$1,
                          style:
                              AT.body(size: 12.5, weight: FontWeight.w700)),
                      Text(
                          '${events[i].$2}  ·  ${events[i].$3}',
                          style: AT.mono(size: 10, color: AT.faint)),
                    ],
                  ),
                ),
              ),
            ],
          ),
      ],
    );
  }
}

/// Loading veil with the portal's ledger-sync copy.
class LoadingVeil extends StatelessWidget {
  const LoadingVeil({super.key, this.message = 'SYNCING LEDGER…'});

  final String message;

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Container(
        padding: const EdgeInsets.all(22),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(14),
          border: Border.all(color: AT.line),
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            const SizedBox(
              width: 26,
              height: 26,
              child: CircularProgressIndicator(
                  strokeWidth: 2.4, color: AT.gov),
            ),
            const SizedBox(height: 14),
            Text(message, style: AT.mono(size: 10.5, color: AT.sub)),
          ],
        ),
      ),
    );
  }
}

/// Small helper: rupee formatting used by farm-gate value KPIs.
String inr(num? v) {
  if (v == null) return '₹ —';
  final s = v.toStringAsFixed(v.truncateToDouble() == v ? 0 : 2);
  final buf = StringBuffer();
  var count = 0;
  for (final ch in s.split('').reversed) {
    buf.write(ch);
    count++;
    if (count % 3 == 0 && count < s.replaceAll('-', '').length) {
      buf.write(',');
    }
  }
  return '₹ ${buf.toString().split('').reversed.join()}';
}

double clamp01(double v) => math.min(1.0, math.max(0.0, v));
