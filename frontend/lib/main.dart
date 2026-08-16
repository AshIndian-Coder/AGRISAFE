import 'package:flutter/material.dart';
import 'screens/farmer_voice_register_screen.dart';
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
      title: 'AGRISAFE | Food Safety & Traceability',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        useMaterial3: true,
        fontFamily: 'Roboto',
        colorScheme: ColorScheme.fromSeed(
          seedColor: const Color(0xFF1B5E20),
          primary: const Color(0xFF1B5E20),
          secondary: const Color(0xFF2E7D32),
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
  int _selectedIndex = 0;
  bool _isEnglish = true;

  // Soft Light Green background color for the workspace
  static const Color lightGreenDashboardBg = Color(0xFFE8F5E9);

  final List<Widget> _screens = const [
    DashboardOverviewTab(),
    FarmerBatchInitiationView(),
    TransporterHandoffView(),
    SupplierScreen(),
    RetailerScreen(),
    FDAInspectorWebView(),
    ConsumerScreen(),
  ];

  @override
  Widget build(BuildContext context) {
    final bool isDesktop = MediaQuery.of(context).size.width > 900;
    const Color govGreenDark = Color(0xFF1B5E20);
    const Color govGreenLight = Color(0xFF2E7D32);

    return Scaffold(
      backgroundColor: Colors.white,
      // 1. Clean White Top Header Bar
      appBar: PreferredSize(
        preferredSize: const Size.fromHeight(90),
        child: Container(
          color: Colors.white,
          padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 8),
          child: SafeArea(
            child: Row(
              children: [
                Image.asset(
                  'assets/national emblem of India.png',
                  height: 62,
                  fit: BoxFit.contain,
                  errorBuilder: (context, error, stackTrace) => const Icon(
                    Icons.account_balance,
                    color: govGreenDark,
                    size: 48,
                  ),
                ),
                const SizedBox(width: 14),
                Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      _isEnglish
                          ? "कृषि एवं किसान कल्याण विभाग"
                          : "DEPARTMENT OF AGRICULTURE & FARMERS WELFARE",
                      style: const TextStyle(
                        color: govGreenDark,
                        fontSize: 16,
                        fontWeight: FontWeight.bold,
                        letterSpacing: 0.2,
                      ),
                    ),
                    const SizedBox(height: 2),
                    Text(
                      _isEnglish
                          ? "DEPARTMENT OF AGRICULTURE & FARMERS WELFARE"
                          : "कृषि एवं किसान कल्याण विभाग",
                      style: const TextStyle(
                        color: Colors.black87,
                        fontSize: 12,
                        fontWeight: FontWeight.w700,
                        letterSpacing: 0.5,
                      ),
                    ),
                    const SizedBox(height: 3),
                    Row(
                      children: [
                        Text(
                          _isEnglish ? "भारत सरकार  |" : "GOVERNMENT OF INDIA  |",
                          style: const TextStyle(
                            color: Colors.black54,
                            fontSize: 10,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                        const SizedBox(width: 6),
                        Text(
                          _isEnglish
                              ? "कृषि एवं किसान कल्याण मंत्रालय"
                              : "MINISTRY OF AGRICULTURE & FARMERS WELFARE",
                          style: const TextStyle(
                            color: govGreenLight,
                            fontSize: 10,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
                const Spacer(),
                if (isDesktop)
                  Container(
                    width: 220,
                    height: 38,
                    decoration: BoxDecoration(
                      color: const Color(0xFFF0F4F1),
                      borderRadius: BorderRadius.circular(6),
                      border: Border.all(color: const Color(0xFFC8E6C9)),
                    ),
                    child: const TextField(
                      style: TextStyle(color: Colors.black87, fontSize: 13),
                      decoration: InputDecoration(
                        hintText: "Search portal... Ctrl K",
                        hintStyle: TextStyle(color: Colors.black45, fontSize: 12),
                        prefixIcon: Icon(Icons.search, color: govGreenDark, size: 18),
                        border: InputBorder.none,
                        contentPadding: EdgeInsets.symmetric(vertical: 8),
                      ),
                    ),
                  ),
                const SizedBox(width: 14),
                InkWell(
                  onTap: () => setState(() => _isEnglish = !_isEnglish),
                  child: Container(
                    padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
                    decoration: BoxDecoration(
                      color: const Color(0xFFE8F5E9),
                      borderRadius: BorderRadius.circular(6),
                      border: Border.all(color: const Color(0xFFA5D6A7)),
                    ),
                    child: Row(
                      children: [
                        const Icon(Icons.language, color: govGreenDark, size: 16),
                        const SizedBox(width: 6),
                        Text(
                          _isEnglish ? "English" : "हिंदी",
                          style: const TextStyle(color: govGreenDark, fontSize: 12, fontWeight: FontWeight.bold),
                        ),
                        const Icon(Icons.arrow_drop_down, color: govGreenDark),
                      ],
                    ),
                  ),
                ),
                const SizedBox(width: 16),
                Row(
                  children: [
                    Image.asset(
                      'assets/150_logo.jpeg',
                      height: 54,
                      fit: BoxFit.contain,
                      errorBuilder: (c, e, s) => const SizedBox(),
                    ),
                    const SizedBox(width: 12),
                    Image.asset(
                      'assets/G20_logo.jpeg',
                      height: 54,
                      fit: BoxFit.contain,
                      errorBuilder: (c, e, s) => const SizedBox(),
                    ),
                    const SizedBox(width: 12),
                    Image.asset(
                      'assets/75_logo.jpeg',
                      height: 54,
                      fit: BoxFit.contain,
                      errorBuilder: (c, e, s) => const SizedBox(),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),
      ),
      body: Column(
        children: [
          Row(
            children: [
              Expanded(child: Container(height: 4, color: const Color(0xFFFF9933))),
              Expanded(child: Container(height: 4, color: Colors.white)),
              Expanded(child: Container(height: 4, color: const Color(0xFF138808))),
            ],
          ),
          Expanded(
            child: Row(
              children: [
                // 2. Clean White Left Navigation Menu Panel
                Container(
                  width: isDesktop ? 220 : 70,
                  color: Colors.white,
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      if (isDesktop)
                        const Padding(
                          padding: EdgeInsets.only(left: 16, top: 16, bottom: 8),
                          child: Text(
                            "• NAVIGATION MENU",
                            style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold, color: Colors.grey, letterSpacing: 0.8),
                          ),
                        ),
                      Expanded(
                        child: ListView(
                          padding: const EdgeInsets.symmetric(vertical: 8),
                          children: [
                            _buildNavItem(0, Icons.grid_view, "Dashboard", isDesktop),
                            _buildNavItem(1, Icons.agriculture, "Farmer Portal", isDesktop),
                            _buildNavItem(2, Icons.local_shipping, "Receiving Agent", isDesktop),
                            _buildNavItem(3, Icons.swap_horiz, "Middleman", isDesktop),
                            _buildNavItem(4, Icons.precision_manufacturing, "Manufacturing", isDesktop),
                            _buildNavItem(5, Icons.shopping_bag_outlined, "Retailer", isDesktop),
                            _buildNavItem(6, Icons.verified_user_outlined, "FSSAI Inspector", isDesktop),
                          ],
                        ),
                      ),
                      const Divider(height: 1),
                      Padding(
                        padding: const EdgeInsets.all(12.0),
                        child: Row(
                          children: [
                            const Icon(Icons.shield_outlined, size: 20, color: govGreenDark),
                            if (isDesktop) ...[
                              const SizedBox(width: 8),
                              const Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Text("Secure Portal", style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: Colors.black87)),
                                  Text("NIC · MeitY Certified", style: TextStyle(fontSize: 9, color: Colors.grey)),
                                ],
                              )
                            ]
                          ],
                        ),
                      )
                    ],
                  ),
                ),
                const VerticalDivider(width: 1, thickness: 1, color: Color(0xFFE0E0E0)),

                // 3. Light Green Middle Content Area
                Expanded(
                  child: Container(
                    color: lightGreenDashboardBg,
                    child: IndexedStack(
                      index: _selectedIndex,
                      children: _screens,
                    ),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildNavItem(int index, IconData icon, String label, bool isDesktop) {
    final bool isSelected = _selectedIndex == index;

    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
      decoration: BoxDecoration(
        color: isSelected ? const Color(0xFF1B5E20) : Colors.transparent,
        borderRadius: BorderRadius.circular(8),
      ),
      child: ListTile(
        dense: true,
        contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 0),
        leading: Icon(
          icon,
          color: isSelected ? Colors.white : Colors.black87,
          size: 20,
        ),
        title: isDesktop
            ? Text(
                label,
                style: TextStyle(
                  color: isSelected ? Colors.white : Colors.black87,
                  fontWeight: isSelected ? FontWeight.bold : FontWeight.w500,
                  fontSize: 13,
                ),
              )
            : null,
        trailing: isSelected && isDesktop
            ? const Icon(Icons.chevron_right, color: Colors.white, size: 16)
            : null,
        onTap: () => setState(() => _selectedIndex = index),
      ),
    );
  }
}

// ====================================================================
// DASHBOARD OVERVIEW TAB
// ====================================================================
class DashboardOverviewTab extends StatelessWidget {
  const DashboardOverviewTab({super.key});

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(24.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Icon(Icons.grid_view, color: Color(0xFF1B5E20), size: 22),
                      SizedBox(width: 8),
                      Text("Dashboard", style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold, color: Color(0xFF1B5E20))),
                    ],
                  ),
                  SizedBox(height: 4),
                  Text("Overview of AGRISAFE food safety traceability across India", style: TextStyle(color: Colors.black54, fontSize: 13)),
                ],
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(20),
                  border: Border.all(color: const Color(0xFFA5D6A7)),
                ),
                child: const Row(
                  children: [
                    CircleAvatar(radius: 4, backgroundColor: Color(0xFF2E7D32)),
                    SizedBox(width: 6),
                    Text("Live · Updated just now", style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: Color(0xFF1B5E20))),
                  ],
                ),
              ),
            ],
          ),
          const SizedBox(height: 20),
          LayoutBuilder(
            builder: (context, constraints) {
              final int count = constraints.maxWidth > 800 ? 4 : 2;
              return GridView.count(
                crossAxisCount: count,
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                crossAxisSpacing: 16,
                mainAxisSpacing: 16,
                childAspectRatio: 1.8,
                children: const [
                  _MetricCard(title: "REGISTERED FARMERS", value: "14,82,391", change: "+2.4% this month", changeColor: Color(0xFF2E7D32)),
                  _MetricCard(title: "SHIPMENTS TRACKED", value: "3,67,120", change: "+8.1% this month", changeColor: Color(0xFF2E7D32)),
                  _MetricCard(title: "FSSAI INSPECTIONS", value: "12,841", change: "+0.7% this month", changeColor: Colors.orange),
                  _MetricCard(title: "PUBLIC VERIFICATIONS", value: "98,460", change: "+15.3% this month", changeColor: Color(0xFF2E7D32)),
                ],
              );
            },
          ),
          const SizedBox(height: 24),
          Card(
            elevation: 0,
            color: Colors.white,
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(8),
              side: BorderSide(color: Colors.grey.shade300),
            ),
            child: Padding(
              padding: const EdgeInsets.all(20.0),
              child: Column(
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      const Text("Recent Activity", style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: Color(0xFF1B5E20))),
                      TextButton(
                        onPressed: () {},
                        child: const Text("View all", style: TextStyle(color: Color(0xFF2E7D32), fontWeight: FontWeight.bold, fontSize: 12)),
                      ),
                    ],
                  ),
                  const Divider(),
                  _buildActivityRow("AGS-20240816-0041", "Batch certified by FSSAI", "Nashik, MH", "14 min ago", "Approved", const Color(0xFF2E7D32)),
                  _buildActivityRow("AGS-20240816-0040", "Produce received by agent", "Ludhiana, PB", "31 min ago", "Received", Colors.blue),
                  _buildActivityRow("AGS-20240816-0039", "Retailer stock updated", "Chennai, TN", "1 hr ago", "Updated", Colors.orange),
                  _buildActivityRow("AGS-20240816-0038", "Farmer registration", "Vidisha, MP", "2 hr ago", "Pending", Colors.amber.shade800),
                  _buildActivityRow("AGS-20240816-0037", "Public QR verification", "Jaipur, RJ", "3 hr ago", "Verified", const Color(0xFF2E7D32)),
                ],
              ),
            ),
          )
        ],
      ),
    );
  }

  static Widget _buildActivityRow(String id, String action, String location, String time, String status, Color statusColor) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 10.0),
      child: Row(
        children: [
          SizedBox(width: 140, child: Text(id, style: const TextStyle(fontSize: 12, color: Colors.black54))),
          Expanded(child: Text(action, style: const TextStyle(fontSize: 13, fontWeight: FontWeight.w500, color: Colors.black87))),
          SizedBox(width: 120, child: Text(location, style: const TextStyle(fontSize: 12, color: Colors.black54))),
          SizedBox(width: 90, child: Text(time, style: const TextStyle(fontSize: 12, color: Colors.black54))),
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
            decoration: BoxDecoration(
              color: statusColor.withOpacity(0.12),
              borderRadius: BorderRadius.circular(12),
            ),
            child: Text(status, style: TextStyle(color: statusColor, fontSize: 11, fontWeight: FontWeight.bold)),
          ),
        ],
      ),
    );
  }
}

class _MetricCard extends StatelessWidget {
  final String title;
  final String value;
  final String change;
  final Color changeColor;

  const _MetricCard({
    super.key,
    required this.title,
    required this.value,
    required this.change,
    required this.changeColor,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 0,
      color: Colors.white,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(8),
        side: BorderSide(color: Colors.grey.shade300),
      ),
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(
              title,
              style: const TextStyle(
                fontSize: 10,
                fontWeight: FontWeight.bold,
                color: Colors.black54,
                letterSpacing: 0.5,
              ),
            ),
            Text(
              value,
              style: const TextStyle(
                fontSize: 22,
                fontWeight: FontWeight.bold,
                color: Color(0xFF1B5E20),
              ),
            ),
            Text(
              change,
              style: TextStyle(
                fontSize: 11,
                fontWeight: FontWeight.bold,
                color: changeColor,
              ),
            ),
          ],
        ),
      ),
    );
  }
}