# UI / UX 设计参考库

给后续苹果风安卓项目使用，也适用于 Flutter、React Native、Web 与原生 View。先看完整页面和真实流程，再决定组件；保留目标项目的技术栈。

站点与访问条件核对日期：**2026-10-22**。以下为公开页面与官方定价页的核对结果，未登录付费资料库。资源内容、订阅和系统版本会变化，具体任务使用前再确认。

## 最常用的路线

**Apple 官方资源定基线 → Mobbin / Pttrns 找同类完整页面 → Page Flows 看流程和动效 → 用 Orchard 实现与验证。**

预算优先时，用 Apple + Pttrns + 公开案例即可开始；确实需要完整录像或大量历史页面，再评估订阅。无需一次购买所有站点。

| 当前问题 | 先打开 | 看什么，留下什么 |
| --- | --- | --- |
| 怎么符合当前苹果官方 App 的设计 | [Apple 资源](https://developer.apple.com/design/resources/) + [HIG](https://developer.apple.com/design/human-interface-guidelines/) | 当前版本、导航/标题/搜索/弹层规则；记录具体规范与对应 App 页面 |
| 设置、列表、搜索、编辑页怎么排 | [Mobbin](https://mobbin.com/) / [Pttrns](https://www.pttrns.com/) | 同一 App 的相邻页面、文字层级、密度与主要操作，不只截一个控件 |
| 返回、菜单、展开、收起怎样连起来 | [Page Flows · iOS](https://pageflows.com/ios/) | 触发动作、转场、取消、中途打断、完成后的状态；记录像时间点 |
| 纸感、书架、阅读器的整体风格 | [Behance UI/UX](https://www.behance.net/galleries/ui-ux) + [Wenku8 参考记录](research/2026-10-22-wenku8-reference.md) | 排版、封面、内容连续性；区分概念图、实际演示与当前源码 |
| 首次使用、权限说明、订阅流程 | [ScreensDesign](https://screensdesign.com/) / Page Flows | 到达核心功能需要几步、能否跳过、拒绝后能否继续；只研究产品实际需要的流程 |
| 对比更多布局或查中文设计方法 | [Refero · iOS](https://refero.design/ios-apps) / [优设](https://www.uisdc.com/) | 对比信息结构、阅读层级与方法，结论回到当前项目的真实任务 |

## 官方规范与设计素材

| 资源 | 用途 | 使用方式与边界 |
| --- | --- | --- |
| [Apple Design Resources](https://developer.apple.com/design/resources/) | 官方 UI Kit、控件、布局和图标模板入口 | 本次页面列出 iOS / iPadOS 27；后续从此入口核对版本，不能永久把“27”当最新 |
| [Human Interface Guidelines](https://developer.apple.com/design/human-interface-guidelines/) | 导航、组件行为、材质、无障碍与适配原则 | 先查当前问题对应条目，再结合具体官方 App 页面；规范与真实 App 可能存在差异 |
| [Apple Design · What's new](https://developer.apple.com/design/whats-new/) | 追踪新版设计变化 | 区分新变化、沿用特性与尚未在目标平台实现的部分 |
| [官方 iOS / iPadOS 27 Figma UI Kit](https://www.figma.com/community/file/1651309003795292092/ios-and-ipados-27) | 查控件构成、比例与完整设计结构 | 链接来源为 Apple 资源页；使用时核对文件版本、许可和所需账号权限 |

公开可获取不等于允许把素材随安卓 App 再分发。字体、SF Symbols、截图、封面与商业模板分别核对权利；本库不打包 Apple 字体或 SF Symbols。自行实现布局和控件时，参考[苹果视觉基线](APPLE_VISUALS.md)、[官方 App 对照](APPLE_APP_REFERENCES.md)和[新版研究](research/2026-10-22-ios27.md)。

## 真实页面与完整流程

| 站点 | 最适合 | 免费 / 付费范围（核对日） | 注意 |
| --- | --- | --- | --- |
| [Mobbin](https://mobbin.com/) · [定价](https://mobbin.com/pricing) | 同类 App 页面、设计模式、相邻流程与版本比较 | 免费账号内容有限；完整 flows 与 animations 不在免费计划内，完整库需订阅 | 搜索时选移动端 / iOS；记录 App 与版本，旧截图不代表当前系统 |
| [Page Flows · iOS](https://pageflows.com/ios/) · [主站](https://pageflows.com/) | 实际录屏、任务流程、转场与操作顺序 | 完整访问需订阅；页面所列 3 天试用为 $2.95，属于付费试用 | 看一次完整任务，记录触发/结果与时间点；演示录像不能证明安卓实现性能 |
| [Pttrns](https://www.pttrns.com/) | 免费检索 App 页面与常见设计模式 | 当前首页明确为完全免费，创建账号后访问；无卡、无试用或升级要求 | 这是本次核对到的新政策，不沿用旧榜单里的付费标签 |
| [Refero · iOS](https://refero.design/ios-apps) · [定价](https://refero.design/pricing) | 页面布局、层级与不同产品的对比 | 免费预览约 3% 资料库；完整页面库与 user flows 属于付费范围 | 优先从 iOS 入口开始，避免混入桌面 Web 布局 |
| [ScreensDesign](https://screensdesign.com/) | iOS 首次使用、付费墙及留存流程 | 公开首页有示例，站内有订阅；未确认完整免费额度 | 不因示例丰富就给每个产品增加账号、欢迎页或付费墙 |

费用只用于判断访问门槛，不是购买建议；价格、地区税费和权限以购买时官方页面为准。缺少付费权限时，明确记录缺项，用公开来源继续推进，不能声称看过未访问的完整流程。

## 整体视觉与中文方法

| 站点 | 如何使用 | 参考强度与访问 |
| --- | --- | --- |
| [Behance · UI/UX](https://www.behance.net/galleries/ui-ux) | 搜完整 App 案例，观察字体、排版、配色和多页一致性 | 公开项目可浏览；案例可能是概念设计，源文件/素材未必免费 |
| [Dribbble · Mobile](https://dribbble.com/shots/popular/mobile) | 探索视觉方向、局部细节与微动效 | 公开作品可浏览；单张展示图不证明完整 UX、可实现性或无障碍 |
| [优设](https://www.uisdc.com/) | 中文方法、排版和趋势检索 | 许多公开文章可读，课程另有付费；文章观点与官方规范分别标注 |

苹果官方 App 的还原以官方资料和具体版本为准。第三方作品用于补充产品风格，不能替代官方依据；同一页面尽量沿用一套字体、密度、圆角和动效逻辑。

## 按开发场景检索

英文站优先用英文关键词，再组合产品名、平台和版本。搜索结果中的 `iOS` / `Liquid Glass` 标签需要回到实际页面验证。

| 场景 | 可直接复制的关键词 | 对应本库 |
| --- | --- | --- |
| 苹果完整页面 | `iOS app settings` · `large title` · `inset grouped list` | [官方 App 对照](APPLE_APP_REFERENCES.md)、[页面配方](SCREEN_RECIPES.md) |
| 导航与搜索 | `floating tab bar` · `scroll edge effect` · `search interaction` | [Liquid Glass](LIQUID_GLASS.md)、[组件目录](COMPONENTS.md) |
| 菜单与弹层 | `context menu submenu` · `sheet detents` · `interactive dismissal` | [UX 模式](UX_PATTERNS.md)、[动态流程范围](LIQUID_GLASS.md) |
| 书架与阅读 | `editorial app` · `book reader` · `bookshelf interaction` · `minimal mobile` | [Wenku8 参考](research/2026-10-22-wenku8-reference.md)、[页面配方](SCREEN_RECIPES.md) |
| 笔记与编辑 | `notes editor` · `unsaved changes` · `keyboard toolbar` | [UX 模式](UX_PATTERNS.md)、[组件目录](COMPONENTS.md) |
| 音乐与媒体 | `music library` · `mini player` · `now playing transition` | [官方 App 对照](APPLE_APP_REFERENCES.md)、[Flutter 音乐宿主](../examples/flutter-liquid/README.md) |
| 无障碍与回退 | `dark mode` · `Dynamic Type` · `Reduce Motion` · `Reduce Transparency` | [质量标准](QUALITY.md)、[审查模板](../templates/UI_REVIEW.md) |

例如查当前系统时，在 `floating tab bar` 后加本次已核对的 iOS 版本；书架任务则优先搜索内容展示和阅读连续性，不因为玻璃热门就把整页正文变透明。

## 从参考变成可复用实现

1. **定义任务。** 助手根据需求补[项目简报](../templates/PROJECT_UI_BRIEF.md)，明确一个主要动作、目标平台、页面和必要状态。用户无需填表。
2. **选少量相互兼容的参考。** 默认一份官方基线、一套完整页面、一段相关流程即可；已有证据足够就停止扩展搜索。确认来源日期、App / 系统版本与真实或概念属性。
3. **记录取舍。** 用[参考记录模板](../templates/DESIGN_REFERENCE.md)区分直接观察、推测、采用和暂不采用。保存链接与时间点；不把其他网站截图或资产批量搬进库。
4. **对照已有实现。** 查[组件目录](COMPONENTS.md)、[页面配方](SCREEN_RECIPES.md)和[技术选型](STACK_CHOICES.md)。优先复用，确有缺口再评估新依赖；版本/commit/许可记入[取舍记录](../templates/DECISION_LOG.md)。共享变量仍只改 `tokens/orchard.json` 后生成。
5. **先做一个完整流程。** 例如书架 → 详情 → 阅读 → 返回保留位置。先看同一套完整画面，再抽通用组件；安卓返回、键盘、安全区、TalkBack与目标平台能力一起设计。
6. **按实际改动验证并留证据。** 检查浅深色、窄屏/大字号、空/加载/失败、取消和连续操作；涉及动效时实际运行并记录。把适配层、宿主、真机各自结果写清，不把静态图或第三方演示当成本库验收。

新参考保存在 `docs/research/YYYY-MM-DD-主题.md`，在需求池对应任务引用；采用后链接具体实现和验证记录，后续任务可直接接续。参考被记录、代码被集成、运行通过、用户认可分别写明。

## 已有参考与旧链接

| 记录 | 当前用途 / 状态 |
| --- | --- |
| [官方 App 页面基线](APPLE_APP_REFERENCES.md) | 设置、备忘录与音乐页面的具体来源及本库差异 |
| [2026-10-22 最新系统与库研究](research/2026-10-22-ios27.md) | 版本核对、候选库与已采用后端分开记录 |
| [用户推荐 Wenku8](research/2026-10-22-wenku8-reference.md) | 纸感排版、立体书架、滚动边缘材质和阅读连续性；为候选参考，未迁入运行时 |

旧入口在核对日的跳转：`screenlane.com` → [Page Flows](https://pageflows.com/)；`uisources.com` → [ScreensDesign](https://screensdesign.com/)。后续收藏优先用表中的当前地址。
