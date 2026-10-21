# 验证记录 · 0.1.0

日期：2026-10-21。源码为本库首版工作树，验证后提交；准确 GitHub 来源提交由 HANDOFF / PR 远端回执绑定。这里没有引用用户其他 App 的历史通过结果。

## 本轮已运行

| 范围 | 命令与环境 | 结果与含义 |
| --- | --- | --- |
| 共享变量 | `python3 -m unittest discover -s tools/tests -v` | passed，3 项：8 个色彩方案常用文字/边界对比度、基础触控/字号、生成文件同步 |
| Compose 编译与 APK | `./gradlew :orchard:assembleDebug :catalog:assembleDebug --max-workers=2`，JDK21、SDK36 | passed；实际生成组件 AAR 与独立展示 APK |
| Compose UI | `./gradlew :catalog:testDebugUnitTest --max-workers=2`，Robolectric4.17/API35 Native graphics、Roborazzi1.76.0 | passed，9 项：忙碌按钮、开关/选择、失败保存/重试、Android dispatcher 返回保护、常规浅深色/主题和320dp/200%字号渲染；5张实际输出图已目检 |
| Android lint | `./gradlew :orchard:lintDebug :catalog:lintDebug` | passed，无错误；保留依赖更新等提示，静态检查不等于设备验收 |
| Flutter | `flutter analyze` / `flutter test`，Flutter3.41.5、Dart3.11.3 | passed，静态分析无问题，5项 widget test；包含保存失败/重试、返回确认、选择、加载防重与全部主题的窄屏/200%字号无布局异常 |
| React Native | `npm run typecheck` / `npm test`，RN0.83.2、React19.2、TS5.9 | passed，5项 renderer/unit test；包含状态语义、回调、主题、失败保留与 Android BackHandler 接线；未构建 RN 原生 APK |
| Web | `CHROMIUM_PATH=/usr/bin/chromium npm run test:web`，Playwright1.56.1 | passed，4项：键盘开关/分段、失败保留/重试、原生dialog的Escape/焦点归还、所有主题320px/200%基础字级无横向溢出、减少动态效果 |

首轮共 26 个测试；各测试证明范围以本表为准。后续复验、release AAR/本地 Maven 和文件链接/CI语义检查的准确结果见 HANDOFF 当前回执。

后续 `bash cloud/check.sh android` 已通过：组件 release AAR、Catalog debug APK、9项UI测试与两个模块lint；minSdk24库配置在此轮编译。`./gradlew :orchard:publishReleasePublicationToMavenLocal` 也已通过，生成本地POM/模块元数据/源码包；这是当前云端环境的Maven local，不是已发布到外部制品仓库。新增Flutter/RN复制示例后，Flutter analyze/test和RN typecheck已复验通过。`bash cloud/check.sh quick`、shell语法、`git diff --check`与actionlint1.7.7均已通过。

## 观察到并修复的环境/检查问题

- 环境起初只有 JRE，没有 javac；改用临时完整 JDK21，工具包 SHA256 已核对。初始化脚本增加完整 JDK 检查。
- 临时 JDK 默认 CA 不含当前代理信任；使用当前系统 Java CA，保留 TLS 校验。云端初始化保留同样规则。
- 首次 Roborazzi 未开启 record，不会输出截图；补上记录参数，并断言文件实际产生后重新运行测试。这里附的5张图来自修复后的实际输出。
- RN renderer 的节点查询按语义与可调用处理器定位，兼容 fragment/memo 节点；没有跳过原交互断言。

## 尚未验证

实体安卓设备的键盘/系统栏、预测性返回、TalkBack实际朗读、权限/选择器、低端性能、动画顺滑度与不同厂商行为；Flutter/RN原生打包与宿主路由集成；真实用户数据/正式签名/应用升级。宿主截图和renderer test不能替代这些内容。

GitHub Actions仅保存手动工作流，本轮未运行；没有声称远端CI已通过。没有公开包发布或应用发布。
