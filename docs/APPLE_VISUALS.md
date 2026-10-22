# Apple 视觉基线

当前 **0.3 以完整 App 页面为基线**，先读 [原版对照与实现范围](APPLE_APP_REFERENCES.md)。下文保留0.2的基础控件约定。

用户对0.1的原话：“不行呀，我要的是苹果，你这像是古早安卓”。这说明0.1的Material外观没有达到目标；构建和交互测试通过不等于视觉已被接受。本批按苹果控件的具体形状和布局修正，不以颜色替换代替设计。

## 一眼能看出的区别

| 项目 | 0.2 采用的视觉 | 不应重新引入的旧外观 |
| --- | --- | --- |
| 分段控件 | 一条连续灰色轨道，等宽选项，内嵌滑动选中块 | 分散的FilterChip/ChoiceChip、描边标签 |
| 开关 | 51×31轨道、27圆形白色滑块、绿色开启 | Material长轨道、小圆点、安卓原生Switch外形 |
| 输入 | 持续标签+行内值；搜索为灰色填充 | 浮动标签、缺口描边、底部Android默认下划线 |
| 按钮 | 胶囊、主要/轻强调/文字三级，按下透明度反馈 | Material水波纹、宽大的圆角矩形堆叠 |
| 分组列表 | 紧凑行、右侧值与细chevron、图标后缩进分隔线 | 把值全部堆到标题下面、每行过度留白 |
| 底部导航 | 浮起的一整块圆润材质，整项选中 | 全宽直角NavigationBar、只在图标后放选中胶囊 |
| 确认弹窗 | 居中标题/说明、底部分隔动作、危险色文字 | 默认Android Alert/Material AlertDialog按钮布局 |
| 浅/深色 | 默认浅灰分组底/白内容，黑色深色底/深灰内容 | 仅依赖Material调色板推导所有可见状态 |

34/22/17/15/13字阶保持明确；行高按用途收紧。视觉开关小于48，但整行点击区域至少52。大字号分段控件改为纵向等宽选择，输入标签和值上下布局，避免挤压或裁切。

## 各框架怎样实现

- **Compose**：基础文本、语义、选择与点击用Foundation；按钮、开关、输入、分段、导航和确认布局由Orchard绘制。MaterialTheme/Scaffold提供颜色、布局基础，Slider/ModalBottomSheet保留交互基础并重绘可见部分，不展示其默认外形。
- **Flutter**：固定Flutter3.41.5，直接用CupertinoButton、CupertinoSwitch、CupertinoTextField/SearchTextField、CupertinoSlidingSegmentedControl、CupertinoAlertDialog与CupertinoNavigationBar。MaterialApp/ThemeData只是可兼容现有工程的宿主与语义主题桥，不把平台强制改成iOS。
- **React Native**：受控原语绘制开关、分段与浮动导航；确认用Provider内的受控Modal。0.2提供`useOrchardConfirm()`；旧的模块级`orchardConfirm()`不再作为默认Android Alert入口。
- **Web**：语义HTML控件保留键盘行为，CSS绘制苹果式外形；原生dialog仍负责模态焦点。导航使用backdrop-filter，提供不透明回退。

## 材质与回退

玻璃用于导航/临时控制层，正文列表保持实色。Compose的OrchardScaffold记录独立内容图层，OrchardGlassSurface在API31+采样后模糊；图标与文字单独绘制以保持清晰。API24–30、200%等大字号或`reducedTransparency=true`使用不透明表面，避免大字从导航后面透出影响阅读。把可滚动内容的底部留白放在滚动容器内部，才能在导航下面滚过且仍可完整访问。

Flutter使用BackdropFilter；Web使用backdrop-filter，均支持降低透明度。RN不捆绑新的模糊原生依赖，默认是实色回退；宿主可以给OrchardGlassSurface/TabBar传`renderBackdrop`。不要把实色回退称为实时背景模糊，也不声称跨平台实现了Apple专有的Liquid Glass折射引擎。

`buttonFill` / `buttonText`与链接/强调的`primary`分开：深色下仍可用深蓝底白字按钮，同时保持链接和各状态足够对比度。`toggleOn/Off`、`segmentTrack/Thumb`同样来自共享变量，不依赖各平台默认颜色。

## 参考与检查

本批读取并目检了苹果官方参考：[Materials](https://developer.apple.com/design/human-interface-guidelines/materials)、[Tab bars](https://developer.apple.com/design/human-interface-guidelines/tab-bars)、[Segmented controls](https://developer.apple.com/design/human-interface-guidelines/segmented-controls)。图层实现参考[Compose graphics modifiers](https://developer.android.com/develop/ui/compose/graphics/draw/modifiers)。官方图像只用于临时对照，没有作为本库资产分发。

实际样本见[当前预览](previews/README.md)。需要看默认浅/深色、分组列表、确认、降低透明度与窄屏/200%字号；测试仅证明对应范围，不能替代用户对外观的判断或实体手机的手势/IME/动效性能验收。
