# 组件目录与覆盖范围

v0.1 提供可独立选用的适配层。名称统一表达意图，API 按各框架习惯编写，不要求跨语言参数完全一致。所有数据、回调、持久化与导航上下文由宿主提供。

图例：**代码**＝已实现；**样式**＝CSS/语义 HTML，行为由宿主实现；**宿主**＝应使用平台官方能力或项目实现。本表不把文档模式算成已实现的组件。

| 能力 | Compose | Flutter | React Native | Web |
| --- | --- | --- | --- | --- |
| 4 套配色与深浅主题 | `OrchardTheme` | `orchardThemeData` | `OrchardProvider` | 生成 CSS 变量 |
| 按钮、次要/文字/危险样式、忙碌状态 | `OrchardButton` | `OrchardButton` | `OrchardButton` | `.o-button` 样式 |
| 图标按钮 | `OrchardIconButton` | 官方 `IconButton` | 宿主 Pressable | button + 标签 |
| 输入与错误说明 | `OrchardTextField` | 同名 | 同名 | `.o-field` / `.o-input` |
| 搜索与清空 | `OrchardSearchField` | 同名 | 同名 | input + 宿主清空 |
| 开关行 | `OrchardSwitchRow` | 同名 | 同名 | checkbox/role=switch 样式 |
| 复选行 | `OrchardCheckboxRow` | 官方 CheckboxListTile | 宿主 | 原生 checkbox |
| 分段选择/筛选 | `OrchardSegments` | 同名 | 同名 | radio fieldset 样式 |
| 滑块 | `OrchardSlider` | 官方 Slider | 宿主库 | 原生 range |
| 卡片 | `OrchardCard` | 同名 | 同名 | `.o-card` |
| 分组与分区 | `OrchardGroup` / `OrchardSection` | `OrchardSection` | 同名 | `.o-group` / `.o-section` |
| 列表行/长文字/次级值 | `OrchardListRow` | 同名 | 同名 | `.o-row` |
| 分隔线 | `OrchardDivider` | 官方 Divider | `OrchardDivider` | `.o-divider` |
| 大标题 | `OrchardLargeTitle` | 同名 | 同名 | `.o-large-title` |
| 标签 | `OrchardBadge` | 同名 | 同名 | `.o-badge` |
| 数值摘要 | `OrchardMetric` | 同名 | 同名 | card + 标题样式 |
| 提示/错误横幅 | `OrchardBanner` | 同名 | 同名 | `.o-banner` |
| 空状态/加载/错误重试 | `OrchardEmptyState` / `OrchardLoadingState` / `OrchardErrorState` | 同名 | 同名 | 样式 + 原生状态语义 |
| 静态骨架/确定进度 | `OrchardSkeleton` / `OrchardProgress` | 官方进度 | 宿主 | `.o-skeleton` / progress |
| 顶栏/底部导航/页面骨架 | `OrchardTopBar` / `OrchardTabBar` / `OrchardScaffold` | 官方 AppBar / NavigationBar / Scaffold | 宿主路由器与安全区 | `.o-bottom-nav` + nav |
| 短反馈 | `OrchardSnackbarHost` | 官方 ScaffoldMessenger | 宿主 | role=status |
| 底部面板 | `OrchardSheet` | 官方 showModalBottomSheet | `OrchardSheet` | 宿主/原生 dialog |
| 确认对话框 | `OrchardConfirmDialog` | `orchardConfirm` | `orchardConfirm` | 原生 dialog |
| 列表页面 | `OrchardCollectionScreen` | 宿主 ListView.builder | 宿主 FlatList | 宿主 |
| 设置页面 | `OrchardSettingsScreen` | 同名 | Section 组合示例 | Section 组合 |
| 草稿编辑页面 | `OrchardEditorScreen` | 同名 | 同名 | 宿主，规则已提供 |
| 导入预览 | `OrchardImportReview` | 同名 | 宿主组合 | 宿主组合 |
| 有限宽可选文字阅读 | `OrchardReadingSurface` | 同名 | 同名 | `.o-reading` |

## 核心约定

- 输入字段为受控值/外部 controller。库不替项目创建数据库、不直接请求网络。
- 按钮 loading 禁止重复回调；宿主异步函数也要保证幂等和防并发。重试不等于写操作自动重放。
- 组件允许长文字自然换行。分段选项使用 Flow/Wrap，而非固定宽度单行。
- Compose 开关行以整行为一个语义开关；Flutter 交给官方合并语义；RN 使用原生 Switch，不再把父行也声明成第二个开关。
- 弹层需要额外保护时传 `canDismiss`（Compose/RN），并在 `onDismissBlocked` 中提示继续编辑/放弃。复杂编辑优先完整页。
- 页面模板是参考组合，宿主负责大数据虚拟化、状态恢复、安全区和路由。不能用滚动容器模板替代十万条记录的虚拟化列表。

## 未实现的系统能力

真实背景模糊、复杂日期选择器、图表、地图、拖拽排序、分页阅读引擎、文件/媒体解析、数据库迁移、下载器、应用更新、登录、AI 调用、真机触感实现均不在首版核心代码中。优先复用 [成熟能力](STACK_CHOICES.md)，必要时加适配层。

验证范围见 [VERIFICATION](VERIFICATION.md)。原生界面宿主渲染、Flutter widget test、RN renderer test 与 Chromium 测试证明的内容不同，均不能代替所有真实设备体验。
