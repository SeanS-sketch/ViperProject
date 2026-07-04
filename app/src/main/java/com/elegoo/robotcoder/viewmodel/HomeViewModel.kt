package com.elegoo.robotcoder.viewmodel

import androidx.lifecycle.ViewModel
import com.elegoo.robotcoder.utils.AppConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
/**
 * UI state for the Home screen.
 */
data class HomeUiState(
    val appName: String = AppConstants.APP_NAME,
    val tagline: String = AppConstants.APP_TAGLINE,
)

/**
 * ViewModel for the Home screen.
 *
 * Future responsibilities:
 * - Surface recent program summaries once program storage exists.
 * - Show rover connection status pulled from the Bluetooth layer.
 * - Provide onboarding or first-run guidance when needed.
 */
class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
}
