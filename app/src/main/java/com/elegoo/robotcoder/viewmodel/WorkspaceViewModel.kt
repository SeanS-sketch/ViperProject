package com.elegoo.robotcoder.viewmodel

import androidx.lifecycle.ViewModel
import com.elegoo.robotcoder.model.BlockType
import com.elegoo.robotcoder.model.WorkspaceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * UI state for the Programming Workspace screen.
 */
data class WorkspaceUiState(
    val workspaceState: WorkspaceState = WorkspaceState(),
)

/**
 * ViewModel for the Programming Workspace screen.
 *
 * Milestone 3 is intentionally UI-only. This ViewModel tracks visual block
 * placement for the current session and does not execute robot behavior.
 */
class WorkspaceViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(WorkspaceUiState())
    val uiState: StateFlow<WorkspaceUiState> = _uiState.asStateFlow()

    fun addBlock(type: BlockType, x: Float, y: Float) {
        _uiState.value = _uiState.value.copy(
            workspaceState = _uiState.value.workspaceState.addBlock(
                type = type,
                x = x,
                y = y,
            ),
        )
    }

    fun removeBlock(id: String) {
        _uiState.value = _uiState.value.copy(
            workspaceState = _uiState.value.workspaceState.removeBlock(id),
        )
    }

    fun updateBlockPosition(id: String, x: Float, y: Float) {
        _uiState.value = _uiState.value.copy(
            workspaceState = _uiState.value.workspaceState.updateBlockPosition(
                id = id,
                x = x,
                y = y,
            ),
        )
    }
}
