import 'package:flutter/material.dart';

import '../../core/app_theme.dart';
import '../../core/roles.dart';
import '../components/chrome.dart';
import 'consumer_verify_screen.dart';
import 'dynamic_auth_screen.dart';
import 'settings_screen.dart';

/// STEP 1 — Select your operating role.
class RoleSelectionScreen extends StatelessWidget {
  const RoleSelectionScreen({super.key});

  static const route = '/';

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: PortalHeader(
        step: 1,
        actions: [
          IconButton(
            tooltip: 'Backend settings',
            icon: const Icon(Icons.settings_outlined, size: 19),
            onPressed: () => Navigator.of(context).push(
              MaterialPageRoute(builder: (_) => const SettingsScreen()),
            ),
          ),
        ],
      ),
      body: ListView(
        padding: const EdgeInsets.only(bottom: 40),
        children: [
          const SizedBox(height: 8),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 20),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text('Select your operating role',
                    style: AT.body(size: 19, weight: FontWeight.w800)),
                const SizedBox(height: 6),
                Text(
                  'Each node of the supply chain authenticates with a different credential set and receives a purpose-built operational console. Choose the role you are registered under.',
                  style: AT.body(size: 12.5, color: AT.sub),
                ),
              ],
            ),
          ),
          const SizedBox(height: 10),
          for (final role in NodeRole.values)
            _NodeCard(
              role: role,
              onTap: () => Navigator.of(context).push(
                MaterialPageRoute(
                  builder: (_) => DynamicAuthScreen(role: role),
                ),
              ),
            ),
          const SizedBox(height: 4),
          _ConsumerCard(
            onTap: () => Navigator.of(context).push(
              MaterialPageRoute(
                  builder: (_) => const ConsumerVerifyScreen()),
            ),
          ),
          const SizedBox(height: 16),
          const _PrivacyCharter(),
        ],
      ),
    );
  }
}

class _NodeCard extends StatelessWidget {
  const _NodeCard({required this.role, required this.onTap});

  final NodeRole role;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    final m = role.meta;
    return Container(
      margin: const EdgeInsets.fromLTRB(20, 10, 20, 0),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(AT.radius),
        border: Border.all(color: AT.line),
      ),
      child: Material(
        color: Colors.transparent,
        child: InkWell(
          borderRadius: BorderRadius.circular(AT.radius),
          onTap: onTap,
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    Container(
                      width: 46,
                      height: 46,
                      decoration: BoxDecoration(
                        color: AT.slateBg,
                        borderRadius: BorderRadius.circular(12),
                        border: Border.all(color: AT.line),
                      ),
                      alignment: Alignment.center,
                      child: Text(m.emoji,
                          style: const TextStyle(fontSize: 22)),
                    ),
                    const SizedBox(width: 12),
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(m.nodeCode,
                              style: AT.mono(
                                  size: 9.5,
                                  weight: FontWeight.w700,
                                  color: AT.saffron,
                                  letterSpacing: 1.6)),
                          const SizedBox(height: 2),
                          Text(m.title,
                              style: AT.body(
                                  size: 15.5, weight: FontWeight.w800)),
                        ],
                      ),
                    ),
                    const Icon(Icons.arrow_forward, size: 18, color: AT.faint),
                  ],
                ),
                const SizedBox(height: 10),
                Text(m.tagline,
                    style: AT.body(
                        size: 11.5,
                        weight: FontWeight.w600,
                        color: AT.govMid)),
                const SizedBox(height: 4),
                Text(m.description,
                    style: AT.body(size: 12.5, color: AT.sub)),
                const Divider(height: 20),
                Row(
                  children: [
                    const Icon(Icons.account_balance_outlined,
                        size: 13, color: AT.faint),
                    const SizedBox(width: 6),
                    Expanded(
                      child: Text(m.ministry,
                          style:
                              AT.mono(size: 9.5, color: AT.faint)),
                    ),
                    Text('CONTINUE →',
                        style: AT.mono(
                            size: 10,
                            weight: FontWeight.w700,
                            color: AT.gov)),
                  ],
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class _ConsumerCard extends StatelessWidget {
  const _ConsumerCard({required this.onTap});

  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.fromLTRB(20, 10, 20, 0),
      decoration: BoxDecoration(
        color: AT.emeraldBg,
        borderRadius: BorderRadius.circular(AT.radius),
        border: Border.all(color: const Color(0xFFA7F3D0)),
      ),
      child: Material(
        color: Colors.transparent,
        child: InkWell(
          borderRadius: BorderRadius.circular(AT.radius),
          onTap: onTap,
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Row(
              children: [
                const Text('🔍', style: TextStyle(fontSize: 22)),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text('PUBLIC · CONSUMER VERIFICATION',
                          style: AT.mono(
                              size: 9.5,
                              weight: FontWeight.w700,
                              color: AT.emeraldInk,
                              letterSpacing: 1.4)),
                      const SizedBox(height: 3),
                      Text('Verify a retail trace QR',
                          style: AT.body(
                              size: 14.5, weight: FontWeight.w800)),
                      Text(
                          'No login required — open government verification endpoint.',
                          style: AT.body(size: 11.5, color: AT.sub)),
                    ],
                  ),
                ),
                const Icon(Icons.qr_code_scanner,
                    size: 20, color: AT.emeraldInk),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class _PrivacyCharter extends StatelessWidget {
  const _PrivacyCharter();

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.fromLTRB(20, 10, 20, 0),
      padding: const EdgeInsets.all(18),
      decoration: BoxDecoration(
        color: AT.govDark,
        borderRadius: BorderRadius.circular(AT.radius),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              const Icon(Icons.shield_outlined,
                  size: 16, color: AT.saffron),
              const SizedBox(width: 8),
              Text('PRIVACY CHARTER',
                  style: AT.mono(
                      size: 10.5,
                      weight: FontWeight.w700,
                      color: AT.saffron,
                      letterSpacing: 1.6)),
            ],
          ),
          const SizedBox(height: 10),
          Text('Zero raw-identifier policy',
              style: AT.body(
                  size: 14, weight: FontWeight.w800, color: Colors.white)),
          const SizedBox(height: 6),
          Text(
            'Aadhaar numbers, South-Korean RRNs and Japanese MyNumbers are validated in volatile memory and destroyed immediately. They are never stored, echoed, printed to logs, embedded in QR payloads or rendered in any table. Sessions display only [Aadhaar Verified] or [Govt ID Linked].',
            style: AT.body(size: 11.5, color: const Color(0xFFC7D6E5),
                height: 1.55),
          ),
          const SizedBox(height: 12),
          for (final b in const [
            '6-digit PIN re-entry on every resume',
            'Hash-chained, tamper-evident activity ledger',
            'Strict zero-state on first dashboard load',
          ])
            Padding(
              padding: const EdgeInsets.only(bottom: 5),
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text('• ',
                      style: AT.body(size: 12, color: AT.saffron)),
                  Expanded(
                    child: Text(b,
                        style: AT.body(
                            size: 12, color: const Color(0xFFE2ECF5))),
                  ),
                ],
              ),
            ),
        ],
      ),
    );
  }
}
