import 'package:flutter/material.dart';
import 'farmer_voice_register_screen.dart';

class RoleSelectionScreen extends StatefulWidget {
  const RoleSelectionScreen({super.key});

  @override
  State<RoleSelectionScreen> createState() => _RoleSelectionScreenState();
}

class _RoleSelectionScreenState extends State<RoleSelectionScreen> {
  // Language Switch State: true = English, false = Hindi
  bool _isEnglish = true;

  @override
  Widget build(BuildContext context) {
    // Exact agriwelfare.gov.in color palette
    const Color govNavyDark = Color(0xFF0D1B2A);
    const Color govHeaderGreen = Color(0xFF0B4D1A);
    const Color govBackground = Color(0xFFF9F9F4);
    const Color govGoldAccent = Color(0xFFC89B3C);

    return Scaffold(
      backgroundColor: govBackground,
      body: SafeArea(
        child: SingleChildScrollView(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // 1. Top Government Utility Bar with Language Switcher
              Container(
                color: govNavyDark,
                padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      _isEnglish
                          ? "भारत सरकार | Government of India"
                          : "भारत सरकार | Government of India",
                      style: const TextStyle(
                          color: Colors.white,
                          fontSize: 11,
                          fontWeight: FontWeight.w500),
                    ),
                    // Language Switcher Button
                    TextButton.icon(
                      style: TextButton.styleFrom(
                        padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                        backgroundColor: govGoldAccent,
                        foregroundColor: Colors.black,
                        minimumSize: Size.zero,
                        tapTargetSize: MaterialTapTargetSize.shrinkWrap,
                      ),
                      icon: const Icon(Icons.language, size: 14, color: Colors.black),
                      label: Text(
                        _isEnglish ? "हिंदी" : "English",
                        style: const TextStyle(
                            fontSize: 12, fontWeight: FontWeight.bold),
                      ),
                      onPressed: () {
                        setState(() {
                          _isEnglish = !_isEnglish;
                        });
                      },
                    ),
                  ],
                ),
              ),

              // 2. Official Header Banner with Govt Image Elements
              Container(
                color: Colors.white,
                padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                child: Row(
                  children: [
                    // Element 1: National Emblem (Ashoka Stambha)
                    Container(
                      padding: const EdgeInsets.all(6),
                      decoration: BoxDecoration(
                        shape: BoxShape.circle,
                        color: govHeaderGreen.withOpacity(0.08),
                      ),
                      child: const Icon(
                        Icons.account_balance, // Represents State Emblem
                        size: 36,
                        color: govHeaderGreen,
                      ),
                    ),
                    const SizedBox(width: 12),

                    // Element 2: Ministry Title Block
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            _isEnglish
                                ? "कृषि एवं किसान कल्याण मंत्रालय"
                                : "Ministry of Agriculture & Farmers Welfare",
                            style: const TextStyle(
                                fontSize: 13,
                                fontWeight: FontWeight.bold,
                                color: govHeaderGreen),
                          ),
                          Text(
                            _isEnglish
                                ? "Ministry of Agriculture & Farmers Welfare"
                                : "कृषि एवं किसान कल्याण मंत्रालय",
                            style: const TextStyle(
                                fontSize: 12,
                                fontWeight: FontWeight.w600,
                                color: Colors.black87),
                          ),
                          Text(
                            _isEnglish
                                ? "National Blockchain Traceability Portal (AgriChain)"
                                : "राष्ट्रीय ब्लॉकचेन ट्रैसेबिलिटी पोर्टल (एग्रीचेन)",
                            style: const TextStyle(
                                fontSize: 10, color: Colors.grey),
                          ),
                        ],
                      ),
                    ),

                    // Element 3: Azadi Ka Amrit Mahotsav / MoA Govt Badge
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                      decoration: BoxDecoration(
                        border: Border.all(color: govGoldAccent, width: 1.5),
                        borderRadius: BorderRadius.circular(4),
                        color: Colors.amber.shade50,
                      ),
                      child: const Column(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          Icon(Icons.military_tech, size: 20, color: Color(0xFFC89B3C)),
                          Text(
                            "75 Azadi",
                            style: TextStyle(
                                fontSize: 9,
                                fontWeight: FontWeight.bold,
                                color: Color(0xFFC89B3C)),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),

              // 3. Indian National Flag Tri-Color Strip
              Row(
                children: [
                  Expanded(child: Container(height: 4, color: const Color(0xFFFF9933))), // Saffron
                  Expanded(child: Container(height: 4, color: Colors.white)),            // White
                  Expanded(child: Container(height: 4, color: const Color(0xFF138808))), // Green
                ],
              ),

              // 4. Main Content Body
              Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    // Portal Notice Bar
                    Container(
                      width: double.infinity,
                      padding: const EdgeInsets.all(12),
                      decoration: BoxDecoration(
                        color: const Color(0xFFEAF4EC),
                        border: Border.all(color: const Color(0xFFA2D2AB)),
                        borderRadius: BorderRadius.circular(4),
                      ),
                      child: Row(
                        children: [
                          const Icon(Icons.info_outline, color: govHeaderGreen, size: 20),
                          const SizedBox(width: 8),
                          Expanded(
                            child: Text(
                              _isEnglish
                                  ? "Select your designated stakeholder portal below to access blockchain ledger services."
                                  : "ब्लॉकचेन लेजर सेवाओं तक पहुंचने के लिए नीचे अपना निर्दिष्ट हितधारक पोर्टल चुनें।",
                              style: const TextStyle(
                                  fontSize: 12,
                                  color: govHeaderGreen,
                                  fontWeight: FontWeight.w500),
                            ),
                          ),
                        ],
                      ),
                    ),
                    const SizedBox(height: 20),

                    Text(
                      _isEnglish
                          ? "Unified Stakeholder Login / Identity Portal"
                          : "एकीकृत हितधारक लॉगिन / पहचान पोर्टल",
                      style: const TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.bold,
                          color: govNavyDark),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      _isEnglish
                          ? "Single Sign-On (SSO) & e-KYC Verification Portal"
                          : "सिंगल साइन-ऑन (SSO) और ई-केवाईसी सत्यापन पोर्टल",
                      style: const TextStyle(fontSize: 12, color: Colors.grey),
                    ),
                    const SizedBox(height: 16),

                    // Grid of Government Service Cards
                    GridView.count(
                      crossAxisCount: MediaQuery.of(context).size.width > 600 ? 3 : 1,
                      shrinkWrap: true,
                      physics: const NeverScrollableScrollPhysics(),
                      crossAxisSpacing: 12,
                      mainAxisSpacing: 12,
                      childAspectRatio: MediaQuery.of(context).size.width > 600 ? 1.6 : 2.5,
                      children: [
                        _buildGovTile(
                          context,
                          title: _isEnglish ? "Farmer Portal" : "किसान पोर्टल",
                          hindiTitle: _isEnglish ? "किसान पंजीकरण" : "Kisan Portal",
                          subtitle: _isEnglish
                              ? "Regional Voice STT Onboarding & Aadhaar e-KYC"
                              : "क्षेत्रीय आवाज एसटीटी ऑनबोर्डिंग और आधार ई-केवाईसी",
                          icon: Icons.agriculture,
                          badgeColor: govHeaderGreen,
                          onTap: () {
                            Navigator.push(
                              context,
                              MaterialPageRoute(
                                  builder: (_) => const FarmerVoiceRegisterScreen()),
                            );
                          },
                        ),
                        _buildGovTile(
                          context,
                          title: _isEnglish ? "Transporter Portal" : "परिवहन पोर्टल",
                          hindiTitle: _isEnglish ? "परिवहन अधिकारी" : "Transporter",
                          subtitle: _isEnglish
                              ? "Custody Handover & Live Cold-Chain GPS Log"
                              : "कस्टडी हैंडओवर और लाइव कोल्ड-चेन जीपीएस लॉग",
                          icon: Icons.local_shipping,
                          badgeColor: const Color(0xFF1565C0),
                          onTap: () {
                            ScaffoldMessenger.of(context).showSnackBar(
                              SnackBar(
                                  content: Text(_isEnglish
                                      ? "Transporter Registration Selected"
                                      : "परिवहन पंजीकरण चुना गया")),
                            );
                          },
                        ),
                        _buildGovTile(
                          context,
                          title: _isEnglish ? "Supplier / Mandi Portal" : "आपूर्तिकर्ता / मंडी पोर्टल",
                          hindiTitle: _isEnglish ? "विक्रेता / मंडी" : "Supplier / Mandi",
                          subtitle: _isEnglish
                              ? "Smart Contract Settlement & Batch Splitting"
                              : "स्मार्ट अनुबंध निपटान और बैच विभाजन",
                          icon: Icons.store,
                          badgeColor: const Color(0xFFE65100),
                          onTap: () {
                            ScaffoldMessenger.of(context).showSnackBar(
                              SnackBar(
                                  content: Text(_isEnglish
                                      ? "Supplier Registration Selected"
                                      : "आपूर्तिकर्ता पंजीकरण चुना गया")),
                            );
                          },
                        ),
                        _buildGovTile(
                          context,
                          title: _isEnglish ? "Retailer Portal" : "खुदरा विक्रेता पोर्टल",
                          hindiTitle: _isEnglish ? "खुदरा विक्रेता" : "Retailer",
                          subtitle: _isEnglish
                              ? "Child Batch QR Scan & Shelf Inventory Sync"
                              : "चाइल्ड बैच क्यूआर स्कैन और शेल्फ इन्वेंट्री सिंक",
                          icon: Icons.shopping_basket,
                          badgeColor: const Color(0xFF6A1B9A),
                          onTap: () {
                            ScaffoldMessenger.of(context).showSnackBar(
                              SnackBar(
                                  content: Text(_isEnglish
                                      ? "Retailer Registration Selected"
                                      : "खुदरा विक्रेता पंजीकरण चुना गया")),
                            );
                          },
                        ),
                        _buildGovTile(
                          context,
                          title: _isEnglish ? "FDA / Regulatory Officer" : "खाद्य सुरक्षा अधिकारी",
                          hindiTitle: _isEnglish ? "खाद्य सुरक्षा निरीक्षक" : "FDA Inspector",
                          subtitle: _isEnglish
                              ? "Purity Testing & Sister-Batch Quarantine Blast"
                              : "शुद्धता परीक्षण और सिस्टर-बैच संगरोध ब्लास्ट",
                          icon: Icons.verified_user,
                          badgeColor: const Color(0xFFC62828),
                          onTap: () {
                            ScaffoldMessenger.of(context).showSnackBar(
                              SnackBar(
                                  content: Text(_isEnglish
                                      ? "FDA Inspector Registration Selected"
                                      : "एफडीए निरीक्षक पंजीकरण चुना गया")),
                            );
                          },
                        ),
                      ],
                    ),

                    const SizedBox(height: 24),
                    // Government Footer Attribution (Helpline Removed)
                    Container(
                      width: double.infinity,
                      padding: const EdgeInsets.all(12),
                      decoration: BoxDecoration(
                        color: Colors.white,
                        border: Border.all(color: Colors.grey.shade300),
                        borderRadius: BorderRadius.circular(4),
                      ),
                      child: Text(
                        _isEnglish
                            ? "Website Content Owned & Managed by Department of Agriculture & Farmers Welfare, Government of India.\nDesigned & Developed by National Informatics Centre (NIC) / AgriChain Enterprise."
                            : "वेबसाइट सामग्री का स्वामित्व एवं प्रबंधन कृषि एवं किसान कल्याण विभाग, भारत सरकार द्वारा किया जाता है।\nराष्ट्रीय सूचना विज्ञान केंद्र (एनआईसी) / एग्रीचेन एंटरप्राइज द्वारा डिज़ाइन एवं विकसित।",
                        style: const TextStyle(fontSize: 10, color: Colors.grey),
                        textAlign: TextAlign.center,
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildGovTile(
    BuildContext context, {
    required String title,
    required String hindiTitle,
    required String subtitle,
    required IconData icon,
    required Color badgeColor,
    required VoidCallback onTap,
  }) {
    return Card(
      elevation: 2,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(4),
        side: BorderSide(color: Colors.grey.shade300, width: 1),
      ),
      child: InkWell(
        onTap: onTap,
        child: Padding(
          padding: const EdgeInsets.all(12.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                    decoration: BoxDecoration(
                      color: badgeColor.withOpacity(0.1),
                      borderRadius: BorderRadius.circular(2),
                    ),
                    child: Text(
                      hindiTitle,
                      style: TextStyle(
                          color: badgeColor,
                          fontSize: 11,
                          fontWeight: FontWeight.bold),
                    ),
                  ),
                  Icon(icon, color: badgeColor, size: 24),
                ],
              ),
              const SizedBox(height: 8),
              Text(
                title,
                style: const TextStyle(
                    fontWeight: FontWeight.bold,
                    fontSize: 14,
                    color: Color(0xFF0D1B2A)),
              ),
              Text(
                subtitle,
                style: const TextStyle(fontSize: 11, color: Colors.grey),
              ),
              const SizedBox(height: 4),
              Row(
                children: [
                  Text(
                    _isEnglish ? "Proceed to Access" : "पहुंच के लिए आगे बढ़ें",
                    style: TextStyle(
                        fontSize: 11,
                        color: badgeColor,
                        fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(width: 4),
                  Icon(Icons.arrow_forward, size: 12, color: badgeColor),
                ],
              )
            ],
          ),
        ),
      ),
    );
  }
}