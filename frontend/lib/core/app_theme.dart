import 'package:flutter/material.dart';

/// Design tokens lifted from the AgroTrace web portal
/// ("Farm-to-Shelf Traceability Grid").
class AT {
  AT._();

  // Government palette
  static const Color gov = Color(0xFF0D3B66);
  static const Color govDark = Color(0xFF082A4A);
  static const Color govMid = Color(0xFF12497D);

  // National palette
  static const Color saffron = Color(0xFFFF9933);
  static const Color leaf = Color(0xFF1B5E20);
  static const Color leafLight = Color(0xFF2E7D32);

  // Surfaces & ink
  static const Color slateBg = Color(0xFFF4F6F9);
  static const Color ink = Color(0xFF1E293B);
  static const Color sub = Color(0xFF64748B);
  static const Color faint = Color(0xFF94A3B8);
  static const Color line = Color(0xFFE2E8F0);
  static const Color card = Colors.white;

  // Status tints
  static const Color emeraldBg = Color(0xFFECFDF5);
  static const Color emeraldInk = Color(0xFF065F46);
  static const Color amberBg = Color(0xFFFFF7ED);
  static const Color amberInk = Color(0xFF92400E);
  static const Color roseBg = Color(0xFFFFF1F2);
  static const Color roseInk = Color(0xFF9F1239);
  static const Color skyBg = Color(0xFFF0F9FF);
  static const Color skyInk = Color(0xFF075985);

  static const double radius = 14;

  /// Inter (bundled variable font).
  static TextStyle body({double size = 14, FontWeight weight = FontWeight.w400,
      Color color = ink, double height = 1.45}) {
    return TextStyle(
      fontFamily: 'Inter',
      fontSize: size,
      fontWeight: weight,
      color: color,
      height: height,
    );
  }

  /// JetBrains Mono (bundled variable font) for ledger data.
  static TextStyle mono({double size = 12.5, FontWeight weight = FontWeight.w500,
      Color color = ink, double? letterSpacing}) {
    return TextStyle(
      fontFamily: 'JetBrainsMono',
      fontSize: size,
      fontWeight: weight,
      color: color,
      letterSpacing: letterSpacing,
    );
  }

  static TextStyle label({double size = 11, Color color = sub}) {
    return TextStyle(
      fontFamily: 'Inter',
      fontSize: size,
      fontWeight: FontWeight.w700,
      color: color,
      letterSpacing: 1.1,
    );
  }
}

ThemeData buildAppTheme() {
  final base = ThemeData(
    useMaterial3: true,
    fontFamily: 'Inter',
    colorScheme: ColorScheme.fromSeed(
      seedColor: AT.gov,
      brightness: Brightness.light,
      primary: AT.gov,
    ),
    scaffoldBackgroundColor: AT.slateBg,
  );

  return base.copyWith(
    appBarTheme: const AppBarTheme(
      backgroundColor: AT.govDark,
      foregroundColor: Colors.white,
      elevation: 0,
      centerTitle: false,
    ),
    inputDecorationTheme: InputDecorationTheme(
      filled: true,
      fillColor: Colors.white,
      isDense: true,
      contentPadding: const EdgeInsets.symmetric(horizontal: 14, vertical: 13),
      hintStyle: AT.body(size: 13.5, color: AT.faint),
      border: OutlineInputBorder(
        borderRadius: BorderRadius.circular(10),
        borderSide: const BorderSide(color: AT.line),
      ),
      enabledBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(10),
        borderSide: const BorderSide(color: AT.line),
      ),
      focusedBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(10),
        borderSide: const BorderSide(color: AT.gov, width: 1.6),
      ),
      errorBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(10),
        borderSide: const BorderSide(color: Color(0xFFE11D48)),
      ),
      focusedErrorBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(10),
        borderSide: const BorderSide(color: Color(0xFFE11D48), width: 1.6),
      ),
    ),
    elevatedButtonTheme: ElevatedButtonThemeData(
      style: ElevatedButton.styleFrom(
        backgroundColor: AT.gov,
        foregroundColor: Colors.white,
        elevation: 0,
        padding: const EdgeInsets.symmetric(vertical: 15, horizontal: 18),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
        textStyle: AT.body(size: 14, weight: FontWeight.w700),
      ),
    ),
    outlinedButtonTheme: OutlinedButtonThemeData(
      style: OutlinedButton.styleFrom(
        foregroundColor: AT.gov,
        side: const BorderSide(color: AT.gov),
        padding: const EdgeInsets.symmetric(vertical: 14, horizontal: 18),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
        textStyle: AT.body(size: 14, weight: FontWeight.w700),
      ),
    ),
    snackBarTheme: SnackBarThemeData(
      behavior: SnackBarBehavior.floating,
      backgroundColor: AT.ink,
      contentTextStyle: AT.body(size: 13, color: Colors.white),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
    ),
    dividerTheme: const DividerThemeData(color: AT.line, thickness: 1, space: 1),
  );
}

/// Saffron / white / green hairline — the national tricolor accent used by
/// the portal header.
class TricolorHairline extends StatelessWidget implements PreferredSizeWidget {
  const TricolorHairline({super.key, this.height = 3});

  final double height;

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: height,
      child: const Row(
        children: [
          Expanded(child: ColoredBox(color: AT.saffron)),
          Expanded(child: ColoredBox(color: Colors.white)),
          Expanded(child: ColoredBox(color: AT.leafLight)),
        ],
      ),
    );
  }

  @override
  Size get preferredSize => Size.fromHeight(height);
}
