# 0.3 · App 页面实际预览

默认展示从组件陈列改为完整设置页，更多菜单切换备忘录、音乐与次级组件目录。具体官方来源与采用范围见 [原版对照](../APPLE_APP_REFERENCES.md)。

以下图片来自本库实际 Compose 宿主：Robolectric4.17/API35、Native graphics、Roborazzi1.76.0，393dp×852dp（普通字号）。使用合成内容，没有模拟苹果状态栏或把官方截图当作代码输出；不是概念图。来源提交与每张文件的SHA256见 [manifest](manifest.json)。

| 设置 | 备忘录 | 音乐资料库 |
| --- | --- | --- |
| ![设置浅色](settings-light.png) | ![备忘录浅色](notes-light.png) | ![音乐浅色](music-light.png) |
| ![设置深色](settings-dark.png) | ![备忘录深色](notes-dark.png) | ![音乐深色](music-dark.png) |

| 显示与亮度 | 笔记编辑 | 滚动后的导航 |
| --- | --- | --- |
| ![显示与亮度](display-light.png) | ![笔记编辑](note-editor-light.png) | ![滚动后的备忘录](notes-scrolled-light.png) |

| 320dp / 200% 设置 | 320dp / 200% 备忘录 |
| --- | --- |
| ![大字号设置](settings-320dp-font200.png) | ![大字号备忘录](notes-320dp-font200.png) |

可交互的跨框架参考见 [Web 示例源码](../../examples/web-reference/index.html)，从仓库根目录启动本地HTTP服务器后打开该路径。`python3 tools/build_reference.py` 生成离线单文件预览，命令和接线示例见 [接入指南](../INTEGRATION.md)。没有公开部署私有源码。

## 次级组件目录

| 浅色控件 | 深色控件 | 分组列表 |
| --- | --- | --- |
| ![控件浅色](components-light.png) | ![控件深色](components-dark.png) | ![分组列表](grouped-lists-light.png) |

| 强调色 | 确认弹窗 | 降低透明度 |
| --- | --- | --- |
| ![强调色](appearance-sage-light.png) | ![确认弹窗](confirmation-light.png) | ![不透明回退](components-opaque-glass-light.png) |

| 320dp / 200% 浅色 | 320dp / 200% 深色 |
| --- | --- |
| ![大字号控件浅色](components-320dp-font200-light.png) | ![大字号控件深色](components-320dp-font200-dark.png) |

截图不证明真机系统栏、IME、TalkBack、手势或动画帧率；字体与材质也不能据此宣称和苹果100%相同。实际检查见[验证记录](../VERIFICATION.md)。v0.1/v0.2的历史画面保留在对应Git标签。
