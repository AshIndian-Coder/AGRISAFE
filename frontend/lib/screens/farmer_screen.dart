import 'package:flutter/material.dart';

class FarmerBatchInitiationView extends StatefulWidget {
  const FarmerBatchInitiationView({super.key});

  @override
  State<FarmerBatchInitiationView> createState() =>
      _FarmerBatchInitiationViewState();
}

class _FarmerBatchInitiationViewState
    extends State<FarmerBatchInitiationView> {
  final _formKey = GlobalKey<FormState>();
  String _selectedCommodity = 'Organic Alphonso Mangoes';
  double _weightKg = 400.0;
  bool _isRecordingVoice = false;

  // Expanded Commodities List
  final List<String> _commodities = [
    'Organic Alphonso Mangoes',
    'Kashmiri Saffron',
    'Loose Raw Milk',
    'Basmati Rice',
    'Nashik Red Onions',
    'Darjeeling Organic Tea',
    'Nagpur Oranges',
    'Punjab Sharbati Wheat',
    'Coorg Arabica Coffee',
  ];

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16.0),
      child: Form(
        key: _formKey,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              "Module 2.2 | Create Harvest Batch",
              style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
            ),
            const Text(
              "Generates digital identity & hashes metadata for blockchain recording.",
              style: TextStyle(color: Colors.grey, fontSize: 13),
            ),
            const SizedBox(height: 16),
            DropdownButtonFormField<String>(
              value: _selectedCommodity,
              decoration: const InputDecoration(
                labelText: 'Commodity Type',
                border: OutlineInputBorder(),
              ),
              items: _commodities
                  .map((c) => DropdownMenuItem(value: c, child: Text(c)))
                  .toList(),
              onChanged: (val) => setState(() => _selectedCommodity = val!),
            ),
            const SizedBox(height: 16),
            TextFormField(
              initialValue: _weightKg.toString(),
              decoration: const InputDecoration(
                labelText: 'Harvest Weight (kg / Liters)',
                border: OutlineInputBorder(),
              ),
              keyboardType: TextInputType.number,
              onSaved: (val) => _weightKg = double.parse(val ?? '0'),
            ),
            const SizedBox(height: 16),
            OutlinedButton.icon(
              onPressed: () {
                setState(() => _isRecordingVoice = !_isRecordingVoice);
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(
                    content: Text(
                      _isRecordingVoice
                          ? "Listening (Speech-to-Text Active)..."
                          : "Voice entry parsed: 400kg Mangoes added.",
                    ),
                  ),
                );
              },
              icon: Icon(
                _isRecordingVoice ? Icons.mic : Icons.mic_none,
                color: _isRecordingVoice ? Colors.red : Colors.green,
              ),
              label: Text(
                _isRecordingVoice
                    ? "Recording... Tap to Stop"
                    : "Speak Details (Voice STT)",
              ),
            ),
            const SizedBox(height: 20),
            ElevatedButton.icon(
              style: ElevatedButton.styleFrom(
                backgroundColor: Theme.of(context).colorScheme.primary,
                foregroundColor: Colors.white,
                minimumSize: const Size(double.infinity, 48),
              ),
              icon: const Icon(Icons.qr_code_2),
              label: const Text("Mint Batch & Generate Signed QR"),
              onPressed: () {
                if (_formKey.currentState!.validate()) {
                  _formKey.currentState!.save();
                  _showQRDialog(context, "BATCH-2026-X99");
                }
              },
            ),
          ],
        ),
      ),
    );
  }

  void _showQRDialog(BuildContext context, String batchId) {
    showDialog(
      context: context,
      builder: (_) => AlertDialog(
        title: Text("Batch: $batchId", style: const TextStyle(fontSize: 18)),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            const Icon(Icons.qr_code_2, size: 140, color: Colors.black87),
            const SizedBox(height: 10),
            Text(
              "Commodity: $_selectedCommodity\nWeight: $_weightKg kg",
              textAlign: TextAlign.center,
              style: const TextStyle(fontSize: 13),
            ),
            const SizedBox(height: 10),
            const Text(
              "Status: Written to Blockchain Ledger",
              style: TextStyle(
                  color: Colors.green,
                  fontWeight: FontWeight.bold,
                  fontSize: 12),
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text("Close"),
          )
        ],
      ),
    );
  }
}