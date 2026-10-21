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
    val Sliders = icon("Sliders") { moveTo(5f, 4f); lineTo(5f, 20f); moveTo(12f, 4f); lineTo(12f, 20f); moveTo(19f, 4f); lineTo(19f, 20f); moveTo(2f, 8f); lineTo(8f, 8f); moveTo(9f, 16f); lineTo(15f, 16f); moveTo(16f, 10f); lineTo(22f, 10f) }
    val Moon = icon("Moon") { moveTo(17f, 18f); curveTo(7f, 24f, -1f, 10f, 9f, 4f); curveTo(6f, 14f, 13f, 18f, 20f, 15f); lineTo(17f, 18f) }
    val Shield = icon("Shield") { moveTo(12f, 3f); lineTo(21f, 7f); lineTo(19f, 16f); lineTo(12f, 22f); lineTo(5f, 16f); lineTo(3f, 7f); close(); moveTo(8f, 12f); lineTo(11f, 15f); lineTo(16f, 9f) }
    val Sparkle = icon("Sparkle") { moveTo(12f, 2f); lineTo(15f, 9f); lineTo(22f, 12f); lineTo(15f, 15f); lineTo(12f, 22f); lineTo(9f, 15f); lineTo(2f, 12f); lineTo(9f, 9f); close() }
    val Bell = icon("Bell") { moveTo(5f, 17f); lineTo(7f, 14f); lineTo(7f, 9f); curveTo(7f, 2f, 17f, 2f, 17f, 9f); lineTo(17f, 14f); lineTo(19f, 17f); close(); moveTo(10f, 21f); lineTo(14f, 21f) }
}
