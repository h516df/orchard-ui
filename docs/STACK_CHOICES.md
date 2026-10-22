# 技术与成熟能力选型

先看项目现状、团队熟悉度与功能约束，再选技术。苹果风是设计方向，不是选择 Kotlin、Flutter、RN 或 Web 的理由。以下引用用于选型，除“已依赖”外均未自动集成；采用具体版本前核对维护状态、许可证、包体、系统版本和测试。

## 最新苹果视觉后端

见[2026-10-22核对](research/2026-10-22-ios27.md)：Flutter liquid_glass_widgets、Compose Backdrop、Android View Liquid-Glass-Android、RN react-native-blur，以及Web实验候选。区分真实折射与blur回退，固定发布版本/源码提交；0.4实际采用Compose1.0.6/Flutter1.14.0/RN6.0.2，见[接入与兼容说明](LIQUID_GLASS.md)。Flutter内置Cupertino或传统compose-cupertino不自动代表最新iOS27材质与交互。

## 框架

| 场景 | 可选路径 | 适配与边界 |
| --- | --- | --- |
| 现有原生安卓 App | 保留现有 View / Compose | 用共享 tokens 与 UX 规则；不为了风格重写数据与业务。Compose 适配层仅用于已有或选定 Compose 的页面 |
| 新项目希望以后多平台 | Flutter 或 React Native | 原生能力、插件兼容、包体、状态管理与团队能力共同决定。先跑一个有键盘/导入/返回的代表场景 |
| 快速验证产品或已有 Web 工程 | React / Vue / Svelte + 可选容器 | 共享 CSS/语义；WebView 的媒体、后台、系统返回与文件能力必须单独评估 |
| 阅读、媒体、复杂文本 | 框架 + 专门引擎 | 框架的 Text/SelectableText 不等于 EPUB/PDF 引擎或富文本编辑器 |

## 优先复用的成熟能力

| 需求 | 官方/成熟候选 | 本库如何搭配 |
| --- | --- | --- |
| 安卓主题、控件、语义与状态 | [Jetpack Compose / Material 3](https://developer.android.com/develop/ui/compose/designsystems/material3) | 已依赖；基础主题/布局与语义可复用；可见控件按Orchard Apple基线绘制，不沿用默认Material外观 |
| Flutter 组件与平台适配 | [Flutter widgets](https://docs.flutter.dev/ui/widgets) · [自动平台适配](https://docs.flutter.dev/ui/adaptive-responsive/platform-adaptations) | 已依赖 SDK；系统行为保留，视觉可定制；0.2默认使用固定SDK内置Cupertino控件，不把平台全局硬改 |
| RN 交互、原生开关与无障碍 | [React Native components](https://reactnative.dev/docs/components-and-apis) · [Accessibility](https://reactnative.dev/docs/0.83/accessibility) | 已依赖；路由和安全区由宿主选型 |
| 导航 | [Android Navigation](https://developer.android.com/guide/navigation) / [Flutter Navigator](https://docs.flutter.dev/ui/navigation) / [React Navigation](https://reactnavigation.org/docs/getting-started) | 候选；接草稿保护与状态恢复，不在库里强制路由方案 |
| 视频和音频 | [Android Media3](https://developer.android.com/media/media3) / [Flutter video_player](https://pub.dev/packages/video_player) | 候选；现有记一下已有 Media3，优先沿用；明确后台、缓存与资源释放 |
| 图片加载 | [Coil](https://coil-kt.github.io/coil/) / [Flutter image widgets](https://api.flutter.dev/flutter/widgets/Image-class.html) / [RN Image](https://reactnative.dev/docs/image) | 候选；缩略图与原件分开，控制内存和占位，不盲目复制大图 |
| EPUB 阅读 | [Readium Kotlin Toolkit](https://github.com/readium/kotlin-toolkit) | 下一章相关 PR 已有引擎工作，接入时读该分支，不用本库的 Text 组件替换成熟引擎 |
| 安卓应用更新 | [h516df/android-update-kit](https://github.com/h516df/android-update-kit) | 用户已有私有库；当前仅作为复用入口，不搬签名、凭据或业务版本配置 |
| 通用线性图标 | [Lucide](https://lucide.dev/) / 平台官方图标 | 候选；按锁定版本确认 ISC/其他适用许可。不要直接搬 Apple SF Symbols 作为 Android 素材 |
| 日期、文件、权限与分享 | 各平台系统控件 / 官方文档 | 让系统完成它擅长的部分，减少权限与自制选择器 |
| 图表、地图、富文本 | 根据具体需求再选 | 首版未选定，不为凑数量安装全部依赖 |

## 选型记录需要回答

它解决了哪一个具体问题？现有项目是否已有实现？需要什么系统版本和权限？许可是否允许当前分发？维护与 Android 兼容情况怎样？代价是包体、性能、锁定还是迁移？如何用一个最小但真实的流程验证？

用 [DECISION_LOG](../templates/DECISION_LOG.md) 留下结论和固定版本，避免下次只看一个截图又重新换库。
