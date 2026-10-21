package dev.h516.orchard

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalDensity

internal class OrchardBackdrop(val layer: GraphicsLayer) {
    var origin by mutableStateOf(Offset.Zero)
}
internal val LocalOrchardBackdrop = staticCompositionLocalOf<OrchardBackdrop?> { null }
val LocalOrchardReducedTransparency = staticCompositionLocalOf { false }

/** Samples the sibling content layer. API24–30 and reduced transparency use an opaque surface. */
@Composable
fun OrchardGlassSurface(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    val c = MaterialTheme.colorScheme
    val backdrop = LocalOrchardBackdrop.current
    val blur = backdrop != null && Build.VERSION.SDK_INT >= 31 && !LocalOrchardReducedTransparency.current && LocalDensity.current.fontScale <= 1.5f
    val dark = c.background.luminance() < .2f
    var origin by remember { mutableStateOf(Offset.Zero) }
    val shape = RoundedCornerShape(50)
    Box(modifier.shadow(10.dp, shape, ambientColor = Color.Black.copy(alpha = .12f), spotColor = Color.Black.copy(alpha = .16f))
        .clip(shape).onGloballyPositioned { origin = it.positionInRoot() }) {
        if (blur) Box(Modifier.matchParentSize().graphicsLayer {
            renderEffect = BlurEffect(24.dp.toPx(), 24.dp.toPx(), TileMode.Clamp)
        }.drawBehind {
            translate(backdrop.origin.x - origin.x, backdrop.origin.y - origin.y) { drawLayer(backdrop.layer) }
        })
        Box(Modifier.matchParentSize().background(if (blur) c.surface.copy(alpha = if (dark) .76f else .72f) else c.surface))
        Box(Modifier.matchParentSize().border(.75.dp, Brush.verticalGradient(listOf(Color.White.copy(alpha = if (dark) .23f else .95f), c.outlineVariant.copy(alpha = .2f))), shape))
        content()
    }
}
