package com.elegoo.robotcoder.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Typed destinations for Navigation Compose. Centralizing routes prevents typos
 * and makes future deep links easier to add.
 */
sealed class AppScreen(
    val route: String,
    val title: String,
    val icon: ImageVector,
) {
    data object Home : AppScreen(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home,
    )

    data object Workspace : AppScreen(
        route = "workspace",
        title = "Workspace",
        icon = Icons.Default.Code,
    )

    data object Connect : AppScreen(
        route = "connect",
        title = "Connect",
        icon = Icons.Default.Bluetooth,
    )

    data object Settings : AppScreen(
        route = "settings",
        title = "Settings",
        icon = Icons.Default.Settings,
    )

    companion object {
        val bottomNavItems = listOf(Home, Workspace, Connect, Settings)
    }
}
