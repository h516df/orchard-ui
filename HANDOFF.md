# 当前交接 · Orchard UI 0.2 Apple视觉修正

日期：2026-10-21。当前目标与长期视觉规则见docs/APPLE_VISUALS.md；需求状态见docs/BACKLOG.md。

- **用户纠正**：“不行呀，我要的是苹果，你这像是古早安卓”。0.1视觉不被用户接受，不能用当时26个测试代替视觉认可。
- **分支/基线**：fix/apple-native-visuals / main@64721540a26c9521c5f108a7b6871d44cc68adb9；只修改本库，不改其他App、签名或数据。
- **已修改**：四框架共享Apple视觉变量；胶囊按钮、绿色开关、连续滑动分段、无浮动标签输入、紧凑分组列表、浮动导航、居中分隔动作确认；深色按钮填充与链接色分开。Compose自绘，Flutter默认Cupertino，RN用受控原语与Provider确认，Web保留原生HTML语义并重绘。
- **新增接口**：OrchardGlassSurface；Flutter/RN新增OrchardTabBar；RN确认改用useOrchardConfirm()或OrchardConfirmDialog，旧模块级orchardConfirm()不再导出。Compose OrchardTheme新增reducedTransparency，Scaffold保留参数使用习惯，但滚动底部留白需放到滚动容器内以支持底栏后滚动。
- **材质边界**：Compose API31+记录独立内容图层并采样模糊，旧API/大字号/降低透明度走实色；FlutterBackdropFilter/Webbackdrop-filter；RN默认实色，模糊由renderBackdrop注入。不宣称完整Apple专有Liquid Glass引擎。
- **本批验证**：共享3、Compose13、Flutter6、RN6、Web5，共33项passed；Android release AAR/debug APK/lint passed；Flutter analyze与RN typecheck passed。准确命令与未覆盖项见docs/VERIFICATION.md；新渲染图由本批宿主运行产生。Maven local 0.2发布也通过；8张预览已与最后一次宿主输出逐字节核对，已验证源码为`e33d1d4c003d98b1968b6913609a62508599da1b`，远端分支与本地SHA一致，提交时工作区clean。
- **未覆盖**：真机IME/系统栏/TalkBack/预测性返回/性能与动画顺滑度、Flutter/RN原生构建和宿主路由。没有运行Actions或发布已有App。
- **已保存主线**：源码`e33d1d4c003d98b1968b6913609a62508599da1b`已通过[PR #2](https://github.com/h516df/orchard-ui/pull/2)合入main，合并提交`8fbd1da59f10a92c80e8e7f13ea20056039c17a4`。本次回执为后续文档提交，版本v0.2.0；保留v0.1.0历史。仓库private，无额外权限/付费/其他App变更。
- **接续**：先读本段、AGENTS与Apple视觉基线，核对远端main和本批PR。未来UI改动不得回退到默认Material/.adaptive安卓外形；具体项目接入需在其授权范围内进行。

## 0.2产物回执

- 当前云端Catalog debug APK SHA256：`0cd06e18d70010ef6f9a2c0c88673fee0ff8dee986b8a820829ac3f852f6ff8e`。
- Orchard release AAR SHA256：`4e8854e245d0d7bf7c7696820745a84ca3d879c3063e906addc8c63ee954e787`。
- 产物对应上述已验证源码；没有发布正式App或上传公开渠道。源码、33项检查、8张渲染及文档回执已保存私有库。
- 额外静态检查：shell语法、生成同步、文档链接、git空白、actionlint1.7.7 passed。新会话按各适配层现有命令恢复工具链，不把当前VM或Actions当作已验证的长期环境。
- 后续以远端main/v0.2.0接续；用户尚未认可新版外观，不能替用户写“视觉验收通过”。

## 0.1历史交接（保留，不代表视觉获认可）

# 当前交接 · Orchard UI 0.1.0

日期：2026-10-21。先读 AGENTS、README、CLOUD_DEVELOPMENT，再核对 GitHub main、任务分支与相关 PR。

- **目标**：用户委托建立私有、跨项目可复用的苹果风 UI/UX 资源库，并明确不绑定 Kotlin/Compose。需求状态见 docs/BACKLOG.md。
- **仓库**：https://github.com/h516df/orchard-ui ，ID1428294865，private，默认分支main；已通过Make和GitHub读回确认。
- **已交付主线**：首版源码 `8eecdc49f04526b30e65d4f5df31433f527393da` 已通过 [PR #1](https://github.com/h516df/orchard-ui/pull/1) 合入 main，合并提交 `90fd3e79ac212d2340e0b100191a939725af601d`。当前回执为后续文档提交；从远端 main 接续，版本标记 v0.1.0。原任务分支 feat/orchard-foundation 保留来源，不需继续未合并任务。
- **已实现**：共享JSON变量与4语言生成；4配色×深浅色；Compose、Flutter、React Native、Web适配；Compose可操作展示App；中文视觉/组件/UX/页面/选型/接入/审查/迁移资料；合成宿主预览图。
- **验证**：共享3项、Compose UI9项、Flutter5项、RN5项、Web4项，共26项passed；Android release AAR/debug Catalog APK与lint passed；Flutter analyze无问题；RN typecheck passed；文档链接/生成同步 passed；actionlint1.7.7语义校验passed。精确命令和边界见docs/VERIFICATION.md。
- **未验证**：实体手机、预测性返回、IME/系统栏、TalkBack、性能与动效；Flutter/RN原生APK和宿主路由；没有正式App发布。Actions手动工作流仅保存，未运行，不宣称远端CI通过。
- **建仓路径**：GitHub CLI创建被当前integration权限拒绝；复用既有Make按需GitHub API shell6715378/连接11842349，GET /user确认h516df与repo scope；POST /user/repos显式private:true，execution2051afa0cf98454398e971ace838d09f成功；读回executionacb0dab337f743718727d91ece8992d5。没有新建/修改Make场景、定时任务或凭据。
- **通用工作流**：固定采用h516df/vibe-ui-quality-kit@e548b9c66075989847c0e528cd37031f438b1400/workflow；来源保存在.ai-workflow.json。本仓库接入，不改写其恢复资料或其他任务。
- **环境**：JDK21、SDK36、Gradle9.1、AGP9.0.1、Kotlin/Compose2.3.20/BOM2026.03.01；Flutter3.41.5/Dart3.11.3；RN0.83.2/React19.2/TS5.9.3；Playwright1.56.1。SDK/JDK/缓存为可重建临时环境，不能假设新会话已有。
- **下一步**：先核对本库当前main和已保存的来源SHA。未来用户提出具体项目UI任务时选择合适适配层，读目标项目现有PR与数据约束，从代表页面逐步接入；不要直接批量迁移全部App。

## 常用命令

```bash
bash cloud/check.sh quick
bash cloud/check.sh android
bash cloud/check.sh flutter
bash cloud/check.sh react-native
bash cloud/check.sh web
```

各工具链须在PATH，Android需完整JDK和SDK；Web可指定系统CHROMIUM_PATH。没有工具时检查明确失败，不能把guard或历史结果当功能通过。

## 首版产物回执

源码：`8eecdc49f04526b30e65d4f5df31433f527393da`，通过后的代码内容已核对；本回执不含后续业务修改。

- Catalog debug APK SHA256：`f909b64d1435de61dfa8c00c49ea5935bbd9492d3f0e23ced64db62c566af4c4`。
- Orchard release AAR SHA256：`bb22134aa6717925b1c2a9f09b389c465f4ff7e4b93654c5a5fa7cff1373f8fb`。
- Maven local发布passed；Android release AAR/debug APK/UI9项/lint最终复验passed；Flutter含新增复制示例的analyze/test passed；RN含示例的typecheck passed。
- 5张文档预览与最后一次宿主输出逐字节一致；文档链接、生成同步、shell语法、git空白与actionlint1.7.7 passed。
- 源码分支推送回执：远端/本地 `8eecdc49f04526b30e65d4f5df31433f527393da` 一致，工作区clean；PR已读回merged。最终main与tag由交付时远端读回，不把当前文档自身SHA写成自引用。
- 安装包与AAR仅为本轮云端构建输出，没有上传到公开渠道，也没有发布正式应用。后续环境可按README重新构建。
