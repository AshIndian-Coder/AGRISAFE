import 'package:flutter/material.dart';

import '../../core/app_theme.dart';

/// Six-dot display + numeric keypad, matching the web portal's
/// Security PIN step.
class PinPad extends StatelessWidget {
  const PinPad({
    super.key,
    required this.pin,
    required this.onDigit,
    required this.onBackspace,
    this.error = false,
  });

  final String pin;
  final ValueChanged<String> onDigit;
  final VoidCallback onBackspace;
  final bool error;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: List.generate(6, (i) {
            final filled = i < pin.length;
            return AnimatedContainer(
              duration: const Duration(milliseconds: 120),
              margin: const EdgeInsets.symmetric(horizontal: 7),
              width: 16,
              height: 16,
              decoration: BoxDecoration(
                shape: BoxShape.circle,
                color: filled
                    ? (error ? const Color(0xFFE11D48) : AT.gov)
                    : Colors.transparent,
                border: Border.all(
                  color: filled
                      ? (error ? const Color(0xFFE11D48) : AT.gov)
                      : AT.faint,
                  width: 1.6,
                ),
              ),
            );
          }),
        ),
        const SizedBox(height: 26),
        _Row(
          keys: const ['1', '2', '3'],
          onDigit: onDigit,
        ),
        _Row(keys: const ['4', '5', '6'], onDigit: onDigit),
        _Row(keys: const ['7', '8', '9'], onDigit: onDigit),
        _Row(
          keys: const ['', '0', 'del'],
          onDigit: onDigit,
          onBackspace: onBackspace,
        ),
      ],
    );
  }
}

class _Row extends StatelessWidget {
  const _Row({required this.keys, required this.onDigit, this.onBackspace});

  final List<String> keys;
  final ValueChanged<String> onDigit;
  final VoidCallback? onBackspace;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 12),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: keys.map((k) {
          if (k.isEmpty) {
            return const SizedBox(width: 78, height: 58);
          }
          final isDel = k == 'del';
          return Padding(
            padding: const EdgeInsets.symmetric(horizontal: 8),
            child: Material(
              color: isDel ? Colors.transparent : Colors.white,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(14),
                side: BorderSide(
                    color: isDel ? Colors.transparent : AT.line),
              ),
              child: InkWell(
                borderRadius: BorderRadius.circular(14),
                onTap: () =>
                    isDel ? (onBackspace ?? () {})() : onDigit(k),
                child: SizedBox(
                  width: 78,
                  height: 58,
                  child: Center(
                    child: isDel
                        ? const Icon(Icons.backspace_outlined,
                            size: 20, color: AT.sub)
                        : Text(k,
                            style: AT.mono(
                                size: 20,
                                weight: FontWeight.w600,
                                color: AT.ink)),
                  ),
                ),
              ),
            ),
          );
        }).toList(),
      ),
    );
  }
}
