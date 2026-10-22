# 接入指南

先读目标项目的 AGENTS、构建配置和现有主题。不要仅为采用本库而更换项目技术栈。固定版本/commit 后接入，先迁移一个代表页面；安装包身份、存储与业务能力保持兼容。

## 通用接入步骤

1. 选择一套主题，记录要迁移的页面与状态，参考 [简报模板](../templates/PROJECT_UI_BRIEF.md)。
2. 把仓库作为私有依赖或固定提交的 submodule / vendor 引入；已有云端 GitHub 身份通过平台配置使用，不把 token 写进 URL。
3. 选适配层并应用 Theme；项目级定制集中写入自己的 theme/skin，不到处写硬编码色值。
4. 用共享组件替换代表页面，连上项目已有状态与保存逻辑。
5. 验证浅深色、窄屏/大字号、失败、返回与草稿后，再扩展到更多页面。

```bash
# 在目标项目内，仅当该路径不存在且该项目已授权接入时执行
git submodule add https://github.com/h516df/orchard-ui.git vendor/orchard-ui
```

父仓库记录 submodule 的精确提交。云端拉取需 `git submodule update --init --recursive` 和本私有库读取权限。GitHub Actions 的当前仓库 GITHUB_TOKEN 通常不能读另一个私有仓库，需配置有范围的 GitHub App 或专用只读 Secret；本库不会自动配置凭据或增权。也可审阅后只复制所需适配代码与 NOTICE，并记录来源 SHA。

## Kotlin / Compose

验证基线：AGP 9.0.1、Kotlin Compose 插件 2.3.20、Gradle 9.1.0、Compose BOM 2026.03.01、JDK 21、compileSdk 36；库 minSdk 24。AGP 9 使用内建 Kotlin，不重复应用 kotlin-android。目标项目版本不同应先评估兼容性，不直接升级业务工程。

**选项 A：本地 Maven**，适合一次构建/当前开发环境：

```bash
# Orchard 仓库内
./gradlew :orchard:publishReleasePublicationToMavenLocal
```

目标工程的 repositories 添加 `mavenLocal()`，模块依赖 `implementation("dev.h516.orchard:orchard:0.4.0")`。这是本地缓存，不是已上传 Maven Central 或 GitHub Packages；换机器需重建。

**选项 B：固定源码的 composite build**，适合版本相容的长期接入：

```kotlin
// settings.gradle.kts；路径按项目实际位置调整
includeBuild("vendor/orchard-ui") {
    dependencySubstitution {
        substitute(module("dev.h516.orchard:orchard")).using(project(":orchard"))
    }
}
```

```kotlin
// app/build.gradle.kts
dependencies { implementation("dev.h516.orchard:orchard:0.4.0") }

// 界面入口，存储与回调继续用原项目实现
OrchardTheme(palette = OrchardPalette.Sage) {
    OrchardSettingsScreen {
        OrchardSection("显示") {
            OrchardSwitchRow("触感反馈", haptics, onHapticsChanged)
        }
    }
}
```

宿主 Activity 负责 edge-to-edge、系统栏明暗图标与权限；主题组件不会全局更改窗口。库对系统动画缩放做监听，也允许传入 `reducedMotion`。

## Flutter

验证 Flutter 3.41.5 / Dart 3.11.3。使用固定私有 Git 提交，或父项目管理的 path 依赖：

```yaml
dependencies:
  orchard_ui:
    path: vendor/orchard-ui/adapters/flutter
```

```dart
import 'package:orchard_ui/orchard_ui.dart';

MaterialApp(
  theme: orchardThemeData(palette: OrchardPalette.sage),
  darkTheme: orchardThemeData(palette: OrchardPalette.sage, brightness: Brightness.dark),
  themeMode: ThemeMode.system,
  home: OrchardSettingsScreen(children: [
    OrchardSection(title: '显示', children: [
      OrchardSwitchRow(title: '触感反馈', value: haptics, onChanged: onHapticsChanged),
    ]),
  ]),
);
```

`MaterialApp` / Navigator 保留真实平台设置，不把整个安卓工程强制伪装为其他平台。视觉默认使用Cupertino按钮、开关、输入、分段和确认；不能用.adaptive()把安卓外形带回来。系统返回、路由与权限仍按实际平台处理。外部 controller 由宿主 dispose，保存状态与草稿落盘由宿主维护。完整可复制示例见 [gallery.dart](../adapters/flutter/example/gallery.dart)。

## React Native

验证 React Native 0.83.2、React 19.2.0、TypeScript 5.9.3。库输出 TSX 源码，交给 RN/Metro 编译。它不是 Expo 项目脚手架。

```bash
# 也可 npm pack 后把私有 tgz 安装到目标工程；不要 npm publish
npm install ./vendor/orchard-ui/adapters/react-native
```

使用 `file:` / symlink 源依赖时，Metro 必须能看到 vendor 的源码及宿主的依赖。不要把适配层自己的开发 node_modules 一并 vendor 进去，避免重复 React。源码位于项目根外时配置 `watchFolders` 与合适的 `resolver.nodeModulesPaths`；具体路径按现有 Metro/Expo 版本配置，并核对官方文档。

```tsx
import {OrchardProvider, OrchardSection, OrchardSwitchRow} from '@h516df/orchard-ui';

<OrchardProvider palette="sage" mode="system">
  <OrchardSection title="显示">
    <OrchardSwitchRow title="触感反馈" value={haptics} onValueChange={onHapticsChanged} />
  </OrchardSection>
</OrchardProvider>
```

宿主负责 SafeAreaProvider/安全区、系统栏、IME、路由和本地偏好。0.2确认调用改为Provider内的`useOrchardConfirm()`或受控`OrchardConfirmDialog`，不再使用默认Android Alert。`OrchardTabBar`可直接使用；0.4默认接入原生材质，New Architecture/Fabric为前提。`renderBackdrop`仍可覆盖后端，降低透明度优先实色。编辑模板拦截 Android hardware back 与自己的返回按钮；React Navigation 的 header、beforeRemove、手势离开等必须统一连到草稿保护规则。示例见 [Gallery.tsx](../adapters/react-native/example/Gallery.tsx)。

## Web / React / Vue / 混合容器

见 [Web 适配说明](../adapters/web/README.md)。只要求语义 HTML，不强制选择框架。原生 dialog、checkbox、radio 优先；样式本身不会处理保存、路由、权限或 Android WebView 返回。

## 避免后续到处改

项目中保存一份短的 UI 简报和设计取舍。共享变化先改 tokens/公共适配层，版本升级由项目逐个评估；产品专属页面留在产品仓库。不要通过一个库的更新自动重写所有 App。


## 采用完整 App 页面模式

先看 [原版对照](APPLE_APP_REFERENCES.md)，再选择骨架与组件。Compose示例在 `ReferenceApp.kt`；Flutter [NotesReference](../adapters/flutter/example/notes_reference.dart)、RN [NotesReference](../adapters/react-native/example/NotesReference.tsx) 可作为宿主接线起点。Web [交互参考](../examples/web-reference/index.html) 使用共享CSS，未绑定React/Vue。

`OrchardAppPage`自带滚动容器，不放在另一个竖向ScrollView内。底部搜索/播放器覆盖内容时保留相应的滚动底部空间；RN显式传宿主安全区insets和图标，Flutter/RN的路由/存储/播放器继续用原项目能力。Compose `OrchardDestination.selectedIcon` 可提供单独选中图标。新参考页面没有用户数据库迁移需求。

新版玻璃设置、引擎条件和完整可运行宿主见 [Liquid Glass](LIQUID_GLASS.md)。
