import 'package:flutter/material.dart';
import 'package:flutter/cupertino.dart';
import 'package:liquid_glass_widgets/liquid_glass_widgets.dart';
import 'generated_tokens.dart';

/// Call once before runApp. Prewarms the installed package's actual shaders.
Future<void> initializeOrchardGlass() => LiquidGlassWidgets.initialize();

/// App-wide renderer/accessibility setup. Place above MaterialApp/CupertinoApp.
class OrchardGlassApp extends StatelessWidget {
  const OrchardGlassApp({super.key, required this.child});
  final Widget child;
  @override
  Widget build(BuildContext context) => LiquidGlassWidgets.wrap(
    child: child,
    brightnessResolver: Theme.maybeBrightnessOf,
    adaptiveQuality: true,
  );
}

/// Continuous appearance shared by navigation overlays. Do not put premium
/// surfaces inside a scrolling body: host them in GlassScaffold's bottomBar.
class OrchardGlassScope extends InheritedWidget {
  const OrchardGlassScope({
    super.key,
    required super.child,
    this.tint = OrchardTokens.glassTint / 100,
    this.reducedTransparency = false,
    this.quality = GlassQuality.premium,
  });
  final double tint;
  final bool reducedTransparency;
  final GlassQuality quality;
  static OrchardGlassScope? maybeOf(BuildContext context) =>
      context.dependOnInheritedWidgetOfExactType<OrchardGlassScope>();
  static bool opaqueOf(BuildContext context) =>
      (maybeOf(context)?.reducedTransparency ?? false) ||
      MediaQuery.highContrastOf(context) ||
      MediaQuery.textScalerOf(context).scale(17) > 25.5;
  static LiquidGlassSettings settingsOf(BuildContext context) {
    final input = maybeOf(context)?.tint ?? OrchardTokens.glassTint / 100;
    final tint = input.isFinite
        ? input.clamp(0.0, 1.0)
        : OrchardTokens.glassTint / 100;
    final dark = Theme.of(context).brightness == Brightness.dark;
    final base = dark
        ? LiquidGlassSettings.ios27Dark
        : LiquidGlassSettings.ios27Light;
    return base.copyWith(
      blur: opaqueOf(context) ? 0 : base.blur,
      standardOpacityMultiplier: opaqueOf(context)
          ? 1
          : base.standardOpacityMultiplier,
      glassColor: Theme.of(context).colorScheme.surface.withValues(
        alpha: opaqueOf(context) ? 1 : .18 + .78 * tint,
      ),
      frost: opaqueOf(context)
          ? 0
          : OrchardTokens.glassBlurMin +
                (OrchardTokens.glassBlurMax - OrchardTokens.glassBlurMin) *
                    tint,
    );
  }

  @override
  bool updateShouldNotify(OrchardGlassScope oldWidget) =>
      tint != oldWidget.tint ||
      reducedTransparency != oldWidget.reducedTransparency ||
      quality != oldWidget.quality;
}

/// Controlled Apple-style slider. Hosts own persistence and side effects.
class OrchardSlider extends StatelessWidget {
  const OrchardSlider({
    super.key,
    required this.value,
    required this.onChanged,
    required this.label,
  });
  final double value;
  final ValueChanged<double> onChanged;
  final String label;
  @override
  Widget build(BuildContext context) => Semantics(
    label: label,
    child: CupertinoSlider(
      value: value.isFinite ? value.clamp(0.0, 1.0) : .45,
      onChanged: onChanged,
    ),
  );
}
