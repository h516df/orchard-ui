package dev.h516.orchard.catalog

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.github.takahirom.roborazzi.captureRoboImage
import dev.h516.orchard.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.LooperMode
import java.io.File

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(sdk = [35], application = Application::class, qualifiers = "zh-rCN-w393dp-h852dp-xhdpi")
class OrchardUiTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    private fun capture(name: String, dialog: Boolean = false) {
        compose.waitForIdle()
        val file = File("build/outputs/orchard-ui/$name.png")
        file.parentFile!!.mkdirs()
        (if (dialog) compose.onNode(isDialog()) else compose.onRoot()).captureRoboImage(file.absolutePath)
        assertTrue("Host render must produce a real image", file.isFile && file.length() > 0)
    }

    @Test fun busyButtonPreventsDuplicateSubmission() {
        var clicks = 0
        compose.setContent { OrchardTheme { OrchardButton("保存", { clicks++ }, loading = true) } }
        compose.onNodeWithText("保存 · 正在处理").assertIsNotEnabled().performClick()
        assertEquals(0, clicks)
    }

    @Test fun switchRowExposesOneToggleAndSegmentsChangeSelection() {
        compose.setContent {
            var checked by remember { mutableStateOf(false) }
            var selected by remember { mutableStateOf("全部") }
            OrchardTheme { Surface { Column {
                OrchardSwitchRow("触感反馈", checked, { checked = it })
                OrchardSegments(listOf("全部", "收藏"), selected, { selected = it }, { it })
            } } }
        }
        compose.onNodeWithText("触感反馈").assertIsOff().performClick().assertIsOn()
        compose.onNodeWithText("收藏").performClick().assertIsSelected()
    }

    @Test fun failedSaveRetainsTextAndRetrySucceeds() {
        var attempts = 0
        compose.setContent {
            var text by remember { mutableStateOf("未保存的输入") }
            var error by remember { mutableStateOf<String?>(null) }
            OrchardTheme { OrchardEditorScreen("编辑", text, { text = it }, true,
                onSave = { attempts++; error = if (attempts == 1) "合成保存失败，输入保留" else null }, onClose = {}, error = error) }
        }
        compose.onNodeWithText("保存").performClick()
        compose.onNodeWithText("合成保存失败，输入保留").assertExists()
        compose.onNode(hasSetTextAction()).assertTextContains("未保存的输入")
        compose.onNodeWithText("保存").performClick()
        compose.onNodeWithText("合成保存失败，输入保留").assertDoesNotExist()
        assertEquals(2, attempts)
    }

    @Test fun androidBackProtectsDirtyEditor() {
        var closed = false
        compose.setContent { OrchardTheme { OrchardEditorScreen("编辑", "草稿", {}, true, {}, { closed = true }) } }
        compose.runOnIdle { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.onNodeWithText("放弃修改？").assertExists()
        compose.onNodeWithText("继续编辑").performClick()
        assertFalse(closed)
        compose.onNode(hasSetTextAction()).assertTextContains("草稿")
        compose.onNodeWithContentDescription("返回").performClick()
        compose.onNodeWithText("放弃修改", useUnmergedTree = true).performClick()
        assertTrue(closed)
    }

    @Test fun componentsLightRender() { compose.setContent { ComponentCatalogApp(initialDark = false) }; capture("components-light") }
    @Test fun componentsDarkRender() { compose.setContent { ComponentCatalogApp(initialDark = true) }; capture("components-dark") }
    @Test fun groupedListCategoryAndSearchWork() {
        compose.setContent { ComponentCatalogApp(initialDark = false) }
        compose.onNodeWithText("列表").performClick()
        compose.onNodeWithText("跟随系统").assertExists()
        capture("grouped-lists-light")
        compose.onNodeWithText("控件").performClick()
        compose.onNodeWithContentDescription("搜索组件").performTextReplacement("偏好")
        compose.onNodeWithText("触感反馈").assertExists()
        compose.onNodeWithText("继续").assertDoesNotExist()
        compose.onNodeWithContentDescription("清除搜索").performClick()
        compose.onNodeWithText("继续").assertExists()
    }
    @Test fun appleDialogAndOpaqueGlassRender() {
        var open by mutableStateOf(true)
        compose.setContent { OrchardTheme {
            if (open) OrchardConfirmDialog("删除这条记录？", "这是一个确认样例，不会删除你的资料。", "删除", { open = false }, { open = false }, destructive = true)
        } }
        capture("confirmation-light", dialog = true)
        compose.onNodeWithText("取消").performClick()
        assertFalse(open)
    }
    @Test fun reducedTransparencyRetainsNavigation() {
        compose.setContent { ComponentCatalogApp(initialDark = false, initialTab = "appearance") }
        compose.onNodeWithText("降低透明度").performScrollTo().performClick().assertIsOn()
        compose.onNodeWithText("浏览").performClick()
        compose.onNodeWithText("组件").assertExists()
        capture("components-opaque-glass-light")
    }
    @Test fun tabNavigationKeepsControlValues() {
        compose.setContent { ComponentCatalogApp(initialDark = false) }
        compose.onNodeWithContentDescription("名称").performTextReplacement("我的组件")
        compose.onNodeWithText("触感反馈").performClick().assertIsOff()
        compose.onNodeWithText("外观").performClick()
        compose.onNodeWithText("浏览").performClick()
        compose.onNodeWithContentDescription("名称").assertTextContains("我的组件")
        compose.onNodeWithText("触感反馈").assertIsOff()
    }
    @Test fun settingsChangePalette() {
        compose.setContent { ComponentCatalogApp(initialDark = false) }
        compose.onNodeWithText("外观").performClick()
        compose.onNodeWithText("纸感绿").performClick()
        compose.onNodeWithText("已选用").assertExists()
        capture("appearance-sage-light")
    }
    @Test @Config(qualifiers = "zh-rCN-w320dp-h1000dp-xhdpi")
    fun narrowLargeTextLight() {
        RuntimeEnvironment.setFontScale(2f)
        compose.setContent { ComponentCatalogApp(initialDark = false) }
        compose.onNodeWithText("组件").assertIsDisplayed()
        capture("components-320dp-font200-light")
    }
    @Test @Config(qualifiers = "zh-rCN-w320dp-h1000dp-night-xhdpi")
    fun narrowLargeTextDark() {
        RuntimeEnvironment.setFontScale(2f)
        compose.setContent { ComponentCatalogApp(initialDark = true) }
        compose.onNodeWithText("组件").assertIsDisplayed()
        capture("components-320dp-font200-dark")
    }

    @Test fun referenceSettingsSearchNavigationAndSwitch() {
        compose.setContent { CatalogApp(initialDark = false) }
        compose.onNodeWithText("飞行模式").assertIsOff().performClick().assertIsOn()
        capture("settings-light")
        compose.onNodeWithContentDescription("搜索").performTextReplacement("显示")
        compose.onNodeWithText("飞行模式").assertDoesNotExist()
        compose.onNodeWithText("显示与亮度").performClick()
        compose.onNodeWithText("浅色").performClick().assertIsSelected()
        capture("display-light")
        compose.onNodeWithText("降低透明度").performScrollTo().performClick().assertIsOn()
        compose.runOnIdle { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.onNodeWithText("设置").assertExists()
    }
    @Test fun referenceNotesCanEditSaveSearchAndGuardBack() {
        compose.setContent { CatalogApp(initialDark = false, initialPage = "notes") }
        capture("notes-light")
        compose.onNodeWithText("周末的小计划").performClick()
        compose.onNodeWithContentDescription("备忘录正文").performTextReplacement("修改后的正文")
        capture("note-editor-light")
        compose.runOnIdle { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.onNodeWithText("继续编辑").performClick()
        compose.onNodeWithContentDescription("备忘录正文").assertTextContains("修改后的正文")
        compose.onNodeWithContentDescription("完成").performClick()
        compose.onNodeWithContentDescription("搜索").performTextReplacement("修改后的正文")
        compose.onNodeWithText("周末的小计划").assertExists()
        compose.onNodeWithText("灵感随记").assertDoesNotExist()
        compose.onNodeWithContentDescription("新建备忘录").performClick()
        compose.onNodeWithContentDescription("备忘录标题").performTextReplacement("新的一则")
        compose.onNodeWithContentDescription("完成").performClick()
        compose.onNodeWithContentDescription("清除搜索").performClick()
        compose.onNodeWithText("新的一则").assertExists()
    }
    @Test fun referenceMusicControlsAndTabs() {
        compose.setContent { CatalogApp(initialDark = false, initialPage = "music") }
        capture("music-light")
        compose.onNodeWithContentDescription("播放微风").performClick()
        compose.onNodeWithContentDescription("暂停").assertExists().performClick()
        compose.onAllNodesWithText("微风").assertCountEquals(2)
        compose.onNodeWithContentDescription("下一首").performClick()
        compose.onAllNodesWithText("慢一点").assertCountEquals(2)
        compose.onNode(hasText("广播") and hasClickAction()).performClick().assertIsSelected()
        compose.onNodeWithText("电台精选").assertExists()
        compose.onNode(hasText("搜索") and hasClickAction()).performClick().assertIsSelected()
        compose.onNodeWithContentDescription("音乐、艺人、专辑").performTextReplacement("沿途")
        compose.onNodeWithContentDescription("播放微风").assertDoesNotExist()
        compose.onNodeWithContentDescription("播放沿途").assertExists()
    }
    @Test fun referencePickerChangesPages() {
        compose.setContent { CatalogApp(initialDark = false) }
        compose.onNodeWithContentDescription("更多").performClick()
        compose.onNodeWithText("备忘录", useUnmergedTree = true).performClick()
        compose.onNodeWithContentDescription("新建备忘录").assertExists()
    }
    @Test fun referenceDarkPagesRender() {
        compose.setContent { CatalogApp(initialDark = true, initialPage = "notes") }
        capture("notes-dark")
    }
    @Test fun referenceMusicDarkRender() {
        compose.setContent { CatalogApp(initialDark = true, initialPage = "music") }
        capture("music-dark")
    }
    @Test fun referenceSettingsDarkRender() {
        compose.setContent { CatalogApp(initialDark = true) }; capture("settings-dark")
    }
    @Test @Config(qualifiers = "zh-rCN-w320dp-h1000dp-xhdpi")
    fun referenceNarrowLargeText() {
        RuntimeEnvironment.setFontScale(2f)
        compose.setContent { CatalogApp(initialDark = false) }
        compose.onNodeWithText("飞行模式").performScrollTo().assertIsDisplayed()
        capture("settings-320dp-font200")
        compose.onNodeWithContentDescription("更多").performClick()
        compose.onNodeWithText("备忘录", useUnmergedTree = true).performClick()
        compose.onNodeWithContentDescription("新建备忘录").assertIsDisplayed()
        capture("notes-320dp-font200")
    }

    @Test fun largeTitleCollapsesAboveScrolledContent() {
        compose.setContent { CatalogApp(initialDark = false, initialPage = "notes") }
        compose.onRoot().performTouchInput { swipeUp() }
        compose.onAllNodesWithText("备忘录").assertCountEquals(8)
        compose.onNodeWithText("雨停之后").assertIsDisplayed()
        capture("notes-scrolled-light")
    }
}
