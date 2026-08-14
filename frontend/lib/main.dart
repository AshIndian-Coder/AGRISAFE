import 'package:flutter/material.dart';
import 'screens/role_selection_screen.dart';
import 'screens/farmer_screen.dart';
import 'screens/driver_screen.dart';
import 'screens/supplier_screen.dart';
import 'screens/retailer_screen.dart';
import 'screens/inspector_screen.dart';
import 'screens/consumer_screen.dart';

void main() {
  runApp(const AgriChainApp());
}

class AgriChainApp extends StatelessWidget {
  const AgriChainApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'AgriChain SIH 2026',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        useMaterial3: true,
        colorScheme: ColorScheme.fromSeed(
          seedColor: const Color(0xFF2E7D32),
          primary: const Color(0xFF2E7D32),
          secondary: const Color(0xFF1565C0),
        ),
      ),
      home: const MainDashboardShell(),
    );
  }
}

class MainDashboardShell extends StatefulWidget {
  const MainDashboardShell({super.key});

  @override
  State<MainDashboardShell> createState() => _MainDashboardShellState();
}

class _MainDashboardShellState extends State<MainDashboardShell> {
  int _selectedRoleIndex = 0;

  final List<String> _roles = [
    'Farmer / Producer',
    'Transporter / Driver',
    'Supplier / Wholesaler',
    'Retailer / Store',
    'Government FDA Inspector',
    'Public Consumer'
  ];

  @override
  Widget build(BuildContext context) {
    final bool isWebDesktop = MediaQuery.of(context).size.width > 800;

    return Scaffold(
      appBar: AppBar(
        title: const Text(
          'AgriChain | Enterprise v2.0',
          style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
        ),
        backgroundColor: Theme.of(context).colorScheme.primary,
        foregroundColor: Colors.white,
        actions: [
          IconButton(
            icon: const Icon(Icons.person_add),
            tooltip: "Role Onboarding",
            onPressed: () {
              Navigator.push(
                context,
                MaterialPageRoute(builder: (_) => const RoleSelectionScreen()),
              );
            },
          ),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 8.0),
            child: DropdownButton<int>(
              value: _selectedRoleIndex,
              dropdownColor: Theme.of(context).colorScheme.primary,
              style: const TextStyle(
                  color: Colors.white,
                  fontWeight: FontWeight.bold,
                  fontSize: 13),
              underline: const SizedBox(),
              icon: const Icon(Icons.swap_horiz, color: Colors.white),
              items: List.generate(_roles.length, (index) {
                return DropdownMenuItem(
                  value: index,
                  child: Text(_roles[index]),
                );
              }),
              onChanged: (val) => setState(() => _selectedRoleIndex = val!),
            ),
          )
        ],
      ),
      body: Row(
        children: [
          if (isWebDesktop)
            NavigationRail(
              selectedIndex: _selectedRoleIndex,
              onDestinationSelected: (int index) {
                setState(() => _selectedRoleIndex = index);
              },
              labelType: NavigationRailLabelType.all,
              destinations: const [
                NavigationRailDestination(
                  icon: Icon(Icons.agriculture),
                  selectedIcon: Icon(Icons.agriculture, color: Colors.green),
                  label: Text('Farmer'),
                ),
                NavigationRailDestination(
                  icon: Icon(Icons.local_shipping),
                  selectedIcon: Icon(Icons.local_shipping, color: Colors.blue),
                  label: Text('Driver'),
                ),
                NavigationRailDestination(
                  icon: Icon(Icons.store),
                  selectedIcon: Icon(Icons.store, color: Colors.amber),
                  label: Text('Supplier'),
                ),
                NavigationRailDestination(
                  icon: Icon(Icons.shopping_basket),
                  selectedIcon: Icon(Icons.shopping_basket, color: Colors.purple),
                  label: Text('Retailer'),
                ),
                NavigationRailDestination(
                  icon: Icon(Icons.verified_user),
                  selectedIcon: Icon(Icons.verified_user, color: Colors.red),
                  label: Text('FDA Officer'),
                ),
                NavigationRailDestination(
                  icon: Icon(Icons.qr_code_scanner),
                  selectedIcon: Icon(Icons.qr_code_scanner, color: Colors.teal),
                  label: Text('Consumer'),
                ),
              ],
            ),
          if (isWebDesktop) const VerticalDivider(thickness: 1, width: 1),
          Expanded(
            child: IndexedStack(
              index: _selectedRoleIndex,
              children: const [
                FarmerBatchInitiationView(),
                TransporterHandoffView(),
                SupplierScreen(),
                RetailerScreen(),
                FDAInspectorWebView(),
                ConsumerScreen(),
              ],
            ),
          ),
        ],
      ),
    );
  }
}