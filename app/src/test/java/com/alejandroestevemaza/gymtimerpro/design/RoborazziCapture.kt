package com.alejandroestevemaza.gymtimerpro.design

import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.alejandroestevemaza.gymtimerpro.core.designsystem.theme.GymTimerProTheme
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.RoborazziTaskType
import com.github.takahirom.roborazzi.captureRoboImage
import java.io.File
import org.robolectric.Robolectric
import org.robolectric.Shadows

@OptIn(ExperimentalRoborazziApi::class)
internal fun captureScreenRoboImage(
    name: String,
    content: @Composable () -> Unit,
) {
    val controller = Robolectric.buildActivity(ComponentActivity::class.java).setup()
    val activity = controller.get()
    val outputDir = File(
        System.getProperty("roborazzi.output.dir") ?: "build/outputs/roborazzi",
    ).apply { mkdirs() }
    val options = RoborazziOptions(taskType = resolveTaskType())
    activity.setContent {
        GymTimerProTheme(darkTheme = false) {
            Box(modifier = Modifier.fillMaxSize()) {
                content()
            }
        }
    }

    Shadows.shadowOf(Looper.getMainLooper()).idle()
    activity.window.decorView.captureRoboImage(
        file = File(outputDir, "$name.png"),
        roborazziOptions = options,
    )
    controller.pause().stop().destroy()
}

@OptIn(ExperimentalRoborazziApi::class)
private fun resolveTaskType(): RoborazziTaskType {
    val record = System.getProperty("roborazzi.test.record") == "true"
    val compare = System.getProperty("roborazzi.test.compare") == "true"
    val verify = System.getProperty("roborazzi.test.verify") == "true"
    return RoborazziTaskType.Companion.of(record, compare, verify)
}
