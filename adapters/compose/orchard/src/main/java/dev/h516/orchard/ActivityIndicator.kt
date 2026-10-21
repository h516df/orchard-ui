package dev.h516.orchard

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate

@Composable
internal fun OrchardActivityIndicator(modifier: Modifier = Modifier) {
    val reduced = LocalOrchardReducedMotion.current
    val transition = rememberInfiniteTransition(label = "activity")
    val turn by transition.animateFloat(0f, 360f, infiniteRepeatable(tween(900, easing = LinearEasing)), label = "activity-turn")
    val color = MaterialTheme.colorScheme.onSurfaceVariant
    Canvas(modifier) {
        rotate(if (reduced) 0f else turn) {
            repeat(12) { spoke -> rotate(spoke * 30f) {
                drawLine(color.copy(alpha = (spoke + 1) / 12f), Offset(center.x, size.height * .12f), Offset(center.x, size.height * .31f),
                    strokeWidth = size.width * .09f, cap = StrokeCap.Round)
            } }
        }
    }
}
