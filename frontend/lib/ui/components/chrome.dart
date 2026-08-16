import 'package:flutter/material.dart';

import '../../core/app_theme.dart';

/// The five-step wizard rail shown by the web portal:
/// 1 Role Selection › 2 Dynamic Auth › 3 Security PIN › 4 Zero-State Board
/// › 5 Action Console.
class StepIndicator extends StatelessWidget {
  const StepIndicator({super.key, required this.active});

  /// 1-based index of the active step.
  final int active;

  static const _labels = [
    'Role Selection',
    'Dynamic Auth',
    'Security PIN',
    'Zero-State Board',
    'Action Console',
  ];

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      scrollDirection: Axis.horizontal,
      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
      child: Row(
        children: [
          for (var i = 0; i < _labels.length; i++) ...[
            if (i > 0)
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 6),
                child: Text('›',
                    style: AT.mono(
                        size: 12,
                        color: i < active ? AT.saffron : AT.faint)),
              ),
            _Chip(label: '${i + 1} ${_labels[i]}', on: i + 1 <= active),
          ],
        ],
      ),
    );
  }
}

class _Chip extends StatelessWidget {
  const _Chip({required this.label, required this.on});

  final String label;
  final bool on;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
      decoration: BoxDecoration(
        color: on ? AT.gov : Colors.white,
        borderRadius: BorderRadius.circular(999),
        border: Border.all(color: on ? AT.gov : AT.line),
      ),
      child: Text(
        label,
        style: AT.mono(
          size: 10.5,
          weight: FontWeight.w600,
          color: on ? Colors.white : AT.sub,
        ),
      ),
    );
  }
}

/// Navy masthead shared by every portal screen.
class PortalHeader extends StatelessWidget implements PreferredSizeWidget {
  const PortalHeader({
    super.key,
    this.title = 'AGROTRACE PORTAL',
    this.subtitle = 'FARM-TO-SHELF TRACEABILITY GRID',
    this.leading,
    this.actions,
    this.step,
  });

  final String title;
  final String subtitle;
  final Widget? leading;
  final List<Widget>? actions;
  final int? step;

  @override
  Size get preferredSize =>
      Size.fromHeight(64 + (step == null ? 3 : 47));

  @override
  Widget build(BuildContext context) {
    final canPop = Navigator.of(context).canPop();
    return AppBar(
      automaticallyImplyLeading: false,
      backgroundColor: AT.govDark,
      titleSpacing: 0,
      toolbarHeight: 64,
      title: Row(
        children: [
          if (leading != null) leading!,
          if (leading == null && canPop)
            Padding(
              padding: const EdgeInsets.only(right: 4, left: 8),
              child: IconButton(
                visualDensity: VisualDensity.compact,
                padding: EdgeInsets.zero,
                icon: const Icon(Icons.arrow_back, size: 20),
                onPressed: () => Navigator.of(context).maybePop(),
              ),
            ),
          const SizedBox(width: 8),
          Container(
            width: 34,
            height: 34,
            decoration: BoxDecoration(
              color: AT.govMid,
              borderRadius: BorderRadius.circular(9),
              border: Border.all(color: Colors.white24),
            ),
            alignment: Alignment.center,
            child: const Text('🌾', style: TextStyle(fontSize: 17)),
          ),
          const SizedBox(width: 10),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(title,
                    style: AT.body(
                        size: 15,
                        weight: FontWeight.w800,
                        color: Colors.white,
                        height: 1.1)),
                const SizedBox(height: 2),
                Text(subtitle,
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                    style: AT.mono(
                        size: 9.5,
                        color: const Color(0xFF9DB8D2),
                        letterSpacing: 1.4)),
              ],
            ),
          ),
          if (actions != null) ...actions!,
          const SizedBox(width: 8),
        ],
      ),
      bottom: PreferredSize(
        preferredSize: Size.fromHeight(step == null ? 3 : 47),
        child: Column(
          children: [
            const TricolorHairline(height: 3),
            if (step != null) StepIndicator(active: step!),
          ],
        ),
      ),
    );
  }
}

/// White rounded panel with the portal's thin border.
class Panel extends StatelessWidget {
  const Panel({
    super.key,
    required this.child,
    this.padding = const EdgeInsets.all(18),
    this.margin = const EdgeInsets.fromLTRB(20, 12, 20, 12),
    this.color = AT.card,
    this.dashed = false,
  });

  final Widget child;
  final EdgeInsets padding;
  final EdgeInsets margin;
  final Color color;
  final bool dashed;

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: margin,
      padding: padding,
      decoration: BoxDecoration(
        color: color,
        borderRadius: BorderRadius.circular(AT.radius),
        border: Border.all(
          color: AT.line,
          style: dashed ? BorderStyle.solid : BorderStyle.solid,
        ),
      ),
      child: child,
    );
  }
}

/// Small over-line section caption: "■ ACTION CONSOLE".
class SectionCaption extends StatelessWidget {
  const SectionCaption(this.text, {super.key, this.color = AT.gov});

  final String text;
  final Color color;

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Container(width: 8, height: 8, color: color),
        const SizedBox(width: 8),
        Expanded(
          child: Text(text.toUpperCase(),
              style: AT.label(size: 10.5, color: color)),
        ),
      ],
    );
  }
}

/// Full-width primary action button.
class GovButton extends StatelessWidget {
  const GovButton({
    super.key,
    required this.label,
    required this.onPressed,
    this.loading = false,
    this.icon,
    this.secondary = false,
  });

  final String label;
  final VoidCallback? onPressed;
  final bool loading;
  final IconData? icon;
  final bool secondary;

  @override
  Widget build(BuildContext context) {
    final child = loading
        ? const SizedBox(
            width: 18,
            height: 18,
            child: CircularProgressIndicator(
                strokeWidth: 2, color: Colors.white),
          )
        : Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              if (icon != null) ...[
                Icon(icon, size: 17),
                const SizedBox(width: 8),
              ],
              Text(label),
            ],
          );
    return SizedBox(
      width: double.infinity,
      child: secondary
          ? OutlinedButton(
              onPressed: loading ? null : onPressed,
              style: OutlinedButton.styleFrom(
                foregroundColor: loading ? AT.faint : AT.gov,
              ),
              child: loading
                  ? const SizedBox(
                      width: 18,
                      height: 18,
                      child: CircularProgressIndicator(
                          strokeWidth: 2, color: AT.gov),
                    )
                  : child,
            )
          : ElevatedButton(
              onPressed: loading ? null : onPressed,
              child: child,
            ),
    );
  }
}

/// Backend error envelope banner (code + message + trace id).
class ErrorBanner extends StatelessWidget {
  const ErrorBanner({super.key, required this.message, this.code, this.traceId});

  final String message;
  final String? code;
  final String? traceId;

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.fromLTRB(20, 10, 20, 2),
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: AT.roseBg,
        borderRadius: BorderRadius.circular(10),
        border: Border.all(color: const Color(0xFFFECDD3)),
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Icon(Icons.gpp_bad_outlined, size: 18, color: AT.roseInk),
          const SizedBox(width: 10),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                if (code != null && code!.isNotEmpty)
                  Text(code!,
                      style: AT.mono(
                          size: 10.5,
                          weight: FontWeight.w700,
                          color: AT.roseInk)),
                Text(message,
                    style: AT.body(size: 12.5, color: AT.roseInk)),
                if (traceId != null)
                  Text('trace: $traceId',
                      style: AT.mono(size: 9.5, color: AT.roseInk)),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

/// Small mono chip used for statuses / IDs.
class MonoChip extends StatelessWidget {
  const MonoChip({
    super.key,
    required this.text,
    this.bg = AT.skyBg,
    this.fg = AT.skyInk,
  });

  final String text;
  final Color bg;
  final Color fg;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3.5),
      decoration: BoxDecoration(
        color: bg,
        borderRadius: BorderRadius.circular(6),
      ),
      child: Text(text,
          style: AT.mono(size: 10, weight: FontWeight.w700, color: fg)),
    );
  }
}

Color statusColor(String status) {
  switch (status.toUpperCase()) {
    case 'CREATED':
    case 'OPEN':
    case 'PENDING':
      return AT.saffron;
    case 'ACCEPTED':
    case 'RECEIVED':
    case 'IN_TRANSIT':
    case 'IN-TRANSIT':
    case 'DISPATCHED':
      return AT.skyInk;
    case 'PASSED':
    case 'RESOLVED':
    case 'COMPLETED':
    case 'VERIFIED':
    case 'RETAILER_RECEIVED':
      return AT.leafLight;
    case 'REJECTED':
    case 'RECALLED':
    case 'QUARANTINED':
    case 'FAILED':
      return const Color(0xFFE11D48);
    default:
      return AT.sub;
  }
}

void showError(BuildContext context, Object e) {
  final msg = e.toString().replaceAll('ApiException: ', '');
  ScaffoldMessenger.of(context)
    ..hideCurrentSnackBar()
    ..showSnackBar(SnackBar(content: Text(msg)));
}
