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
    private fun capture(name: String) {
        compose.waitForIdle()
        val file = File("build/outputs/orchard-ui/$name.png")
        file.parentFile!!.mkdirs()
        compose.onRoot().captureRoboImage(file.absolutePath)
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

    @Test fun componentsLightRender() { compose.setContent { CatalogApp(initialDark = false) }; capture("components-light") }
    @Test fun componentsDarkRender() { compose.setContent { CatalogApp(initialDark = true) }; capture("components-dark") }
    @Test fun settingsChangePalette() {
        compose.setContent { CatalogApp(initialDark = false) }
        compose.onNodeWithText("外观").performClick()
        compose.onNodeWithText("纸感绿").performClick()
        compose.onNodeWithText("已选用").assertExists()
        capture("appearance-sage-light")
    }
    @Test @Config(qualifiers = "zh-rCN-w320dp-h1000dp-xhdpi")
    fun narrowLargeTextLight() {
        RuntimeEnvironment.setFontScale(2f)
        compose.setContent { CatalogApp(initialDark = false) }
        compose.onNodeWithText("少一点打扰").assertIsDisplayed()
        capture("components-320dp-font200-light")
    }
    @Test @Config(qualifiers = "zh-rCN-w320dp-h1000dp-night-xhdpi")
    fun narrowLargeTextDark() {
        RuntimeEnvironment.setFontScale(2f)
        compose.setContent { CatalogApp(initialDark = true) }
        compose.onNodeWithText("少一点打扰").assertIsDisplayed()
        capture("components-320dp-font200-dark")
    }
}
