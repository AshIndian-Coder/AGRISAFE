import 'package:flutter/material.dart';

class FDAInspectorWebView extends StatefulWidget {
  const FDAInspectorWebView({super.key});

  @override
  State<FDAInspectorWebView> createState() => _FDAInspectorWebViewState();
}

class _FDAInspectorWebViewState extends State<FDAInspectorWebView> {
  final TextEditingController _searchController = TextEditingController(text: "BATCH-2026-X99");
  bool _hasSearched = true;

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text("FDA Regulatory Portal", style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
                  Text("Full Parent-Child Tree & Real-Time Child Locations", style: TextStyle(color: Colors.grey, fontSize: 12)),
                ],
              ),
              Chip(
                avatar: const Icon(Icons.circle, color: Colors.green, size: 10),
                label: const Text("Polygon Amoy", style: TextStyle(fontSize: 11)),
              )
            ],
          ),
          const SizedBox(height: 16),

          Row(
            children: [
              Expanded(
                child: TextField(
                  controller: _searchController,
                  decoration: const InputDecoration(
                    hintText: "Enter Master or Child Batch ID",
                    prefixIcon: Icon(Icons.search),
                    border: OutlineInputBorder(),
                  ),
                ),
              ),
              const SizedBox(width: 8),
              ElevatedButton(
                style: ElevatedButton.styleFrom(backgroundColor: Theme.of(context).colorScheme.primary, foregroundColor: Colors.white, padding: const EdgeInsets.symmetric(vertical: 16, horizontal: 16)),
                onPressed: () => setState(() => _hasSearched = true),
                child: const Text("Trace Tree"),
              )
            ],
          ),
          const SizedBox(height: 16),

          if (_hasSearched) ...[
            Card(
              elevation: 3,
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Row(
                      children: [
                        Icon(Icons.account_tree, color: Colors.blue),
                        SizedBox(width: 8),
                        Text("Master Batch Lineage & Child Tree", style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                      ],
                    ),
                    const Divider(),
                    const Text("Master Batch ID: BATCH-2026-X99 (Total: 400 kg)", style: TextStyle(fontWeight: FontWeight.bold)),
                    const Text("Origin Farm: Ramesh Kumar (Nashik Cluster)"),
                    const Text("Lab Test Status: PASSED (Uploaded by Officer Mehta)", style: TextStyle(color: Colors.green, fontWeight: FontWeight.bold)),
                    const SizedBox(height: 16),

                    const Text("Derived Child Batches & Real-Time Locations:", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                    const SizedBox(height: 8),

                    _buildChildBatchTile(
                      childId: "CHILD-2026-A1",
                      weight: "100 kg",
                      location: "Store A (Mumbai)",
                      status: "Contaminated / Chemical Filler Flagged",
                      isAlert: true,
                      onQuarantine: () => _showQuarantineDialog(context, "CHILD-2026-A1"),
                    ),
                    const Divider(),
                    _buildChildBatchTile(
                      childId: "CHILD-2026-B2",
                      weight: "100 kg",
                      location: "In-Transit (Truck MH-15-2026)",
                      status: "Normal / Clear",
                      isAlert: false,
                      onQuarantine: () => _showQuarantineDialog(context, "CHILD-2026-B2"),
                    ),
                    const Divider(),
                    _buildChildBatchTile(
                      childId: "CHILD-2026-C3",
                      weight: "200 kg",
                      location: "Central Warehouse Y (Pune)",
                      status: "Normal / Clear",
                      isAlert: false,
                      onQuarantine: () => _showQuarantineDialog(context, "CHILD-2026-C3"),
                    ),
                  ],
                ),
              ),
            )
          ]
        ],
      ),
    );
  }

  Widget _buildChildBatchTile({
    required String childId,
    required String weight,
    required String location,
    required String status,
    required bool isAlert,
    required VoidCallback onQuarantine,
  }) {
    return Container(
      padding: const EdgeInsets.all(8),
      decoration: BoxDecoration(
        color: isAlert ? Colors.red.shade50 : Colors.transparent,
        borderRadius: BorderRadius.circular(8),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text("• $childId ($weight)", style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
              Chip(
                visualDensity: VisualDensity.compact,
                backgroundColor: isAlert ? Colors.red : Colors.green,
                label: Text(isAlert ? "FLAGGED" : "CLEAR", style: const TextStyle(color: Colors.white, fontSize: 10)),
              )
            ],
          ),
          Text("  Current Location: $location", style: const TextStyle(fontSize: 12)),
          Text("  Status: $status", style: TextStyle(fontSize: 12, color: isAlert ? Colors.red : Colors.grey)),
          const SizedBox(height: 6),
          SizedBox(
            width: double.infinity,
            child: OutlinedButton.icon(
              style: OutlinedButton.styleFrom(foregroundColor: Colors.red, side: const BorderSide(color: Colors.red)),
              icon: const Icon(Icons.block, size: 16),
              label: const Text("Quarantine This Child & Alert Sisters", style: TextStyle(fontSize: 11)),
              onPressed: onQuarantine,
            ),
          )
        ],
      ),
    );
  }

  void _showQuarantineDialog(BuildContext context, String batchId) {
    showDialog(
      context: context,
      builder: (_) => AlertDialog(
        title: Text("Quarantine $batchId"),
        content: Text("Executing this command will flag $batchId on the Polygon Blockchain and send an automated recall blast to sister child batches."),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context), child: const Text("Cancel")),
          ElevatedButton(
            style: ElevatedButton.styleFrom(backgroundColor: Colors.red, foregroundColor: Colors.white),
            onPressed: () {
              Navigator.pop(context);
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(content: Text("SUCCESS: $batchId flagged on Blockchain & Sister-Blast Sent!")),
              );
            },
            child: const Text("Execute Quarantine"),
          )
        ],
      ),
    );
  }
}