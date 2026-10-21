package dev.h516.orchard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp

enum class OrchardButtonStyle { Filled, Soft, Plain, Destructive }

@Composable
fun OrchardButton(
    text: String, onClick: () -> Unit, modifier: Modifier = Modifier,
    style: OrchardButtonStyle = OrchardButtonStyle.Filled,
    enabled: Boolean = true, loading: Boolean = false, loadingLabel: String = "正在处理",
) {
    val c = MaterialTheme.colorScheme
    val colors = when (style) {
        OrchardButtonStyle.Filled -> ButtonDefaults.buttonColors()
        OrchardButtonStyle.Soft -> ButtonDefaults.buttonColors(containerColor = c.primaryContainer, contentColor = c.onPrimaryContainer)
        OrchardButtonStyle.Plain -> ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color.Transparent, contentColor = c.primary)
        OrchardButtonStyle.Destructive -> ButtonDefaults.buttonColors(containerColor = c.error, contentColor = c.onError)
    }
    Button(
        onClick = onClick, enabled = enabled && !loading, colors = colors,
        shape = RoundedCornerShape(OrchardTokens.radius_control.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        modifier = modifier.defaultMinSize(minHeight = OrchardTokens.size_touch.dp).semantics {
            if (loading) { stateDescription = loadingLabel; liveRegion = LiveRegionMode.Polite }
        },
    ) { Text(if (loading) "$text · $loadingLabel" else text) }
}

@Composable
fun OrchardIconButton(icon: ImageVector, label: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    IconButton(onClick = onClick, enabled = enabled, modifier = modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)) {
        Icon(icon, contentDescription = label)
    }
}

@Composable
fun OrchardTextField(
    value: String, onValueChange: (String) -> Unit, label: String,
    modifier: Modifier = Modifier, hint: String? = null, error: String? = null,
    enabled: Boolean = true, singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange, enabled = enabled,
        label = { Text(label) }, isError = error != null,
        supportingText = if (error != null || hint != null) {{ Text(error ?: hint.orEmpty()) }} else null,
        singleLine = singleLine, minLines = if (singleLine) 1 else 4,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(OrchardTokens.radius_control.dp), modifier = modifier.fillMaxWidth(),
    )
}

@Composable
fun OrchardSearchField(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier, label: String = "搜索") {
    OutlinedTextField(
        value = value, onValueChange = onValueChange, singleLine = true,
        placeholder = { Text(label) },
        leadingIcon = { Icon(OrchardIcons.Search, null) },
        trailingIcon = if (value.isNotEmpty()) {{ OrchardIconButton(OrchardIcons.Close, "清除搜索", { onValueChange("") }) }} else null,
        shape = RoundedCornerShape(OrchardTokens.radius_control.dp),
        modifier = modifier.fillMaxWidth().semantics { contentDescription = label },
    )
}

@Composable
fun OrchardSwitchRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier = Modifier, subtitle: String? = null, enabled: Boolean = true) {
    Row(
        modifier.fillMaxWidth().toggleable(value = checked, enabled = enabled, role = Role.Switch, onValueChange = onCheckedChange)
            .padding(horizontal = 16.dp, vertical = 12.dp).heightIn(min = 48.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            subtitle?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
        Switch(checked = checked, onCheckedChange = null, enabled = enabled)
    }
}

@Composable
fun OrchardCheckboxRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Row(modifier.fillMaxWidth().toggleable(checked, enabled = enabled, role = Role.Checkbox, onValueChange = onCheckedChange)
        .padding(horizontal = 12.dp, vertical = 4.dp).heightIn(min = 48.dp), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked, onCheckedChange = null, enabled = enabled)
        Text(title, modifier = Modifier.padding(start = 8.dp), style = MaterialTheme.typography.bodyLarge)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> OrchardSegments(options: List<T>, selected: T, onSelect: (T) -> Unit, label: (T) -> String, modifier: Modifier = Modifier) {
    FlowRow(modifier.fillMaxWidth().selectableGroup(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { option ->
            FilterChip(selected = option == selected, onClick = { onSelect(option) }, label = { Text(label(option)) },
                modifier = Modifier.heightIn(min = 48.dp).semantics { role = Role.RadioButton })
        }
    }
}

@Composable
fun OrchardSlider(value: Float, onValueChange: (Float) -> Unit, label: String, modifier: Modifier = Modifier, range: ClosedFloatingPointRange<Float> = 0f..1f) {
    Column(modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelLarge)
        Slider(value = value, onValueChange = onValueChange, valueRange = range,
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = label })
    }
}
