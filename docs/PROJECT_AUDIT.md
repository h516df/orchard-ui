# 现有项目观察与迁移建议

只读观察于 2026-10-21，目标是为本库选择复用边界。没有在这批任务中修改、换肤或发布这些 App。以下是开发建议，不是用户逐项确认的迁移计划。每次接入前需重新核对 main、相关 PR 和对应分支交接。

## 已读证据

| 项目 | 读取来源与快照 | 观察 |
| --- | --- | --- |
| 记一下 | README、AGENTS/CLAUDE、HANDOFF、Gradle、`ui/Theme.kt`、WritingSettings、WritingUiTest；main `29e4629b99f3abec4a2d5d2eebef7fc36ad0a59b` | Compose，纸色背景与绿色情绪已有雏形；编辑失败/草稿、导入/备份、媒体与主题都需要跨页面一致的规则 |
| 下一章 | README、AGENTS/HANDOFF、Gradle、主界面与 ReaderUiTest；main `3e75d2857eb40612548264ad00b968490f0ae172` | 阅读/选区编辑需要保留上下文和版本一致性，通用阅读排版可借用，但不应代替成熟阅读/编辑引擎 |
| 开饭啦 | README、`app/src/main/java/com/my/firstapp/Theme.kt`；main `f9af0aa670a3c6dac6273e80f5af130afdfe61b6` | 已有稳定主题 ID、多种皮肤，字体/圆角/背景随风格变化；主题语义可借鉴，不能全部覆盖成同一套蓝色圆角 |
| 留着吧 | README；main `0bfc3cea38d651bb1dfa9a2fa8977297e54f7c1d` | 档案、媒体、草稿、备份和应用锁有自己的能力边界；其最低 API24 需要被依赖兼容性尊重；本轮未做该项目源码级 UI 审计 |
| TimeWorth | README、docs/DESIGN；main `dbc90ee2afb4d588a39319ad321840eb7bebcc4f` | 主线仍是规划与入口，网页体验在相关 PR；技术栈未定。可以先采用跨框架设计变量与流程规范 |
| vibe-ui-quality-kit | README、AGENTS、workflow 主线；`e548b9c66075989847c0e528cd37031f438b1400` | 主要是通用工作流与恢复资料，应保持原职责；运行时 UI 组件放在 Orchard 独立私有库 |

来源仓库入口：[记一下](https://github.com/h516df/jiyixia)、[下一章](https://github.com/h516df/xiayizhang)、[开饭啦](https://github.com/h516df/kaifanla)、[留着吧](https://github.com/h516df/liuzheba)、[TimeWorth](https://github.com/h516df/timeworth-android)。这些是快照，不代表未来分支状态。

## 相关进行中工作

读取时，下一章 PR [#6](https://github.com/h516df/xiayizhang/pull/6) 的 `feat/mature-reading-editing` / `bed951c64940596500149b4eb178e76ea8998ffa` 包含 Readium 与成熟编辑器接入；不能只按 main 再做一个自制阅读引擎。记一下还有更新源 PR #28；通用工具库有多 Codex 协作 PR #9/#10。本轮没有合并或修改它们，也不把它们算成本库成果。

## 适合集中复用的部分

1. **语义主题和排版**：共用字体层级、间距与控件状态；每个产品保留自己的皮肤、主题 ID 和内容密度。
2. **列表、设置与反馈**：统一标题/说明/值/操作层次、无结果与空库差异、离线和重试。
3. **编辑与返回保护**：草稿、失败保留、保存中阻止连点、统一返回路径；实际持久化留在各项目。
4. **导入和恢复预览**：共用呈现与确认顺序，解析器、容量限制、冲突策略和数据库兼容留在各项目。
5. **验证方法**：合成数据、深浅色/大字号、宿主渲染与真机边界一致，不重复发明验收口径。

## 建议顺序

先在一个低风险设置页接主题与分组列表，确认视觉和兼容性；再迁移一个编辑页，测失败与返回；最后扩到导入/阅读周边。首页和产品核心交互保持单独设计，例如餐食滚轮、媒体发现页和金额换算不抽象成万能组件。

本库 Compose minSdk 24，展示 App minSdk 26；仍要核对宿主的 AGP/Compose/Java 版本与依赖约束。新项目可选 Flutter/RN/Web，不因为当前观察多数是 Compose 就锁定技术。

库接入本身不动原应用 ID、签名、备份字段、数据库、更新源或用户文件。后续具体项目有改 UI 请求时，再在其授权范围内接入。
