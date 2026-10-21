package dev.h516.orchard

import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class OrchardPalette(val key: String, val label: String) {
    Blue("blue", "清透蓝"), Sage("sage", "纸感绿"), Peach("peach", "暖杏色"), Ink("ink", "墨色紫")
}

val LocalOrchardReducedMotion = staticCompositionLocalOf { false }
data class OrchardControlColors(val toggleOn: Color, val toggleOff: Color, val segmentTrack: Color, val segmentThumb: Color, val buttonFill: Color = Color(0xFF0066DD), val buttonText: Color = Color.White)
val LocalOrchardControlColors = staticCompositionLocalOf { OrchardControlColors(Color(0xFF34C759), Color(0xFFDCDCE0), Color(0xFFE5E5EA), Color.White) }

@Composable
fun rememberOrchardReducedMotion(): Boolean {
    val resolver = LocalContext.current.contentResolver
    fun read() = Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    var reduced by remember(resolver) { mutableStateOf(read()) }
    DisposableEffect(resolver) {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) { reduced = read() }
        }
        resolver.registerContentObserver(Settings.Global.getUriFor(Settings.Global.ANIMATOR_DURATION_SCALE), false, observer)
        onDispose { resolver.unregisterContentObserver(observer) }
    }
    return reduced
}

fun orchardColorScheme(palette: OrchardPalette, dark: Boolean): ColorScheme {
    val data = OrchardTokenData.schemes.getValue(palette.key).getValue(if (dark) "dark" else "light")
    fun c(key: String) = Color(data.getValue(key))
    val base = if (dark) darkColorScheme() else lightColorScheme()
    return base.copy(
        primary = c("primary"), onPrimary = c("onPrimary"),
        primaryContainer = c("primaryContainer"), onPrimaryContainer = c("onPrimaryContainer"),
        secondary = c("primary"), onSecondary = c("onPrimary"),
        secondaryContainer = c("primaryContainer"), onSecondaryContainer = c("onPrimaryContainer"),
        background = c("background"), onBackground = c("text"),
        surface = c("surface"), onSurface = c("text"), surfaceTint = Color.Transparent,
        surfaceVariant = c("surfaceMuted"), onSurfaceVariant = c("textSecondary"),
        surfaceContainer = c("surface"), surfaceContainerLow = c("surface"),
        surfaceContainerHigh = c("surfaceMuted"), surfaceContainerHighest = c("surfaceMuted"),
        surfaceContainerLowest = c("background"), surfaceBright = c("surface"), surfaceDim = c("background"),
        outline = c("outline"), outlineVariant = c("divider"),
        error = c("danger"), onError = c("onDanger"),
        errorContainer = c("dangerContainer"), onErrorContainer = c("onDangerContainer"),
    )
}

private val OrchardTypography = Typography(
    headlineLarge = TextStyle(fontSize = OrchardTokens.type_largeTitle.sp, lineHeight = 41.sp, letterSpacing = (-.8).sp, fontWeight = FontWeight.Bold),
    headlineSmall = TextStyle(fontSize = OrchardTokens.type_title.sp, lineHeight = 30.sp, fontWeight = FontWeight.SemiBold),
    titleLarge = TextStyle(fontSize = OrchardTokens.type_title.sp, lineHeight = 30.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontSize = OrchardTokens.type_body.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontSize = OrchardTokens.type_body.sp, lineHeight = 22.sp, letterSpacing = (-.2).sp),
    bodyMedium = TextStyle(fontSize = OrchardTokens.type_label.sp, lineHeight = 23.sp),
    labelLarge = TextStyle(fontSize = OrchardTokens.type_label.sp, lineHeight = 22.sp, fontWeight = FontWeight.SemiBold),
    labelMedium = TextStyle(fontSize = OrchardTokens.type_caption.sp, lineHeight = 19.sp),
    bodySmall = TextStyle(fontSize = OrchardTokens.type_caption.sp, lineHeight = 20.sp),
)

@Composable
fun OrchardTheme(
    palette: OrchardPalette = OrchardPalette.Blue,
    dark: Boolean = isSystemInDarkTheme(),
    reducedMotion: Boolean = rememberOrchardReducedMotion(),
    reducedTransparency: Boolean = false,
    content: @Composable () -> Unit,
) {
    val values = OrchardTokenData.schemes.getValue(palette.key).getValue(if (dark) "dark" else "light")
    CompositionLocalProvider(LocalOrchardReducedMotion provides reducedMotion,
        LocalOrchardReducedTransparency provides reducedTransparency,
        LocalOrchardControlColors provides OrchardControlColors(Color(values.getValue("toggleOn")), Color(values.getValue("toggleOff")), Color(values.getValue("segmentTrack")), Color(values.getValue("segmentThumb")), Color(values.getValue("buttonFill")), Color(values.getValue("buttonText")))) {
        MaterialTheme(
            colorScheme = orchardColorScheme(palette, dark), typography = OrchardTypography,
            shapes = Shapes(
                small = RoundedCornerShape(OrchardTokens.radius_control.dp),
                medium = RoundedCornerShape(OrchardTokens.radius_card.dp),
                large = RoundedCornerShape(OrchardTokens.radius_sheet.dp),
            ), content = content,
        )
    }
}
