package dev.h516.orchard

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/** Original geometric line icons. No Apple SF Symbols assets are bundled. */
object OrchardIcons {
    private fun icon(name: String, mirror: Boolean = false, draw: androidx.compose.ui.graphics.vector.PathBuilder.() -> Unit): ImageVector =
        ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f, autoMirror = mirror).apply {
            path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f,
                strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round, strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round, pathBuilder = draw)
        }.build()
    val Back = icon("Back", true) { moveTo(14f, 5f); lineTo(7f, 12f); lineTo(14f, 19f) }
    val Chevron = icon("Chevron", true) { moveTo(9f, 6f); lineTo(15f, 12f); lineTo(9f, 18f) }
    val Close = icon("Close") { moveTo(6f, 6f); lineTo(18f, 18f); moveTo(18f, 6f); lineTo(6f, 18f) }
    val Plus = icon("Plus") { moveTo(12f, 5f); lineTo(12f, 19f); moveTo(5f, 12f); lineTo(19f, 12f) }
    val Check = icon("Check") { moveTo(5f, 12f); lineTo(10f, 17f); lineTo(19f, 7f) }
    val Search = icon("Search") { moveTo(16f, 10f); curveTo(16f, 2f, 4f, 2f, 4f, 10f); curveTo(4f, 18f, 16f, 18f, 16f, 10f); moveTo(15f, 15f); lineTo(21f, 21f) }
    val Grid = icon("Grid") { moveTo(4f, 4f); lineTo(10f, 4f); lineTo(10f, 10f); lineTo(4f, 10f); close(); moveTo(14f, 4f); lineTo(20f, 4f); lineTo(20f, 10f); lineTo(14f, 10f); close(); moveTo(4f, 14f); lineTo(10f, 14f); lineTo(10f, 20f); lineTo(4f, 20f); close(); moveTo(14f, 14f); lineTo(20f, 14f); lineTo(20f, 20f); lineTo(14f, 20f); close() }
    val Book = icon("Book") { moveTo(12f, 6f); curveTo(9f, 4f, 5f, 4f, 3f, 5f); lineTo(3f, 19f); curveTo(6f, 18f, 9f, 18f, 12f, 20f); curveTo(15f, 18f, 18f, 18f, 21f, 19f); lineTo(21f, 5f); curveTo(18f, 4f, 15f, 4f, 12f, 6f); lineTo(12f, 20f) }
}
