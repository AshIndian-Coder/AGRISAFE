import 'package:flutter/material.dart';
import 'farmer_voice_register_screen.dart';

class RoleSelectionScreen extends StatelessWidget {
  const RoleSelectionScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('AgriChain | Select Identity', style: TextStyle(fontWeight: FontWeight.bold)),
        backgroundColor: Theme.of(context).colorScheme.primary,
        foregroundColor: Colors.white,
      ),
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(20.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text(
                "Welcome to AgriChain",
                style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 6),
              const Text(
                "Select your role in the supply chain to register or log in.",
                style: TextStyle(color: Colors.grey, fontSize: 14),
              ),
              const SizedBox(height: 24),
              Expanded(
                child: ListView(
                  children: [
                    _buildRoleCard(
                      context,
                      title: "Farmer / Producer",
                      subtitle: "Voice-assisted regional language onboarding",
                      icon: Icons.agriculture,
                      color: Colors.green,
                      onTap: () {
                        Navigator.push(
                          context,
                          MaterialPageRoute(builder: (_) => const FarmerVoiceRegisterScreen()),
                        );
                      },
                    ),
                    const SizedBox(height: 12),
                    _buildRoleCard(
                      context,
                      title: "Transporter / Driver",
                      subtitle: "Vehicle registration & dual-key handoff",
                      icon: Icons.local_shipping,
                      color: Colors.blue,
                      onTap: () {
                        ScaffoldMessenger.of(context).showSnackBar(
                          const SnackBar(content: Text("Driver Registration Selected")),
                        );
                      },
                    ),
                    const SizedBox(height: 12),
                    _buildRoleCard(
                      context,
                      title: "Supplier / Wholesaler",
                      subtitle: "Consignment acceptance & batch splitting",
                      icon: Icons.store,
                      color: Colors.amber.shade800,
                      onTap: () {
                        ScaffoldMessenger.of(context).showSnackBar(
                          const SnackBar(content: Text("Supplier Registration Selected")),
                        );
                      },
                    ),
                    const SizedBox(height: 12),
                    _buildRoleCard(
                      context,
                      title: "Retailer / Shop Owner",
                      subtitle: "Child batch QR scanner & shelf sync",
                      icon: Icons.shopping_basket,
                      color: Colors.purple,
                      onTap: () {
                        ScaffoldMessenger.of(context).showSnackBar(
                          const SnackBar(content: Text("Retailer Registration Selected")),
                        );
                      },
                    ),
                    const SizedBox(height: 12),
                    _buildRoleCard(
                      context,
                      title: "Government / FDA Officer",
                      subtitle: "Quality testing & sister-batch quarantine",
                      icon: Icons.verified_user,
                      color: Colors.red,
                      onTap: () {
                        ScaffoldMessenger.of(context).showSnackBar(
                          const SnackBar(content: Text("FDA Officer Registration Selected")),
                        );
                      },
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

  Widget _buildRoleCard(
    BuildContext context, {
    required String title,
    required String subtitle,
    required IconData icon,
    required Color color,
    required VoidCallback onTap,
  }) {
    return Card(
      elevation: 3,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: ListTile(
        contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
        leading: CircleAvatar(
          radius: 26,
          backgroundColor: color.withOpacity(0.15),
          child: Icon(icon, color: color, size: 28),
        ),
        title: Text(title, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
        subtitle: Text(subtitle, style: const TextStyle(fontSize: 12)),
        trailing: const Icon(Icons.arrow_forward_ios, size: 16, color: Colors.grey),
        onTap: onTap,
      ),
    );
  }
}