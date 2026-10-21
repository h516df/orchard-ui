package dev.h516.orchard

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** State and persistence belong to the consuming application. */
@Composable
fun <T> OrchardCollectionScreen(
    title: String, subtitle: String, items: List<T>, key: (T) -> Any,
    query: String, onQueryChange: (String) -> Unit,
    emptyTitle: String = "这里还很安静", emptyBody: String = "添加第一条内容，慢慢积累。",
    loading: Boolean = false, error: String? = null, onRetry: () -> Unit = {},
    actions: @Composable () -> Unit = {}, itemContent: @Composable (T) -> Unit,
) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(OrchardTokens.spacing_page.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { OrchardLargeTitle(title, subtitle = subtitle) }
        item { OrchardSearchField(query, onQueryChange) }
        item { actions() }
        when {
            loading && items.isEmpty() -> item { OrchardLoadingState() }
            error != null && items.isEmpty() -> item { OrchardErrorState(error, onRetry) }
            items.isEmpty() -> item { OrchardEmptyState(emptyTitle, emptyBody) }
            else -> {
                if (error != null) item { OrchardErrorState(error, onRetry) }
                items(items, key = key) { itemContent(it) }
            }
        }
    }
}

@Composable
fun OrchardSettingsScreen(title: String = "偏好设置", subtitle: String = "让它更合你的习惯", content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(OrchardTokens.spacing_page.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)) {
        OrchardLargeTitle(title, subtitle = subtitle)
        content()
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun OrchardEditorScreen(
    title: String, text: String, onTextChange: (String) -> Unit, dirty: Boolean,
    onSave: () -> Unit, onClose: () -> Unit, saving: Boolean = false, error: String? = null,
    label: String = "正文", discardMessage: String = "本次修改尚未保存，确定放弃吗？",
) {
    var confirmClose by rememberSaveable { mutableStateOf(false) }
    val requestClose = { if (!saving) { if (dirty) confirmClose = true else onClose() } }
    BackHandler { requestClose() }
    OrchardScaffold(topBar = { OrchardTopBar(title, requestClose) }) { padding ->
        Column(Modifier.padding(padding).imePadding().fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OrchardTextField(text, onTextChange, label, singleLine = false, enabled = !saving)
            error?.let { OrchardBanner("还没有保存", it, error = true) }
            OrchardButton("保存", onSave, Modifier.fillMaxWidth(), enabled = dirty, loading = saving)
            Text("保存成功后再离开；失败时保留输入。", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
    if (confirmClose) OrchardConfirmDialog("放弃修改？", discardMessage, "放弃修改",
        onConfirm = { confirmClose = false; onClose() }, onDismiss = { confirmClose = false }, destructive = true, cancelLabel = "继续编辑")
}

@Composable
fun OrchardImportReview(
    source: String, summary: String, warnings: List<String>, canImport: Boolean,
    onImport: () -> Unit, onCancel: () -> Unit, importing: Boolean = false,
    preview: @Composable () -> Unit,
) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OrchardLargeTitle("先看看，再收入", subtitle = source)
        Text(summary, style = MaterialTheme.typography.bodyLarge)
        warnings.forEach { OrchardBanner("需要留意", it) }
        preview()
        OrchardButton("确认导入", onImport, Modifier.fillMaxWidth(), enabled = canImport, loading = importing)
        OrchardButton("取消", onCancel, Modifier.fillMaxWidth(), style = OrchardButtonStyle.Plain, enabled = !importing)
    }
}

@Composable
fun OrchardReadingSurface(title: String, text: String, modifier: Modifier = Modifier, textSize: Float = 19f) {
    Box(modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        Column(Modifier.widthIn(max = OrchardTokens.size_readingMax.dp).fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
            Text(title, style = MaterialTheme.typography.headlineSmall)
            SelectionContainer { Text(text, style = MaterialTheme.typography.bodyLarge.copy(fontSize = textSize.coerceIn(14f, 36f).sp, lineHeight = (textSize.coerceIn(14f, 36f) * 1.7f).sp)) }
        }
    }
}
