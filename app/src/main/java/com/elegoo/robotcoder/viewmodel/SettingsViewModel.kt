package com.elegoo.robotcoder.viewmodel

import androidx.lifecycle.ViewModel
import com.elegoo.robotcoder.utils.AppConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * UI state for the Settings screen.
 */
data class SettingsUiState(
    val isDarkModeEnabled: Boolean = false,
    val useDarkModeOverride: Boolean = false,
    val aboutText: String = AppConstants.ABOUT_TEXT,
    val creditsText: String = AppConstants.CREDITS_TEXT,
    val versionName: String = AppConstants.APP_VERSION,
)

/**
 * ViewModel for the Settings screen.
 *
 * Future responsibilities:
 * - Persist theme and accessibility preferences locally.
 * - Expose advanced developer or classroom-safe options if needed later.
 * - Surface app diagnostics such as last successful rover sync.
 */
class SettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    /**
     * Toggles the in-session dark mode preference. Persistence will be added
     * in a later milestone when local storage is introduced.
     */
    fun setDarkModeEnabled(enabled: Boolean) {
        _uiState.update {
            it.copy(
                isDarkModeEnabled = enabled,
                useDarkModeOverride = true,
            )
        }
    }
}
