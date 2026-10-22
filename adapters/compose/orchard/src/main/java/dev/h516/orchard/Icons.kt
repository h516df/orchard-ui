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
    private fun filled(name: String, draw: androidx.compose.ui.graphics.vector.PathBuilder.() -> Unit): ImageVector =
        ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.Black), pathBuilder = draw)
        }.build()
    val More = filled("More") { for (x in listOf(4f, 12f, 20f)) { moveTo(x+1.8f,12f); arcTo(1.8f,1.8f,0f,true,true,x-1.8f,12f); arcTo(1.8f,1.8f,0f,true,true,x+1.8f,12f); close() } }
    val Airplane = filled("Airplane") { moveTo(12f,1f); curveTo(14f,1f,14f,3f,14f,4f); lineTo(14f,9f); lineTo(23f,15f); lineTo(23f,18f); lineTo(14f,14f); lineTo(14f,20f); lineTo(17f,22f); lineTo(17f,24f); lineTo(12f,22f); lineTo(7f,24f); lineTo(7f,22f); lineTo(10f,20f); lineTo(10f,14f); lineTo(1f,18f); lineTo(1f,15f); lineTo(10f,9f); lineTo(10f,4f); curveTo(10f,3f,10f,1f,12f,1f); close() }
    val Home = filled("Home") { moveTo(2f,10f); lineTo(12f,2f); lineTo(22f,10f); lineTo(20f,12f); lineTo(20f,21f); lineTo(14.5f,21f); lineTo(14.5f,14f); lineTo(9.5f,14f); lineTo(9.5f,21f); lineTo(4f,21f); lineTo(4f,12f); close() }
    val Music = filled("Music") { moveTo(10f,5f); lineTo(22f,2f); lineTo(22f,17f); curveTo(22f,22f,14f,23f,14f,18f); curveTo(14f,15f,17f,14f,20f,15f); lineTo(20f,7f); lineTo(12f,9f); lineTo(12f,19f); curveTo(12f,24f,4f,25f,4f,20f); curveTo(4f,17f,7f,16f,10f,17f); close() }
    val Library = filled("Library") { moveTo(5f,2f); lineTo(19f,2f); lineTo(19f,4f); lineTo(5f,4f); close(); moveTo(3f,6f); lineTo(21f,6f); lineTo(21f,8f); lineTo(3f,8f); close(); moveTo(2f,10f); lineTo(22f,10f); lineTo(22f,23f); lineTo(2f,23f); close() }
    val Radio = icon("Radio") { moveTo(3f,5f); curveTo(-1f,9f,-1f,15f,3f,19f); moveTo(6f,8f); curveTo(3f,10f,3f,14f,6f,16f); moveTo(21f,5f); curveTo(25f,9f,25f,15f,21f,19f); moveTo(18f,8f); curveTo(21f,10f,21f,14f,18f,16f); moveTo(12f,11f); lineTo(12f,21f); moveTo(13.5f,9f); arcTo(1.5f,1.5f,0f,true,true,10.5f,9f); arcTo(1.5f,1.5f,0f,true,true,13.5f,9f) }
    val Play = filled("Play") { moveTo(6f,3f); lineTo(21f,12f); lineTo(6f,21f); close() }
    val Pause = filled("Pause") { moveTo(5f,3f); lineTo(10f,3f); lineTo(10f,21f); lineTo(5f,21f); close(); moveTo(14f,3f); lineTo(19f,3f); lineTo(19f,21f); lineTo(14f,21f); close() }
    val Forward = filled("Forward") { moveTo(2f,4f); lineTo(13f,12f); lineTo(2f,20f); close(); moveTo(12f,4f); lineTo(23f,12f); lineTo(12f,20f); close() }
    val Compose = icon("Compose") { moveTo(13f,4f); lineTo(4f,4f); lineTo(4f,21f); lineTo(21f,21f); lineTo(21f,12f); moveTo(10f,14f); lineTo(11f,10f); lineTo(20f,1f); lineTo(23f,4f); lineTo(14f,13f); close() }
    val Folder = icon("Folder") { moveTo(2f,6f); lineTo(10f,6f); lineTo(12f,9f); lineTo(22f,9f); lineTo(22f,21f); lineTo(2f,21f); close(); moveTo(2f,9f); lineTo(22f,9f) }
    val Pin = icon("Pin") { moveTo(9f,3f); lineTo(18f,3f); lineTo(16f,10f); lineTo(20f,14f); lineTo(5f,14f); lineTo(9f,10f); close(); moveTo(12f,14f); lineTo(12f,23f) }
    val Trash = icon("Trash") { moveTo(3f,6f); lineTo(21f,6f); moveTo(8f,6f); lineTo(8f,2f); lineTo(16f,2f); lineTo(16f,6f); moveTo(5f,6f); lineTo(6f,22f); lineTo(18f,22f); lineTo(19f,6f); moveTo(10f,10f); lineTo(10f,18f); moveTo(14f,10f); lineTo(14f,18f) }
    val Wifi = icon("Wifi") { moveTo(2f,7f); curveTo(8f,2f,16f,2f,22f,7f); moveTo(5f,11f); curveTo(9f,7f,15f,7f,19f,11f); moveTo(8f,15f); curveTo(10f,13f,14f,13f,16f,15f); moveTo(12f,19f); lineTo(12f,19.1f) }
    val Bluetooth = icon("Bluetooth") { moveTo(12f,2f); lineTo(19f,8f); lineTo(5f,18f); moveTo(5f,6f); lineTo(19f,16f); lineTo(12f,22f); lineTo(12f,2f) }
    val Sun = icon("Sun") { moveTo(16f,12f); arcTo(4f,4f,0f,true,true,8f,12f); arcTo(4f,4f,0f,true,true,16f,12f); for (i in 0..7) { val a=i*Math.PI/4; moveTo(12f+8f*kotlin.math.cos(a).toFloat(),12f+8f*kotlin.math.sin(a).toFloat()); lineTo(12f+10.5f*kotlin.math.cos(a).toFloat(),12f+10.5f*kotlin.math.sin(a).toFloat()) } }
    val Gear = icon("Gear") { moveTo(20f,12f); arcTo(8f,8f,0f,true,true,4f,12f); arcTo(8f,8f,0f,true,true,20f,12f); moveTo(15f,12f); arcTo(3f,3f,0f,true,true,9f,12f); arcTo(3f,3f,0f,true,true,15f,12f); for (i in 0..7) { val a=i*Math.PI/4; moveTo(12f+8f*kotlin.math.cos(a).toFloat(),12f+8f*kotlin.math.sin(a).toFloat()); lineTo(12f+10f*kotlin.math.cos(a).toFloat(),12f+10f*kotlin.math.sin(a).toFloat()) } }
    val Person = filled("Person") { moveTo(16f,7f); arcTo(4f,4f,0f,true,true,8f,7f); arcTo(4f,4f,0f,true,true,16f,7f); moveTo(3f,22f); curveTo(3f,11f,21f,11f,21f,22f); close() }
}
