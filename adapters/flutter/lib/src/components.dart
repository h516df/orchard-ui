import 'package:flutter/material.dart';
import 'package:flutter/cupertino.dart';
import 'package:liquid_glass_widgets/liquid_glass_widgets.dart';
import 'liquid.dart';
import 'generated_tokens.dart';
import 'theme.dart';

Color _control(BuildContext context, String key) =>
    Theme.of(context).extension<OrchardColors>()?[key] ??
    Color(
      orchardSchemes['blue']![Theme.of(context).brightness == Brightness.dark
          ? 'dark'
          : 'light']![key]!,
    );

enum OrchardButtonKind { filled, soft, plain, destructive }

class OrchardButton extends StatelessWidget {
  const OrchardButton({
    super.key,
    required this.label,
    required this.onPressed,
    this.kind = OrchardButtonKind.filled,
    this.loading = false,
    this.loadingLabel = '正在处理',
  });
  final String label;
  final VoidCallback? onPressed;
  final OrchardButtonKind kind;
  final bool loading;
  final String loadingLabel;
  @override
  Widget build(BuildContext context) {
    final c = Theme.of(context).colorScheme;
    final background = switch (kind) {
      OrchardButtonKind.filled => _control(context, 'buttonFill'),
      OrchardButtonKind.soft => c.primaryContainer,
      OrchardButtonKind.destructive => c.error,
      OrchardButtonKind.plain => null,
    };
    final foreground = switch (kind) {
      OrchardButtonKind.filled => _control(context, 'buttonText'),
      OrchardButtonKind.soft => c.onPrimaryContainer,
      OrchardButtonKind.destructive => c.onError,
      OrchardButtonKind.plain => c.primary,
    };
    return Semantics(
      liveRegion: loading,
      child: CupertinoButton(
        onPressed: loading ? null : onPressed,
        color: background,
        foregroundColor: foreground,
        disabledColor: c.surfaceContainerHighest,
        minimumSize: const Size(48, 48),
        borderRadius: BorderRadius.circular(999),
        pressedOpacity: .55,
        padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
        child: Text(
          loading ? '$label · $loadingLabel' : label,
          textAlign: TextAlign.center,
          style: Theme.of(context).textTheme.bodyLarge?.copyWith(
            color: foreground,
            fontWeight: FontWeight.w600,
          ),
        ),
      ),
    );
  }
}

class OrchardCard extends StatelessWidget {
  const OrchardCard({
    super.key,
    required this.child,
    this.padding = const EdgeInsets.all(20),
  });
  final Widget child;
  final EdgeInsetsGeometry padding;
  @override
  Widget build(BuildContext context) => Material(
    color: Theme.of(context).colorScheme.surface,
    borderRadius: BorderRadius.circular(OrchardTokens.radiusCard),
    clipBehavior: Clip.antiAlias,
    child: Padding(padding: padding, child: child),
  );
}

class OrchardSection extends StatelessWidget {
  const OrchardSection({
    super.key,
    required this.title,
    required this.children,
    this.footnote,
  });
  final String title;
  final List<Widget> children;
  final String? footnote;
  @override
  Widget build(BuildContext context) => Column(
    crossAxisAlignment: CrossAxisAlignment.stretch,
    children: [
      Padding(
        padding: const EdgeInsets.fromLTRB(12, 0, 12, 8),
        child: Semantics(
          header: true,
          child: Text(title, style: Theme.of(context).textTheme.bodySmall),
        ),
      ),
      OrchardGroup(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: children,
        ),
      ),
      if (footnote != null)
        Padding(
          padding: const EdgeInsets.fromLTRB(12, 8, 12, 0),
          child: Text(footnote!, style: Theme.of(context).textTheme.bodySmall),
        ),
    ],
  );
}

class OrchardListRow extends StatelessWidget {
  const OrchardListRow({
    super.key,
    required this.title,
    this.subtitle,
    this.value,
    this.onTap,
    this.destructive = false,
    this.leading,
  });
  final String title;
  final String? subtitle;
  final String? value;
  final VoidCallback? onTap;
  final bool destructive;
  final Widget? leading;
  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final c = theme.colorScheme;
    final large = MediaQuery.textScalerOf(context).scale(17) > 25.5;
    final body = ConstrainedBox(
      constraints: const BoxConstraints(minHeight: OrchardTokens.sizeRow),
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 9),
        child: Row(
          children: [
            if (leading != null) ...[leading!, const SizedBox(width: 12)],
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    title,
                    style: theme.textTheme.bodyLarge?.copyWith(
                      color: destructive ? c.error : c.onSurface,
                    ),
                  ),
                  if (subtitle != null) ...[
                    const SizedBox(height: 3),
                    Text(subtitle!, style: theme.textTheme.bodySmall),
                  ],
                  if (large && value != null)
                    Text(
                      value!,
                      style: theme.textTheme.bodyMedium?.copyWith(
                        color: c.onSurfaceVariant,
                      ),
                    ),
                ],
              ),
            ),
            if (!large && value != null) ...[
              const SizedBox(width: 12),
              Flexible(
                child: Text(
                  value!,
                  textAlign: TextAlign.end,
                  style: theme.textTheme.bodyLarge?.copyWith(
                    color: c.onSurfaceVariant,
                  ),
                ),
              ),
            ],
            if (onTap != null) ...[
              const SizedBox(width: 10),
              Icon(
                CupertinoIcons.chevron_forward,
                size: 14,
                color: c.onSurfaceVariant,
              ),
            ],
          ],
        ),
      ),
    );
    return onTap == null
        ? body
        : CupertinoButton(
            onPressed: onTap,
            padding: EdgeInsets.zero,
            minimumSize: const Size(48, 48),
            child: body,
          );
  }
}

class OrchardSwitchRow extends StatelessWidget {
  const OrchardSwitchRow({
    super.key,
    required this.title,
    required this.value,
    required this.onChanged,
    this.subtitle,
  });
  final String title;
  final String? subtitle;
  final bool value;
  final ValueChanged<bool>? onChanged;
  @override
  Widget build(BuildContext context) => MergeSemantics(
    child: GestureDetector(
      behavior: HitTestBehavior.opaque,
      onTap: onChanged == null ? null : () => onChanged!(!value),
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 9),
        child: ConstrainedBox(
          constraints: const BoxConstraints(minHeight: 34),
          child: Row(
            children: [
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(title, style: Theme.of(context).textTheme.bodyLarge),
                    if (subtitle != null)
                      Text(
                        subtitle!,
                        style: Theme.of(context).textTheme.bodySmall,
                      ),
                  ],
                ),
              ),
              const SizedBox(width: 16),
              CupertinoSwitch(
                value: value,
                onChanged: onChanged,
                activeTrackColor: _control(context, 'toggleOn'),
                inactiveTrackColor: _control(context, 'toggleOff'),
              ),
            ],
          ),
        ),
      ),
    ),
  );
}

class OrchardTextField extends StatelessWidget {
  const OrchardTextField({
    super.key,
    required this.controller,
    required this.label,
    this.hint,
    this.error,
    this.onChanged,
    this.maxLines = 1,
    this.enabled = true,
    this.keyboardType,
  });
  final TextEditingController controller;
  final String label;
  final String? hint;
  final String? error;
  final ValueChanged<String>? onChanged;
  final int? maxLines;
  final bool enabled;
  final TextInputType? keyboardType;
  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final stacked =
        maxLines != 1 || MediaQuery.textScalerOf(context).scale(17) > 25.5;
    final field = Semantics(
      label: label,
      child: CupertinoTextField(
        controller: controller,
        onChanged: onChanged,
        maxLines: maxLines,
        enabled: enabled,
        keyboardType: keyboardType,
        decoration: null,
        padding: stacked
            ? EdgeInsets.zero
            : const EdgeInsets.symmetric(vertical: 13),
        style: theme.textTheme.bodyLarge,
        cursorColor: theme.colorScheme.primary,
        textAlign: stacked ? TextAlign.start : TextAlign.end,
        minLines: maxLines == 1 ? 1 : 6,
      ),
    );
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        DecoratedBox(
          decoration: BoxDecoration(
            color: theme.colorScheme.surface,
            borderRadius: BorderRadius.circular(14),
          ),
          child: Padding(
            padding: stacked
                ? const EdgeInsets.all(16)
                : const EdgeInsets.symmetric(horizontal: 16, vertical: 2),
            child: stacked
                ? Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        label,
                        style: theme.textTheme.bodyMedium?.copyWith(
                          color: theme.colorScheme.onSurfaceVariant,
                        ),
                      ),
                      const SizedBox(height: 12),
                      field,
                    ],
                  )
                : Row(
                    children: [
                      Text(label, style: theme.textTheme.bodyLarge),
                      const SizedBox(width: 16),
                      Expanded(child: field),
                    ],
                  ),
          ),
        ),
        if (error != null || hint != null)
          Padding(
            padding: const EdgeInsets.fromLTRB(16, 6, 16, 0),
            child: Semantics(
              liveRegion: error != null,
              child: Text(
                error ?? hint!,
                style: theme.textTheme.bodySmall?.copyWith(
                  color: error == null ? null : theme.colorScheme.error,
                ),
              ),
            ),
          ),
      ],
    );
  }
}

class OrchardSearchField extends StatelessWidget {
  const OrchardSearchField({
    super.key,
    required this.controller,
    required this.onChanged,
    this.label = '搜索',
  });
  final TextEditingController controller;
  final ValueChanged<String> onChanged;
  final String label;
  @override
  Widget build(BuildContext context) => Semantics(
    label: label,
    child: ConstrainedBox(
      constraints: const BoxConstraints(minHeight: 48),
      child: CupertinoSearchTextField(
        controller: controller,
        onChanged: onChanged,
        placeholder: label,
        style: Theme.of(context).textTheme.bodyLarge,
        borderRadius: BorderRadius.circular(14),
        padding: const EdgeInsets.symmetric(vertical: 12, horizontal: 10),
      ),
    ),
  );
}

class OrchardSegments<T extends Object> extends StatelessWidget {
  const OrchardSegments({
    super.key,
    required this.options,
    required this.selected,
    required this.label,
    required this.onSelected,
  });
  final List<T> options;
  final T selected;
  final String Function(T) label;
  final ValueChanged<T> onSelected;
  @override
  Widget build(BuildContext context) {
    if (options.isEmpty) return const SizedBox.shrink();
    final large = MediaQuery.textScalerOf(context).scale(17) > 25.5;
    if (large || options.length == 1) {
      return DecoratedBox(
        decoration: BoxDecoration(
          color: _control(context, 'segmentTrack'),
          borderRadius: BorderRadius.circular(14),
        ),
        child: Padding(
          padding: const EdgeInsets.all(4),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              for (final option in options)
                Semantics(
                  selected: option == selected,
                  child: CupertinoButton(
                    onPressed: () => onSelected(option),
                    color: option == selected
                        ? _control(context, 'segmentThumb')
                        : null,
                    borderRadius: BorderRadius.circular(10),
                    minimumSize: const Size(48, 48),
                    padding: const EdgeInsets.all(12),
                    child: Text(
                      label(option),
                      style: Theme.of(context).textTheme.bodyMedium,
                    ),
                  ),
                ),
            ],
          ),
        ),
      );
    }
    return ConstrainedBox(
      constraints: const BoxConstraints(minHeight: 48),
      child: Center(
        child: SizedBox(
          width: double.infinity,
          child: CupertinoSlidingSegmentedControl<T>(
            groupValue: selected,
            onValueChanged: (v) {
              if (v != null) onSelected(v);
            },
            backgroundColor: _control(context, 'segmentTrack'),
            thumbColor: _control(context, 'segmentThumb'),
            children: {
              for (final option in options)
                option: Padding(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 8,
                    vertical: 7,
                  ),
                  child: Text(
                    label(option),
                    style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                ),
            },
          ),
        ),
      ),
    );
  }
}

class OrchardLargeTitle extends StatelessWidget {
  const OrchardLargeTitle({super.key, required this.title, this.subtitle});
  final String title;
  final String? subtitle;
  @override
  Widget build(BuildContext context) => Padding(
    padding: const EdgeInsets.symmetric(vertical: 12),
    child: Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Semantics(
          header: true,
          child: Text(title, style: Theme.of(context).textTheme.headlineLarge),
        ),
        if (subtitle != null)
          Padding(
            padding: const EdgeInsets.only(top: 8),
            child: Text(
              subtitle!,
              style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                color: Theme.of(context).colorScheme.onSurfaceVariant,
              ),
            ),
          ),
      ],
    ),
  );
}

class OrchardBadge extends StatelessWidget {
  const OrchardBadge({super.key, required this.label});
  final String label;
  @override
  Widget build(BuildContext context) => DecoratedBox(
    decoration: BoxDecoration(
      color: Theme.of(context).colorScheme.primaryContainer,
      borderRadius: BorderRadius.circular(999),
    ),
    child: Padding(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
      child: Text(
        label,
        style: Theme.of(context).textTheme.bodySmall?.copyWith(
          color: Theme.of(context).colorScheme.onPrimaryContainer,
        ),
      ),
    ),
  );
}

class OrchardBanner extends StatelessWidget {
  const OrchardBanner({
    super.key,
    required this.title,
    required this.message,
    this.error = false,
    this.action,
  });
  final String title;
  final String message;
  final bool error;
  final Widget? action;
  @override
  Widget build(BuildContext context) {
    final c = Theme.of(context).colorScheme;
    return Semantics(
      liveRegion: true,
      child: DecoratedBox(
        decoration: BoxDecoration(
          color: error ? c.errorContainer : c.primaryContainer,
          borderRadius: BorderRadius.circular(16),
        ),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: DefaultTextStyle(
            style: Theme.of(context).textTheme.bodyMedium!.copyWith(
              color: error ? c.onErrorContainer : c.onPrimaryContainer,
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  title,
                  style: const TextStyle(fontWeight: FontWeight.w600),
                ),
                const SizedBox(height: 8),
                Text(message),
                if (action != null)
                  Padding(
                    padding: const EdgeInsets.only(top: 8),
                    child: action!,
                  ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class OrchardEmptyState extends StatelessWidget {
  const OrchardEmptyState({
    super.key,
    required this.title,
    required this.message,
    this.action,
  });
  final String title;
  final String message;
  final Widget? action;
  @override
  Widget build(BuildContext context) => Padding(
    padding: const EdgeInsets.symmetric(vertical: 32),
    child: Column(
      children: [
        Icon(
          CupertinoIcons.book,
          size: 36,
          color: Theme.of(context).colorScheme.primary,
        ),
        const SizedBox(height: 12),
        Text(
          title,
          style: Theme.of(context).textTheme.titleLarge,
          textAlign: TextAlign.center,
        ),
        const SizedBox(height: 8),
        Text(message, textAlign: TextAlign.center),
        if (action != null)
          Padding(padding: const EdgeInsets.only(top: 16), child: action!),
      ],
    ),
  );
}

class OrchardMetric extends StatelessWidget {
  const OrchardMetric({
    super.key,
    required this.label,
    required this.value,
    this.detail,
  });
  final String label;
  final String value;
  final String? detail;
  @override
  Widget build(BuildContext context) => OrchardCard(
    child: Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(label, style: Theme.of(context).textTheme.bodySmall),
        const SizedBox(height: 8),
        Text(value, style: Theme.of(context).textTheme.headlineLarge),
        if (detail != null)
          Text(detail!, style: Theme.of(context).textTheme.bodySmall),
      ],
    ),
  );
}

class OrchardLoadingState extends StatelessWidget {
  const OrchardLoadingState({super.key, this.label = '正在加载'});
  final String label;
  @override
  Widget build(BuildContext context) => Semantics(
    liveRegion: true,
    child: Padding(
      padding: const EdgeInsets.all(32),
      child: Column(
        children: [
          const SizedBox(
            width: 28,
            height: 28,
            child: CupertinoActivityIndicator(),
          ),
          const SizedBox(height: 16),
          Text(label),
        ],
      ),
    ),
  );
}

class OrchardErrorState extends StatelessWidget {
  const OrchardErrorState({
    super.key,
    required this.message,
    required this.onRetry,
  });
  final String message;
  final VoidCallback onRetry;
  @override
  Widget build(BuildContext context) => OrchardBanner(
    title: '暂时没能完成',
    message: message,
    error: true,
    action: OrchardButton(
      label: '重试',
      onPressed: onRetry,
      kind: OrchardButtonKind.soft,
    ),
  );
}

class OrchardReadingSurface extends StatelessWidget {
  const OrchardReadingSurface({
    super.key,
    required this.title,
    required this.text,
    this.textSize = 19,
  });
  final String title;
  final String text;
  final double textSize;
  @override
  Widget build(BuildContext context) => Align(
    alignment: Alignment.topCenter,
    child: ConstrainedBox(
      constraints: const BoxConstraints(maxWidth: OrchardTokens.sizeReadingMax),
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(title, style: Theme.of(context).textTheme.titleLarge),
            const SizedBox(height: 24),
            SelectableText(
              text,
              style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                fontSize: textSize.clamp(14, 36),
                height: 1.7,
              ),
            ),
          ],
        ),
      ),
    ),
  );
}

class OrchardIconTile extends StatelessWidget {
  const OrchardIconTile({super.key, required this.icon, required this.color});
  final IconData icon;
  final Color color;
  @override
  Widget build(BuildContext context) => DecoratedBox(
    decoration: BoxDecoration(
      color: color,
      borderRadius: BorderRadius.circular(7),
    ),
    child: SizedBox(
      width: 30,
      height: 30,
      child: Icon(icon, color: CupertinoColors.white, size: 19),
    ),
  );
}

class OrchardGlassSurface extends StatelessWidget {
  const OrchardGlassSurface({
    super.key,
    required this.child,
    this.reducedTransparency = false,
  });
  final Widget child;
  final bool reducedTransparency;
  @override
  Widget build(BuildContext context) {
    final c = Theme.of(context).colorScheme;
    final opaque = reducedTransparency || OrchardGlassScope.opaqueOf(context);
    if (opaque) {
      return DecoratedBox(
        decoration: BoxDecoration(color: c.surface, borderRadius: BorderRadius.circular(999), border: Border.all(color: c.outlineVariant, width: .75)),
        child: child,
      );
    }
    return AdaptiveGlass(
      shape: const LiquidRoundedSuperellipse(borderRadius: 999),
      settings: OrchardGlassScope.settingsOf(context),
      quality: OrchardGlassScope.maybeOf(context)?.quality ?? GlassQuality.standard,
      child: child,
    );
  }
}

class OrchardDestination {
  const OrchardDestination({
    required this.key,
    required this.label,
    required this.icon,
  });
  final String key;
  final String label;
  final IconData icon;
}

class OrchardTabBar extends StatelessWidget {
  const OrchardTabBar({
    super.key,
    required this.destinations,
    required this.selected,
    required this.onSelect,
    this.reducedTransparency = false,
  });
  final List<OrchardDestination> destinations;
  final String selected;
  final ValueChanged<String> onSelect;
  final bool reducedTransparency;
  @override
  Widget build(BuildContext context) {
    final c = Theme.of(context).colorScheme;
    return SafeArea(
      top: false,
      minimum: const EdgeInsets.fromLTRB(24, 0, 24, 8),
      child: OrchardGlassSurface(
        reducedTransparency: reducedTransparency,
        child: Padding(
          padding: const EdgeInsets.all(4),
          child: Row(
            children: [
              for (final destination in destinations)
                Expanded(
                  child: Semantics(
                    selected: destination.key == selected,
                    inMutuallyExclusiveGroup: true,
                    child: CupertinoButton(
                      onPressed: () => onSelect(destination.key),
                      padding: const EdgeInsets.symmetric(
                        horizontal: 6,
                        vertical: 4,
                      ),
                      minimumSize: const Size(48, 50),
                      borderRadius: BorderRadius.circular(999),
                      color: destination.key == selected
                          ? c.surface.withValues(alpha: .85)
                          : null,
                      child: Column(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          Icon(
                            destination.icon,
                            size: 24,
                            color: destination.key == selected
                                ? c.primary
                                : c.onSurfaceVariant,
                          ),
                          Text(
                            destination.label,
                            style: TextStyle(
                              fontSize: 10,
                              height: 1.3,
                              fontWeight: FontWeight.w600,
                              color: destination.key == selected
                                  ? c.primary
                                  : c.onSurfaceVariant,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
            ],
          ),
        ),
      ),
    );
  }
}

/// Inset grouped lists use the App reference radius, separate from content cards.
class OrchardGroup extends StatelessWidget {
  const OrchardGroup({super.key, required this.child});
  final Widget child;
  @override
  Widget build(BuildContext context) => ClipRRect(
    borderRadius: BorderRadius.circular(OrchardTokens.radiusGroup),
    child: ColoredBox(
      color: Theme.of(context).colorScheme.surface,
      child: child,
    ),
  );
}
