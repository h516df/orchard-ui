package dev.h516.orchard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun OrchardLargeTitle(title: String, modifier: Modifier = Modifier, subtitle: String? = null) {
    Column(modifier.fillMaxWidth().padding(vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.headlineLarge, modifier = Modifier.semantics { heading() })
        subtitle?.let { Text(it, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrchardTopBar(title: String, onBack: (() -> Unit)? = null, actions: @Composable RowScope.() -> Unit = {}) {
    TopAppBar(
        title = { Text(title, style = MaterialTheme.typography.titleMedium) },
        navigationIcon = { onBack?.let { OrchardIconButton(OrchardIcons.Back, "返回", it) } },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
    )
}

data class OrchardDestination(val key: String, val label: String, val icon: ImageVector)

@Composable
fun OrchardTabBar(destinations: List<OrchardDestination>, selected: String, onSelect: (String) -> Unit) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 0.dp) {
        destinations.forEach { destination ->
            NavigationBarItem(selected = selected == destination.key, onClick = { onSelect(destination.key) },
                icon = { Icon(destination.icon, null) }, label = { Text(destination.label) })
        }
    }
}

@Composable
fun OrchardScaffold(
    modifier: Modifier = Modifier, topBar: @Composable () -> Unit = {}, bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {}, content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(modifier = modifier.fillMaxSize(), containerColor = MaterialTheme.colorScheme.background,
        topBar = topBar, bottomBar = bottomBar, snackbarHost = snackbarHost, content = content)
}

@Composable
fun OrchardSnackbarHost(state: SnackbarHostState, modifier: Modifier = Modifier) {
    SnackbarHost(state, modifier) { data -> Snackbar(data, shape = MaterialTheme.shapes.small) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrchardSheet(title: String, onDismiss: () -> Unit, canDismiss: Boolean = true, onDismissBlocked: () -> Unit = {}, content: @Composable ColumnScope.() -> Unit) {
    // Reading latest values avoids capturing a stale dirty flag in rememberModalBottomSheetState.
    val latestCanDismiss by rememberUpdatedState(canDismiss)
    val latestBlocked by rememberUpdatedState(onDismissBlocked)
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true, confirmValueChange = { target ->
        if (target == SheetValue.Hidden && !latestCanDismiss) { latestBlocked(); false } else true
    })
    ModalBottomSheet(onDismissRequest = { if (latestCanDismiss) onDismiss() else latestBlocked() }, sheetState = state,
        containerColor = MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.large) {
        Column(Modifier.fillMaxWidth().imePadding().padding(horizontal = 24.dp).padding(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(title, style = MaterialTheme.typography.headlineSmall)
            content()
        }
    }
}

@Composable
fun OrchardConfirmDialog(title: String, message: String, confirmLabel: String, onConfirm: () -> Unit, onDismiss: () -> Unit, destructive: Boolean = false, cancelLabel: String = "取消") {
    AlertDialog(
        onDismissRequest = onDismiss, title = { Text(title) }, text = { Text(message) },
        confirmButton = { TextButton(onClick = onConfirm) { Text(confirmLabel, color = if (destructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(cancelLabel) } },
        containerColor = MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium,
    )
}
