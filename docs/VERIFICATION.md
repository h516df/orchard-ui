# 验证记录 · 0.3 Apple App 页面

日期：2026-10-22。基线为v0.2.0 `cc309ec68d0e6f07a8e78ca8add47a32c9d69ee7`；本批最终源码 `7fb0b80`。下表分别保留实际执行的来源提交；已用Git差异核对后续提交没有改变通过检查的相应平台源码。

| 范围 | 命令 / 来源 | 结果 |
| --- | --- | --- |
| 共享变量 | `bash cloud/check.sh quick` / 最终源码+预览文档 | passed，3项，8主题对比度/尺寸、四层生成同步、文档链接 |
| Android | `JAVA_HOME=/workspace/scratch/jdk21 PATH=/workspace/scratch/jdk21/bin:$PATH bash cloud/check.sh android` / `01d586e` | passed，release AAR、Catalog debug APK、22项Compose UI、两个模块lint；后续源码仅改变Web |
| Flutter | `PATH=/workspace/scratch/flutter-sdk/bin:$PATH bash cloud/check.sh flutter` / `510ae61` | passed，analyze无问题、8项widget test；后续提交未改变Flutter源码 |
| RN | `bash cloud/check.sh react-native` / `01d586e` | passed，tsc、7项renderer/unit test；后续未改变RN源码 |
| Web | `CHROMIUM_PATH=/usr/bin/chromium bash cloud/check.sh web` / `7fb0b80` | passed，Chromium10项；包含原组件5项+三页搜索/编辑/确认/播放/主题字号/滚动导航5项 |

合计50项测试通过。19张Compose宿主预览逐字节复制并用[manifest](previews/manifest.json)记录SHA256；包括三个参考页浅深色、二级设置、编辑、滚动导航、两张320dp/200%字样及原组件目录8张。图片均来自宿主实际输出；未把Apple官方素材冒充本库画面。

单文件预览由 `python3 tools/build_reference.py` 生成。Chromium通过本地HTTP宿主实际打开三页、切换深色/页面，pageerror为0、外部请求为0。当前环境Chromium的file://导航被管理策略阻止，因此不声称已在此环境直接用文件协议验收；同一单文件在HTTP宿主的行为已验证。临时预览与产物不进入Git。

本批发现并修复：Compose内容层内的玻璃不能采样自身记录层，否则Native Graphics递归绘制崩溃；现将采样限制在兄弟导航层，内容内回退实色。Web事件委托区分页面按钮与App根节点，防止开关点击导致整页重建；修正工具栏定位与唱片封面排版。失败已定位后按影响范围复验，最终结果以上表为准。

动态行为已在运行的UI宿主/Chromium中检查：返回保护、导航过渡后操作、实际滚动触发紧凑标题、减少动态效果、播放器暂停保留歌曲名。没有测量物理设备动画帧率，也没有声称达到Apple原生的交互物理或Liquid Glass光学表现。

未覆盖：实体安卓的IME、系统栏、预测性返回、TalkBack实读、低端性能；Flutter/RN原生APK与路由宿主；账户、无线设置、真实音频播放与业务落盘。Actions未运行；没有发布正式App或公开站点。视觉是否达到用户目标尚未获用户认可。来源与差异见[原版对照](APPLE_APP_REFERENCES.md)。

---

# 验证记录 · 0.2 Apple视觉修正

日期：2026-10-21。基线为0.1主线`64721540a26c9521c5f108a7b6871d44cc68adb9`，本批已验证源码`e33d1d4c003d98b1968b6913609a62508599da1b`，通过[PR #2](https://github.com/h516df/orchard-ui/pull/2)合入main；后续回执只修改文档。用户未接受0.1的视觉，旧测试通过不能代表新的视觉被认可。

| 范围 | 本批命令 | 结果 |
| --- | --- | --- |
| 共享变量 | `python3 -m unittest discover -s tools/tests -v` | passed，3项，8主题方案的常用文字/新按钮色对、触控/字号、四适配层生成同步 |
| Android | `bash cloud/check.sh android`，完整JDK21/SDK36 | passed，release AAR/debug Catalog APK、13项实际Compose UI测试、两个模块lint |
| Flutter | `flutter analyze` / `flutter test`，SDK3.41.5 | passed，分析无问题，6项widget test：Cupertino控件、浮动导航/透明度回退、保存重试、返回保护、大字号/主题 |
| React Native | `npm run typecheck` / `npm test` | passed，6项renderer/unit test：控件语义、主题、保存保留、Provider确认取消/卸载、Android返回接线；没有构建RN APK |
| Web | `CHROMIUM_PATH=/usr/bin/chromium npm run test:web` | passed，5项：键盘选择、保存失败/重试、dialog焦点/Escape、8主题320px/200%基础字号、减少动效、浮动导航选择/不透明回退 |

共33项测试。Compose新增覆盖搜索/分类、导航切换保留输入/开关、Apple确认布局、降低透明度，并输出8张实际宿主渲染。代码/样本由同一工作树产生，预览复制前核对输出；新版本图不是概念图或图像生成结果。

视觉对照：[Apple基线](APPLE_VISUALS.md)；[新版预览](previews/README.md)。构建/测试证明可执行性与指定行为，不证明用户已认可外观。

已知边界：实体安卓上的系统栏、IME、预测性返回、TalkBack实际朗读、低端性能和动效顺滑度尚未验收；Flutter/RN原生APK与宿主路由未验。Compose API31+模糊的旧系统回退分支有实现，未声称每个旧系统/厂商设备都测过。RN默认不透明，可插入宿主模糊层；没有声称已实现Apple专有折射引擎。Actions仍为手动工作流，本批未运行，没有正式App发布。

本批其余静态检查与远端回执在HANDOFF记录；下方保留0.1历史，不能用于冒充0.2结果。旧版图片可在Git标签v0.1.0查看。

---

# 验证记录 · 0.1.0

日期：2026-10-21。已验证源码提交为 `8eecdc49f04526b30e65d4f5df31433f527393da`，通过 [PR #1](https://github.com/h516df/orchard-ui/pull/1) 合入 main；后续回执提交只更新文档。这里没有引用用户其他 App 的历史通过结果。

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
