# Orchard UI · 果园

**给 h516df 的跨项目 UI / UX 复用库。苹果风视觉，安卓友好的交互，不绑定技术栈。私有仓库。**

**0.3 改为按苹果 App 的完整页面重做：设置、备忘录、音乐。** [查看新版实际预览](docs/previews/README.md) · [原版对照与实现范围](docs/APPLE_APP_REFERENCES.md)

不是每个新项目重新调一遍圆角、列表和保存流程。把共同的视觉变量、组件与体验规则集中在这里，项目保留自己的产品性格和数据逻辑。

当前版本校正：0.3示例混用了旧版参考，未达到最新iOS27的完整视觉/交互目标。[最新iOS与GitHub候选核对](docs/research/2026-10-22-ios27.md)列出正式版本、现有缺项和多框架候选；这些依赖尚未集成。

## 从这里开始

| 你要做什么 | 入口 |
| --- | --- |
| 让一个新项目沿用这套风格 | [接入指南](docs/INTEGRATION.md) · [给开发助手的规则](templates/AGENTS.fragment.md) |
| 决定用什么技术或成熟组件 | [技术与资源选型](docs/STACK_CHOICES.md) |
| 调整配色、字号、间距和材质 | [视觉基础](docs/FOUNDATIONS.md) · [共享变量](tokens/orchard.json) |
| 找可复用组件及实际覆盖范围 | [组件目录](docs/COMPONENTS.md) |
| 设计编辑、导入、搜索、阅读等流程 | [UX 模式](docs/UX_PATTERNS.md) · [页面配方](docs/SCREEN_RECIPES.md) |
| 检查界面、交互和无障碍 | [质量标准](docs/QUALITY.md) · [审查模板](templates/UI_REVIEW.md) |
| 给现有项目逐步换界面 | [项目观察与迁移建议](docs/PROJECT_AUDIT.md) |
| 接续这个仓库的开发 | [AGENTS](AGENTS.md) · [交接](HANDOFF.md) · [验证记录](docs/VERIFICATION.md) |

## 已有代码

- **共享设计变量**：4 套配色 × 深浅色，语义色、字阶、间距、圆角、触控尺寸、动效时间。JSON 为源，生成 Kotlin、Dart、TypeScript、CSS；有同步和对比度检查。
- **Compose 适配层**：[代码](adapters/compose/orchard/src/main/java/dev/h516/orchard)；主题、常用控件、列表/分组/状态、导航、弹层，以及列表、设置、草稿编辑、导入预览和阅读模式。
- **Flutter 适配层**：[代码](adapters/flutter/lib)；主题和常用组件、设置页、草稿编辑、导入预览，默认使用 Cupertino 控件。
- **React Native 适配层**：[代码](adapters/react-native/src)；主题 Provider、常用组件、弹层和草稿编辑，使用 React Native primitives。
- **Web 适配层**：[样式与接入](adapters/web/README.md)；语义 HTML + CSS，可用于 React、Vue、Svelte 或混合容器。交互由宿主负责。
- **框架无关交互参考**：[Web 三页示例](examples/web-reference)，也能构建离线单文件预览；无公开部署。
- **安卓展示 App**：[Compose Catalog](examples/compose-catalog)；默认设置页，可切换备忘录列表/编辑、音乐资料库与组件目录。包名独立，不接触现有 App 数据。
- **开发资料**：UX 状态规则、页面配方、选型边界、审查表、项目接入模板、来源与许可说明。

各适配层的覆盖范围不同，详见[组件目录](docs/COMPONENTS.md)。这不是四个完整应用框架，也不包含业务数据库、文件解析、媒体引擎或更新下载器。

## 改一处主题，让各技术栈沿用

```bash
# 编辑 tokens/orchard.json 后
python3 tools/generate_tokens.py
python3 tools/generate_tokens.py --check
python3 -m unittest discover -s tools/tests -v
```

配色：清透蓝 `blue`、纸感绿 `sage`、暖杏色 `peach`、墨色紫 `ink`。项目可以选择不同主题；共用语义，不要求所有 App 长得一样。

## 运行和验证

```bash
# Android：JDK 21（需要 javac）、SDK 36
bash cloud/setup.sh
source .cloud/env.sh
./gradlew :orchard:assembleDebug :catalog:assembleDebug :catalog:testDebugUnitTest :orchard:lintDebug :catalog:lintDebug --max-workers=2

# Flutter：固定验证版本 3.41.5
cd adapters/flutter
flutter pub get
flutter analyze
flutter test

# React Native：源码 / 类型 / 单元验证，未声称原生 APK 验收
cd adapters/react-native
npm ci
npm run typecheck
npm test
```

Web：仓库根目录 `npm ci`、`npx playwright install chromium`、`npm run test:web`。已有系统 Chromium 可设置 `CHROMIUM_PATH=/path/to/chromium`。各组命令从各自目录执行，完整环境和验证边界见 [CLOUD_DEVELOPMENT](CLOUD_DEVELOPMENT.md)。

展示 APK 为 `examples/compose-catalog/build/outputs/apk/debug/catalog-debug.apk`，是独立样本，不是已有项目的升级包。没有发布公开站点或公开软件包。

## 使用原则

先理解产品，再选页面配方。视觉以苹果控件为基线，系统返回、键盘与权限保留安卓行为。界面换肤时保留原应用 ID、签名、数据库、备份和现有功能。源码私有；不附带 Apple 字体、SF Symbols 或用户数据。依赖许可见 [THIRD_PARTY_NOTICES](THIRD_PARTY_NOTICES.md)。
