package com.elegoo.robotcoder.viewmodel

import androidx.lifecycle.ViewModel
import com.elegoo.robotcoder.model.ConnectionStatus
import com.elegoo.robotcoder.model.displayLabel
import com.elegoo.robotcoder.utils.AppConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * UI state for the Connect Rover screen.
 */
data class ConnectRoverUiState(
    val connectionStatus: ConnectionStatus = ConnectionStatus.DISCONNECTED,
    val statusLabel: String = ConnectionStatus.DISCONNECTED.displayLabel(),
    val milestoneMessage: String = AppConstants.BLUETOOTH_MILESTONE_MESSAGE,
    val isScanEnabled: Boolean = false,
    val isConnectEnabled: Boolean = false,
    val isDisconnectEnabled: Boolean = false,
)

/**
 * ViewModel for the Connect Rover screen.
 *
 * Future responsibilities:
 * - Request runtime Bluetooth permissions when required.
 * - Scan for the paired ELEGOO rover and manage connection lifecycle events.
 * - Expose connection health and error messages to the rest of the app.
 */
class ConnectRoverViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ConnectRoverUiState())
    val uiState: StateFlow<ConnectRoverUiState> = _uiState.asStateFlow()
}
