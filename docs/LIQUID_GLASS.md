# 0.4 · Liquid Glass 材质与动态流程

本批承接 [2026-10-22研究](research/2026-10-22-ios27.md)，按当日Apple正式基线iOS27.0.1补足连续着色与功能层动态。设计目标是Apple官方App的布局和交互；这些跨平台实现是独立引擎，尚无用户视觉认可，也不等同于UIKit全部系统能力。

## 实际采用与运行边界

| 适配 | 已安装版本 | 本批实现 | 回退/限制 |
| --- | --- | --- | --- |
| Compose | `io.github.kyant0:backdrop:1.0.6`，Apache-2.0 | 独立内容层采样，透镜折射/色散/模糊/着色；移动选择底片与拖动提交/取消；滚动收起底栏；锚点子菜单；半屏/展开Sheet | API33+折射、31–32模糊、24–30实色。大字号或降低透明度实色。菜单是高可读实色；Sheet没有共享几何形变/玻璃融合 |
| Flutter | `liquid_glass_widgets:1.14.0`，MIT | ios27Light/Dark配置，AdaptiveGlass；GlassScaffold隔离滚动源；可收起底栏/附件融合、锚点子菜单、从触发器形变的双高度Sheet | premium完整着色器依赖Impeller；Web/Skia走标准近似。禁止在滚动内容内部堆premium玻璃。平台高对比度与手动降低透明度合并；默认组件未设置Scope时使用standard |
| React Native | `@sbaiahmed1/react-native-blur:6.0.2`，MIT | LiquidGlassView默认后端，连续着色/减少动态效果/实色回退，滚动收起回调、TabBar宽度和选择底片动画 | RN0.80+ New Architecture/Fabric；本库验证RN0.83.2类型。Android33+折射，较老OS blur。Android下采样软件背景捕获有成本；Android没有原生容器融合。本批无原生宿主/设备运行证据 |
| Web | 原生CSS/JS，无额外运行库 | 连续着色，方向阈值收起底栏，Popover锚点/键盘/子菜单，双高度播放器与开启缩放近似 | backdrop-filter是近似材质，不包含光学折射；需现代浏览器Popover支持。无Apple专有引擎或系统手势 |

选择Compose1.0.6的依据来自实际Maven POM/AAR：该版本Kotlin2.3.10、Compose1.10.3、minCompileSdk36，适配当前Kotlin2.3.20/SDK36。2.0.1要求Kotlin2.4.10、Compose Multiplatform1.12、SDK37；2.0.0也要求SDK37。本批保留已构建的工具链，使用兼容版本的真实lens能力，不把旧引擎版本冒充最新版包。

导航/播放器覆盖在内容层之上。Compose在捕获层内清空背景采样上下文，避免玻璃录到自己造成循环；Flutter让GlassScaffold负责隔离，premium组件仅放在导航/弹层。内容卡片和正文保持实色。低性能/视频/地图等场景由宿主再评估质量，不应为整个页面添加玻璃。

## 接入

共享 `tokens/orchard.json` 的 `glass` 组是默认值唯一来源；着色45表示45%。各框架公开参数统一接受0..1，非有限值回到默认，越界值钳制。参数不是系统设置，不会改变Android/iOS全局外观。

Compose：

```kotlin
OrchardTheme(glassTint = .6f, reducedTransparency = opaque, reducedMotion = reduce) {
    val scroll = rememberScrollState()
    val collapsed = rememberOrchardDockCollapsed(scroll)
    OrchardAppPage("资料库", onMore = {}, scroll = scroll, bottomBar = {
        OrchardTabBar(destinations, selected, onSelect, compact = collapsed)
    }) { /* opaque content */ }
}
```

`OrchardMenu(expanded, onDismiss, actions) { trigger() }`依附触发器定位；`OrchardMenuAction(children=...)`进入子菜单，叶子动作关闭后调用宿主回调。外部点击/系统返回可关闭；大字号底栏保留标签。拖动选择在抬手时提交，手势取消保持原选择。

Flutter：

```dart
WidgetsFlutterBinding.ensureInitialized();
await initializeOrchardGlass();
runApp(OrchardGlassApp(child: OrchardGlassScope(
  tint: .6,
  child: MaterialApp(home: MyPage()),
)));
```

在MaterialApp中提供Material/Scaffold默认文字样式。可运行的 [Flutter音乐宿主](../examples/flutter-liquid) 包含初始化、主题/外观、内容/导航隔离、列表、搜索、待播队列、播放器、弹层滚动控制器接线。不要从Web截图推断Impeller视觉或帧率。

RN：

```tsx
<OrchardProvider glassTint={.6} reducedTransparency={opaque} reduceMotion={reduce}>
  <OrchardAppPage title="资料库" onMore={openMenu}
    onDockCollapsedChange={setCompact}
    bottomBar={<OrchardTabBar compact={compact} destinations={tabs}
      selected={selected} onSelect={setSelected} />}>
    {content}
  </OrchardAppPage>
</OrchardProvider>
```

安装依赖后重新构建Fabric宿主；iOS宿主执行Pods安装。`renderBackdrop`仍可自定义采样后端，但辅助功能的实色回退优先。RN的菜单/Sheet仍是旧受控接口，本批未提供Apple式共享形变，不能把Flutter库具备的特性写成RN也已实现。

Web：

```js
import {setOrchardGlassTint, OrchardDockController, showOrchardMenu}
  from './adapters/web/orchard.js';
setOrchardGlassTint(.6);
const dock = new OrchardDockController(document.querySelector('.o-media-dock'));
// 组件卸载/替换DOM前调用 dock.destroy()。
```

动态收起使用方向累积阈值48/20，避免在一条分界线上反复闪动；键盘聚焦底栏会展开。更多路径见 [参考宿主](../examples/web-reference/reference.js)。减少动态效果关闭CSS位移/缩放和开启形变；弹层手柄支持拖动及点击/键盘切换高度。

## 验证与未实现

最终源码/命令/产物归属见 [验证记录](VERIFICATION.md) 和 [预览清单](previews/README.md)。宿主渲染、Widget/Renderer/Chromium检查与Android构建证明的内容不同；没有真实设备的IME、TalkBack、预测性返回、帧率/功耗、Impeller光学效果与跨厂商GPU验收。音乐仅受控UI/本地演示状态，实际音频播放接宿主引擎。

View/XML适配、Compose共享玻璃融合、RN形变菜单/Sheet、交互式跨页返回、iPhone Duo竖向工具栏与完整系统工具集仍未实现；研究候选与实际采用分开保留。没有改写其他App或公开部署。
