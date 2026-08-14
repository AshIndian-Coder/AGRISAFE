import 'package:flutter/material.dart';

class ConsumerScreen extends StatelessWidget {
  const ConsumerScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            "Public Consumer Scan",
            style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
          ),
          const Text(
            "Clean farm-to-fork provenance without complex enterprise batch clutter.",
            style: TextStyle(color: Colors.grey, fontSize: 13),
          ),
          const SizedBox(height: 16),

          // Consumer Product Card
          Card(
            elevation: 3,
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      const Text("Organic Alphonso Mangoes", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                      Chip(
                        avatar: const Icon(Icons.verified, color: Colors.white, size: 16),
                        label: const Text("100% Genuine", style: TextStyle(color: Colors.white, fontSize: 11)),
                        backgroundColor: Colors.green,
                        visualDensity: VisualDensity.compact,
                      )
                    ],
                  ),
                  const Divider(),
                  const Text("Package Code: CHILD-2026-A1", style: TextStyle(fontSize: 12, color: Colors.grey)),
                  const SizedBox(height: 16),

                  // Provenance Path Timeline (Clean Public View)
                  const Text("Farm-to-Fork Journey", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                  const SizedBox(height: 12),
                  const ListTile(
                    contentPadding: EdgeInsets.zero,
                    leading: CircleAvatar(backgroundColor: Colors.green, child: Icon(Icons.agriculture, color: Colors.white)),
                    title: Text("Harvested at Nashik Farm Cluster", style: TextStyle(fontSize: 13, fontWeight: FontWeight.bold)),
                    subtitle: Text("Farmer: Ramesh Kumar | Pure Organic Soil Certified"),
                  ),
                  const Divider(indent: 20),
                  const ListTile(
                    contentPadding: EdgeInsets.zero,
                    leading: CircleAvatar(backgroundColor: Colors.blue, child: Icon(Icons.science, color: Colors.white)),
                    title: Text("Govt Quality Inspection Passed", style: TextStyle(fontSize: 13, fontWeight: FontWeight.bold)),
                    subtitle: Text("Lab Test Status: 0% Pesticides / Adulteration"),
                  ),
                  const Divider(indent: 20),
                  const ListTile(
                    contentPadding: EdgeInsets.zero,
                    leading: CircleAvatar(backgroundColor: Colors.amber, child: Icon(Icons.local_shipping, color: Colors.white)),
                    title: Text("Cold-Chain Transit Verified", style: TextStyle(fontSize: 13, fontWeight: FontWeight.bold)),
                    subtitle: Text("Transporter: Express Cold Logistics (4.2°C maintained)"),
                  ),
                  const Divider(indent: 20),
                  const ListTile(
                    contentPadding: EdgeInsets.zero,
                    leading: CircleAvatar(backgroundColor: Colors.purple, child: Icon(Icons.store, color: Colors.white)),
                    title: Text("Arrived at Store A", style: TextStyle(fontSize: 13, fontWeight: FontWeight.bold)),
                    subtitle: Text("Available on Shelf for Consumer Purchase"),
                  ),
                ],
              ),
            ),
          )
        ],
      ),
    );
  }
}