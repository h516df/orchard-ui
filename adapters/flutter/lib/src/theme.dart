import 'package:flutter/material.dart';
import 'generated_tokens.dart';

enum OrchardPalette {
  blue('清透蓝'),
  sage('纸感绿'),
  peach('暖杏色'),
  ink('墨色紫');

  const OrchardPalette(this.label);
  final String label;
}

@immutable
class OrchardColors extends ThemeExtension<OrchardColors> {
  const OrchardColors(this.values);
  final Map<String, int> values;
  Color operator [](String key) => Color(values[key]!);
  @override
  OrchardColors copyWith({Map<String, int>? values}) =>
      OrchardColors(values ?? this.values);
  @override
  OrchardColors lerp(OrchardColors? other, double t) => other == null
      ? this
      : OrchardColors({
          for (final entry in values.entries)
            entry.key: Color.lerp(
              Color(entry.value),
              Color(other.values[entry.key] ?? entry.value),
              t,
            )!.toARGB32(),
        });
}

ThemeData orchardThemeData({
  OrchardPalette palette = OrchardPalette.blue,
  Brightness brightness = Brightness.light,
}) {
  final values =
      orchardSchemes[palette.name]![brightness == Brightness.dark
          ? 'dark'
          : 'light']!;
  Color c(String key) => Color(values[key]!);
  final scheme =
      ColorScheme.fromSeed(
        seedColor: c('primary'),
        brightness: brightness,
      ).copyWith(
        primary: c('primary'),
        onPrimary: c('onPrimary'),
        primaryContainer: c('primaryContainer'),
        onPrimaryContainer: c('onPrimaryContainer'),
        surface: c('surface'),
        onSurface: c('text'),
        onSurfaceVariant: c('textSecondary'),
        surfaceContainerHighest: c('surfaceMuted'),
        outline: c('outline'),
        outlineVariant: c('divider'),
        error: c('danger'),
        onError: c('onDanger'),
        errorContainer: c('dangerContainer'),
        onErrorContainer: c('onDangerContainer'),
      );
  final base = ThemeData(
    useMaterial3: true,
    brightness: brightness,
    colorScheme: scheme,
  );
  return base.copyWith(
    extensions: [OrchardColors(values)],
    scaffoldBackgroundColor: c('background'),
    textTheme: base.textTheme.copyWith(
      headlineLarge: TextStyle(
        fontSize: OrchardTokens.typeLargeTitle,
        height: 1.2,
        letterSpacing: -.8,
        fontWeight: FontWeight.w700,
        color: c('text'),
      ),
      titleLarge: TextStyle(
        fontSize: OrchardTokens.typeTitle,
        height: 1.35,
        fontWeight: FontWeight.w600,
        color: c('text'),
      ),
      bodyLarge: TextStyle(
        fontSize: OrchardTokens.typeBody,
        height: 1.3,
        letterSpacing: -.2,
        color: c('text'),
      ),
      bodyMedium: TextStyle(
        fontSize: OrchardTokens.typeLabel,
        height: 1.5,
        color: c('text'),
      ),
      bodySmall: TextStyle(
        fontSize: OrchardTokens.typeCaption,
        height: 1.5,
        color: c('textSecondary'),
      ),
    ),
    appBarTheme: AppBarTheme(
      backgroundColor: c('background'),
      foregroundColor: c('text'),
      elevation: 0,
      scrolledUnderElevation: 0,
    ),
    inputDecorationTheme: InputDecorationTheme(
      border: OutlineInputBorder(
        borderRadius: BorderRadius.circular(OrchardTokens.radiusControl),
      ),
      contentPadding: const EdgeInsets.all(16),
    ),
    filledButtonTheme: FilledButtonThemeData(
      style: FilledButton.styleFrom(
        minimumSize: const Size(48, 48),
        padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 14),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(OrchardTokens.radiusControl),
        ),
      ),
    ),
    dividerTheme: DividerThemeData(color: c('divider'), thickness: 1, space: 1),
    bottomSheetTheme: BottomSheetThemeData(
      backgroundColor: c('surface'),
      showDragHandle: true,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(28)),
      ),
    ),
  );
}
