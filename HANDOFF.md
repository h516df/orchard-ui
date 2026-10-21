# 当前交接 · Orchard UI 0.1.0

日期：2026-10-21。先读 AGENTS、README、CLOUD_DEVELOPMENT，再核对 GitHub main、任务分支与相关 PR。

- **目标**：用户委托建立私有、跨项目可复用的苹果风 UI/UX 资源库，并明确不绑定 Kotlin/Compose。需求状态见 docs/BACKLOG.md。
- **仓库**：https://github.com/h516df/orchard-ui ，ID1428294865，private，默认分支main；已通过Make和GitHub读回确认。
- **任务分支**：feat/orchard-foundation；本记录与源码将一并推送，准确源码提交和PR在下一次回执中填写。
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
