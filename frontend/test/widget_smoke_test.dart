import 'package:agrotrace_portal/ui/screens/role_selection_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  testWidgets('role selection board renders all five nodes', (tester) async {
    await tester.pumpWidget(
      const MaterialApp(home: RoleSelectionScreen()),
    );

    expect(find.text('Select your operating role'), findsOneWidget);
    expect(find.text('Farmer / Producer'), findsOneWidget);
    expect(find.text('Receiving Agent'), findsOneWidget);

    // The board is a lazy ListView — scroll to materialise lower nodes.
    await tester.drag(find.byType(ListView).first, const Offset(0, -500));
    await tester.pump();
    expect(find.text('Middleman / Aggregator'), findsOneWidget);
    expect(find.text('Retailer / Business'), findsOneWidget);

    await tester.drag(find.byType(ListView).first, const Offset(0, -500));
    await tester.pump();
    expect(find.text('FSSAI Inspector'), findsOneWidget);
    expect(find.text('PRIVACY CHARTER'), findsOneWidget);
    expect(find.textContaining('Zero raw-identifier policy'), findsOneWidget);
  });
}
