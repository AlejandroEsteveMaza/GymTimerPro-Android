package com.alejandroestevemaza.gymtimerpro.app.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.alejandroestevemaza.gymtimerpro.R

enum class AppTab(
    val route: String,
    @param:StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    Training(
        route = "training",
        labelRes = R.string.app_navigation_training,
        icon = Icons.Rounded.FitnessCenter,
    ),
    Routines(
        route = "routines",
        labelRes = R.string.app_navigation_routines,
        icon = Icons.Rounded.Repeat,
    ),
    Progress(
        route = "progress",
        labelRes = R.string.app_navigation_progress,
        icon = Icons.Rounded.Insights,
    ),
    Settings(
        route = "settings",
        labelRes = R.string.app_navigation_settings,
        icon = Icons.Rounded.Settings,
    ),
}
