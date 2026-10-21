package dev.h516.orchard.catalog

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
fun CatalogApp(initialDark: Boolean? = null) {
    var paletteKey by rememberSaveable { mutableStateOf("blue") }
    var mode by rememberSaveable { mutableStateOf("跟随系统") }
    var reduce by rememberSaveable { mutableStateOf(false) }
    var tab by rememberSaveable { mutableStateOf("components") }
    var screen by rememberSaveable { mutableStateOf("") }
    var input by rememberSaveable { mutableStateOf("") }
    var saved by rememberSaveable { mutableStateOf("") }
    var saveError by rememberSaveable { mutableStateOf<String?>(null) }
    var failNext by rememberSaveable { mutableStateOf(true) }
    val dark = initialDark ?: when (mode) { "浅色" -> false; "深色" -> true; else -> isSystemInDarkTheme() }
    val palette = OrchardPalette.entries.first { it.key == paletteKey }
    val view = LocalView.current
    SideEffect {
        (view.context as? Activity)?.window?.let { window ->
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !dark
                isAppearanceLightNavigationBars = !dark
            }
        }
    }
    OrchardTheme(palette, dark, reducedMotion = reduce || rememberOrchardReducedMotion()) {
        if (screen == "editor") {
            OrchardEditorScreen("随手记录", input, { input = it; saveError = null }, input != saved,
                onSave = {
                    if (failNext) { saveError = "演示一次保存失败：输入仍在，点保存即可重试。"; failNext = false }
                    else { saved = input; saveError = null; screen = "" }
                }, onClose = { input = saved; screen = "" }, error = saveError)
        } else {
            OrchardScaffold(bottomBar = {
                OrchardTabBar(listOf(
                    OrchardDestination("components", "组件", OrchardIcons.Grid),
                    OrchardDestination("patterns", "场景", OrchardIcons.Book),
                    OrchardDestination("appearance", "外观", OrchardIcons.Plus),
                ), tab, { tab = it })
            }) { padding ->
                Box(Modifier.padding(padding).fillMaxSize()) {
                    when (tab) {
                        "components" -> ComponentsPage()
                        "patterns" -> PatternsPage { screen = "editor"; failNext = true }
                        else -> OrchardSettingsScreen("你的风格", "同一份变量，适配不同技术栈") {
                            OrchardSection("主题配色", footnote = "修改语义变量后，各框架统一生成。") {
                                OrchardPalette.entries.forEach { p -> OrchardListRow(p.label, value = if (p == palette) "已选用" else null, onClick = { paletteKey = p.key }) }
                            }
                            OrchardSection("显示") {
                                Column(Modifier.padding(16.dp)) { OrchardSegments(listOf("跟随系统", "浅色", "深色"), mode, { mode = it }, { it }) }
                                OrchardDivider()
                                OrchardSwitchRow("减少动态效果", reduce, { reduce = it }, subtitle = "同时尊重系统的动画设置")
                            }
                            OrchardBanner("这是一份组件样本", "只使用合成内容，不连接账号，也不读取你的项目资料。")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ComponentsPage() {
    var text by rememberSaveable { mutableStateOf("") }
    var checked by rememberSaveable { mutableStateOf(true) }
    var selected by rememberSaveable { mutableStateOf("全部") }
    var progress by rememberSaveable { mutableFloatStateOf(0.6f) }
    var sheet by rememberSaveable { mutableStateOf(false) }
    var dialog by rememberSaveable { mutableStateOf(false) }
    val snack = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    OrchardScaffold(snackbarHost = { OrchardSnackbarHost(snack) }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
            OrchardLargeTitle("少一点打扰", subtitle = "果园 UI · 为日常应用准备的界面与体验")
            OrchardCard {
                OrchardBadge("跨框架 · 苹果风")
                Text("清晰，自然，留有余地。", style = MaterialTheme.typography.headlineSmall)
                Text("从一个按钮，到一次安心的保存。", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                OrchardButton("看看反馈", { scope.launch { snack.showSnackbar("已完成示例操作", actionLabel = "知道了") } })
            }
            OrchardSection("操作") {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OrchardButton("主要操作", { sheet = true }, Modifier.fillMaxWidth())
                    OrchardButton("次要操作", { dialog = true }, Modifier.fillMaxWidth(), style = OrchardButtonStyle.Soft)
                    OrchardButton("不可用", {}, Modifier.fillMaxWidth(), enabled = false)
                    OrchardButton("保存", {}, Modifier.fillMaxWidth(), loading = true)
                }
            }
            OrchardSection("输入与选择") {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OrchardTextField(text, { text = it }, "给它起个名字", hint = "长文字会自然换行")
                    OrchardSegments(listOf("全部", "最近", "已收藏"), selected, { selected = it }, { it })
                    OrchardSlider(progress, { progress = it }, "阅读进度")
                }
                OrchardSwitchRow("触感反馈", checked, { checked = it }, subtitle = "仅在明确完成操作时轻轻提示")
            }
            OrchardMetric("本周留给自己的时间", "2 小时", detail = "合成展示数据")
            OrchardBanner("内容只保存在这里", "导出备份后，可以在另一台设备继续。")
            OrchardErrorState("网络暂时不可用，已有内容仍可阅读。", { scope.launch { snack.showSnackbar("示例：已触发重试") } })
            OrchardProgress(progress, "进度 ${(progress * 100).toInt()}%")
            OrchardEmptyState("从第一条开始", "不用填满，留下此刻就好。")
        }
    }
    if (sheet) OrchardSheet("更多选择", { sheet = false }) {
        OrchardListRow("保留当前内容", subtitle = "弹层关闭后仍留在原页面")
        OrchardButton("完成", { sheet = false }, Modifier.fillMaxWidth())
    }
    if (dialog) OrchardConfirmDialog("演示确认", "确认后会显示一条反馈。", "确认",
        onConfirm = { dialog = false; scope.launch { snack.showSnackbar("示例操作已确认") } }, onDismiss = { dialog = false })
}

@Composable
private fun PatternsPage(onEditor: () -> Unit) {
    var import by rememberSaveable { mutableStateOf(false) }
    var imported by rememberSaveable { mutableStateOf(false) }
    OrchardSettingsScreen("熟悉的场景", "把体验规则也带进下一个项目") {
        OrchardSection("记录与阅读") {
            OrchardListRow("安心编辑", subtitle = "模拟保存失败、重试与未保存返回", onClick = onEditor)
            OrchardDivider()
            OrchardListRow("导入前预览", subtitle = if (imported) "示例条目已收入" else "先说明范围，再确认收入", onClick = { import = true })
        }
        OrchardReadingSurface("雨停之后", "窗外的树叶被洗得很亮。\n\n走回家的路上，买了一小束花。那些不经意的小事，慢慢成了日子的轮廓。\n\n这是用于排版展示的原创合成文字。")
        OrchardBanner("同一种体验，多种实现", "Compose、Flutter、React Native 和 Web 共用语义变量；各平台的返回与输入行为由对应实现处理。")
    }
    if (import) OrchardSheet("导入预览", { import = false }) {
        OrchardImportReview("合成示例.txt", "1 条记录 · 只展示流程", listOf("这是演示，不读取文件或写入你的记录。"), !imported,
            onImport = { imported = true; import = false }, onCancel = { import = false }) {
            OrchardCard { Text("雨停之后", style = MaterialTheme.typography.titleMedium); Text("一段用于验证预览的合成文字。") }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OrchardPreview() { CatalogApp(initialDark = false) }
