import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import '../../core/app_theme.dart';

/// Portal-style labelled text field with a format hint underneath.
class GovField extends StatelessWidget {
  const GovField({
    super.key,
    required this.label,
    required this.controller,
    this.hint,
    this.helper,
    this.mono = false,
    this.obscure = false,
    this.keyboardType,
    this.inputFormatters,
    this.suffix,
    this.prefixIcon,
    this.maxLines = 1,
    this.onChanged,
    this.enabled = true,
    this.autofocus = false,
  });

  final String label;
  final TextEditingController controller;
  final String? hint;
  final String? helper;
  final bool mono;
  final bool obscure;
  final TextInputType? keyboardType;
  final List<TextInputFormatter>? inputFormatters;
  final Widget? suffix;
  final Widget? prefixIcon;
  final int maxLines;
  final ValueChanged<String>? onChanged;
  final bool enabled;
  final bool autofocus;

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(label.toUpperCase(), style: AT.label(size: 10)),
        const SizedBox(height: 6),
        TextField(
          controller: controller,
          obscureText: obscure,
          keyboardType: keyboardType,
          inputFormatters: inputFormatters,
          maxLines: maxLines,
          onChanged: onChanged,
          enabled: enabled,
          autofocus: autofocus,
          style: mono
              ? AT.mono(size: 13.5, weight: FontWeight.w600)
              : AT.body(size: 14),
          decoration: InputDecoration(
            hintText: hint,
            suffixIcon: suffix,
            prefixIcon: prefixIcon,
          ),
        ),
        if (helper != null) ...[
          const SizedBox(height: 5),
          Text(helper!, style: AT.mono(size: 10, color: AT.faint)),
        ],
      ],
    );
  }
}

/// Portal-style dropdown.
class GovDropdown<T> extends StatelessWidget {
  const GovDropdown({
    super.key,
    required this.label,
    required this.value,
    required this.items,
    required this.onChanged,
    this.helper,
    this.enabled = true,
  });

  final String label;
  final T? value;
  final List<DropdownMenuItem<T>> items;
  final ValueChanged<T?> onChanged;
  final String? helper;
  final bool enabled;

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(label.toUpperCase(), style: AT.label(size: 10)),
        const SizedBox(height: 6),
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 6),
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(10),
            border: Border.all(color: AT.line),
          ),
          child: DropdownButtonHideUnderline(
            child: DropdownButton<T>(
              value: value,
              items: items,
              onChanged: enabled ? onChanged : null,
              isExpanded: true,
              icon: const Icon(Icons.keyboard_arrow_down, color: AT.sub),
              style: AT.body(size: 14),
              borderRadius: BorderRadius.circular(10),
            ),
          ),
        ),
        if (helper != null) ...[
          const SizedBox(height: 5),
          Text(helper!, style: AT.mono(size: 10, color: AT.faint)),
        ],
      ],
    );
  }
}
