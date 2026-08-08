package com.surendra.suryanotes.ui.editor

import com.surendra.suryanotes.domain.model.Note

/**
 * Immutable UI state for the Editor screen.
 *
 * This state will gradually evolve as editor features
 * are implemented.
 */
data class EditorUiState(
    val noteId: Long? = null,
    val title: String = "",
    val content: String = "",
    val isLoading: Boolean = false
)