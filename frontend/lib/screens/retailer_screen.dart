import 'package:flutter/material.dart';

class RetailerScreen extends StatefulWidget {
  const RetailerScreen({super.key});

  @override
  State<RetailerScreen> createState() => _RetailerScreenState();
}

class _RetailerScreenState extends State<RetailerScreen> {
  bool _childBatchAccepted = false;
  final String _scannedChildId = "CHILD-2026-A1";

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            "Retailer / Shop Portal",
            style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
          ),
          const Text(
            "Accept child batches from driver & make them available on store shelves.",
            style: TextStyle(color: Colors.grey, fontSize: 13),
          ),
          const SizedBox(height: 16),

          // Scan Child Batch QR
          Card(
            elevation: 2,
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text("Incoming Child Consignment", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
                  const Divider(),
                  ListTile(
                    contentPadding: EdgeInsets.zero,
                    leading: const CircleAvatar(
                      backgroundColor: Colors.purple,
                      child: Icon(Icons.shopping_basket, color: Colors.white),
                    ),
                    title: Text(_scannedChildId, style: const TextStyle(fontWeight: FontWeight.bold)),
                    subtitle: const Text("Quantity: 100 kg | Parent: BATCH-2026-X99"),
                  ),
                  const SizedBox(height: 12),
                  SizedBox(
                    width: double.infinity,
                    child: ElevatedButton.icon(
                      style: ElevatedButton.styleFrom(
                        backgroundColor: _childBatchAccepted ? Colors.grey : Colors.purple,
                        foregroundColor: Colors.white,
                      ),
                      icon: Icon(_childBatchAccepted ? Icons.check_circle : Icons.qr_code_scanner),
                      label: Text(_childBatchAccepted ? "Accepted onto Shelf" : "Scan & Confirm Store Receiving"),
                      onPressed: () {
                        setState(() {
                          _childBatchAccepted = true;
                        });
                        ScaffoldMessenger.of(context).showSnackBar(
                          const SnackBar(content: Text("Child Batch Accepted! Product Ready for Public Consumer Scan.")),
                        );
                      },
                    ),
                  )
                ],
              ),
            ),
          ),

          if (_childBatchAccepted) ...[
            const SizedBox(height: 16),
            Card(
              color: Colors.purple.shade50,
              elevation: 2,
              child: const Padding(
                padding: EdgeInsets.all(16.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: [
                        Icon(Icons.storefront, color: Colors.purple),
                        SizedBox(width: 8),
                        Text("Active Store Shelf Status", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
                      ],
                    ),
                    Divider(),
                    Text("• Child Batch ID: CHILD-2026-A1", style: TextStyle(fontSize: 13)),
                    Text("• Stock Remaining: 100 kg", style: TextStyle(fontSize: 13)),
                    Text("• Customer Verification QR: Print Enabled", style: TextStyle(fontSize: 13, color: Colors.green)),
                  ],
                ),
              ),
            )
          ]
        ],
      ),
    );
  }
}