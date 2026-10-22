package dev.h516.orchard

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** 44dp visible control inside a 48dp hit region. Host owns the action and platform routing. */
@Composable
fun OrchardRoundButton(icon: ImageVector, label: String, onClick: () -> Unit, modifier: Modifier = Modifier, tint: Color = MaterialTheme.colorScheme.onSurface, fill: Color? = null) {
    val source = orchardPressSource()
    val pressed by source.collectIsPressedAsState()
    Box(modifier.size(48.dp).alpha(if (pressed) .55f else 1f).clickable(source, null, role = Role.Button, onClick = onClick), contentAlignment = Alignment.Center) {
        if (fill != null) Box(Modifier.size(OrchardTokens.size_navigationControl.dp).background(fill, CircleShape), contentAlignment = Alignment.Center) { Icon(icon, label, tint = tint, modifier = Modifier.size(22.dp)) }
        else OrchardGlassSurface(Modifier.size(OrchardTokens.size_navigationControl.dp)) { Icon(icon, label, tint = tint, modifier = Modifier.size(22.dp).align(Alignment.Center)) }
    }
}

/** A large title scrolls away; the compact title takes its place without consuming extra space. */
@Composable
fun OrchardAppPage(title: String, modifier: Modifier = Modifier, subtitle: String? = null,
    onBack: (() -> Unit)? = null, onMore: () -> Unit, scroll: ScrollState = rememberScrollState(),
    bottomBar: (@Composable () -> Unit)? = null, background: Color = MaterialTheme.colorScheme.background, largeTitle: Boolean = true,
    content: @Composable ColumnScope.() -> Unit) {
    val density = LocalDensity.current
    val reduced = LocalOrchardReducedMotion.current
    val compactProgress by remember(scroll, density, reduced) { derivedStateOf {
        val y = scroll.value / density.density
        if (reduced) if (y > 72f) 1f else 0f else ((y - 48f) / 24f).coerceIn(0f, 1f)
    } }
    OrchardScaffold(modifier, bottomBar = bottomBar) { padding ->
        Box(Modifier.fillMaxSize().background(background)) {
            Column(Modifier.fillMaxSize().verticalScroll(scroll).padding(top = padding.calculateTopPadding(), bottom = padding.calculateBottomPadding() + 24.dp)) {
                if (largeTitle) Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp).heightIn(min = 56.dp), verticalAlignment = Alignment.CenterVertically) {
                    onBack?.let { OrchardRoundButton(OrchardIcons.Back, "返回", it) }
                    Spacer(Modifier.weight(1f))
                    OrchardRoundButton(OrchardIcons.More, "更多", onMore)
                } else Spacer(Modifier.height(56.dp))
                if (largeTitle) Column(Modifier.padding(start = 24.dp, end = 24.dp, top = 6.dp, bottom = 24.dp)) {
                    OrchardLargeTitle(title)
                    subtitle?.let { Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyLarge) }
                }
                if (!largeTitle) Spacer(Modifier.height(28.dp))
                Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(28.dp), content = content)
            }
            if (compactProgress > 0f || !largeTitle) Surface(color = background.copy(alpha = .98f), modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter).graphicsLayer { alpha = if (largeTitle) compactProgress else 1f }) {
                Row(Modifier.statusBarsPadding().padding(horizontal = 18.dp).heightIn(min = 56.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (onBack != null) OrchardRoundButton(OrchardIcons.Back, "返回", onBack) else Spacer(Modifier.width(48.dp))
                    Text(title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    OrchardRoundButton(OrchardIcons.More, "更多", onMore)
                }
            }
        }
    }
}

/** Floating Notes-style search, separate compose action, no bottom tabs on a single-destination page. */
@Composable
fun OrchardSearchDock(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier, onCompose: (() -> Unit)? = null, label: String = "搜索") {
    val c = MaterialTheme.colorScheme
    Row(modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 24.dp).padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
        OrchardGlassSurface(Modifier.weight(1f)) {
            Row(Modifier.fillMaxWidth().heightIn(min = 50.dp).padding(start = 16.dp, end = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(OrchardIcons.Search, null, Modifier.size(21.dp), tint = c.onSurfaceVariant)
                BasicTextField(query, onQueryChange, modifier = Modifier.weight(1f).padding(vertical = 12.dp).semantics { contentDescription = label }, singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = c.onSurface), cursorBrush = SolidColor(c.primary),
                    decorationBox = { inner -> Box { if (query.isEmpty()) Text(label, style = MaterialTheme.typography.bodyLarge, color = c.onSurfaceVariant); inner() } })
                if (query.isNotEmpty()) OrchardIconButton(OrchardIcons.Close, "清除搜索", { onQueryChange("") }, Modifier.size(48.dp))
            }
        }
        onCompose?.let { OrchardRoundButton(OrchardIcons.Compose, "新建备忘录", it, Modifier.size(50.dp)) }
    }
}

/** Notes have a time/snippet and folder, without a disclosure chevron or right-aligned date. */
@Composable
fun OrchardNoteRow(title: String, preview: String, time: String, onClick: () -> Unit, modifier: Modifier = Modifier, folder: String = "备忘录", pinned: Boolean = false) {
    val c = MaterialTheme.colorScheme
    Column(modifier.fillMaxWidth().clickable(orchardPressSource(), null, role = Role.Button, onClick = onClick).padding(horizontal = 20.dp, vertical = 11.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            if (pinned) Icon(OrchardIcons.Pin, "已置顶", Modifier.size(13.dp), tint = c.onSurfaceVariant)
            Text(title, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Text("$time  $preview", style = MaterialTheme.typography.bodyMedium, color = c.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(OrchardIcons.Folder, null, Modifier.size(13.dp), tint = c.onSurfaceVariant)
            Text(folder, style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant)
        }
    }
}

/** UI only: playback and next track callbacks belong to the host's media engine. */
@Composable
fun OrchardMiniPlayer(title: String, playing: Boolean, onPlayPause: () -> Unit, onNext: () -> Unit, modifier: Modifier = Modifier, onOpen: (() -> Unit)? = null, artwork: @Composable () -> Unit = { Icon(OrchardIcons.Music, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(22.dp)) }) {
    val c = MaterialTheme.colorScheme
    OrchardGlassSurface(modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().heightIn(min = 50.dp).padding(start = 10.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(Modifier.size(32.dp).clip(androidx.compose.foundation.shape.RoundedCornerShape(6.dp)).background(c.onSurface.copy(alpha = .05f)), contentAlignment = Alignment.Center) { artwork() }
            Text(title, modifier = Modifier.weight(1f).then(if (onOpen != null) Modifier.clickable(role = Role.Button, onClickLabel = "打开正在播放", onClick = onOpen) else Modifier), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), maxLines = 1, overflow = TextOverflow.Ellipsis)
            OrchardIconButton(if (playing) OrchardIcons.Pause else OrchardIcons.Play, if (playing) "暂停" else "播放", onPlayPause, tint = c.onSurface)
            OrchardIconButton(OrchardIcons.Forward, "下一首", onNext, tint = c.onSurface)
        }
    }
}
