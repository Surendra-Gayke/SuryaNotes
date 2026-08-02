package com.surendra.suryanotes.ui.editor

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for the Editor screen.
 *
 * Responsibilities:
 * - Own the EditorUiState.
 * - Coordinate editor operations.
 * - Interact with repositories.
 */
class EditorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        EditorUiState()
    )

    val uiState: StateFlow<EditorUiState> =
        _uiState.asStateFlow()

}