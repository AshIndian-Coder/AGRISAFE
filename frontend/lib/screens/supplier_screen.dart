import 'package:flutter/material.dart';

class SupplierScreen extends StatefulWidget {
  const SupplierScreen({super.key});

  @override
  State<SupplierScreen> createState() => _SupplierScreenState();
}

class _SupplierScreenState extends State<SupplierScreen> {
  bool _consignmentScanned = false;
  bool _paymentReleased = false;

  final String _parentBatchId = "BATCH-2026-X99";
  final double _parentWeight = 400.0;

  int _childBatchCount = 2;
  final List<TextEditingController> _weightControllers = [
    TextEditingController(text: "200"),
    TextEditingController(text: "200"),
  ];
  final List<TextEditingController> _destControllers = [
    TextEditingController(text: "Store A (Mumbai)"),
    TextEditingController(text: "Store B (Pune)"),
  ];

  bool _isSplitGenerated = false;

  void _updateChildCount(int newCount) {
    if (newCount < 1 || newCount > 10) return;
    setState(() {
      _childBatchCount = newCount;
      while (_weightControllers.length < newCount) {
        _weightControllers.add(TextEditingController(text: "50"));
        _destControllers.add(TextEditingController(text: "Retail Outlet"));
      }
      while (_weightControllers.length > newCount) {
        _weightControllers.removeLast();
        _destControllers.removeLast();
      }
    });
  }

  void _generateSplits() {
    double totalWeight = 0;
    for (var controller in _weightControllers) {
      totalWeight += double.tryParse(controller.text) ?? 0;
    }

    if (totalWeight > _parentWeight) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Error: Total child weight ($totalWeight kg) exceeds parent batch ($_parentWeight kg)!"), backgroundColor: Colors.red),
      );
      return;
    }

    setState(() => _isSplitGenerated = true);
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text("Child Batches Cryptographically Minted & Linked to Parent!"), backgroundColor: Colors.green),
    );
  }

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text("Supplier / Wholesaler Portal", style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
          const Text("Custom batch splitting with strict mass-balance validation.", style: TextStyle(color: Colors.grey, fontSize: 13)),
          const SizedBox(height: 16),

          Card(
            elevation: 2,
            child: ListTile(
              leading: Icon(_consignmentScanned ? Icons.check_circle : Icons.qr_code_scanner, color: _consignmentScanned ? Colors.green : Colors.blue, size: 32),
              title: Text(_consignmentScanned ? "Batch Verified: $_parentBatchId" : "Scan Incoming Consignment"),
              subtitle: Text(_consignmentScanned ? "Available Weight: $_parentWeight kg" : "Scan QR from Driver"),
              trailing: ElevatedButton(
                onPressed: () => setState(() => _consignmentScanned = true),
                child: Text(_consignmentScanned ? "Verified" : "Scan QR"),
              ),
            ),
          ),
          const SizedBox(height: 16),

          if (_consignmentScanned)
            Card(
              elevation: 2,
              color: Colors.green.shade50,
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text("Automated Smart Contract Payment", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
                    const SizedBox(height: 4),
                    const Text("Payout Amount: ₹40,000 (400kg @ ₹100/kg)", style: TextStyle(fontSize: 13)),
                    const SizedBox(height: 12),
                    SizedBox(
                      width: double.infinity,
                      child: ElevatedButton.icon(
                        style: ElevatedButton.styleFrom(backgroundColor: _paymentReleased ? Colors.grey : Colors.green.shade700, foregroundColor: Colors.white),
                        icon: Icon(_paymentReleased ? Icons.check : Icons.account_balance_wallet),
                        label: Text(_paymentReleased ? "Payment Executed (Tx #0x7f3...)" : "Release Payment to Farmer Wallet"),
                        onPressed: _paymentReleased ? null : () => setState(() => _paymentReleased = true),
                      ),
                    )
                  ],
                ),
              ),
            ),
          const SizedBox(height: 16),

          if (_paymentReleased)
            Card(
              elevation: 3,
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text("Custom Mass-Balance Batch Splitting", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
                    const Text("Define custom child batch count and sizes:", style: TextStyle(fontSize: 12, color: Colors.grey)),
                    const SizedBox(height: 12),

                    Row(
                      children: [
                        const Text("Number of Child Batches: ", style: TextStyle(fontWeight: FontWeight.bold)),
                        IconButton(onPressed: () => _updateChildCount(_childBatchCount - 1), icon: const Icon(Icons.remove_circle_outline)),
                        Text("$_childBatchCount", style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                        IconButton(onPressed: () => _updateChildCount(_childBatchCount + 1), icon: const Icon(Icons.add_circle_outline)),
                      ],
                    ),
                    const Divider(),

                    ListView.builder(
                      shrinkWrap: true,
                      physics: const NeverScrollableScrollPhysics(),
                      itemCount: _childBatchCount,
                      itemBuilder: (context, index) {
                        return Padding(
                          padding: const EdgeInsets.symmetric(vertical: 6.0),
                          child: Row(
                            children: [
                              Text("Child #${index + 1}: ", style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 12)),
                              Expanded(
                                flex: 2,
                                child: TextField(
                                  controller: _weightControllers[index],
                                  keyboardType: TextInputType.number,
                                  decoration: const InputDecoration(labelText: "Weight (kg)", border: OutlineInputBorder(), contentPadding: EdgeInsets.symmetric(horizontal: 8, vertical: 8)),
                                ),
                              ),
                              const SizedBox(width: 8),
                              Expanded(
                                flex: 3,
                                child: TextField(
                                  controller: _destControllers[index],
                                  decoration: const InputDecoration(labelText: "Destination", border: OutlineInputBorder(), contentPadding: EdgeInsets.symmetric(horizontal: 8, vertical: 8)),
                                ),
                              ),
                            ],
                          ),
                        );
                      },
                    ),
                    const SizedBox(height: 16),

                    SizedBox(
                      width: double.infinity,
                      child: ElevatedButton.icon(
                        style: ElevatedButton.styleFrom(backgroundColor: Theme.of(context).colorScheme.primary, foregroundColor: Colors.white),
                        icon: const Icon(Icons.qr_code_2),
                        label: const Text("Mint & Print Child Batch QRs"),
                        onPressed: _generateSplits,
                      ),
                    ),

                    if (_isSplitGenerated) ...[
                      const SizedBox(height: 12),
                      const Text("✔ Child Batches Successfully Minted & Linked to Parent BATCH-2026-X99", style: TextStyle(color: Colors.green, fontWeight: FontWeight.bold, fontSize: 12)),
                    ]
                  ],
                ),
              ),
            )
        ],
      ),
    );
  }
}