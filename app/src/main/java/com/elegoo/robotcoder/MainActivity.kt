package com.elegoo.robotcoder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elegoo.robotcoder.navigation.RoverCoderNavHost
import com.elegoo.robotcoder.ui.theme.RoverCoderTheme
import com.elegoo.robotcoder.viewmodel.SettingsViewModel

/**
 * Single-activity entry point for the Rover Coder application.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val settingsState by settingsViewModel.uiState.collectAsStateWithLifecycle()
            val systemDarkTheme = isSystemInDarkTheme()
            val darkTheme = if (settingsState.useDarkModeOverride) {
                settingsState.isDarkModeEnabled
            } else {
                systemDarkTheme
            }

            RoverCoderTheme(darkTheme = darkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RoverCoderNavHost(settingsViewModel = settingsViewModel)
                }
            }
        }
    }
}
