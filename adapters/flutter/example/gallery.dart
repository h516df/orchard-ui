// Copy into an existing Flutter application's lib/main.dart after adding orchard_ui.
import 'package:flutter/material.dart';
import 'package:orchard_ui/orchard_ui.dart';

void main() => runApp(const OrchardGallery());

class OrchardGallery extends StatefulWidget {
  const OrchardGallery({super.key});
  @override
  State<OrchardGallery> createState() => _OrchardGalleryState();
}

class _OrchardGalleryState extends State<OrchardGallery> {
  OrchardPalette _palette = OrchardPalette.blue;
  ThemeMode _mode = ThemeMode.system;
  bool _haptics = true;
  @override
  Widget build(BuildContext context) => MaterialApp(
    title: '果园 UI',
    theme: orchardThemeData(palette: _palette),
    darkTheme: orchardThemeData(palette: _palette, brightness: Brightness.dark),
    themeMode: _mode,
    home: OrchardSettingsScreen(
      title: '你的风格',
      subtitle: '一套体验，可以有不同的实现',
      children: [
        OrchardSection(
          title: '主题配色',
          children: [
            for (final palette in OrchardPalette.values)
              OrchardListRow(
                title: palette.label,
                value: palette == _palette ? '已选用' : null,
                onTap: () => setState(() => _palette = palette),
              ),
          ],
        ),
        OrchardSection(
          title: '显示',
          children: [
            Padding(
              padding: const EdgeInsets.all(16),
              child: OrchardSegments(
                options: ThemeMode.values,
                selected: _mode,
                label: (mode) => switch (mode) {
                  ThemeMode.system => '跟随系统',
                  ThemeMode.light => '浅色',
                  ThemeMode.dark => '深色',
                },
                onSelected: (mode) => setState(() => _mode = mode),
              ),
            ),
            const Divider(indent: 16, endIndent: 16),
            OrchardSwitchRow(
              title: '触感反馈',
              subtitle: '这里只展示偏好状态，不调用振动器',
              value: _haptics,
              onChanged: (value) => setState(() => _haptics = value),
            ),
          ],
        ),
        const OrchardMetric(label: '留给自己的时间', value: '2 小时', detail: '合成示例数据'),
        const OrchardReadingSurface(
          title: '雨停之后',
          text: '窗外的树叶被洗得很亮。\n\n走回家的路上，买了一小束花。\n\n这是一段原创合成文字。',
        ),
        const OrchardBanner(title: '从小处开始', message: '先把一个页面做舒服，再逐步扩展。'),
      ],
    ),
  );
}
