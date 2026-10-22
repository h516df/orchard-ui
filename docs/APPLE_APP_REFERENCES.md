# Apple App 页面基线 · 0.3

用户要求：“改呀，要做到就是苹果官方 App”。本批将验收对象从单个控件改成完整页面，移除默认首页里的 ORCHARD 标语、组件分类和按钮陈列。展示 App 默认进入设置，右上角“更多 → 参考页面”切换备忘录、音乐和次级组件目录。

## 具体参考

| 官方来源 | 核对内容 | 本库采用 |
| --- | --- | --- |
| [Apple Notes 官方说明](https://support.apple.com/en-us/118442)中的 [iOS 26 列表](https://cdsassets.apple.com/live/7WUAS350/images/ios/ios-26-iphone-17-pro-notes-swipe-delete.png) | 标题/数量、日期分区、标题/摘要/文件夹三层行、底部搜索与新建分离 | Notes 列表、编辑、查询与新建；不用设置行的 chevron 冒充笔记行 |
| 同页 [iOS 26 编辑器](https://cdsassets.apple.com/live/7WUAS350/images/ios/ios-26-iphone-17-pro-notes-text-formatting-options.png) | 白色编辑画布、独立标题、无边框正文、圆形导航动作、金色完成 | 独立编辑页面，保存/删除确认与安卓返回保护；未复制格式面板或附件功能 |
| [Apple Settings 官方说明](https://support.apple.com/en-us/109351)中的 [显示与亮度](https://cdsassets.apple.com/live/7WUAS350/images/ios/ios-17-iphone-15-pro-settings-display-brightness.png) | 浅深色双预览、自动开关、亮度/原彩显示分组、二级页紧凑标题 | 显示与亮度、自动模式和辅助选项。该素材明确是 **iOS 17**，只用于结构，不能标成 iOS 26 全页复刻 |
| [Apple HIG Tab bars](https://developer.apple.com/design/human-interface-guidelines/tab-bars)的 [Music 导航](https://developer.apple.com/tutorials/images/com.apple.HIG/tab-bar-with-accessory-expanded@2x.png) | 独立迷你播放器、四项浮动导航、整项选择、填充图标 | Music 资料库页面和播放器/导航组合；暂停保留歌曲名，切换标签保留播放状态 |
| [Apple HIG Materials](https://developer.apple.com/design/human-interface-guidelines/materials) | 导航/操作层材质、内容层实色、降低透明度 | 正文不用玻璃；搜索/播放器/导航采样背景或回退实色 |

读取日期：2026-10-22。Apple Support 同页也出现 iOS 27 内容，因此仅采用上表明确标注版本的素材；未把自动跳转到用户指南首页的链接作为有效页面证据。官方截图用于临时对照，只保存来源链接，不打包成库资产。

## 共享尺寸与页面规则

- `radius.group=26` 与 `radius.card=20` 分开；分组列表和内容卡片不再共用同一形状。
- 普通设置行至少48，图标tile30、图标19，行内正文17，分隔线从图标后58开始；文字放大时行随内容增长。
- 导航按钮视觉44、点击区域48；底部导航58、四项整项选中；分段轨道32、点击区域48。视觉尺寸与触控区域分开。
- 大标题34，笔记分区22，笔记标题17 semibold、摘要15、文件夹13；正文和右侧值不统一加粗。
- 单目的地页面用底部搜索与动作；音乐才显示标签导航。不得给所有页面套同一个底栏。
- 大标题随滚动离开，紧凑导航接替；Compose按滚动进度过渡，Flutter使用CupertinoSliverNavigationBar，RN使用原生Animated，Web使用sticky导航与透明度过渡。
- 备忘录编辑是完整页面，返回保护由演示宿主实现；移植到真实产品时应接入其已有自动保存、草稿与恢复逻辑。

## 已有代码与运行方式

| 范围 | 本批代码 |
| --- | --- |
| 共享 | JSON tokens → Kotlin/Dart/TS/CSS |
| Compose | `ApplePatterns.kt`：AppPage、RoundButton、SearchDock、NoteRow、MiniPlayer；`ReferenceApp.kt`：三个可操作页面与笔记编辑 |
| Flutter | `apple_patterns.dart`：同类组件、官方Cupertino折叠导航；`example/notes_reference.dart` 为宿主接线示例 |
| React Native | 同类受控组件、Animated折叠标题；`example/NotesReference.tsx` 为宿主接线示例，安全区与图标由宿主提供 |
| Web | CSS模式 + `examples/web-reference` 三页交互宿主；原生HTML语义与dialog |

从仓库根目录运行 `python3 -m http.server 4173 --bind 127.0.0.1`，打开 `http://127.0.0.1:4173/examples/web-reference/`。`python3 tools/build_reference.py` 可生成 `.cloud/preview/orchard-reference.html`，用于离线查看；它不请求远端资源，也不发布公开站点。

## 必须保留的真实边界

这是安卓/跨框架实现的参考页面与组件库，不是 Apple 官方软件，也没有连接真实账户、改变设备无线设置或调用音频引擎。笔记内容只在展示宿主内存和界面状态中保存，不是业务数据库。

Android系统字体与原创图标不等同SF Pro/SF Symbols；Flutter使用单独许可的Cupertino图标。Compose和Web的模糊/高光、Flutter的BackdropFilter不等同完整Liquid Glass光学引擎，RN默认实色并接受宿主`renderBackdrop`。Compose只允许导航等兄弟层采样已记录的内容层；被记录内容中的表面自动使用实色，避免图层自采样造成递归绘制。

物理安卓设备上的IME、系统栏、手势、预测性返回、辅助技术朗读、性能与动画帧率仍需设备验收。截图和云端测试不代替这些结果；不填写“100%一致”“用户视觉认可”或无依据的相似度分数。
