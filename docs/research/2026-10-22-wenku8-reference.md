# 用户参考 · Wenku8 Flutter

检索日期：2026-10-22。用户原话：“你看这个的效果也很好”。状态：**参考分析/候选记录，未改运行时、安装新依赖或发布新App**。对应需求池OUI-006。

## 来源与核对范围

- [用户分享原帖《Codex神力》](https://www.xiaoheihe.cn/app/bbs/link/f0b472a1dae5)：作者SpfoX；正文为轻小说文库演示，评论中给出`mikufoxxx/Wenku8_Flutter`。保存规范链接，省略分享追踪参数。
- [原始演示GIF](https://imgheybox1.max-c.com/bbs/2026/10/15/4f245d27103060c202d2e97136c5fe8f.gif)：400×868，254帧，按GIF延时字段累计19.87秒。已下载在临时研究目录，并按时间抽帧检查；SHA256 `fcdfa619d6dd2b9a145092d486a924fb5b1847f6d764d4d980f7298cafbfbcb3`。没有把他人封面/小说/动图拷进本库预览。
- [公开源码](https://github.com/mikufoxxx/Wenku8_Flutter)：核对提交[`ba21d47`](https://github.com/mikufoxxx/Wenku8_Flutter/commit/ba21d477ce32e48dd19f5fb49f3701d095c89a2b)，default branch `master`。README/pubspec记录0.4.6+163、Flutter3.41.5/Dart3.11.3。帖子为10月14日发布，当前源码有后续更新；不能断言这个GIF来自当前提交或展示了全部现有功能。
- 证据：帖子正文/作者评论、原始GIF时序画面、下列固定提交源码。没有安装运行其APK，没有替作者的性能测试或真机表现背书。

## 从演示里实际看到什么

| 时间附近 | 可观察效果 | 对组件库的启发（候选） |
| --- | --- | --- |
| 0秒、7.5秒 | 奶白底、黑色衬线标题、小号英文编号/索引、细线分区；书脊形成有透视的队列，选中封面向前突出，标题/作者/索引配合变化 | 视觉主题应包含字体搭配、信息层级、留白和内容展示方式；增加适用于书籍/唱片/文件的立体内容展示模式 |
| 2.5–5秒 | 封面列表滚动；内容从顶栏、底部胶囊后经过，局部材质把背景压住，正文仍清楚 | 玻璃主要服务导航与工具层；重点检查滚动边缘和文字可读性 |
| 约10秒 | 从书架进入详情的中间帧可见前页边缘与进入中的详情，封面已出现，其他区域显示骨架 | 页面进出、封面和加载状态要联动；不能只给整个页面一个统一淡入 |
| 12.5–15秒 | 目录用编号、细分隔线和一致排版，底部动作变为“开始阅读” | 底部操作随当前任务变化，保持页面视觉语法一致 |
| 17.5–19.5秒 | 阅读页延续纸感/字形/索引标记，顶部与底部操作简洁 | 阅读类应用应有独立的内容优先模式，保留主题与上下文连续性 |

这里的判断来自演示画面，不把GIF帧间隔换算为手机帧率，也不声称已验证触觉、手势中断或功耗。录屏中的系统状态区不列作拟复制的组件。

## 固定提交的实现线索

- [`pubspec.yaml`](https://github.com/mikufoxxx/Wenku8_Flutter/blob/ba21d477ce32e48dd19f5fb49f3701d095c89a2b/pubspec.yaml)：使用本地`third_party/liquid_glass_easy`，另声明`analytic_glass.frag`、`continuous_blur.frag`。这与本库0.4采用的`liquid_glass_widgets`是不同后端。
- [`archive_shelf_geometry.dart`](https://github.com/mikufoxxx/Wenku8_Flutter/blob/ba21d477ce32e48dd19f5fb49f3701d095c89a2b/lib/archive_shelf_geometry.dart)：统一书脊/封面/轨道/前移的几何参数，约束宽度；不是几张平面卡片临时错开。
- [`archive_motion.dart`](https://github.com/mikufoxxx/Wenku8_Flutter/blob/ba21d477ce32e48dd19f5fb49f3701d095c89a2b/lib/archive_motion.dart)：用有限的共享动画时钟驱动标签和分隔线；关闭动效时走静态状态，不为每个进入屏幕的元素持续开独立ticker。
- [`motion_ui.dart`](https://github.com/mikufoxxx/Wenku8_Flutter/blob/ba21d477ce32e48dd19f5fb49f3701d095c89a2b/lib/motion_ui.dart)：`PaperCoverRectTween`、`PaperCoverFlight`、`PaperCoverBend`处理封面Hero几何和纸面弯曲；存在分阶段进入/落定的时序。源码有这项实现，不等于GIF完整展示了当前实现。
- [`PATCHES.md`](https://github.com/mikufoxxx/Wenku8_Flutter/blob/ba21d477ce32e48dd19f5fb49f3701d095c89a2b/third_party/liquid_glass_easy/PATCHES.md)：内嵌玻璃库有本地性能/坐标/透明度子层/渐进边缘/手势取消等修改，不能只按pubspec版本5.0.0把它当作未改动的上游包。该文档内的像素与性能结果是上游自述，本次未重跑。

## 候选方向及边界

优先级建议（尚未实施）：

1. 内容展示与完整页面：立体媒体轮播、封面/标题联动、列表与详情的一致排版；保留本库Apple官方App基线，不把所有项目统一改成小说App皮肤。
2. 连续转场：同一内容从列表/书架到详情，再到阅读的上下文连续；支持中断、返回、减少动态效果和大字号。
3. 材质协调：滚动边缘渐进模糊、顶栏/底栏采样协调、内容文字的可读性；先对照现有后端补缺，再决定是否更换或新增后端。
4. 交付验收：在本库原创合成内容上做完整流程与动效实录，而不只增加静态组件截图。

许可证已核对：应用根LICENSE为GPLv2文本，README声明保留GPLv2或后续版本、当前Flutter组合版按GPLv3分发；内嵌`liquid_glass_easy`保留Ahmed Gamil的MIT许可。整App、本地修改与上游库的归属应分别核对。本次只记录观察和路径，没有复制其应用/着色器代码、书籍封面或字体到Orchard；未据此认定整套可以当MIT组件直接搬入。
