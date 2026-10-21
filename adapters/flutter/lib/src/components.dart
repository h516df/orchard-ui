import 'package:flutter/material.dart';
import 'generated_tokens.dart';

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
    final action = loading ? null : onPressed;
    final child = Text(
      loading ? '$label · $loadingLabel' : label,
      textAlign: TextAlign.center,
    );
    final Widget button;
    if (kind == OrchardButtonKind.plain) {
      button = TextButton(
        onPressed: action,
        style: TextButton.styleFrom(minimumSize: const Size(48, 48)),
        child: child,
      );
    } else {
      button = FilledButton(
        onPressed: action,
        style: kind == OrchardButtonKind.soft
            ? FilledButton.styleFrom(
                backgroundColor: c.primaryContainer,
                foregroundColor: c.onPrimaryContainer,
              )
            : kind == OrchardButtonKind.destructive
            ? FilledButton.styleFrom(
                backgroundColor: c.error,
                foregroundColor: c.onError,
              )
            : null,
        child: child,
      );
    }
    return Semantics(liveRegion: loading, child: button);
  }
}

class OrchardCard extends StatelessWidget {
  const OrchardCard({
    super.key,
    required this.child,
    this.padding = const EdgeInsets.all(24),
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
      OrchardCard(
        padding: EdgeInsets.zero,
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
  });
  final String title;
  final String? subtitle;
  final String? value;
  final VoidCallback? onTap;
  final bool destructive;
  @override
  Widget build(BuildContext context) => ListTile(
    contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
    onTap: onTap,
    title: Text(
      title,
      style: Theme.of(context).textTheme.bodyLarge?.copyWith(
        color: destructive ? Theme.of(context).colorScheme.error : null,
      ),
    ),
    subtitle: subtitle == null && value == null
        ? null
        : Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              if (subtitle != null) Text(subtitle!),
              if (value != null)
                Text(
                  value!,
                  style: TextStyle(
                    color: Theme.of(context).colorScheme.primary,
                  ),
                ),
            ],
          ),
    trailing: onTap != null ? const Icon(Icons.chevron_right) : null,
  );
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
  Widget build(BuildContext context) => SwitchListTile.adaptive(
    title: Text(title),
    subtitle: subtitle == null ? null : Text(subtitle!),
    value: value,
    onChanged: onChanged,
    contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
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
  Widget build(BuildContext context) => TextField(
    controller: controller,
    onChanged: onChanged,
    maxLines: maxLines,
    enabled: enabled,
    keyboardType: keyboardType,
    decoration: InputDecoration(
      labelText: label,
      helperText: hint,
      errorText: error,
      errorMaxLines: 4,
    ),
  );
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
  Widget build(BuildContext context) =>
      ValueListenableBuilder<TextEditingValue>(
        valueListenable: controller,
        builder: (context, value, _) => TextField(
          controller: controller,
          onChanged: onChanged,
          decoration: InputDecoration(
            labelText: label,
            prefixIcon: const Icon(Icons.search),
            suffixIcon: value.text.isEmpty
                ? null
                : IconButton(
                    tooltip: '清除搜索',
                    onPressed: () {
                      controller.clear();
                      onChanged('');
                    },
                    icon: const Icon(Icons.close),
                  ),
          ),
        ),
      );
}

class OrchardSegments<T> extends StatelessWidget {
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
  Widget build(BuildContext context) => Wrap(
    spacing: 8,
    runSpacing: 8,
    children: options
        .map(
          (option) => ConstrainedBox(
            constraints: const BoxConstraints(minHeight: 48),
            child: ChoiceChip(
              label: Text(label(option)),
              selected: selected == option,
              onSelected: (_) => onSelected(option),
              materialTapTargetSize: MaterialTapTargetSize.padded,
            ),
          ),
        )
        .toList(),
  );
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
          Icons.auto_stories_outlined,
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
            child: CircularProgressIndicator(),
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
        padding: const EdgeInsets.all(24),
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
