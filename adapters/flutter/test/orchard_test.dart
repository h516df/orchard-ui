import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:orchard_ui/orchard_ui.dart';

Widget app(
  Widget child, {
  Brightness brightness = Brightness.light,
  double scale = 1,
}) => MaterialApp(
  theme: orchardThemeData(brightness: brightness),
  home: MediaQuery(
    data: MediaQueryData(textScaler: TextScaler.linear(scale)),
    child: Scaffold(
      body: SafeArea(child: SingleChildScrollView(child: child)),
    ),
  ),
);

void main() {
  testWidgets(
    'busy button prevents duplicate submission and announces progress',
    (tester) async {
      var count = 0;
      await tester.pumpWidget(
        app(
          OrchardButton(label: '保存', loading: true, onPressed: () => count++),
        ),
      );
      await tester.tap(find.text('保存 · 正在处理'));
      expect(count, 0);
      expect(
        tester.widget<FilledButton>(find.byType(FilledButton)).onPressed,
        isNull,
      );
      await tester.pumpWidget(
        app(OrchardButton(label: '保存', onPressed: () => count++)),
      );
      await tester.tap(find.text('保存'));
      expect(count, 1);
    },
  );

  testWidgets('selection and switch update through their real controls', (
    tester,
  ) async {
    var selected = '全部';
    var enabled = false;
    await tester.pumpWidget(
      app(
        StatefulBuilder(
          builder: (context, update) => Column(
            children: [
              OrchardSegments(
                options: const ['全部', '收藏'],
                selected: selected,
                label: (v) => v,
                onSelected: (v) => update(() => selected = v),
              ),
              OrchardSwitchRow(
                title: '触感反馈',
                value: enabled,
                onChanged: (v) => update(() => enabled = v),
              ),
            ],
          ),
        ),
      ),
    );
    await tester.tap(find.text('收藏'));
    await tester.pump();
    expect(selected, '收藏');
    await tester.tap(find.byType(Switch));
    await tester.pump();
    expect(enabled, isTrue);
  });

  testWidgets('save failure preserves editor text and retry completes', (
    tester,
  ) async {
    final controller = TextEditingController(text: '未保存的输入');
    addTearDown(controller.dispose);
    var attempts = 0;
    await tester.pumpWidget(
      MaterialApp(
        theme: orchardThemeData(),
        home: OrchardEditorScreen(
          controller: controller,
          dirty: true,
          onSave: () async {
            attempts++;
            if (attempts == 1) throw Exception('synthetic');
          },
          onClose: () {},
        ),
      ),
    );
    await tester.tap(find.text('保存'));
    await tester.pumpAndSettle();
    expect(find.text('还没有保存'), findsOneWidget);
    expect(controller.text, '未保存的输入');
    await tester.tap(find.text('保存'));
    await tester.pumpAndSettle();
    expect(attempts, 2);
    expect(find.text('还没有保存'), findsNothing);
  });

  testWidgets('dirty editor can continue editing after back confirmation', (
    tester,
  ) async {
    final controller = TextEditingController(text: '草稿');
    addTearDown(controller.dispose);
    var closed = false;
    await tester.pumpWidget(
      MaterialApp(
        theme: orchardThemeData(),
        home: OrchardEditorScreen(
          controller: controller,
          dirty: true,
          onSave: () async {},
          onClose: () {
            closed = true;
          },
        ),
      ),
    );
    await tester.tap(find.byTooltip('返回'));
    await tester.pumpAndSettle();
    expect(find.text('放弃修改？'), findsOneWidget);
    await tester.tap(find.text('继续编辑'));
    await tester.pumpAndSettle();
    expect(closed, isFalse);
    expect(controller.text, '草稿');
    await tester.tap(find.byTooltip('返回'));
    await tester.pumpAndSettle();
    await tester.tap(find.text('放弃修改'));
    await tester.pumpAndSettle();
    expect(closed, isTrue);
  });

  testWidgets(
    'narrow screen and 200 percent text have no overflow in all palettes',
    (tester) async {
      tester.view.physicalSize = const Size(320, 1100);
      tester.view.devicePixelRatio = 1;
      addTearDown(tester.view.resetPhysicalSize);
      addTearDown(tester.view.resetDevicePixelRatio);
      for (final palette in OrchardPalette.values) {
        for (final brightness in Brightness.values) {
          await tester.pumpWidget(
            MaterialApp(
              theme: orchardThemeData(palette: palette, brightness: brightness),
              home: MediaQuery(
                data: const MediaQueryData(textScaler: TextScaler.linear(2)),
                child: Scaffold(
                  body: SingleChildScrollView(
                    child: Padding(
                      padding: const EdgeInsets.all(20),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.stretch,
                        children: [
                          const OrchardLargeTitle(
                            title: '留一点时间给喜欢的事',
                            subtitle: '不用填满，留下此刻就好。',
                          ),
                          OrchardSection(
                            title: '偏好设置',
                            children: [
                              OrchardListRow(
                                title: '导出完整资料',
                                subtitle: '包含所有记录与附件，长文字可以换行',
                                value: '合成数据',
                                onTap: () {},
                              ),
                            ],
                          ),
                          const SizedBox(height: 16),
                          OrchardButton(label: '保存这一段很长的文字', onPressed: () {}),
                          const OrchardBanner(
                            title: '内容已保留',
                            message: '暂时无法完成操作，稍后可以继续重试。',
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
              ),
            ),
          );
          await tester.pumpAndSettle();
          expect(
            tester.takeException(),
            isNull,
            reason: '${palette.name} $brightness',
          );
        }
      }
    },
  );
}
