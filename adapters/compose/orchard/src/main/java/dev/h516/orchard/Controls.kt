package dev.h516.orchard

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class OrchardButtonStyle { Filled, Soft, Plain, Destructive }

@Composable
internal fun orchardPressSource(): MutableInteractionSource = remember { MutableInteractionSource() }

/** Press opacity and a full capsule; no Material ripple or minimum-width padding. */
@Composable
fun OrchardButton(
    text: String, onClick: () -> Unit, modifier: Modifier = Modifier,
    style: OrchardButtonStyle = OrchardButtonStyle.Filled,
    enabled: Boolean = true, loading: Boolean = false, loadingLabel: String = "正在处理",
) {
    val c = MaterialTheme.colorScheme
    val inactive = !enabled || loading
    val source = orchardPressSource()
    val pressed by source.collectIsPressedAsState()
    val background = when (style) {
        OrchardButtonStyle.Filled -> LocalOrchardControlColors.current.buttonFill
        OrchardButtonStyle.Soft -> c.primaryContainer
        OrchardButtonStyle.Plain -> Color.Transparent
        OrchardButtonStyle.Destructive -> c.error
    }
    val foreground = when (style) {
        OrchardButtonStyle.Filled -> LocalOrchardControlColors.current.buttonText
        OrchardButtonStyle.Soft -> c.onPrimaryContainer
        OrchardButtonStyle.Plain -> c.primary
        OrchardButtonStyle.Destructive -> c.onError
    }
    Box(modifier.heightIn(min = 48.dp).defaultMinSize(minWidth = 48.dp)
        .alpha(if (inactive) 0.42f else if (pressed) 0.65f else 1f)
        .clip(CircleShape).background(background)
        .clickable(source, indication = null, enabled = !inactive, role = Role.Button, onClick = onClick)
        .semantics { if (loading) { stateDescription = loadingLabel; liveRegion = LiveRegionMode.Polite } }
        .padding(horizontal = 20.dp, vertical = 11.dp), contentAlignment = Alignment.Center) {
        Text(if (loading) "$text · $loadingLabel" else text, color = foreground,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp, lineHeight = 22.sp), textAlign = TextAlign.Center)
    }
}

@Composable
fun OrchardIconButton(icon: ImageVector, label: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    val source = orchardPressSource()
    val pressed by source.collectIsPressedAsState()
    Box(modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp).alpha(if (!enabled) 0.4f else if (pressed) 0.5f else 1f)
        .clickable(source, indication = null, enabled = enabled, role = Role.Button, onClick = onClick), contentAlignment = Alignment.Center) {
        Icon(icon, contentDescription = label, modifier = Modifier.size(22.dp), tint = MaterialTheme.colorScheme.primary)
    }
}

/** Persistent label + inline value, never a floating label or outlined box. */
@Composable
fun OrchardTextField(
    value: String, onValueChange: (String) -> Unit, label: String,
    modifier: Modifier = Modifier, hint: String? = null, error: String? = null,
    enabled: Boolean = true, singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    val c = MaterialTheme.colorScheme
    val stacked = !singleLine || LocalDensity.current.fontScale > 1.5f
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        val input: @Composable () -> Unit = {
            BasicTextField(value, onValueChange, enabled = enabled, singleLine = singleLine,
                minLines = if (singleLine) 1 else 6,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = c.onSurface,
                    textAlign = if (stacked) TextAlign.Start else TextAlign.End),
                cursorBrush = SolidColor(c.primary), keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                modifier = Modifier.fillMaxWidth().semantics { contentDescription = label; if (error != null) this.error(error) },
                decorationBox = { inner -> if (singleLine) Box(Modifier.fillMaxWidth().heightIn(min = 48.dp), contentAlignment = if (stacked) Alignment.CenterStart else Alignment.CenterEnd) { inner() } else inner() })
        }
        if (stacked) Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(c.surface).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = c.onSurfaceVariant)
            input()
        } else Row(Modifier.fillMaxWidth().heightIn(min = 52.dp).background(c.surface).padding(horizontal = 16.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(label, style = MaterialTheme.typography.bodyLarge, color = c.onSurface)
            Box(Modifier.weight(1f)) { input() }
        }
        (error ?: hint)?.let {
            Text(it, style = MaterialTheme.typography.bodySmall, color = if (error != null) c.error else c.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp).semantics { if (error != null) liveRegion = LiveRegionMode.Polite })
        }
    }
}

@Composable
fun OrchardSearchField(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier, label: String = "搜索") {
    val c = MaterialTheme.colorScheme
    Row(modifier.fillMaxWidth().heightIn(min = 48.dp).clip(RoundedCornerShape(14.dp))
        .background(c.onSurface.copy(alpha = 0.055f)).padding(start = 12.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(OrchardIcons.Search, null, tint = c.onSurfaceVariant, modifier = Modifier.size(19.dp))
        BasicTextField(value, onValueChange, singleLine = true, textStyle = MaterialTheme.typography.bodyLarge.copy(color = c.onSurface),
            cursorBrush = SolidColor(c.primary), modifier = Modifier.weight(1f).padding(vertical = 10.dp).semantics { contentDescription = label },
            decorationBox = { input -> Box { if (value.isEmpty()) Text(label, style = MaterialTheme.typography.bodyLarge, color = c.onSurfaceVariant); input() } })
        if (value.isNotEmpty()) OrchardIconButton(OrchardIcons.Close, "清除搜索", { onValueChange("") }) else Spacer(Modifier.width(12.dp))
    }
}

@Composable
fun OrchardSwitchRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier = Modifier, subtitle: String? = null, enabled: Boolean = true) {
    val c = MaterialTheme.colorScheme
    val reduced = LocalOrchardReducedMotion.current
    val colors = LocalOrchardControlColors.current
    val track by animateColorAsState(if (checked) colors.toggleOn else colors.toggleOff, tween(if (reduced) 0 else 180), label = "switch-track")
    val offset by animateDpAsState(if (checked) 22.dp else 2.dp, spring(dampingRatio = 0.9f, stiffness = 550f), label = "switch-thumb")
    Row(modifier.fillMaxWidth().alpha(if (enabled) 1f else .4f)
        .toggleable(value = checked, interactionSource = orchardPressSource(), indication = null, enabled = enabled, role = Role.Switch, onValueChange = onCheckedChange)
        .padding(horizontal = 16.dp, vertical = 10.dp).heightIn(min = 34.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = c.onSurface)
            subtitle?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant) }
        }
        Box(Modifier.size(51.dp, 31.dp).clip(CircleShape).background(track)) {
            Box(Modifier.offset(x = if (reduced) if (checked) 22.dp else 2.dp else offset, y = 2.dp)
                .size(27.dp).shadow(2.dp, CircleShape).background(Color.White, CircleShape))
        }
    }
}

@Composable
fun OrchardCheckboxRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    val c = MaterialTheme.colorScheme
    Row(modifier.fillMaxWidth().toggleable(checked, interactionSource = orchardPressSource(), indication = null, enabled = enabled,
        role = Role.Checkbox, onValueChange = onCheckedChange).padding(horizontal = 16.dp, vertical = 10.dp).heightIn(min = 30.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(24.dp).clip(CircleShape).background(if (checked) c.primary else Color.Transparent)
            .then(if (!checked) Modifier.border(1.5.dp, c.outline, CircleShape) else Modifier), contentAlignment = Alignment.Center) {
            if (checked) Icon(OrchardIcons.Check, null, tint = c.onPrimary, modifier = Modifier.size(16.dp))
        }
        Text(title, style = MaterialTheme.typography.bodyLarge, color = c.onSurface)
    }
}

/** One continuous track and a sliding thumb, rather than separate filter chips. */
@Composable
fun <T> OrchardSegments(options: List<T>, selected: T, onSelect: (T) -> Unit, label: (T) -> String, modifier: Modifier = Modifier) {
    if (options.isEmpty()) return
    val c = MaterialTheme.colorScheme
    val control = LocalOrchardControlColors.current
    val large = LocalDensity.current.fontScale > 1.5f
    val reduced = LocalOrchardReducedMotion.current
    if (large) Column(modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(control.segmentTrack).padding(4.dp).selectableGroup()) {
        options.forEach { option ->
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(if (option == selected) control.segmentThumb else Color.Transparent)
                .selectable(option == selected, interactionSource = orchardPressSource(), indication = null, role = Role.RadioButton, onClick = { onSelect(option) })
                .padding(12.dp), contentAlignment = Alignment.Center) { Text(label(option), color = c.onSurface, style = MaterialTheme.typography.bodyMedium) }
        }
    } else BoxWithConstraints(modifier.fillMaxWidth().height(48.dp).selectableGroup(), contentAlignment = Alignment.CenterStart) {
        val cell = (maxWidth - 8.dp) / options.size
        val target = cell * options.indexOf(selected).coerceAtLeast(0)
        val offset by animateDpAsState(target, spring(dampingRatio = .86f, stiffness = 500f), label = "segmented-thumb")
        Box(Modifier.fillMaxWidth().height(36.dp).clip(CircleShape).background(control.segmentTrack))
        Box(Modifier.offset(x = 4.dp + if (reduced) target else offset).width(cell).height(30.dp)
            .shadow(2.dp, CircleShape).background(control.segmentThumb, CircleShape))
        Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
            options.forEach { option ->
                Box(Modifier.weight(1f).height(48.dp)
                    .selectable(option == selected, interactionSource = orchardPressSource(), indication = null, role = Role.RadioButton, onClick = { onSelect(option) }), contentAlignment = Alignment.Center) {
                    Text(label(option), color = c.onSurface, style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp), textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun OrchardSlider(value: Float, onValueChange: (Float) -> Unit, label: String, modifier: Modifier = Modifier, range: ClosedFloatingPointRange<Float> = 0f..1f) {
    val c = MaterialTheme.colorScheme
    Column(modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = c.onSurfaceVariant)
        Slider(value = value, onValueChange = onValueChange, valueRange = range,
            thumb = { Box(Modifier.size(27.dp).shadow(3.dp, CircleShape).background(Color.White, CircleShape)) },
            track = { state -> SliderDefaults.Track(state, thumbTrackGapSize = 0.dp, trackInsideCornerSize = 2.dp,
                modifier = Modifier.height(4.dp), colors = SliderDefaults.colors(inactiveTrackColor = c.onSurface.copy(alpha = .12f))) },
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = label })
    }
}
