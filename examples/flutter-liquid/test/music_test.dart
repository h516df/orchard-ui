import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:orchard_liquid/main.dart';

void main() {
  testWidgets('music playback controls and player sheet remain connected', (
    tester,
  ) async {
    tester.view.physicalSize = const Size(393, 852);
    tester.view.devicePixelRatio = 1;
    addTearDown(tester.view.resetPhysicalSize);
    addTearDown(tester.view.resetDevicePixelRatio);
    await tester.pumpWidget(const OrchardLiquidDemo());
    await tester.pumpAndSettle();
    expect(tester.takeException(), isNull);
    final album = find.byWidgetPredicate(
      (w) => w is Semantics && w.properties.label == '播放微风',
    );
    await tester.tap(album);
    await tester.pumpAndSettle();
    expect(find.bySemanticsLabel('暂停'), findsOneWidget);
    await tester.tap(find.bySemanticsLabel('下一首'));
    await tester.pumpAndSettle();
    expect(find.text('慢一点'), findsWidgets);
    await tester.tap(find.text('慢一点').last);
    await tester.pumpAndSettle();
    expect(find.text('正在播放'), findsOneWidget);
    expect(tester.takeException(), isNull);
  });
  testWidgets(
    'narrow screen with large text keeps navigation and artwork usable',
    (tester) async {
      tester.view.physicalSize = const Size(320, 1000);
      tester.view.devicePixelRatio = 1;
      tester.platformDispatcher.textScaleFactorTestValue = 2;
      addTearDown(tester.view.resetPhysicalSize);
      addTearDown(tester.view.resetDevicePixelRatio);
      addTearDown(tester.platformDispatcher.clearTextScaleFactorTestValue);
      await tester.pumpWidget(const OrchardLiquidDemo());
      await tester.pumpAndSettle();
      expect(tester.takeException(), isNull);
      expect(find.bySemanticsLabel('播放'), findsOneWidget);
    },
  );
  testWidgets(
    'anchored submenu queues a track and Escape dismisses the overlay',
    (tester) async {
      tester.view.physicalSize = const Size(393, 852);
      tester.view.devicePixelRatio = 1;
      addTearDown(tester.view.resetPhysicalSize);
      addTearDown(tester.view.resetDevicePixelRatio);
      await tester.pumpWidget(const OrchardLiquidDemo());
      await tester.pumpAndSettle();
      await tester.tap(find.bySemanticsLabel('微风的操作'));
      await tester.pumpAndSettle();
      expect(find.text('添加到待播'), findsOneWidget);
      await tester.sendKeyEvent(LogicalKeyboardKey.escape);
      await tester.pumpAndSettle();
      expect(find.text('添加到待播'), findsNothing);
      await tester.tap(find.bySemanticsLabel('微风的操作'));
      await tester.pumpAndSettle();
      await tester.tap(find.text('添加到待播'));
      await tester.pumpAndSettle();
      await tester.tap(find.text('插播下一首'));
      await tester.pumpAndSettle();
      await tester.tap(find.bySemanticsLabel('下一首'));
      await tester.pumpAndSettle();
      expect(find.text('微风'), findsWidgets);
      expect(tester.takeException(), isNull);
    },
  );
}
