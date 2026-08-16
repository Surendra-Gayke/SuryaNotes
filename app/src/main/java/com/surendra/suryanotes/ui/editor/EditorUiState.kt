package com.surendra.suryanotes.ui.editor

import androidx.compose.ui.text.input.TextFieldValue
import com.surendra.suryanotes.domain.model.Note

/**
 * Immutable UI state for the Editor screen.
 *
 * This state will gradually evolve as editor features
 * are implemented.
 */
data class EditorUiState(
    val noteId: Long? = null,
    val title: TextFieldValue = TextFieldValue(""),
    val isLoading: Boolean = false
)