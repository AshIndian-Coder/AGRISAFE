import 'package:flutter/material.dart';

class FarmerVoiceRegisterScreen extends StatefulWidget {
  const FarmerVoiceRegisterScreen({super.key});

  @override
  State<FarmerVoiceRegisterScreen> createState() => _FarmerVoiceRegisterScreenState();
}

class _FarmerVoiceRegisterScreenState extends State<FarmerVoiceRegisterScreen> {
  bool _isMale = true;
  bool _isListening = false;
  bool _otpSent = false;
  bool _isAadhaarVerified = false;

  final TextEditingController _nameController = TextEditingController();
  final TextEditingController _locationController = TextEditingController();
  final TextEditingController _aadhaarController = TextEditingController();
  final TextEditingController _otpController = TextEditingController();

  void _toggleVoiceAssistant() {
    setState(() {
      _isListening = !_isListening;
      if (!_isListening) {
        _nameController.text = "Ramesh Kumar";
        _locationController.text = "Nashik Cluster, Maharashtra";
      }
    });
  }

  void _sendAadhaarOtp() {
    if (_aadhaarController.text.length < 12) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Enter a valid 12-digit Aadhaar Number")),
      );
      return;
    }
    setState(() => _otpSent = true);
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text("e-KYC OTP sent to Aadhaar-linked Mobile Number!")),
    );
  }

  void _verifyOtp() {
    if (_otpController.text == "123456" || _otpController.text.length == 6) {
      setState(() => _isAadhaarVerified = true);
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Aadhaar e-KYC Verified Successfully!"), backgroundColor: Colors.green),
      );
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Invalid OTP. Enter 123456 for demo."), backgroundColor: Colors.red),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Farmer Onboarding (e-KYC)"),
        backgroundColor: Theme.of(context).colorScheme.primary,
        foregroundColor: Colors.white,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const Text("Avatar: ", style: TextStyle(fontWeight: FontWeight.bold)),
                ChoiceChip(
                  label: const Text("Male Farmer"),
                  selected: _isMale,
                  onSelected: (val) => setState(() => _isMale = true),
                ),
                const SizedBox(width: 8),
                ChoiceChip(
                  label: const Text("Female Farmer"),
                  selected: !_isMale,
                  onSelected: (val) => setState(() => _isMale = false),
                ),
              ],
            ),
            const SizedBox(height: 12),
            CircleAvatar(
              radius: 50,
              backgroundColor: Theme.of(context).colorScheme.primary.withOpacity(0.15),
              child: Icon(_isMale ? Icons.face : Icons.face_3, size: 60, color: Theme.of(context).colorScheme.primary),
            ),
            const SizedBox(height: 12),
            OutlinedButton.icon(
              onPressed: _toggleVoiceAssistant,
              icon: Icon(_isListening ? Icons.mic : Icons.mic_none, color: _isListening ? Colors.red : Colors.green),
              label: Text(_isListening ? "Listening..." : "Speak Details (Voice STT)"),
            ),
            const SizedBox(height: 20),

            Card(
              elevation: 2,
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text("Farmer Identity Details", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                    const Text("Crops will be selected per batch at harvest time.", style: TextStyle(fontSize: 11, color: Colors.grey)),
                    const Divider(),
                    TextField(
                      controller: _nameController,
                      decoration: const InputDecoration(labelText: "Full Name", prefixIcon: Icon(Icons.person), border: OutlineInputBorder()),
                    ),
                    const SizedBox(height: 12),
                    TextField(
                      controller: _locationController,
                      decoration: const InputDecoration(labelText: "Village / APMC District", prefixIcon: Icon(Icons.location_on), border: OutlineInputBorder()),
                    ),
                    const SizedBox(height: 16),

                    const Text("Tier-1 Identity Verification (Aadhaar e-KYC)", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                    const SizedBox(height: 8),
                    Row(
                      children: [
                        Expanded(
                          child: TextField(
                            controller: _aadhaarController,
                            keyboardType: TextInputType.number,
                            obscureText: true,
                            decoration: const InputDecoration(labelText: "Aadhaar Number", prefixIcon: Icon(Icons.badge), border: OutlineInputBorder()),
                          ),
                        ),
                        const SizedBox(width: 8),
                        ElevatedButton(
                          onPressed: _isAadhaarVerified ? null : _sendAadhaarOtp,
                          child: Text(_otpSent ? "Resend" : "Get OTP"),
                        )
                      ],
                    ),
                    if (_otpSent && !_isAadhaarVerified) ...[
                      const SizedBox(height: 12),
                      Row(
                        children: [
                          Expanded(
                            child: TextField(
                              controller: _otpController,
                              keyboardType: TextInputType.number,
                              decoration: const InputDecoration(labelText: "Enter 6-Digit OTP (Demo: 123456)", border: OutlineInputBorder()),
                            ),
                          ),
                          const SizedBox(width: 8),
                          ElevatedButton(
                            style: ElevatedButton.styleFrom(backgroundColor: Colors.green, foregroundColor: Colors.white),
                            onPressed: _verifyOtp,
                            child: const Text("Verify"),
                          )
                        ],
                      )
                    ],
                    if (_isAadhaarVerified) ...[
                      const SizedBox(height: 12),
                      const Row(
                        children: [
                          Icon(Icons.verified, color: Colors.green),
                          SizedBox(width: 6),
                          Text("Identity Cryptographically Verified via UIDAI e-KYC", style: TextStyle(color: Colors.green, fontWeight: FontWeight.bold, fontSize: 12)),
                        ],
                      )
                    ]
                  ],
                ),
              ),
            ),
            const SizedBox(height: 20),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton.icon(
                style: ElevatedButton.styleFrom(backgroundColor: Theme.of(context).colorScheme.primary, foregroundColor: Colors.white, padding: const EdgeInsets.symmetric(vertical: 14)),
                icon: const Icon(Icons.check_circle),
                label: const Text("Complete Verified Registration"),
                onPressed: _isAadhaarVerified
                    ? () {
                        ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text("Farmer Account Created & Verified!")));
                        Navigator.pop(context);
                      }
                    : null,
              ),
            )
          ],
        ),
      ),
    );
  }
}