package com.elegoo.robotcoder.viewmodel

import androidx.lifecycle.ViewModel
import com.elegoo.robotcoder.utils.AppConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * UI state for the Programming Workspace screen.
 */
data class WorkspaceUiState(
    val placeholderMessage: String = AppConstants.WORKSPACE_PLACEHOLDER_MESSAGE,
    val isRunEnabled: Boolean = false,
    val isStopEnabled: Boolean = false,
)

/**
 * ViewModel for the Programming Workspace screen.
 *
 * Future responsibilities:
 * - Manage the block workspace model and undo/redo history.
 * - Validate programs before they are sent to the rover.
 * - Coordinate Run/Stop actions with the execution engine and Bluetooth transport.
 */
class WorkspaceViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(WorkspaceUiState())
    val uiState: StateFlow<WorkspaceUiState> = _uiState.asStateFlow()
}
