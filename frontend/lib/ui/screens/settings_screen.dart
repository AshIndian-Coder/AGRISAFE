import 'package:flutter/material.dart';

import '../../core/api_client.dart';
import '../../core/app_theme.dart';
import '../../core/deps.dart';
import '../components/chrome.dart';
import '../components/forms.dart';

/// Backend connection settings + prototype identity handbook.
class SettingsScreen extends StatefulWidget {
  const SettingsScreen({super.key});

  @override
  State<SettingsScreen> createState() => _SettingsScreenState();
}

class _SettingsScreenState extends State<SettingsScreen> {
  late final TextEditingController _url;
  String _health = '…';
  bool _checking = false;

  @override
  void initState() {
    super.initState();
    _url = TextEditingController(text: deps.config.baseUrl);
    _ping();
  }

  @override
  void dispose() {
    _url.dispose();
    super.dispose();
  }

  Future<void> _ping() async {
    setState(() {
      _checking = true;
      _health = '…';
    });
    final h = await deps.client.healthCheck();
    if (!mounted) return;
    setState(() {
      _checking = false;
      _health = h;
    });
  }

  Future<void> _save() async {
    await deps.config.setBaseUrl(_url.text);
    if (!mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text('Base URL saved: ${deps.config.baseUrl}')),
    );
    _ping();
  }

  @override
  Widget build(BuildContext context) {
    final healthColor = _health == 'UP'
        ? AT.leafLight
        : _health == 'DOWN'
            ? const Color(0xFFE11D48)
            : AT.saffron;
    return Scaffold(
      appBar: const PortalHeader(subtitle: 'BACKEND LINK · CONFIGURATION'),
      body: ListView(
        padding: const EdgeInsets.only(bottom: 40),
        children: [
          const SizedBox(height: 8),
          Panel(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const SectionCaption('API endpoint'),
                const SizedBox(height: 12),
                GovField(
                  label: 'Base URL',
                  controller: _url,
                  mono: true,
                  hint: AppConfig.defaultBaseUrl,
                  helper:
                      'Android emulator → http://10.0.2.2:8080/api/v1 · physical device → your machine LAN IP · iOS simulator → http://localhost:8080/api/v1',
                ),
                const SizedBox(height: 14),
                Row(
                  children: [
                    Expanded(
                      child: GovButton(
                          label: 'SAVE', onPressed: _save),
                    ),
                    const SizedBox(width: 10),
                    Expanded(
                      child: GovButton(
                        label: _checking ? 'CHECKING…' : 'HEALTH CHECK',
                        secondary: true,
                        onPressed: _checking ? null : _ping,
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 14),
                Container(
                  padding: const EdgeInsets.all(12),
                  decoration: BoxDecoration(
                    color: healthColor.withAlpha(20),
                    borderRadius: BorderRadius.circular(9),
                    border: Border.all(color: healthColor),
                  ),
                  child: Row(
                    children: [
                      Icon(
                        _health == 'UP'
                            ? Icons.check_circle
                            : _health == 'DOWN'
                                ? Icons.error_outline
                                : Icons.hourglass_top,
                        size: 17,
                        color: healthColor,
                      ),
                      const SizedBox(width: 8),
                      Text(
                        'ACTUATOR / HEALTH: $_health',
                        style: AT.mono(
                            size: 11, weight: FontWeight.w700, color: healthColor),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
          Panel(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const SectionCaption('Prototype identities (dev seeder)'),
                const SizedBox(height: 8),
                Text(
                  'Run the backend with --spring.profiles.active=dev. All demo users share PIN 123456.',
                  style: AT.body(size: 11.5, color: AT.sub),
                ),
                const SizedBox(height: 10),
                for (final r in const [
                  ('🌾 Farmer', 'AADHAR-DEMO-FARMER'),
                  ('🏭 Receiving Agent', 'PF-COL-DEMO'),
                  ('🚚 Supplier / Aggregator', 'PF-SUP-DEMO'),
                  ('🏪 Retailer', 'AADHAR-DEMO-RET'),
                  ('🛡️ FSSAI / Government', 'PF-AG-DEMO'),
                  ('🏭 Manufacturer', 'PF-MFG-DEMO'),
                  ('📦 Distributor', 'PF-DIST-DEMO'),
                ])
                  Padding(
                    padding: const EdgeInsets.symmetric(vertical: 5),
                    child: Row(
                      children: [
                        Expanded(
                          child: Text(r.$1, style: AT.body(size: 12)),
                        ),
                        Text(r.$2,
                            style: AT.mono(
                                size: 11, weight: FontWeight.w700)),
                      ],
                    ),
                  ),
              ],
            ),
          ),
          Panel(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const SectionCaption('Zero raw-identifier charter'),
                const SizedBox(height: 10),
                Text(
                  'This app never stores Aadhaar numbers, RRNs or MyNumbers. Only registry reference tokens persist on-device so the PIN gate can re-authenticate you on resume. Sessions display [Aadhaar Verified] instead of raw identifiers.',
                  style: AT.body(size: 12, color: AT.sub, height: 1.55),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
