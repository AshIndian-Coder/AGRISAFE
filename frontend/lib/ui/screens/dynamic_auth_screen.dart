import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import '../../core/app_theme.dart';
import '../../core/roles.dart';
import '../components/chrome.dart';
import '../components/forms.dart';
import 'pin_screen.dart';

/// STEP 2 — Dynamic Auth.
///
/// Every node presents its own credential set (mirroring the web portal),
/// but only registry reference IDs ever leave the device. Aadhaar inputs
/// are marked "verify only" per the zero raw-identifier charter.
class DynamicAuthScreen extends StatefulWidget {
  const DynamicAuthScreen({super.key, required this.role});

  final NodeRole role;

  @override
  State<DynamicAuthScreen> createState() => _DynamicAuthScreenState();
}

class _DynamicAuthScreenState extends State<DynamicAuthScreen> {
  static const _states = [
    'Andhra Pradesh',
    'Karnataka',
    'Madhya Pradesh',
    'Maharashtra',
    'Tamil Nadu',
    'Uttar Pradesh',
    'West Bengal',
  ];
  static const _hubs = [
    'Azadpur Mandi, Delhi',
    'Vashi APMC, Navi Mumbai',
    'Kolar Cold Hub, Karnataka',
    'Guwahati Regional Hub',
    'Kochi Port Terminal',
    'Ludhiana Grain Terminal',
  ];
  static const _formats = [
    'Modern Trade Supermarket',
    'Kirana / General Store',
    'E-commerce Dark Store',
  ];
  static const _shifts = [
    'Shift A (06:00-14:00)',
    'Shift B (14:00-22:00)',
    'Shift C (22:00-06:00)',
  ];

  bool _registerMode = false;

  final _reference = TextEditingController();
  final _aadhaarRef = TextEditingController(text: 'AADHAR-REF-001');
  final _otp = TextEditingController();

  // Decorative node-specific fields (verified in volatile memory only).
  final _f1 = TextEditingController();
  final _f2 = TextEditingController();
  final _f3 = TextEditingController();
  String? _pick1;

  @override
  void initState() {
    super.initState();
    _reference.text = widget.role.meta.demoIdentity;
  }

  @override
  void dispose() {
    _reference.dispose();
    _aadhaarRef.dispose();
    _otp.dispose();
    _f1.dispose();
    _f2.dispose();
    _f3.dispose();
    super.dispose();
  }

  NodeRoleMeta get _m => widget.role.meta;

  void _continue() {
    final identity = _reference.text.trim();
    if (identity.isEmpty) {
      showError(context, 'Registry reference ID is required to authenticate.');
      return;
    }
    if (_registerMode) {
      if (_aadhaarRef.text.trim().isEmpty) {
        showError(context, 'An Aadhaar reference token is required for registration.');
        return;
      }
      if (_otp.text.trim().length != 6) {
        showError(context, 'OTP must be 6 digits (prototype accepts any 6 digits).');
        return;
      }
    }
    Navigator.of(context).push(
      MaterialPageRoute(
        builder: (_) => PinScreen(
          role: widget.role,
          registerMode: _registerMode,
          identity: identity,
          aadhaarReference: _aadhaarRef.text.trim(),
          otp: _otp.text.trim(),
          gstNumber:
              widget.role == NodeRole.retailer ? _f1.text.trim() : '',
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: PortalHeader(step: 2, subtitle: '${_m.nodeCode} · DYNAMIC AUTH'),
      body: ListView(
        padding: const EdgeInsets.only(bottom: 40),
        children: [
          const SizedBox(height: 8),
          Panel(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    Text(_m.emoji, style: const TextStyle(fontSize: 26)),
                    const SizedBox(width: 10),
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(_m.title,
                              style: AT.body(
                                  size: 16, weight: FontWeight.w800)),
                          Text(_m.ministry,
                              style: AT.mono(size: 9.5, color: AT.faint)),
                        ],
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 14),
                if (_m.canRegister)
                  Container(
                    padding: const EdgeInsets.all(4),
                    decoration: BoxDecoration(
                      color: AT.slateBg,
                      borderRadius: BorderRadius.circular(9),
                      border: Border.all(color: AT.line),
                    ),
                    child: Row(
                      children: [
                        _modeTab('SIGN IN', !_registerMode,
                            () => setState(() => _registerMode = false)),
                        _modeTab('NEW REGISTRATION', _registerMode,
                            () => setState(() => _registerMode = true)),
                      ],
                    ),
                  ),
                if (!_m.canRegister)
                  Container(
                    padding: const EdgeInsets.all(10),
                    decoration: BoxDecoration(
                      color: AT.amberBg,
                      borderRadius: BorderRadius.circular(8),
                      border: Border.all(color: const Color(0xFFFDE68A)),
                    ),
                    child: Text(
                      'Regulatory accounts are provisioned by FSSAI only. Sign in with your officer credentials.',
                      style: AT.body(size: 11.5, color: AT.amberInk),
                    ),
                  ),
              ],
            ),
          ),
          Panel(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const SectionCaption('Credential verification'),
                const SizedBox(height: 14),
                GovField(
                  label: _m.identityLabel,
                  controller: _reference,
                  mono: true,
                  hint: _m.identityHint,
                  helper:
                      'Backend login identity — the reference issued at registration. Dev-profile demo: ${_m.demoIdentity}',
                  prefixIcon: const Icon(Icons.badge_outlined,
                      size: 18, color: AT.gov),
                ),
                const SizedBox(height: 16),
                ..._nodeFields(),
                if (_registerMode) ...[
                  const SizedBox(height: 16),
                  GovField(
                    label: 'Aadhaar reference token (verify only)',
                    controller: _aadhaarRef,
                    mono: true,
                    hint: 'AADHAR-REF-001',
                    helper:
                        'Seed registry token — raw numbers are never stored. [Aadhaar Verified]',
                  ),
                  const SizedBox(height: 16),
                  GovField(
                    label: 'One-time passcode',
                    controller: _otp,
                    mono: true,
                    hint: '••••••',
                    keyboardType: TextInputType.number,
                    inputFormatters: [
                      FilteringTextInputFormatter.digitsOnly,
                      LengthLimitingTextInputFormatter(6),
                    ],
                    helper: 'Prototype: any 6-digit OTP is accepted.',
                  ),
                ],
                const SizedBox(height: 20),
                GovButton(
                  label: _registerMode
                      ? 'CONTINUE → SET SECURITY PIN'
                      : 'CONTINUE → SECURITY PIN',
                  icon: Icons.lock_outline,
                  onPressed: _continue,
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _modeTab(String label, bool active, VoidCallback onTap) {
    return Expanded(
      child: GestureDetector(
        onTap: onTap,
        child: Container(
          padding: const EdgeInsets.symmetric(vertical: 9),
          decoration: BoxDecoration(
            color: active ? AT.gov : Colors.transparent,
            borderRadius: BorderRadius.circular(7),
          ),
          alignment: Alignment.center,
          child: Text(
            label,
            style: AT.mono(
              size: 10,
              weight: FontWeight.w700,
              color: active ? Colors.white : AT.sub,
            ),
          ),
        ),
      ),
    );
  }

  List<Widget> _nodeFields() {
    switch (widget.role) {
      case NodeRole.farmer:
        return [
          GovField(
            label: 'PF-ID (Producer / Farmer ID)',
            controller: _f1,
            mono: true,
            hint: 'PF-MH-004512',
            helper: 'Expected format: PF-MH-004512',
          ),
          const SizedBox(height: 16),
          GovDropdown<String>(
            label: 'Land holding state',
            value: _pick1 ?? 'Maharashtra',
            items: [
              for (final s in _states)
                DropdownMenuItem(value: s, child: Text(s)),
            ],
            onChanged: (v) => setState(() => _pick1 = v),
          ),
          const SizedBox(height: 16),
          GovField(
            label: 'Khasra / Survey No.',
            controller: _f2,
            mono: true,
            hint: '112/3B',
          ),
        ];
      case NodeRole.agent:
        return [
          GovField(
            label: 'Company employee ID',
            controller: _f1,
            mono: true,
            hint: 'AGT-104829',
            helper: 'Expected format: AGT-104829',
          ),
          const SizedBox(height: 16),
          Row(
            children: [
              Expanded(
                child: GovField(
                  label: 'Employer GSTIN',
                  controller: _f2,
                  mono: true,
                  hint: 'AAGCB1286Q',
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: GovField(
                  label: 'Facility ID',
                  controller: _f3,
                  mono: true,
                  hint: 'WH-DEL-01',
                  helper: 'Format: WH-DEL-01',
                ),
              ),
            ],
          ),
          const SizedBox(height: 16),
          GovDropdown<String>(
            label: 'Duty shift',
            value: _pick1 ?? _shifts[0],
            items: [
              for (final s in _shifts)
                DropdownMenuItem(value: s, child: Text(s)),
            ],
            onChanged: (v) => setState(() => _pick1 = v),
          ),
        ];
      case NodeRole.supplier:
        return [
          Row(
            children: [
              Expanded(
                child: GovField(
                  label: 'GSTIN',
                  controller: _f1,
                  mono: true,
                  hint: 'AAPFU0939F',
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: GovField(
                  label: 'APMC trade licence',
                  controller: _f2,
                  mono: true,
                  hint: 'APMC-MH-4471',
                  helper: 'Format: APMC-MH-4471',
                ),
              ),
            ],
          ),
          const SizedBox(height: 16),
          GovField(
            label: 'Declared fleet size',
            controller: _f3,
            mono: true,
            keyboardType: TextInputType.number,
            inputFormatters: [FilteringTextInputFormatter.digitsOnly],
            hint: '12',
          ),
          const SizedBox(height: 16),
          GovDropdown<String>(
            label: 'Base operating hub',
            value: _pick1 ?? _hubs[1],
            items: [
              for (final h in _hubs)
                DropdownMenuItem(value: h, child: Text(h)),
            ],
            onChanged: (v) => setState(() => _pick1 = v),
          ),
        ];
      case NodeRole.retailer:
        return [
          GovField(
            label: 'Business GSTIN',
            controller: _f1,
            mono: true,
            hint: 'AAGCB1286Q',
          ),
          const SizedBox(height: 16),
          GovField(
            label: 'Retail outlet code',
            controller: _f2,
            mono: true,
            hint: 'RT-BLR-104',
            helper: 'Expected format: RT-BLR-104',
          ),
          const SizedBox(height: 16),
          GovDropdown<String>(
            label: 'Business format',
            value: _pick1 ?? _formats[0],
            items: [
              for (final f in _formats)
                DropdownMenuItem(value: f, child: Text(f)),
            ],
            onChanged: (v) => setState(() => _pick1 = v),
          ),
        ];
      case NodeRole.inspector:
        return [
          GovField(
            label: 'FSSAI officer code',
            controller: _f1,
            mono: true,
            hint: 'FSSAI-DL-2291',
            helper: 'Expected format: FSSAI-DL-2291',
          ),
          const SizedBox(height: 16),
          GovField(
            label: 'Service employee ID',
            controller: _f2,
            mono: true,
            hint: 'FSO-220914',
          ),
        ];
    }
  }
}
