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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy

internal val LocalOrchardBackdrop = staticCompositionLocalOf<LayerBackdrop?> { null }
val LocalOrchardReducedTransparency = staticCompositionLocalOf { false }
/** Continuous tint from clear (0) to fully tinted (1); content remains opaque. */
val LocalOrchardGlassTint = staticCompositionLocalOf { OrchardTokens.glass_tint / 100f }

/** Sibling-only sampling. API33+ lens; API31–32 blur; older/reduced transparency opaque. */
@Composable
fun OrchardGlassSurface(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    val c = MaterialTheme.colorScheme
    val backdrop = LocalOrchardBackdrop.current
    val tint = LocalOrchardGlassTint.current
    val glass = backdrop != null && Build.VERSION.SDK_INT >= 31 && !LocalOrchardReducedTransparency.current && LocalDensity.current.fontScale <= 1.5f
    val dark = c.background.luminance() < .2f
    val shape = RoundedCornerShape(50)
    val surface = if (glass) Modifier.drawBackdrop(
        backdrop = backdrop, shape = { shape },
        effects = {
            vibrancy()
            blur((OrchardTokens.glass_blurMin + tint * (OrchardTokens.glass_blurMax - OrchardTokens.glass_blurMin)).dp.toPx())
            lens(OrchardTokens.glass_lensRim.dp.toPx(), OrchardTokens.glass_lensDepth.dp.toPx(), chromaticAberration = true)
        },
        onDrawSurface = { drawRect(c.surface.copy(alpha = .18f + .78f * tint)) },
    ) else Modifier.background(c.surface, shape)
    Box(modifier.shadow(10.dp, shape, ambientColor = Color.Black.copy(alpha = .12f), spotColor = Color.Black.copy(alpha = .16f))
        .then(surface).clip(shape)
        .border(.75.dp, Brush.verticalGradient(listOf(Color.White.copy(alpha = if (dark) .28f else .85f), c.outlineVariant.copy(alpha = .2f))), shape)) { content() }
}
