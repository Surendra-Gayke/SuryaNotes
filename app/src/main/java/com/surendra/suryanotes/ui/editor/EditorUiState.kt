package com.surendra.suryanotes.ui.editor

/**
 * Immutable UI state for the Editor screen.
 *
 * This state will gradually evolve as editor features
 * are implemented.
 */
data class EditorUiState(

    /**
     * Indicates whether the editor is currently loading
     * an existing note.
     */
    val isLoading: Boolean = false

)