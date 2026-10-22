# Orchard · Flutter Liquid Glass 音乐参考

可独立运行的Android/Web宿主，依赖本仓库Flutter适配层与固定版本liquid_glass_widgets1.14.0。原创合成专辑；受控播放/待播演示，不含音频引擎或用户媒体。

```bash
flutter pub get
flutter run
flutter test
flutter build apk --debug
flutter build web --no-web-resources-cdn
```

默认音乐资料库，可切换主页/广播/搜索。底栏随滚动收起，播放器附件融合；专辑省略号打开锚点/子菜单，加入待播后“下一首”消费队列；点击迷你播放器标题打开半屏Sheet，可拖动展开。右上角调整主题、玻璃着色与辅助功能。

Android使用Impeller时可启用premium引擎；Web/Skia自动降级到近似材质。高级玻璃只放导航与弹层，滚动正文保持实色。原生构建与运行验证分开，真实设备表现仍需验收；细节见 [材质说明](../../docs/LIQUID_GLASS.md)。

这是独立演示包`dev.h516.orchard_liquid`，不覆盖现有App；调试APK仅供本机试用，不作为商店生产签名发布。
