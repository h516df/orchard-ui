package dev.h516.orchard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp

@Composable
fun OrchardCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Surface(modifier.fillMaxWidth(), shape = RoundedCornerShape(OrchardTokens.radius_card.dp), color = MaterialTheme.colorScheme.surface) {
        Column(Modifier.padding(OrchardTokens.spacing_xl.dp), verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
    }
}

@Composable
fun OrchardGroup(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Surface(modifier.fillMaxWidth(), shape = RoundedCornerShape(OrchardTokens.radius_card.dp), color = MaterialTheme.colorScheme.surface) {
        Column(content = content)
    }
}

@Composable
fun OrchardSection(title: String, modifier: Modifier = Modifier, footnote: String? = null, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 12.dp).semantics { heading() })
        OrchardGroup(content = content)
        footnote?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 12.dp)) }
    }
}

@Composable
fun OrchardDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
fun OrchardListRow(title: String, modifier: Modifier = Modifier, subtitle: String? = null, value: String? = null, onClick: (() -> Unit)? = null, destructive: Boolean = false, leading: (@Composable () -> Unit)? = null) {
    val c = MaterialTheme.colorScheme
    Row(
        modifier.fillMaxWidth().then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier)
            .padding(16.dp).heightIn(min = 40.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically,
    ) {
        leading?.invoke()
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = if (destructive) c.error else c.onSurface)
            subtitle?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant) }
            value?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = c.primary) }
        }
        if (onClick != null) Icon(OrchardIcons.Chevron, null, tint = c.onSurfaceVariant)
    }
}

@Composable
fun OrchardBadge(text: String, modifier: Modifier = Modifier) {
    Surface(modifier, color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(50)) {
        Text(text, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
    }
}

@Composable
fun OrchardBanner(title: String, body: String, modifier: Modifier = Modifier, error: Boolean = false, action: (@Composable () -> Unit)? = null) {
    val c = MaterialTheme.colorScheme
    Surface(modifier.fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite },
        color = if (error) c.errorContainer else c.primaryContainer, shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val color = if (error) c.onErrorContainer else c.onPrimaryContainer
            Text(title, color = color, style = MaterialTheme.typography.titleMedium)
            Text(body, color = color, style = MaterialTheme.typography.bodyMedium)
            action?.invoke()
        }
    }
}

@Composable
fun OrchardMetric(label: String, value: String, modifier: Modifier = Modifier, detail: String? = null) {
    OrchardCard(modifier) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.headlineLarge)
        detail?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}

@Composable
fun OrchardEmptyState(title: String, body: String, modifier: Modifier = Modifier, action: (@Composable () -> Unit)? = null) {
    Column(modifier.fillMaxWidth().padding(vertical = 32.dp), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(OrchardIcons.Book, null, modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.primary)
        Text(title, style = MaterialTheme.typography.headlineSmall)
        Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        action?.invoke()
    }
}

@Composable
fun OrchardLoadingState(modifier: Modifier = Modifier, label: String = "正在加载") {
    Column(modifier.fillMaxWidth().padding(32.dp).semantics { liveRegion = LiveRegionMode.Polite },
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        CircularProgressIndicator(modifier = Modifier.size(28.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun OrchardErrorState(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    OrchardBanner("暂时没能完成", message, modifier, error = true) {
        OrchardButton("重试", onRetry, style = OrchardButtonStyle.Soft)
    }
}

@Composable
fun OrchardSkeleton(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth().semantics { contentDescription = "内容正在加载" }, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        repeat(3) { Box(Modifier.fillMaxWidth(if (it == 2) 0.6f else 1f).height(18.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))) }
    }
}

@Composable
fun OrchardProgress(progress: Float, label: String, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        LinearProgressIndicator(progress = { progress.coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth().semantics { contentDescription = label })
    }
}
