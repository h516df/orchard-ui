package dev.h516.orchard.catalog

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import dev.h516.orchard.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { CatalogApp() }
    }
}

@Composable
fun ComponentCatalogApp(initialDark: Boolean? = null, initialTab: String = "components", onExit: (() -> Unit)? = null) {
    var paletteKey by rememberSaveable { mutableStateOf("blue") }
    var mode by rememberSaveable { mutableStateOf("跟随系统") }
    var reduce by rememberSaveable { mutableStateOf(false) }
    var reduceTransparency by rememberSaveable { mutableStateOf(false) }
    var tab by rememberSaveable { mutableStateOf(initialTab) }
    var screen by rememberSaveable { mutableStateOf("") }
    var input by rememberSaveable { mutableStateOf("") }
    var saved by rememberSaveable { mutableStateOf("") }
    var saveError by rememberSaveable { mutableStateOf<String?>(null) }
    var failNext by rememberSaveable { mutableStateOf(true) }
    var sheet by rememberSaveable { mutableStateOf(false) }
    var dialog by rememberSaveable { mutableStateOf(false) }
    var imported by rememberSaveable { mutableStateOf(false) }
    val dark = initialDark ?: when (mode) { "浅色" -> false; "深色" -> true; else -> isSystemInDarkTheme() }
    val tabs = rememberSaveableStateHolder()
    val palette = OrchardPalette.entries.first { it.key == paletteKey }
    val snack = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val notify: (String) -> Unit = { message -> scope.launch { snack.showSnackbar(message, actionLabel = "好") } }
    val view = LocalView.current
    SideEffect {
        (view.context as? Activity)?.window?.let { window ->
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !dark
                isAppearanceLightNavigationBars = !dark
            }
        }
    }
    OrchardTheme(palette, dark, reducedMotion = reduce || rememberOrchardReducedMotion(), reducedTransparency = reduceTransparency) {
        when (screen) {
            "editor" -> OrchardEditorScreen("新建记录", input, { input = it; saveError = null }, input != saved,
                onSave = {
                    if (failNext) { saveError = "示例保存失败，输入已保留。可以重试。"; failNext = false }
                    else { saved = input; saveError = null; screen = ""; notify("已保存") }
                }, onClose = { input = saved; screen = "" }, error = saveError)
            "reader" -> OrchardScaffold(topBar = { OrchardTopBar("阅读", { screen = "" }) }) { padding ->
                Column(Modifier.padding(padding).verticalScroll(rememberScrollState())) {
                    OrchardReadingSurface("雨停之后", "窗外的树叶被洗得很亮。\n\n走回家的路上，买了一小束花。那些不经意的小事，慢慢成了日子的轮廓。\n\n这是用于排版展示的原创合成文字。")
                }
            }
            else -> OrchardScaffold(bottomBar = {
                OrchardTabBar(listOf(
                    OrchardDestination("components", "浏览", OrchardIcons.Grid),
                    OrchardDestination("patterns", "场景", OrchardIcons.Book),
                    OrchardDestination("appearance", "外观", OrchardIcons.Sliders),
                ), tab, { tab = it })
            }, snackbarHost = { OrchardSnackbarHost(snack) }) { padding ->
                // Padding belongs inside each scroll view, so content can scroll behind the glass bar.
                Box(Modifier.padding(top = padding.calculateTopPadding()).fillMaxSize()) {
                    tabs.SaveableStateProvider(tab) { when (tab) {
                        "components" -> ComponentsPage(padding.calculateBottomPadding(), onExit, { sheet = true }, { dialog = true }, notify) {
                            mode = if (dark) "浅色" else "深色"
                        }
                        "patterns" -> PatternsPage(padding.calculateBottomPadding(),
                            onEditor = { screen = "editor"; failNext = true }, onReader = { screen = "reader" }, onImport = { sheet = true })
                        else -> AppearancePage(padding.calculateBottomPadding(), palette, { paletteKey = it.key }, mode, { mode = it }, reduce, { reduce = it }, reduceTransparency, { reduceTransparency = it })
                    } }
                }
            }
        }
        if (sheet) OrchardSheet("导入预览", { sheet = false }) {
            OrchardImportReview("合成示例.txt", "1 条记录", emptyList(), !imported,
                onImport = { imported = true; sheet = false; notify("示例条目已收入") }, onCancel = { sheet = false }) {
                OrchardGroup { OrchardListRow("雨停之后", subtitle = "用于演示的合成文字，不读取你的文件。") }
            }
        }
        if (dialog) OrchardConfirmDialog("删除这条记录？", "这是一个确认样例，不会删除你的资料。", "删除",
            onConfirm = { dialog = false; notify("示例操作已确认") }, onDismiss = { dialog = false }, destructive = true)
    }
}

@Composable
private fun CatalogPage(bottomPadding: Dp, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = bottomPadding + 24.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp), content = content)
}

@Composable
private fun ComponentsPage(bottomPadding: Dp, onExit: (() -> Unit)?, onSheet: () -> Unit, onDialog: () -> Unit, notify: (String) -> Unit, onToggleMode: () -> Unit) {
    var name by rememberSaveable { mutableStateOf("果园 UI") }
    var query by rememberSaveable { mutableStateOf("") }
    var haptics by rememberSaveable { mutableStateOf(true) }
    var sync by rememberSaveable { mutableStateOf(false) }
    var range by rememberSaveable { mutableStateOf("每天") }
    var category by rememberSaveable { mutableStateOf("控件") }
    var slider by rememberSaveable { mutableFloatStateOf(.62f) }
    val c = MaterialTheme.colorScheme
    val large = LocalDensity.current.fontScale > 1.5f
    CatalogPage(bottomPadding) {
        Column {
            Row(Modifier.fillMaxWidth().heightIn(min = 36.dp), verticalAlignment = Alignment.CenterVertically) {
                if (onExit != null) OrchardIconButton(OrchardIcons.Back, "返回参考页面", onExit)
                Text("ORCHARD", style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.7.sp, fontSize = 11.sp), color = c.onSurfaceVariant, modifier = Modifier.weight(1f))
                OrchardIconButton(OrchardIcons.Moon, "切换明暗", onToggleMode, Modifier.size(36.dp))
            }
            OrchardLargeTitle("组件", subtitle = "熟悉的细节，恰到好处。")
            Spacer(Modifier.height(14.dp))
            OrchardSearchField(query, { query = it }, label = "搜索组件")
        }
        OrchardSegments(listOf("控件", "列表", "反馈"), category, { category = it }, { it })
        val match: (String) -> Boolean = { text -> query.isBlank() || text.contains(query, ignoreCase = true) }
        if (category == "控件") {
            if (match("按钮 继续 稍后 完成")) OrchardSection("按钮") {
                val buttons: @Composable RowScope.() -> Unit = {
                    OrchardButton("继续", onSheet, Modifier.weight(1f))
                    OrchardButton("稍后", { notify("已选择稍后") }, Modifier.weight(1f), style = OrchardButtonStyle.Soft)
                    OrchardButton("完成", { notify("示例操作已完成") }, Modifier.weight(1f), style = OrchardButtonStyle.Plain)
                }
                if (!large) Row(Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), content = buttons)
                else Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OrchardButton("继续", onSheet, Modifier.fillMaxWidth()); OrchardButton("稍后", { notify("已选择稍后") }, Modifier.fillMaxWidth(), style = OrchardButtonStyle.Soft)
                }
            }
            if (match("输入 名称")) OrchardSection("输入", footnote = "标签始终可见，内容可直接编辑。") { OrchardTextField(name, { name = it }, "名称") }
            if (match("偏好 触感反馈 自动播放")) OrchardSection("偏好") {
                OrchardSwitchRow("触感反馈", haptics, { haptics = it })
                OrchardDivider()
                OrchardSwitchRow("自动播放", sync, { sync = it })
            }
            if (match("选择 重复 每天 每周 每月")) OrchardSection("选择") {
                Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) { OrchardSegments(listOf("每天", "每周", "每月"), range, { range = it }, { it }) }
                OrchardDivider()
                OrchardListRow("重复", value = range, onClick = { range = when (range) { "每天" -> "每周"; "每周" -> "每月"; else -> "每天" } })
            }
            if (match("阅读 文字大小")) OrchardSection("阅读") { Column(Modifier.padding(16.dp)) { OrchardSlider(slider, { slider = it }, "文字大小") } }
        } else if (category == "列表") {
            if (match("分组列表 外观 提醒 隐私")) OrchardSection("分组列表") {
                OrchardListRow("外观", value = "跟随系统", onClick = onToggleMode, leading = { OrchardIconTile(OrchardIcons.Moon, Color(0xFF5856D6)) })
                OrchardDivider(leadingInset = 58.dp)
                OrchardListRow("提醒", value = "关闭", onClick = { notify("示例提醒设置") }, leading = { OrchardIconTile(OrchardIcons.Bell, Color(0xFFFF3B30)) })
                OrchardDivider(leadingInset = 58.dp)
                OrchardListRow("隐私", onClick = { notify("资料留在本机") }, leading = { OrchardIconTile(OrchardIcons.Shield, Color(0xFF007AFF)) })
            }
            if (match("内容 雨停之后 慢慢积累")) OrchardSection("内容") {
                OrchardListRow("雨停之后", subtitle = "留一点时间，给生活里的小事。", value = "今天", onClick = { notify("示例阅读条目") })
                OrchardDivider()
                OrchardListRow("慢慢积累", subtitle = "一段合成的展示文字。", value = "昨天", onClick = { notify("示例阅读条目") })
            }
        } else {
            if (match("提示 轻提示")) OrchardSection("提示") { OrchardListRow("轻提示", onClick = { notify("已完成") }, leading = { OrchardIconTile(OrchardIcons.Check, Color(0xFF34C759)) }) }
            if (match("提示 离线 内容已保留")) OrchardBanner("内容已保留", "暂时无法连接，可以稍后重试。")
            if (match("弹层 面板 确认 删除")) OrchardSection("弹层与确认") {
                OrchardListRow("底部面板", onClick = onSheet)
                OrchardDivider()
                OrchardListRow("删除确认", onClick = onDialog, destructive = true)
            }
            if (match("错误 重试")) OrchardErrorState("当前无法完成操作，输入仍在。", { notify("示例：已重新尝试") })
            if (match("进度")) OrchardProgress(.62f, "已完成 62%")
            if (match("空状态")) OrchardEmptyState("还没有内容", "从第一条开始，慢慢积累。")
        }
        val keywords = when (category) {
            "列表" -> listOf("分组列表 外观 提醒 隐私", "内容 雨停之后 慢慢积累")
            "反馈" -> listOf("提示 轻提示 离线 内容已保留", "弹层 面板 确认 删除", "错误 重试", "进度", "空状态")
            else -> listOf("按钮 继续 稍后 完成", "输入 名称", "偏好 触感反馈 自动播放", "选择 重复 每天 每周 每月", "阅读 文字大小")
        }
        if (query.isNotBlank() && !keywords.any(match)) {
            OrchardEmptyState("没有找到组件", "换一个关键词试试。")
        }
    }
}

@Composable
private fun AppearancePage(bottomPadding: Dp, palette: OrchardPalette, onPalette: (OrchardPalette) -> Unit, mode: String, onMode: (String) -> Unit,
    reduce: Boolean, onReduce: (Boolean) -> Unit, reduceTransparency: Boolean, onReduceTransparency: (Boolean) -> Unit) {
    val c = MaterialTheme.colorScheme
    CatalogPage(bottomPadding) {
        OrchardLargeTitle("显示与外观")
        OrchardSection("外观") {
            Row(Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                listOf("浅色", "深色", "跟随系统").forEach { choice ->
                    Column(Modifier.weight(1f).semantics { selected = choice == mode }.clickable(role = Role.RadioButton) { onMode(choice) }, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        val bg = if (choice == "深色") Color(0xFF171719) else Color(0xFFF2F2F7)
                        val fg = if (choice == "深色") Color(0xFF363638) else Color.White
                        Box(Modifier.size(58.dp, 92.dp).clip(RoundedCornerShape(12.dp)).background(bg).border(.7.dp, c.outlineVariant, RoundedCornerShape(12.dp)).padding(7.dp)) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Spacer(Modifier.height(5.dp))
                                Box(Modifier.width(24.dp).height(4.dp).background(if (choice == "深色") Color(0xFFE5E5EA) else Color(0xFF323236), CircleShape))
                                Spacer(Modifier.height(3.dp))
                                repeat(3) { Box(Modifier.fillMaxWidth().height(12.dp).background(fg, RoundedCornerShape(3.dp))) }
                            }
                            if (choice == "跟随系统") Box(Modifier.fillMaxHeight().width(21.dp).align(Alignment.CenterEnd).background(Color(0xFF171719).copy(alpha = .75f)))
                        }
                        Text(choice, style = MaterialTheme.typography.bodySmall, color = c.onSurface)
                        Box(Modifier.size(22.dp).clip(CircleShape).background(if (choice == mode) c.primary else Color.Transparent)
                            .then(if (choice != mode) Modifier.border(1.5.dp, c.outline, CircleShape) else Modifier), contentAlignment = Alignment.Center) {
                            if (choice == mode) Icon(OrchardIcons.Check, null, tint = c.onPrimary, modifier = Modifier.size(15.dp))
                        }
                    }
                }
            }
        }
        OrchardSection("强调色") {
            OrchardPalette.entries.forEachIndexed { index, p ->
                if (index > 0) OrchardDivider(leadingInset = 58.dp)
                OrchardListRow(p.label, value = if (p == palette) "已选用" else null, onClick = { onPalette(p) }, leading = {
                    OrchardIconTile(OrchardIcons.Sparkle, when (p) { OrchardPalette.Blue -> Color(0xFF007AFF); OrchardPalette.Sage -> Color(0xFF3C8455); OrchardPalette.Peach -> Color(0xFFD47B54); OrchardPalette.Ink -> Color(0xFF8B5CF6) })
                })
            }
        }
        OrchardSection("辅助功能", footnote = "同时尊重系统的减少动态效果设置。") {
            OrchardSwitchRow("减少动态效果", reduce, onReduce)
            OrchardDivider()
            OrchardSwitchRow("降低透明度", reduceTransparency, onReduceTransparency)
        }
    }
}

@Composable
private fun PatternsPage(bottomPadding: Dp, onEditor: () -> Unit, onReader: () -> Unit, onImport: () -> Unit) {
    CatalogPage(bottomPadding) {
        OrchardLargeTitle("场景", subtitle = "从组件，到完整的一页。")
        OrchardSection("记录与阅读") {
            OrchardListRow("安心编辑", subtitle = "草稿、保存与返回", onClick = onEditor, leading = { OrchardIconTile(OrchardIcons.Plus, Color(0xFFFF9500)) })
            OrchardDivider(leadingInset = 58.dp)
            OrchardListRow("沉浸阅读", subtitle = "让正文成为主角", onClick = onReader, leading = { OrchardIconTile(OrchardIcons.Book, Color(0xFF007AFF)) })
            OrchardDivider(leadingInset = 58.dp)
            OrchardListRow("导入前预览", subtitle = "看清范围，再确认收入", onClick = onImport, leading = { OrchardIconTile(OrchardIcons.Grid, Color(0xFF34C759)) })
        }
        OrchardReadingSurface("雨停之后", "窗外的树叶被洗得很亮。\n\n走回家的路上，买了一小束花。\n\n这是用于排版展示的原创合成文字。")
    }
}

@Preview(showBackground = true)
@Composable
private fun OrchardPreview() { CatalogApp(initialDark = false) }
