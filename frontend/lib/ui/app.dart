import 'package:flutter/material.dart';

import '../core/app_theme.dart';
import '../core/deps.dart';
import '../core/roles.dart';
import 'screens/consumer_verify_screen.dart';
import 'screens/dashboard_screen.dart';
import 'screens/dynamic_auth_screen.dart';
import 'screens/pin_screen.dart';
import 'screens/role_selection_screen.dart';
import 'screens/settings_screen.dart';

class AgroTraceApp extends StatelessWidget {
  const AgroTraceApp({super.key});

  Widget _home() {
    // Preview deep-links (handy on web):
    //   ?screen=auth|pin|board&role=farmer|agent|supplier|retailer|inspector
    //   ?screen=consumer | ?screen=settings
    final q = Uri.base.queryParameters;
    final screen = q['screen'];
    final role = NodeRole.values.firstWhere(
      (r) => r.name == q['role'],
      orElse: () => NodeRole.farmer,
    );
    switch (screen) {
      case 'auth':
        return DynamicAuthScreen(role: role);
      case 'pin':
        return PinScreen(role: role, identity: role.meta.demoIdentity);
      case 'board':
        return DashboardScreen(role: role);
      case 'consumer':
        return const ConsumerVerifyScreen();
      case 'settings':
        return const SettingsScreen();
    }

    // "6-digit PIN re-entry on every resume" — if an operator identity is
    // on record, boot straight into the PIN gate.
    final identity = deps.config.lastIdentity;
    if (identity != null && identity.isNotEmpty) {
      final role = NodeRole.values.firstWhere(
        (r) => r.name == deps.config.lastRoleName,
        orElse: () => _inferRole(identity),
      );
      return PinScreen(
        role: role,
        unlockMode: true,
        identity: identity,
      );
    }
    return const RoleSelectionScreen();
  }

  NodeRole _inferRole(String identity) {
    final id = identity.toUpperCase();
    if (id.startsWith('PF-COL')) return NodeRole.agent;
    if (id.startsWith('PF-SUP')) return NodeRole.supplier;
    if (id.startsWith('PF-AG') || id.startsWith('FSSAI')) {
      return NodeRole.inspector;
    }
    if (id.startsWith('PF-MFG') || id.startsWith('PF-DIST')) {
      return NodeRole.supplier;
    }
    if (id.contains('RET')) return NodeRole.retailer;
    return NodeRole.farmer;
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'AgroTrace Portal',
      debugShowCheckedModeBanner: false,
      theme: buildAppTheme(),
      home: _home(),
    );
  }
}
