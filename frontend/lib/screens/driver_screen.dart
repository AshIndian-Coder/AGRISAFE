import 'package:flutter/material.dart';

class TransporterHandoffView extends StatelessWidget {
  const TransporterHandoffView({super.key});

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            "Module 2.3 | Custody Handover Protocol",
            style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
          ),
          const Text(
            "Dual-Key co-signing between Sender & Carrier to verify quantities.",
            style: TextStyle(color: Colors.grey, fontSize: 13),
          ),
          const SizedBox(height: 16),
          Card(
            child: ListTile(
              leading:
                  const Icon(Icons.qr_code_scanner, size: 36, color: Colors.blue),
              title: const Text("Scan Batch QR",
                  style: TextStyle(fontSize: 15, fontWeight: FontWeight.bold)),
              subtitle: const Text("Reads physical QR on container",
                  style: TextStyle(fontSize: 12)),
              trailing: ElevatedButton(
                onPressed: () {
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(
                      content:
                          Text("Scan Complete: Batch #BATCH-2026-X99 Verified"),
                    ),
                  );
                },
                child: const Text("Scan"),
              ),
            ),
          ),
          const SizedBox(height: 12),
          Card(
            elevation: 2,
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text(
                    "Dual-Key Handoff Summary",
                    style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15),
                  ),
                  const Divider(),
                  const Text("Scanned Batch: BATCH-2026-X99",
                      style: TextStyle(fontSize: 13)),
                  const Text("Incoming Quantity: 400.0 kg",
                      style: TextStyle(fontSize: 13)),
                  const Text("GPS: 19.0760° N, 72.8777° E (Auto-Captured)",
                      style: TextStyle(fontSize: 13)),
                  const SizedBox(height: 16),
                  SizedBox(
                    width: double.infinity,
                    child: ElevatedButton.icon(
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Colors.blue,
                        foregroundColor: Colors.white,
                      ),
                      icon: const Icon(Icons.edit_note),
                      label: const Text("Co-Sign & Accept Ownership"),
                      onPressed: () {
                        ScaffoldMessenger.of(context).showSnackBar(
                          const SnackBar(
                            content:
                                Text("Handoff Signed & Logged to Blockchain!"),
                          ),
                        );
                      },
                    ),
                  )
                ],
              ),
            ),
          )
        ],
      ),
    );
  }
}