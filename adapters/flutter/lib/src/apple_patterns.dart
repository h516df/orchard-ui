import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'components.dart';
import 'generated_tokens.dart';

/// A native Cupertino collapsing navigation bar. The host owns route transitions.
class OrchardAppPage extends StatelessWidget {
  const OrchardAppPage({
    super.key,
    required this.title,
    required this.onMore,
    required this.children,
    this.onBack,
    this.subtitle,
    this.bottomBar,
    this.bottomContentInset = 100,
    this.backgroundColor,
  });
  final String title;
  final String? subtitle;
  final VoidCallback onMore;
  final VoidCallback? onBack;
  final List<Widget> children;
  final Widget? bottomBar;
  final double bottomContentInset;
  final Color? backgroundColor;
  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final background = backgroundColor ?? theme.scaffoldBackgroundColor;
    return CupertinoTheme(
      data: CupertinoThemeData(
        brightness: theme.brightness,
        primaryColor: theme.colorScheme.primary,
        scaffoldBackgroundColor: background,
      ),
      child: Scaffold(
        backgroundColor: background,
        body: Stack(
          children: [
            CustomScrollView(
              slivers: [
                CupertinoSliverNavigationBar(
                  largeTitle: Text(title),
                  automaticallyImplyLeading: false,
                  leading: onBack == null
                      ? null
                      : OrchardRoundButton(
                          icon: CupertinoIcons.chevron_back,
                          label: '返回',
                          onPressed: onBack!,
                        ),
                  trailing: OrchardRoundButton(
                    icon: CupertinoIcons.ellipsis,
                    label: '更多',
                    onPressed: onMore,
                  ),
                  backgroundColor: background,
                  border: null,
                ),
                SliverPadding(
                  padding: EdgeInsets.fromLTRB(
                    20,
                    12,
                    20,
                    (bottomBar == null ? 24 : bottomContentInset) +
                        MediaQuery.paddingOf(context).bottom,
                  ),
                  sliver: SliverList.list(
                    children: [
                      if (subtitle != null)
                        Padding(
                          padding: const EdgeInsets.only(left: 4, bottom: 24),
                          child: Text(
                            subtitle!,
                            style: theme.textTheme.bodyLarge?.copyWith(
                              color: theme.colorScheme.onSurfaceVariant,
                            ),
                          ),
                        ),
                      for (var i = 0; i < children.length; i++) ...[
                        if (i > 0) const SizedBox(height: 28),
                        children[i],
                      ],
                    ],
                  ),
                ),
              ],
            ),
            if (bottomBar != null)
              Positioned(left: 0, right: 0, bottom: 0, child: bottomBar!),
          ],
        ),
      ),
    );
  }
}

class OrchardRoundButton extends StatelessWidget {
  const OrchardRoundButton({
    super.key,
    required this.icon,
    required this.label,
    required this.onPressed,
    this.color,
  });
  final IconData icon;
  final String label;
  final VoidCallback onPressed;
  final Color? color;
  @override
  Widget build(BuildContext context) => Semantics(
    button: true,
    label: label,
    child: CupertinoButton(
      onPressed: onPressed,
      padding: EdgeInsets.zero,
      minimumSize: const Size(48, 48),
      child: ExcludeSemantics(
        child: Container(
          width: OrchardTokens.sizeNavigationControl,
          height: OrchardTokens.sizeNavigationControl,
          decoration: BoxDecoration(
            shape: BoxShape.circle,
            color: Theme.of(context).colorScheme.surface,
          ),
          child: Icon(
            icon,
            size: 22,
            color: color ?? Theme.of(context).colorScheme.onSurface,
          ),
        ),
      ),
    ),
  );
}

class OrchardSearchDock extends StatelessWidget {
  const OrchardSearchDock({
    super.key,
    required this.controller,
    this.onCompose,
    this.onChanged,
    this.reducedTransparency = false,
    this.placeholder = '搜索',
  });
  final TextEditingController controller;
  final VoidCallback? onCompose;
  final ValueChanged<String>? onChanged;
  final bool reducedTransparency;
  final String placeholder;
  @override
  Widget build(BuildContext context) => SafeArea(
    top: false,
    minimum: const EdgeInsets.fromLTRB(24, 0, 24, 8),
    child: Row(
      children: [
        Expanded(
          child: OrchardGlassSurface(
            reducedTransparency: reducedTransparency,
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
              child: Semantics(
                label: placeholder,
                child: CupertinoSearchTextField(
                  controller: controller,
                  onChanged: onChanged,
                  placeholder: placeholder,
                  backgroundColor: Colors.transparent,
                  style: Theme.of(context).textTheme.bodyLarge,
                ),
              ),
            ),
          ),
        ),
        if (onCompose != null) ...[
          const SizedBox(width: 10),
          OrchardRoundButton(
            icon: CupertinoIcons.square_pencil,
            label: '新建备忘录',
            onPressed: onCompose!,
          ),
        ],
      ],
    ),
  );
}

class OrchardNoteRow extends StatelessWidget {
  const OrchardNoteRow({
    super.key,
    required this.title,
    required this.preview,
    required this.time,
    required this.onTap,
    this.folder = '备忘录',
  });
  final String title, preview, time, folder;
  final VoidCallback onTap;
  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final secondary = theme.colorScheme.onSurfaceVariant;
    return CupertinoButton(
      onPressed: onTap,
      padding: EdgeInsets.zero,
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 11),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Text(
              title,
              maxLines: 1,
              overflow: TextOverflow.ellipsis,
              style: theme.textTheme.bodyLarge?.copyWith(
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 3),
            Text(
              '$time  $preview',
              maxLines: 1,
              overflow: TextOverflow.ellipsis,
              style: theme.textTheme.bodyMedium?.copyWith(color: secondary),
            ),
            const SizedBox(height: 3),
            Row(
              children: [
                Icon(CupertinoIcons.folder, size: 13, color: secondary),
                const SizedBox(width: 4),
                Text(folder, style: theme.textTheme.bodySmall),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

/// Interface only; the host supplies and owns the real media engine.
class OrchardMiniPlayer extends StatelessWidget {
  const OrchardMiniPlayer({
    super.key,
    required this.title,
    required this.playing,
    required this.onPlayPause,
    required this.onNext,
    this.artwork,
    this.reducedTransparency = false,
  });
  final String title;
  final bool playing, reducedTransparency;
  final VoidCallback onPlayPause, onNext;
  final Widget? artwork;
  @override
  Widget build(BuildContext context) {
    final c = Theme.of(context).colorScheme;
    return OrchardGlassSurface(
      reducedTransparency: reducedTransparency,
      child: Padding(
        padding: const EdgeInsets.only(left: 10, right: 4),
        child: Row(
          children: [
            Container(
              width: 32,
              height: 32,
              decoration: BoxDecoration(
                color: c.surfaceContainerHighest,
                borderRadius: BorderRadius.circular(6),
              ),
              child:
                  artwork ??
                  Icon(
                    CupertinoIcons.music_note,
                    color: c.onSurfaceVariant,
                    size: 22,
                  ),
            ),
            const SizedBox(width: 10),
            Expanded(
              child: Text(
                title,
                maxLines: 1,
                overflow: TextOverflow.ellipsis,
                style: Theme.of(
                  context,
                ).textTheme.bodyMedium?.copyWith(fontWeight: FontWeight.w600),
              ),
            ),
            Semantics(
              label: playing ? '暂停' : '播放',
              button: true,
              child: CupertinoButton(
                padding: const EdgeInsets.all(10),
                minimumSize: const Size(48, 48),
                onPressed: onPlayPause,
                child: ExcludeSemantics(
                  child: Icon(
                    playing
                        ? CupertinoIcons.pause_fill
                        : CupertinoIcons.play_fill,
                    color: c.onSurface,
                    size: 22,
                  ),
                ),
              ),
            ),
            Semantics(
              label: '下一首',
              button: true,
              child: CupertinoButton(
                padding: const EdgeInsets.all(10),
                minimumSize: const Size(48, 48),
                onPressed: onNext,
                child: ExcludeSemantics(
                  child: Icon(
                    CupertinoIcons.forward_fill,
                    color: c.onSurface,
                    size: 22,
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
