package dev.h516.orchard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*

/** Directional hysteresis prevents a dock flickering around one scroll threshold. */
@Composable
fun rememberOrchardDockCollapsed(scroll: ScrollState): Boolean {
    var collapsed by remember(scroll) { mutableStateOf(false) }
    val density = LocalDensity.current
    LaunchedEffect(scroll, density) {
        var last = scroll.value
        var travel = 0
        snapshotFlow { scroll.value }.collect { value ->
            val delta = value - last
            travel = if ((delta > 0) == (travel > 0)) travel + delta else delta
            if (value < 48 * density.density || travel < -20 * density.density) collapsed = false
            else if (travel > 48 * density.density) collapsed = true
            last = value
        }
    }
    return collapsed && density.fontScale <= 1.5f
}

data class OrchardMenuAction(
    val label: String,
    val onSelect: () -> Unit = {},
    val children: List<OrchardMenuAction> = emptyList(),
    val destructive: Boolean = false,
)

/** Anchored, nested menu with outside/Back dismissal. Trigger remains owned by the caller. */
@Composable
fun OrchardMenu(expanded: Boolean, onDismiss: () -> Unit, actions: List<OrchardMenuAction>, modifier: Modifier = Modifier,
    trigger: @Composable () -> Unit) {
    val reduced = LocalOrchardReducedMotion.current
    val visibility = remember { MutableTransitionState(false) }
    visibility.targetState = expanded
    var parent by remember(expanded) { mutableStateOf<OrchardMenuAction?>(null) }
    val gap = with(LocalDensity.current) { 8.dp.roundToPx() }
    val position = remember(gap) { object : PopupPositionProvider {
        override fun calculatePosition(anchorBounds: IntRect, windowSize: IntSize, layoutDirection: LayoutDirection, popupContentSize: IntSize): IntOffset {
            val x = if (layoutDirection == LayoutDirection.Ltr) anchorBounds.right - popupContentSize.width else anchorBounds.left
            val below = anchorBounds.bottom + gap
            val y = if (below + popupContentSize.height <= windowSize.height - gap) below else anchorBounds.top - popupContentSize.height - gap
            return IntOffset(x.coerceIn(gap, (windowSize.width - popupContentSize.width - gap).coerceAtLeast(gap)), y.coerceIn(gap, (windowSize.height - popupContentSize.height - gap).coerceAtLeast(gap)))
        }
    } }
    Box(modifier) {
        trigger()
        if (visibility.currentState || visibility.targetState) Popup(popupPositionProvider = position, onDismissRequest = onDismiss, properties = PopupProperties(focusable = true)) {
            AnimatedVisibility(visibility, enter = fadeIn(if (reduced) snap() else tween(100)) + scaleIn(if (reduced) snap() else spring(dampingRatio = .85f), initialScale = .88f, transformOrigin = TransformOrigin(1f, 0f)), exit = fadeOut(if (reduced) snap() else tween(90))) {
                Surface(Modifier.widthIn(max = 280.dp).width(260.dp).semantics { paneTitle = "专辑操作" }, shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.surface.copy(alpha = .98f), shadowElevation = 20.dp) {
                    AnimatedContent(parent, transitionSpec = { if (reduced) EnterTransition.None togetherWith ExitTransition.None else fadeIn(tween(120)) togetherWith fadeOut(tween(90)) }, label = "submenu") { level ->
                        Column(Modifier.padding(6.dp)) {
                            if (level != null) {
                                Text("‹  返回", Modifier.fillMaxWidth().clickable(role = Role.Button) { parent = null }.padding(14.dp), color = MaterialTheme.colorScheme.primary)
                                HorizontalDivider(thickness = .5.dp)
                            }
                            (level?.children ?: actions).forEachIndexed { i, action ->
                                if (i > 0) HorizontalDivider(Modifier.padding(horizontal = 12.dp), thickness = .5.dp)
                                Row(Modifier.fillMaxWidth().clickable(role = Role.Button) { if (action.children.isNotEmpty()) parent = action else { onDismiss(); action.onSelect() } }.heightIn(min = 48.dp).padding(horizontal = 14.dp, vertical = 12.dp)) {
                                    Text(action.label, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge, color = if (action.destructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
                                    if (action.children.isNotEmpty()) Text("›", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
