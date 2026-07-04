package com.elegoo.robotcoder.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.elegoo.robotcoder.ui.screens.ConnectRoverScreen
import com.elegoo.robotcoder.ui.screens.HomeScreen
import com.elegoo.robotcoder.ui.screens.SettingsScreen
import com.elegoo.robotcoder.ui.screens.WorkspaceScreen
import com.elegoo.robotcoder.viewmodel.ConnectRoverViewModel
import com.elegoo.robotcoder.viewmodel.HomeViewModel
import com.elegoo.robotcoder.viewmodel.SettingsViewModel
import com.elegoo.robotcoder.viewmodel.WorkspaceViewModel

/**
 * Root navigation host with persistent bottom navigation across all main screens.
 */
@Composable
fun RoverCoderNavHost(
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                AppScreen.bottomNavItems.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                            )
                        },
                        label = { Text(screen.title) },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppScreen.Home.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(AppScreen.Home.route) {
                val homeViewModel: HomeViewModel = viewModel()
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToWorkspace = {
                        navController.navigate(AppScreen.Workspace.route) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToConnect = {
                        navController.navigate(AppScreen.Connect.route) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToSettings = {
                        navController.navigate(AppScreen.Settings.route) {
                            launchSingleTop = true
                        }
                    },
                )
            }

            composable(AppScreen.Workspace.route) {
                val workspaceViewModel: WorkspaceViewModel = viewModel()
                WorkspaceScreen(viewModel = workspaceViewModel)
            }

            composable(AppScreen.Connect.route) {
                val connectViewModel: ConnectRoverViewModel = viewModel()
                ConnectRoverScreen(viewModel = connectViewModel)
            }

            composable(AppScreen.Settings.route) {
                SettingsScreen(viewModel = settingsViewModel)
            }
        }
    }
}
