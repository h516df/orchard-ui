package dev.h516.orchard

import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.roundToInt
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties

@Composable
fun OrchardLargeTitle(title: String, modifier: Modifier = Modifier, subtitle: String? = null) {
    Column(modifier.fillMaxWidth().padding(top = 8.dp, bottom = 6.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(title, style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.semantics { heading() })
        subtitle?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}

@Composable
fun OrchardTopBar(title: String, onBack: (() -> Unit)? = null, actions: @Composable RowScope.() -> Unit = {}) {
    Box(Modifier.fillMaxWidth().statusBarsPadding().heightIn(min = 52.dp)) {
        Row(Modifier.align(Alignment.CenterStart).fillMaxWidth().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            onBack?.let { OrchardIconButton(OrchardIcons.Back, "返回", it) }
            Spacer(Modifier.weight(1f))
            actions()
        }
        Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.Center).padding(horizontal = 64.dp), textAlign = TextAlign.Center)
    }
}

data class OrchardDestination(val key: String, val label: String, val icon: ImageVector, val selectedIcon: ImageVector = icon)

/** Drag across the moving selection lens; release commits, cancellation restores selection. */
@Composable
fun OrchardTabBar(destinations: List<OrchardDestination>, selected: String, onSelect: (String) -> Unit, compact: Boolean = false) {
    require(destinations.isNotEmpty())
    val c = MaterialTheme.colorScheme
    val reduced = LocalOrchardReducedMotion.current
    val large = LocalDensity.current.fontScale > 1.5f
    val minimized = compact && !large
    val fraction by animateFloatAsState(if (minimized) .72f else 1f, if (reduced) snap() else spring(dampingRatio = .85f, stiffness = 450f), label = "dock-width")
    var dragging by remember { mutableStateOf<Float?>(null) }
    val activeIndex = destinations.indexOfFirst { it.key == selected }.coerceAtLeast(0)
    val lensIndex by animateFloatAsState(dragging ?: activeIndex.toFloat(), if (reduced) snap() else spring(dampingRatio = .78f, stiffness = 500f), label = "selection-lens")
    BoxWithConstraints(Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 8.dp).navigationBarsPadding(), contentAlignment = Alignment.Center) {
        val width = maxWidth * fraction
        val slot = (width - 8.dp) / destinations.size
        val slotPx = with(LocalDensity.current) { slot.toPx() }
        OrchardGlassSurface(Modifier.width(width).semantics { stateDescription = if (minimized) "导航已收起" else "导航已展开" }) {
            Box(Modifier.fillMaxWidth().padding(4.dp)) {
                Box(Modifier.offset(x = slot * lensIndex).width(slot).height(50.dp)
                    .clip(CircleShape).background(c.onSurface.copy(alpha = if (dragging != null) .12f else .08f)))
                Row(Modifier.fillMaxWidth().selectableGroup().pointerInput(destinations, selected, slotPx) {
                    var x = 0f
                    detectHorizontalDragGestures(
                        onDragStart = { x = it.x; dragging = (x / slotPx - .5f).coerceIn(0f, destinations.lastIndex.toFloat()) },
                        onHorizontalDrag = { change, delta -> change.consume(); x += delta; dragging = (x / slotPx - .5f).coerceIn(0f, destinations.lastIndex.toFloat()) },
                        onDragCancel = { dragging = null },
                        onDragEnd = { dragging?.let { onSelect(destinations[it.roundToInt()].key) }; dragging = null })
                }, verticalAlignment = Alignment.CenterVertically) {
                    destinations.forEach { destination ->
                        val active = selected == destination.key
                        Column(Modifier.weight(1f).clip(CircleShape)
                            .selectable(active, interactionSource = orchardPressSource(), indication = null, role = Role.Tab, onClick = { onSelect(destination.key) })
                            .semantics { contentDescription = destination.label }
                            .heightIn(min = 50.dp).padding(horizontal = 4.dp, vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Icon(if (active) destination.selectedIcon else destination.icon, null, tint = if (active) c.primary else c.onSurface, modifier = Modifier.size(24.dp))
                            androidx.compose.animation.AnimatedVisibility(!minimized, enter = androidx.compose.animation.expandVertically(if (reduced) snap() else tween(150)), exit = androidx.compose.animation.shrinkVertically(if (reduced) snap() else tween(150))) {
                                Text(destination.label, color = if (active) c.primary else c.onSurface,
                                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 10.sp, lineHeight = 13.sp, fontWeight = FontWeight.SemiBold))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrchardScaffold(
    modifier: Modifier = Modifier, topBar: @Composable () -> Unit = {}, bottomBar: (@Composable () -> Unit)? = null,
    snackbarHost: @Composable () -> Unit = {}, content: @Composable (PaddingValues) -> Unit,
) {
    val backdrop = rememberLayerBackdrop()
    CompositionLocalProvider(LocalOrchardBackdrop provides backdrop) {
        Scaffold(modifier = modifier.fillMaxSize(), containerColor = MaterialTheme.colorScheme.background,
            topBar = topBar, bottomBar = { bottomBar?.invoke() }, snackbarHost = snackbarHost) { padding ->
            Box(Modifier.fillMaxSize().layerBackdrop(backdrop)) {
                // A surface inside the recorded content must never sample its own graphics layer.
                CompositionLocalProvider(LocalOrchardBackdrop provides null) { content(padding) }
            }
        }
    }
}

@Composable
fun OrchardSnackbarHost(state: SnackbarHostState, modifier: Modifier = Modifier) {
    SnackbarHost(state, modifier.padding(horizontal = 20.dp)) { data ->
        Surface(shape = RoundedCornerShape(22.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 8.dp) {
            Row(Modifier.padding(start = 18.dp, end = 8.dp, top = 6.dp, bottom = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(data.visuals.message, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                data.visuals.actionLabel?.let { OrchardButton(it, { data.performAction() }, style = OrchardButtonStyle.Plain) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrchardSheet(title: String, onDismiss: () -> Unit, canDismiss: Boolean = true, onDismissBlocked: () -> Unit = {}, content: @Composable ColumnScope.() -> Unit) {
    val latestCanDismiss by rememberUpdatedState(canDismiss)
    val latestBlocked by rememberUpdatedState(onDismissBlocked)
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = false, confirmValueChange = { target ->
        if (target == SheetValue.Hidden && !latestCanDismiss) { latestBlocked(); false } else true
    })
    ModalBottomSheet(onDismissRequest = { if (latestCanDismiss) onDismiss() else latestBlocked() }, sheetState = state,
        containerColor = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = { Box(Modifier.padding(top = 8.dp).size(34.dp, 5.dp).background(MaterialTheme.colorScheme.onSurface.copy(alpha = .2f), CircleShape)) }) {
        Column(Modifier.fillMaxWidth().imePadding().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).padding(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(Modifier.fillMaxWidth().heightIn(min = 52.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Center).padding(horizontal = 48.dp), textAlign = TextAlign.Center)
                OrchardIconButton(OrchardIcons.Close, "关闭", { if (latestCanDismiss) onDismiss() else latestBlocked() }, Modifier.align(Alignment.CenterEnd))
            }
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrchardConfirmDialog(title: String, message: String, confirmLabel: String, onConfirm: () -> Unit, onDismiss: () -> Unit, destructive: Boolean = false, cancelLabel: String = "取消") {
    val c = MaterialTheme.colorScheme
    val large = LocalDensity.current.fontScale > 1.5f
    BasicAlertDialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(Modifier.widthIn(max = 320.dp).fillMaxWidth(.85f), shape = RoundedCornerShape(28.dp), color = c.surface) {
            Column {
                Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(title, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
                    Text(message, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                }
                HorizontalDivider(thickness = .5.dp, color = c.outlineVariant)
                val cancel: @Composable (Modifier) -> Unit = { m -> Box(m.clickable(orchardPressSource(), null, role = Role.Button, onClick = onDismiss).padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(cancelLabel, color = c.primary, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold), textAlign = TextAlign.Center) } }
                val confirm: @Composable (Modifier) -> Unit = { m -> Box(m.clickable(orchardPressSource(), null, role = Role.Button, onClick = onConfirm).padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(confirmLabel, color = if (destructive) c.error else c.primary, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center) } }
                if (large) Column { cancel(Modifier.fillMaxWidth()); HorizontalDivider(thickness = .5.dp, color = c.outlineVariant); confirm(Modifier.fillMaxWidth()) }
                else Row(Modifier.height(IntrinsicSize.Min)) { cancel(Modifier.weight(1f)); VerticalDivider(thickness = .5.dp, color = c.outlineVariant); confirm(Modifier.weight(1f)) }
            }
        }
    }
}
