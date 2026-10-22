package dev.h516.orchard.catalog

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import dev.h516.orchard.*

private val MusicRed = Color(0xFFD7003B)
private val NotesGold = Color(0xFFB28600)

/** Local synthetic content only. Never changes Android system settings or reads a media library. */
@Composable
fun CatalogApp(initialDark: Boolean? = null, initialPage: String = "settings", initialDetail: String = "") {
    var page by rememberSaveable { mutableStateOf(initialPage) }
    var detail by rememberSaveable { mutableStateOf(initialDetail) }
    var menu by rememberSaveable { mutableStateOf(false) }
    var mode by rememberSaveable { mutableStateOf("跟随系统") }
    var reduce by rememberSaveable { mutableStateOf(false) }
    var opaque by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    var notesQuery by rememberSaveable { mutableStateOf("") }
    var airplane by rememberSaveable { mutableStateOf(false) }
    var wifi by rememberSaveable { mutableStateOf(true) }
    var bluetooth by rememberSaveable { mutableStateOf(true) }
    var selectedNote by rememberSaveable { mutableIntStateOf(-1) }
    var editor by rememberSaveable { mutableStateOf(false) }
    var draftTitle by rememberSaveable { mutableStateOf("") }
    var draftBody by rememberSaveable { mutableStateOf("") }
    var discard by rememberSaveable { mutableStateOf(false) }
    var notes by rememberSaveable { mutableStateOf(listOf(
        "周末的小计划\n买一束花，去河边散步。\n\n把想做的事写下来，留一点时间给自己。",
        "灵感随记\n让日常的记录，回到简单。\n\n少一点打扰，多一点专注。",
        "想读的书\n关于自然、建筑和生活的几本书。",
        "旅行清单\n相机、充电器、一本随身的小书。",
        "慢慢积累\n一段喜欢的话，一个突然想到的点子。",
        "雨停之后\n窗外的树叶被洗得很亮。\n\n走回家的路上，买了一小束花。")) }
    val referenceStates = rememberSaveableStateHolder()
    val systemDark = isSystemInDarkTheme()
    val dark = initialDark ?: when (mode) { "浅色" -> false; "深色" -> true; else -> systemDark }
    val reduced = reduce || rememberOrchardReducedMotion()
    val view = LocalView.current
    SideEffect { (view.context as? Activity)?.window?.let { WindowCompat.getInsetsController(it, view).apply { isAppearanceLightStatusBars = !dark; isAppearanceLightNavigationBars = !dark } } }
    val openNote: (Int) -> Unit = { i ->
        selectedNote = i
        draftTitle = if (i >= 0) notes[i].substringBefore('\n') else ""
        draftBody = if (i >= 0) notes[i].substringAfter('\n', "") else ""
        editor = true
    }
    val dirty = if (selectedNote in notes.indices) "$draftTitle\n$draftBody" != notes[selectedNote] else draftTitle.isNotBlank() || draftBody.isNotBlank()
    val closeEditor: () -> Unit = { if (dirty) discard = true else editor = false }
    BackHandler(enabled = editor || detail.isNotEmpty() || page != "settings") {
        when { editor -> closeEditor(); detail.isNotEmpty() -> detail = ""; else -> { page = "settings"; query = "" } }
    }
    if (page == "components") {
        ComponentCatalogApp(initialDark = dark, onExit = { page = "settings" })
        return
    }
    OrchardTheme(dark = dark, reducedMotion = reduced, reducedTransparency = opaque) {
        val c = MaterialTheme.colorScheme
        val musicScheme = c.copy(primary = if (dark) Color(0xFFFF536B) else MusicRed)
        AnimatedContent(targetState = if (editor) "editor" else if (page == "settings" && detail != "appearance") "settings/" else "$page/$detail", transitionSpec = {
            if (reduced) EnterTransition.None togetherWith ExitTransition.None
            else (fadeIn(tween(160)) + slideInHorizontally(tween(260)) { it / 8 }) togetherWith fadeOut(tween(120))
        }, label = "reference-navigation") { target ->
            when {
                target == "editor" -> ReferenceNoteEditor(draftTitle, draftBody, { draftTitle = it }, { draftBody = it }, closeEditor, {
                    val entry = "${draftTitle.ifBlank { "新建备忘录" }}\n$draftBody"
                    notes = notes.toMutableList().apply { if (selectedNote >= 0) set(selectedNote, entry) else add(0, entry) }
                    editor = false
                }, { if (selectedNote >= 0) { notes = notes.filterIndexed { index, _ -> index != selectedNote }; editor = false } else editor = false })
                target == "settings/appearance" -> DisplayReference(mode, { mode = it }, reduce, { reduce = it }, opaque, { opaque = it }, { detail = "" }, { menu = true })
                target.startsWith("settings/") -> SettingsReference(query, { query = it }, airplane, { airplane = it }, wifi, bluetooth, { detail = it }, { menu = true })
                target.startsWith("notes/") -> NotesReference(notes, notesQuery, { notesQuery = it }, { openNote(it) }, { page = "settings" }, { menu = true })
                else -> referenceStates.SaveableStateProvider("music") { MaterialTheme(colorScheme = musicScheme) { MusicReference({ menu = true }, dark) } }
            }
        }
        if (detail in listOf("wifi", "bluetooth", "account", "general", "notifications", "privacy", "sounds")) {
            val title = when (detail) { "wifi" -> "无线局域网"; "bluetooth" -> "蓝牙"; "account" -> "个人资料"; "general" -> "通用"; "notifications" -> "通知"; "privacy" -> "隐私与安全性"; else -> "声音与触感" }
            OrchardSheet(title, { detail = "" }) {
                when (detail) {
                    "wifi" -> { OrchardGroup { OrchardSwitchRow("无线局域网", wifi, { wifi = it }) }; Text(if (wifi) "已连接 · Orchard" else "未连接", style = MaterialTheme.typography.bodyMedium) }
                    "bluetooth" -> OrchardGroup { OrchardSwitchRow("蓝牙", bluetooth, { bluetooth = it }) }
                    "account" -> { Text("h516df", style = MaterialTheme.typography.headlineSmall); Text("这是合成的本机账户展示，不接入账户服务。", style = MaterialTheme.typography.bodyMedium) }
                    else -> { OrchardGroup { OrchardSwitchRow("触感反馈", !reduce, { reduce = !it }); OrchardDivider(); OrchardSwitchRow("降低透明度", opaque, { opaque = it }) }; Text("这里的选项仅作用于展示 App。", style = MaterialTheme.typography.bodySmall) }
                }
            }
        }
        if (menu) OrchardSheet("参考页面", { menu = false }) {
            listOf("settings" to "设置", "notes" to "备忘录", "music" to "音乐", "components" to "组件目录").forEach { (key, label) ->
                OrchardListRow(label, onClick = { menu = false; detail = ""; editor = false; page = key }, leading = {
                    OrchardIconTile(when (key) { "notes" -> OrchardIcons.Compose; "music" -> OrchardIcons.Music; "components" -> OrchardIcons.Grid; else -> OrchardIcons.Gear }, when (key) { "notes" -> NotesGold; "music" -> MusicRed; else -> Color(0xFF8E8E93) })
                })
            }
        }
        if (discard) OrchardConfirmDialog("放弃修改？", "本次修改尚未保存。", "放弃修改", { discard = false; editor = false }, { discard = false }, destructive = true, cancelLabel = "继续编辑")
    }
}

@Composable
private fun SettingsReference(query: String, onQuery: (String) -> Unit, airplane: Boolean, onAirplane: (Boolean) -> Unit, wifi: Boolean, bluetooth: Boolean, open: (String) -> Unit, onMore: () -> Unit) {
    val match: (String) -> Boolean = { query.isBlank() || it.contains(query, true) }
    OrchardAppPage("设置", onMore = onMore, bottomBar = { OrchardSearchDock(query, onQuery) }) {
        if (match("h516df 个人资料 账户")) OrchardGroup {
            OrchardListRow("h516df", subtitle = "个人资料、同步与备份", onClick = { open("account") }, leading = {
                Box(Modifier.size(58.dp).background(Color(0xFFADADB4), CircleShape), contentAlignment = Alignment.Center) { Text("H", color = Color.White, fontSize = 29.sp, fontWeight = FontWeight.Medium) }
            })
        }
        if (match("飞行模式 无线局域网 蓝牙")) OrchardGroup {
            if (match("飞行模式")) OrchardSwitchRow("飞行模式", airplane, onAirplane, leading = { OrchardIconTile(OrchardIcons.Airplane, Color(0xFFFF9500)) })
            if (match("无线局域网")) { OrchardDivider(leadingInset = 58.dp); OrchardListRow("无线局域网", value = if (wifi) "Orchard" else "关闭", onClick = { open("wifi") }, leading = { OrchardIconTile(OrchardIcons.Wifi, Color(0xFF007AFF)) }) }
            if (match("蓝牙")) { OrchardDivider(leadingInset = 58.dp); OrchardListRow("蓝牙", value = if (bluetooth) "打开" else "关闭", onClick = { open("bluetooth") }, leading = { OrchardIconTile(OrchardIcons.Bluetooth, Color(0xFF007AFF)) }) }
        }
        val groups = listOf(
            listOf(Triple("通知", "notifications", OrchardIcons.Bell), Triple("声音与触感", "sounds", OrchardIcons.Radio)),
            listOf(Triple("通用", "general", OrchardIcons.Gear), Triple("显示与亮度", "appearance", OrchardIcons.Sun), Triple("辅助功能", "appearance", OrchardIcons.Person), Triple("隐私与安全性", "privacy", OrchardIcons.Shield)))
        groups.forEach { items ->
            val visible = items.filter { match(it.first) }
            if (visible.isNotEmpty()) OrchardGroup {
                visible.forEachIndexed { i, (title, key, icon) ->
                    if (i > 0) OrchardDivider(leadingInset = 58.dp)
                    OrchardListRow(title, onClick = { open(key) }, leading = { OrchardIconTile(icon, when (key) { "notifications", "sounds" -> Color(0xFFFF3B30); "appearance" -> Color(0xFF007AFF); "privacy" -> Color(0xFF30B0C7); else -> Color(0xFF8E8E93) }) })
                }
            }
        }
        if (query.isNotBlank() && !("h516df 个人资料 账户 飞行模式 无线局域网 蓝牙 通知 声音与触感 通用 显示与亮度 辅助功能 隐私与安全性".contains(query, true))) OrchardEmptyState("没有结果", "尝试搜索其他设置。")
    }
}

@Composable
private fun DisplayReference(mode: String, onMode: (String) -> Unit, reduce: Boolean, onReduce: (Boolean) -> Unit, opaque: Boolean, onOpaque: (Boolean) -> Unit, onBack: () -> Unit, onMore: () -> Unit) {
    val c = MaterialTheme.colorScheme
    var brightness by rememberSaveable { mutableFloatStateOf(.6f) }
    var trueTone by rememberSaveable { mutableStateOf(true) }
    OrchardAppPage("显示与亮度", onBack = onBack, onMore = onMore, largeTitle = false) {
        OrchardSection("外观") {
            Row(Modifier.fillMaxWidth().padding(horizontal = 28.dp, vertical = 20.dp).selectableGroup(), horizontalArrangement = Arrangement.spacedBy(28.dp)) {
                listOf("浅色", "深色").forEach { choice ->
                    Column(Modifier.weight(1f).selectable(mode == choice, role = Role.RadioButton, onClick = { onMode(choice) }), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        val dark = choice == "深色"
                        Box(Modifier.size(62.dp, 126.dp).clip(RoundedCornerShape(12.dp)).background(Brush.linearGradient(if (dark) listOf(Color(0xFF231331),Color(0xFF4D2FA5),Color(0xFF1679C0)) else listOf(Color(0xFFFF775C),Color(0xFFF8A2BA),Color(0xFF596BEA)))).padding(7.dp)) {
                            Column(verticalArrangement = Arrangement.spacedBy(5.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("9:41", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                repeat(3) { Box(Modifier.fillMaxWidth().height(17.dp).background((if (dark) Color.Black else Color.White).copy(alpha = .25f), RoundedCornerShape(3.dp))) }
                            }
                        }
                        Text(choice, style = MaterialTheme.typography.bodyLarge)
                        Box(Modifier.size(22.dp).background(if (mode == choice) c.primary else Color.Transparent, CircleShape).border(1.5.dp, if (mode == choice) c.primary else c.outlineVariant, CircleShape), contentAlignment = Alignment.Center) {
                            if (mode == choice) Icon(OrchardIcons.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
            OrchardDivider(); OrchardSwitchRow("自动", mode == "跟随系统", { onMode(if (it) "跟随系统" else "浅色") })
        }
        OrchardSection("亮度", footnote = "亮度滑块用于组件演示，不改变设备屏幕。") {
            Row(Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(OrchardIcons.Sun, null, Modifier.size(18.dp), tint = c.onSurfaceVariant)
                Box(Modifier.weight(1f)) { OrchardSlider(brightness, { brightness = it }, "亮度") }
                Icon(OrchardIcons.Sun, null, Modifier.size(25.dp), tint = c.onSurfaceVariant)
            }
            OrchardDivider(); OrchardSwitchRow("原彩显示", trueTone, { trueTone = it })
        }
        OrchardSection("辅助功能") {
            OrchardSwitchRow("减少动态效果", reduce, onReduce); OrchardDivider(); OrchardSwitchRow("降低透明度", opaque, onOpaque)
        }
    }
}

@Composable
private fun NotesReference(notes: List<String>, query: String, onQuery: (String) -> Unit, onNote: (Int) -> Unit, onBack: () -> Unit, onMore: () -> Unit) {
    OrchardAppPage("备忘录", subtitle = "${notes.size} 则备忘录", onBack = onBack, onMore = onMore,
        bottomBar = { OrchardSearchDock(query, onQuery, onCompose = { onNote(-1) }) }) {
        val visible = notes.indices.filter { query.isBlank() || notes[it].contains(query, true) }
        if (visible.isEmpty()) OrchardEmptyState("没有结果", "尝试搜索其他内容，或新建备忘录。")
        else listOf("今天" to visible.filter { it < 2 }, "昨天" to visible.filter { it >= 2 }).forEach { (title, indices) ->
            if (indices.isNotEmpty()) Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(title, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(start = 4.dp).semantics { heading() })
                OrchardGroup {
                    indices.forEachIndexed { row, i ->
                        if (row > 0) OrchardDivider(leadingInset = 20.dp)
                        OrchardNoteRow(notes[i].substringBefore('\n'), notes[i].substringAfter('\n').substringBefore('\n'), if (i == 0) "09:41" else if (i == 1) "08:26" else "昨天", { onNote(i) })
                    }
                }
            }
        }
    }
}

@Composable
private fun ReferenceNoteEditor(title: String, body: String, onTitle: (String) -> Unit, onBody: (String) -> Unit, onBack: () -> Unit, onDone: () -> Unit, onDelete: () -> Unit) {
    val c = MaterialTheme.colorScheme
    var confirm by rememberSaveable { mutableStateOf(false) }
    val bg = if (c.background.luminance() < .2f) Color.Black else Color.White
    OrchardScaffold(topBar = {
        Row(Modifier.fillMaxWidth().background(bg).statusBarsPadding().padding(horizontal = 18.dp).heightIn(min = 60.dp), verticalAlignment = Alignment.CenterVertically) {
            OrchardRoundButton(OrchardIcons.Back, "返回", onBack)
            Spacer(Modifier.weight(1f))
            OrchardRoundButton(OrchardIcons.Trash, "删除备忘录", { confirm = true })
            OrchardRoundButton(OrchardIcons.Check, "完成", onDone, tint = Color.White, fill = NotesGold)
        }
    }) { padding ->
        Column(Modifier.fillMaxSize().background(bg).padding(padding).imePadding().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
            BasicTextField(title, onTitle, textStyle = MaterialTheme.typography.headlineSmall.copy(color = c.onSurface, fontSize = 28.sp, lineHeight = 35.sp, fontWeight = FontWeight.Bold), cursorBrush = SolidColor(NotesGold), modifier = Modifier.fillMaxWidth().semantics { contentDescription = "备忘录标题" }, decorationBox = { inner -> Box { if (title.isBlank()) Text("标题", color = c.onSurfaceVariant, fontSize = 28.sp); inner() } })
            BasicTextField(body, onBody, textStyle = MaterialTheme.typography.bodyLarge.copy(color = c.onSurface, lineHeight = 29.sp), cursorBrush = SolidColor(NotesGold), modifier = Modifier.fillMaxWidth().heightIn(min = 400.dp).semantics { contentDescription = "备忘录正文" }, decorationBox = { inner -> Box { if (body.isBlank()) Text("开始记录…", color = c.onSurfaceVariant); inner() } })
        }
    }
    if (confirm) OrchardConfirmDialog("删除备忘录？", "这则演示备忘录将从列表移除。", "删除", { confirm = false; onDelete() }, { confirm = false }, destructive = true)
}

@Composable
private fun MusicReference(onMore: () -> Unit, dark: Boolean) {
    var tab by rememberSaveable { mutableStateOf("library") }
    var playing by rememberSaveable { mutableStateOf(false) }
    var hasTrack by rememberSaveable { mutableStateOf(false) }
    var track by rememberSaveable { mutableIntStateOf(0) }
    var selected by rememberSaveable { mutableStateOf("") }
    var search by rememberSaveable { mutableStateOf("") }
    val names = listOf("微风", "慢一点", "沿途", "日落以后")
    val c = MaterialTheme.colorScheme
    val background = if (dark) Color.Black else Color.White
    val title = when (tab) { "home" -> "主页"; "radio" -> "广播"; "search" -> "搜索"; else -> selected.ifEmpty { "资料库" } }
    OrchardAppPage(title, onMore = onMore, onBack = if (selected.isNotEmpty()) ({ selected = "" }) else null, background = background, bottomBar = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OrchardMiniPlayer(if (hasTrack) names[track] else "未在播放", playing, { playing = !playing; hasTrack = true }, { track = (track + 1) % names.size; playing = true; hasTrack = true }, Modifier.padding(horizontal = 24.dp))
            OrchardTabBar(listOf(OrchardDestination("home", "主页", OrchardIcons.Home), OrchardDestination("radio", "广播", OrchardIcons.Radio), OrchardDestination("library", "资料库", OrchardIcons.Library), OrchardDestination("search", "搜索", OrchardIcons.Search)), tab, { tab = it; selected = "" })
        }
    }) {
        if (tab == "library" && selected.isEmpty()) Column {
            listOf("播放列表", "艺人", "专辑", "歌曲", "已下载").forEachIndexed { i, label ->
                if (i > 0) OrchardDivider(leadingInset = 42.dp)
                Row(Modifier.fillMaxWidth().heightIn(min = 52.dp).clickable { selected = label }.padding(horizontal = 4.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Icon(if (i == 0) OrchardIcons.Radio else if (i == 1) OrchardIcons.Person else if (i == 2) OrchardIcons.Grid else OrchardIcons.Music, null, Modifier.size(24.dp), tint = c.primary)
                    Text(label, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp))
                    Icon(OrchardIcons.Chevron, null, Modifier.size(13.dp), tint = c.onSurfaceVariant.copy(alpha = .6f))
                }
            }
        }
        if (tab == "search") OrchardSearchField(search, { search = it }, label = "音乐、艺人、专辑")
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(if (tab == "radio") "电台精选" else if (tab == "home") "为你精选" else if (selected.isNotEmpty()) selected else if (tab == "search") "浏览分类" else "最近添加", style = MaterialTheme.typography.headlineSmall)
            names.indices.filter { search.isBlank() || names[it].contains(search) }.chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    row.forEach { i -> Column(Modifier.weight(1f).semantics { contentDescription = "播放${names[i]}" }.clickable { track = i; playing = true; hasTrack = true }, verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        AlbumArt(names[i], i, Modifier.fillMaxWidth().aspectRatio(1f))
                        Text(names[i], style = MaterialTheme.typography.bodyMedium)
                        Text("Orchard Sessions", style = MaterialTheme.typography.bodySmall, color = c.onSurfaceVariant)
                    } }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun AlbumArt(title: String, index: Int, modifier: Modifier) {
    val colors = listOf(listOf(Color(0xFF96BBAD),Color(0xFF294D4A)),listOf(Color(0xFFF0C5A4),Color(0xFFAE6D5F)),listOf(Color(0xFFB4BBDA),Color(0xFF444A76)),listOf(Color(0xFFE2BD77),Color(0xFF624A38)))[index % 4]
    Box(modifier.clip(RoundedCornerShape(8.dp)).background(Brush.linearGradient(colors)).padding(14.dp)) {
        Box(Modifier.size(95.dp).offset(x = 35.dp, y = 38.dp).border(1.dp, Color.White.copy(alpha = .3f), CircleShape))
        Box(Modifier.size(95.dp).offset(x = 20.dp, y = 25.dp).border(1.dp, Color.White.copy(alpha = .3f), CircleShape))
        Text(title, color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Light, modifier = Modifier.align(Alignment.BottomStart))
        Text("ORCHARD", color = Color.White.copy(alpha = .8f), fontSize = 8.sp, letterSpacing = 2.sp)
    }
}
