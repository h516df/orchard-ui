import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:liquid_glass_widgets/liquid_glass_widgets.dart';
import 'package:orchard_ui/orchard_ui.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await initializeOrchardGlass();
  runApp(const OrchardLiquidDemo());
}

class OrchardLiquidDemo extends StatefulWidget {
  const OrchardLiquidDemo({super.key});
  @override
  State<OrchardLiquidDemo> createState() => _DemoState();
}

class _DemoState extends State<OrchardLiquidDemo> {
  bool dark = false, opaque = false, reduced = false;
  double tint = .45;
  @override
  Widget build(BuildContext context) => OrchardGlassApp(
    child: OrchardGlassScope(
      tint: tint,
      reducedTransparency: opaque,
      child: MaterialApp(
        debugShowCheckedModeBanner: false,
        theme: orchardThemeData(),
        darkTheme: orchardThemeData(brightness: Brightness.dark),
        themeMode: dark ? ThemeMode.dark : ThemeMode.light,
        builder: (context, child) => MediaQuery(
          data: MediaQuery.of(context).copyWith(
            disableAnimations:
                reduced || MediaQuery.disableAnimationsOf(context),
            highContrast: opaque || MediaQuery.highContrastOf(context),
          ),
          child: Material(type: MaterialType.transparency, child: child!),
        ),
        home: Builder(
          builder: (context) => MusicPage(
            onAppearance: () => Navigator.of(context).push(
              CupertinoPageRoute<void>(
                builder: (context) => StatefulBuilder(
                  builder: (context, update) => OrchardAppPage(
                    title: '外观与材质',
                    onMore: () {},
                    onBack: () => Navigator.pop(context),
                    children: [
                      OrchardCard(
                        child: Column(
                          children: [
                            OrchardSwitchRow(
                              title: '深色外观',
                              value: dark,
                              onChanged: (v) {
                                setState(() => dark = v);
                                update(() {});
                              },
                            ),
                            OrchardSwitchRow(
                              title: '降低透明度',
                              value: opaque,
                              onChanged: (v) {
                                setState(() => opaque = v);
                                update(() {});
                              },
                            ),
                            OrchardSwitchRow(
                              title: '减少动态效果',
                              value: reduced,
                              onChanged: (v) {
                                setState(() => reduced = v);
                                update(() {});
                              },
                            ),
                          ],
                        ),
                      ),
                      Text(
                        'Liquid Glass',
                        style: Theme.of(context).textTheme.headlineSmall,
                      ),
                      OrchardSlider(
                        value: tint,
                        onChanged: (v) {
                          setState(() => tint = v);
                          update(() {});
                        },
                        label: '玻璃着色',
                      ),
                      const Text('清透                                  着色'),
                      const OrchardGlassSurface(
                        child: Padding(
                          padding: EdgeInsets.all(20),
                          child: Text('导航材质预览'),
                        ),
                      ),
                      const Text('内容保持实色。高对比度、大字号和降低透明度时优先可读性。'),
                    ],
                  ),
                ),
              ),
            ),
          ),
        ),
      ),
    ),
  );
}

const names = ['微风', '慢一点', '沿途', '日落以后', '回声', '向海'];
const colors = [
  Color(0xff294d4a),
  Color(0xffae6d5f),
  Color(0xff444a76),
  Color(0xff624a38),
  Color(0xff587a95),
  Color(0xff9d697f),
];
const labels = ['主页', '广播', '资料库', '搜索'];
const icons = [
  CupertinoIcons.house_fill,
  CupertinoIcons.antenna_radiowaves_left_right,
  CupertinoIcons.square_stack_fill,
  CupertinoIcons.search,
];

class MusicPage extends StatefulWidget {
  const MusicPage({super.key, required this.onAppearance});
  final VoidCallback onAppearance;
  @override
  State<MusicPage> createState() => _MusicState();
}

class _MusicState extends State<MusicPage> {
  final scroll = ScrollController();
  final minimize = GlassTabBarMinimizeController();
  final menus = List.generate(names.length, (_) => GlassMenuController());
  @override
  void initState() {
    super.initState();
    HardwareKeyboard.instance.addHandler(closeMenuOnEscape);
  }

  bool closeMenuOnEscape(KeyEvent event) {
    if (event is! KeyDownEvent ||
        event.logicalKey != LogicalKeyboardKey.escape) {
      return false;
    }
    final opened = menus.where((menu) => menu.isOpen).toList();
    for (final menu in opened) {
      menu.close();
    }
    return opened.isNotEmpty;
  }

  int tab = 2, track = 0;
  bool playing = false, hasTrack = false;
  String query = '', category = '';
  final queue = <int>[];
  @override
  void dispose() {
    HardwareKeyboard.instance.removeHandler(closeMenuOnEscape);
    scroll.dispose();
    minimize.dispose();
    super.dispose();
  }

  void play(int index) => setState(() {
    track = index;
    playing = true;
    hasTrack = true;
  });
  void next() =>
      play(queue.isEmpty ? (track + 1) % names.length : queue.removeAt(0));
  void presentPlayer(GlassMorphAnchor anchor) {
    final reduced = MediaQuery.disableAnimationsOf(context);
    GlassModalSheet.show<void>(
      context: context,
      morphFrom: reduced ? null : anchor,
      settings: OrchardGlassScope.settingsOf(context),
      quality: OrchardGlassScope.opaqueOf(context)
          ? GlassQuality.minimal
          : GlassQuality.premium,
      sheetAnimationStyle: reduced ? AnimationStyle.noAnimation : null,
      builder: (context) => StatefulBuilder(
        builder: (context, update) => SingleChildScrollView(
          controller: ScrollControllerProvider.of(context)?.controller,
          child: Padding(
            padding: const EdgeInsets.all(24),
            child: Column(
              children: [
                const Text(
                  '正在播放',
                  style: TextStyle(fontWeight: FontWeight.w600),
                ),
                const SizedBox(height: 20),
                SizedBox(
                  width: 220,
                  height: 220,
                  child: AlbumArt(index: track),
                ),
                const SizedBox(height: 20),
                Text(
                  hasTrack ? names[track] : '未在播放',
                  style: Theme.of(context).textTheme.headlineSmall,
                ),
                const Text('Orchard Sessions · 本地演示'),
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    CupertinoButton(
                      foregroundColor: Theme.of(context).colorScheme.onSurface,
                      onPressed: () {
                        setState(() {
                          playing = !playing;
                          hasTrack = true;
                        });
                        update(() {});
                      },
                      child: Icon(
                        playing
                            ? CupertinoIcons.pause_fill
                            : CupertinoIcons.play_fill,
                        semanticLabel: playing ? '暂停' : '播放',
                      ),
                    ),
                    CupertinoButton(
                      foregroundColor: Theme.of(context).colorScheme.onSurface,
                      onPressed: () {
                        next();
                        update(() {});
                      },
                      child: const Icon(
                        CupertinoIcons.forward_end_fill,
                        semanticLabel: '下一首',
                      ),
                    ),
                  ],
                ),
                Text('待播 ${queue.length} 首'),
                CupertinoButton(
                  foregroundColor: Theme.of(context).colorScheme.onSurface,
                  onPressed: () => Navigator.pop(context),
                  child: const Text('完成'),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final settings = OrchardGlassScope.settingsOf(context);
    final quality = OrchardGlassScope.opaqueOf(context)
        ? GlassQuality.minimal
        : GlassQuality.premium;
    final visible = List.generate(
      names.length,
      (i) => i,
    ).where((i) => names[i].contains(query)).toList();
    return GlassScaffold(
      backgroundColor: Theme.of(context).colorScheme.surface,
      settings: settings,
      appBar: GlassAppBar(
        title: AnimatedBuilder(
          animation: scroll,
          builder: (context, _) => Opacity(
            opacity: scroll.hasClients
                ? ((scroll.offset - 48) / 24).clamp(0.0, 1.0)
                : 0,
            child: Text(category.isEmpty ? labels[tab] : category),
          ),
        ),
        leading: category.isNotEmpty
            ? GlassIconButton(
                icon: const Icon(CupertinoIcons.back, semanticLabel: '返回'),
                onPressed: () => setState(() => category = ''),
              )
            : null,
        actions: [
          GlassIconButton(
            icon: const Icon(
              CupertinoIcons.slider_horizontal_3,
              semanticLabel: '外观与材质',
            ),
            onPressed: widget.onAppearance,
          ),
        ],
      ),
      body: ListView(
        controller: scroll,
        padding: const EdgeInsets.fromLTRB(24, 120, 24, 240),
        children: [
          Text(
            category.isEmpty ? labels[tab] : category,
            style: const TextStyle(
              fontSize: 34,
              fontWeight: FontWeight.bold,
              letterSpacing: -.8,
            ),
          ),
          const SizedBox(height: 24),
          if (tab == 2 && category.isEmpty)
            ...['播放列表', '艺人', '专辑', '歌曲', '已下载'].map(
              (label) => OrchardListRow(
                title: label,
                leading: const Icon(
                  CupertinoIcons.music_note_2,
                  color: Color(0xffd7003b),
                ),
                onTap: () => setState(() => category = label),
              ),
            ),
          if (tab == 3)
            CupertinoSearchTextField(
              placeholder: '音乐、艺人、专辑',
              onChanged: (v) => setState(() => query = v),
            ),
          const SizedBox(height: 24),
          Text(
            tab == 0
                ? '为你精选'
                : tab == 1
                ? '电台精选'
                : '最近添加',
            style: const TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 16),
          for (var row = 0; row < visible.length; row += 2)
            Padding(
              padding: const EdgeInsets.only(bottom: 24),
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  for (var col = 0; col < 2; col++) ...[
                    if (col > 0) const SizedBox(width: 16),
                    Expanded(
                      child: row + col >= visible.length
                          ? const SizedBox()
                          : album(visible[row + col], settings, quality),
                    ),
                  ],
                ],
              ),
            ),
          if (visible.isEmpty) const Text('没有结果'),
          Text('待播 ${queue.length} 首', semanticsLabel: '待播 ${queue.length} 首'),
        ],
      ),
      bottomBar: GlassTabBar.minimizable(
        tabs: List.generate(
          labels.length,
          (i) => GlassTab(icon: Icon(icons[i]), label: labels[i]),
        ),
        selectedIndex: tab,
        onTabSelected: (v) => setState(() {
          tab = v;
          query = '';
          category = '';
          minimize.expand();
          scroll.jumpTo(0);
        }),
        minimizeController: minimize,
        scrollController: scroll,
        onMinimizedTabTap: minimize.expand,
        settings: settings,
        quality: quality,
        barHeight: 58,
        horizontalPadding: 24,
        verticalPadding: 8,
        selectedIconColor: const Color(0xffd7003b),
        selectedLabelColor: const Color(0xffd7003b),
        bottomAccessoryHeight: 50,
        bottomAccessory: GlassMorphTrigger(
          builder: (context, anchor) => OrchardGlassSurface(
            child: Row(
              children: [
                const SizedBox(width: 8),
                SizedBox(
                  width: 32,
                  height: 32,
                  child: ExcludeSemantics(child: AlbumArt(index: track)),
                ),
                Expanded(
                  child: CupertinoButton(
                    foregroundColor: Theme.of(context).colorScheme.onSurface,
                    padding: const EdgeInsets.symmetric(horizontal: 12),
                    onPressed: () => presentPlayer(anchor),
                    child: Text(
                      hasTrack ? names[track] : '未在播放',
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                  ),
                ),
                CupertinoButton(
                  foregroundColor: Theme.of(context).colorScheme.onSurface,
                  padding: const EdgeInsets.all(8),
                  onPressed: () => setState(() {
                    playing = !playing;
                    hasTrack = true;
                  }),
                  child: Icon(
                    playing
                        ? CupertinoIcons.pause_fill
                        : CupertinoIcons.play_fill,
                    semanticLabel: playing ? '暂停' : '播放',
                  ),
                ),
                CupertinoButton(
                  foregroundColor: Theme.of(context).colorScheme.onSurface,
                  padding: const EdgeInsets.all(8),
                  onPressed: next,
                  child: const Icon(
                    CupertinoIcons.forward_end_fill,
                    semanticLabel: '下一首',
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget album(int i, LiquidGlassSettings settings, GlassQuality quality) =>
      Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Semantics(
            button: true,
            label: '播放${names[i]}',
            child: GestureDetector(
              onTap: () => play(i),
              child: AspectRatio(aspectRatio: 1, child: AlbumArt(index: i)),
            ),
          ),
          Row(
            children: [
              Expanded(
                child: Text(names[i], style: const TextStyle(fontSize: 17)),
              ),
              GlassMenu(
                controller: menus[i],
                autoAdjustToScreen: true,
                menuPadding: const EdgeInsets.all(8),
                semanticLabel: '${names[i]}的操作',
                settings: settings.copyWith(
                  glassColor: Theme.of(context).colorScheme.surface.withValues(
                    alpha: OrchardGlassScope.opaqueOf(context) ? 1 : .94,
                  ),
                  frost: 20,
                  blur: 8,
                ),
                quality: quality,
                trigger: const Padding(
                  padding: EdgeInsets.all(12),
                  child: Icon(CupertinoIcons.ellipsis, size: 20),
                ),
                items: [
                  GlassMenuItem(title: '现在播放', onTap: () => play(i)),
                  GlassMenuItem(
                    title: '添加到待播',
                    onTap: () {},
                    submenu: [
                      GlassMenuItem(
                        title: '插播下一首',
                        onTap: () => setState(() => queue.insert(0, i)),
                      ),
                      GlassMenuItem(
                        title: '最后播放',
                        onTap: () => setState(() => queue.add(i)),
                      ),
                    ],
                  ),
                ],
              ),
            ],
          ),
          const Text(
            'Orchard Sessions',
            style: TextStyle(fontSize: 13, color: CupertinoColors.systemGrey),
          ),
        ],
      );
}

class AlbumArt extends StatelessWidget {
  const AlbumArt({super.key, required this.index});
  final int index;
  @override
  Widget build(BuildContext context) => ClipRRect(
    borderRadius: BorderRadius.circular(8),
    child: DecoratedBox(
      decoration: BoxDecoration(
        gradient: LinearGradient(
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
          colors: [Color.lerp(colors[index], Colors.white, .5)!, colors[index]],
        ),
      ),
      child: LayoutBuilder(
        builder: (context, size) => Stack(
          children: [
            Positioned(
              left: size.maxWidth * .2,
              top: size.maxHeight * .2,
              child: Container(
                width: size.maxWidth * .6,
                height: size.maxWidth * .6,
                decoration: BoxDecoration(
                  shape: BoxShape.circle,
                  border: Border.all(color: Colors.white.withValues(alpha: .3)),
                ),
              ),
            ),
            if (size.maxWidth > 80)
              const Positioned(
                left: 14,
                top: 14,
                child: Text(
                  'ORCHARD',
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 8,
                    letterSpacing: 2,
                  ),
                ),
              ),
            if (size.maxWidth > 80)
              Positioned(
                left: 14,
                bottom: 14,
                child: Text(
                  names[index],
                  style: const TextStyle(
                    color: Colors.white,
                    fontSize: 25,
                    fontWeight: FontWeight.w300,
                  ),
                ),
              ),
          ],
        ),
      ),
    ),
  );
}
