# 来源与许可

本库保持私有，原创设计变量、组件组合、示例和文档供 h516df 的项目复用。没有把仓库改为开源，也没有替第三方依赖更改许可。未附加对外通用开源授权；如未来对外分发，应另行明确原创部分许可并保留依赖要求。

## 实际使用

| 来源 | 用途 | 许可/说明 |
| --- | --- | --- |
| [Gradle 9.1.0](https://github.com/gradle/gradle/tree/v9.1.0) | Gradle Wrapper 与构建工具 | Apache-2.0；[附带许可](third_party/gradle-LICENSE.txt)，脚本原版权头保留；分发校验值固定 |
| [AndroidX / Jetpack Compose](https://android.googlesource.com/platform/frameworks/support/) | Compose 组件、Activity 与测试接口 | Apache-2.0；依赖按 Gradle 固定版本解析 |
| [Kotlin](https://github.com/JetBrains/kotlin) | Kotlin/Compose 编译插件 | Apache-2.0 |
| [Flutter](https://github.com/flutter/flutter/tree/3.41.5) | Flutter SDK 的官方 widgets、Material 图标与测试 | Flutter BSD-3-Clause；SDK 内各资源保留各自声明，未单独拷贝资源文件入库 |
| [React Native](https://github.com/facebook/react-native/tree/v0.83.2) / [React](https://github.com/facebook/react) | RN 组件与测试 | MIT；通过 npm 依赖引用 |
| [Playwright](https://github.com/microsoft/playwright) | Web 交互测试 | Apache-2.0；仅开发依赖 |
| [Robolectric](https://github.com/robolectric/robolectric) / [Roborazzi](https://github.com/takahirom/roborazzi) | Compose 宿主测试与画面输出 | MIT；仅测试依赖 |
| [vibe-ui-quality-kit](https://github.com/h516df/vibe-ui-quality-kit) | 已授权的用户通用工作流与云端 SDK 初始化流程 | 固定采用 main `e548b9c66075989847c0e528cd37031f438b1400`；保留来源，未运行恢复/安装器 |

Gradle Wrapper 从用户 xiayizhang 的同版 Wrapper 复用，分发 URL 与 SHA256 保留。组件实现没有复制用户 App 的业务代码、数据库、签名或真实资料。

## 设计与规范参考

[Apple HIG](https://developer.apple.com/design/human-interface-guidelines)、[Android Compose accessibility](https://developer.android.com/develop/ui/compose/accessibility/api-defaults)、[Flutter 平台适配](https://docs.flutter.dev/ui/adaptive-responsive/platform-adaptations)、[React Native accessibility 0.83](https://reactnative.dev/docs/0.83/accessibility)。参考原则和 API，未复制品牌素材或大段规范正文。

没有捆绑 San Francisco 字体、SF Symbols、Apple 官方图标、产品截图或真实用户内容。Compose 线图标是原创几何路径。`docs/previews` 是本库自己的合成演示宿主渲染图。其他候选库仅列在选型文档，未因列名而宣称已集成或核验所有版本。
