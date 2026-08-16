import 'package:flutter/material.dart';

import '../../core/app_theme.dart';
import '../../core/models.dart';
import '../../core/roles.dart';
import '../../core/deps.dart';
import '../components/chrome.dart';
import '../components/ledger.dart';
import '../components/pin_pad.dart';
import 'dashboard_screen.dart';

/// STEP 3 — Security PIN.
///
/// * login   → `POST /auth/login`
/// * register→ role-specific `/auth/*/register` (PIN chosen here)
/// * unlock  → resume gate: re-authenticates with the stored identity
///             ("6-digit PIN re-entry on every resume")
class PinScreen extends StatefulWidget {
  const PinScreen({
    super.key,
    required this.role,
    this.registerMode = false,
    this.unlockMode = false,
    required this.identity,
    this.aadhaarReference = '',
    this.otp = '',
    this.gstNumber = '',
  });

  final NodeRole role;
  final bool registerMode;
  final bool unlockMode;
  final String identity;
  final String aadhaarReference;
  final String otp;
  final String gstNumber;

  @override
  State<PinScreen> createState() => _PinScreenState();
}

class _PinScreenState extends State<PinScreen> {
  String _pin = '';
  bool _busy = false;
  bool _error = false;
  String? _errorText;

  void _onDigit(String d) {
    if (_busy || _pin.length >= 6) return;
    setState(() {
      _pin += d;
      _error = false;
      _errorText = null;
    });
    if (_pin.length == 6) _submit();
  }

  void _onBackspace() {
    if (_busy || _pin.isEmpty) return;
    setState(() => _pin = _pin.substring(0, _pin.length - 1));
  }

  Future<void> _submit() async {
    setState(() => _busy = true);
    try {
      final api = deps.api;
      final AuthSession session;
      if (widget.registerMode) {
        switch (widget.role) {
          case NodeRole.farmer:
            session = await api.registerFarmer(
              aadhaarReference: widget.identity,
              otp: widget.otp,
              pin: _pin,
            );
            break;
          case NodeRole.retailer:
            session = await api.registerRetailer(
              gstNumber: widget.gstNumber.isEmpty
                  ? widget.identity
                  : widget.gstNumber,
              aadhaarReference: widget.identity,
              otp: widget.otp,
              pin: _pin,
            );
            break;
          default:
            session = await api.registerPf(
              pfReference: widget.identity,
              aadhaarReference: widget.aadhaarReference,
              otp: widget.otp,
              pin: _pin,
              userType: widget.role.meta.backendUserType,
            );
        }
      } else {
        session = await api.login(widget.identity, _pin);
      }

      deps.session.auth = session;
      deps.session.identity = widget.identity;
      await deps.config.setLastIdentity(widget.identity);
      await deps.config.setLastRole(widget.role.name);

      if (!mounted) return;
      Navigator.of(context).pushAndRemoveUntil(
        MaterialPageRoute(
          builder: (_) => DashboardScreen(role: widget.role),
        ),
        (r) => false,
      );
    } on ApiException catch (e) {
      setState(() {
        _busy = false;
        _error = true;
        _errorText = e.code == 'PIN_INVALID'
            ? 'Incorrect PIN. 5 attempts are allowed before a 30-minute lockout.'
            : e.code == 'PIN_LOCKED'
                ? 'Account locked after repeated PIN failures. Try again in 30 minutes.'
                : '${e.code}: ${e.message}';
        _pin = '';
      });
    } catch (e) {
      setState(() {
        _busy = false;
        _error = true;
        _errorText = e.toString();
        _pin = '';
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final m = widget.role.meta;
    final heading = widget.unlockMode
        ? 'Resume session'
        : widget.registerMode
            ? 'Create security PIN'
            : 'Enter security PIN';
    final sub = widget.unlockMode
        ? 'Re-enter your 6-digit PIN to reopen the ${m.title} console.'
        : widget.registerMode
            ? 'Choose the 6-digit PIN you will use for every sign-in and operational scan.'
            : 'Authenticate ${m.nodeCode} against the Agro Trace ledger.';

    return Scaffold(
      appBar: PortalHeader(step: 3, subtitle: '${m.nodeCode} · SECURITY PIN'),
      body: ListView(
        padding: const EdgeInsets.only(bottom: 40),
        children: [
          const SizedBox(height: 8),
          Panel(
            child: Column(
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Container(
                      width: 54,
                      height: 54,
                      decoration: BoxDecoration(
                        color: AT.govDark,
                        borderRadius: BorderRadius.circular(14),
                      ),
                      alignment: Alignment.center,
                      child: Text(m.emoji,
                          style: const TextStyle(fontSize: 24)),
                    ),
                  ],
                ),
                const SizedBox(height: 12),
                Text(heading,
                    style: AT.body(size: 17, weight: FontWeight.w800)),
                const SizedBox(height: 4),
                Text(sub,
                    textAlign: TextAlign.center,
                    style: AT.body(size: 12, color: AT.sub)),
                const SizedBox(height: 8),
                MonoChip(
                  text: widget.identity,
                  bg: AT.slateBg,
                  fg: AT.govMid,
                ),
                if (widget.unlockMode) ...[
                  const SizedBox(height: 8),
                  TextButton(
                    onPressed: () =>
                        Navigator.of(context).popUntil((r) => r.isFirst),
                    child: Text('Use a different node',
                        style: AT.mono(size: 10.5, color: AT.faint)),
                  ),
                ],
              ],
            ),
          ),
          if (_errorText != null)
            ErrorBanner(message: _errorText!, code: 'PIN GATE'),
          Panel(
            child: PinPad(
              pin: _pin,
              onDigit: _onDigit,
              onBackspace: _onBackspace,
              error: _error,
            ),
          ),
          if (_busy)
            const Padding(
              padding: EdgeInsets.only(top: 6),
              child: LoadingVeil(message: 'VERIFYING AGAINST LEDGER…'),
            ),
        ],
      ),
    );
  }
}
