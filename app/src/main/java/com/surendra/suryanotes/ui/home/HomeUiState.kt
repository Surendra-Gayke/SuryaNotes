package com.surendra.suryanotes.ui.home

import com.surendra.suryanotes.domain.model.Note

/**
 * Immutable UI state for the Home screen.
 *
 * Every UI update should happen by creating a new instance
 * of this data class.
 */
data class HomeUiState(

    val notes: List<Note> = emptyList(),

    val isLoading: Boolean = false,

    val errorMessage: String? = null,

    val noteToDelete: Note? = null,

    val searchQuery: String = "",

    val selectedNote: Note? = null,

    val isSearchActive: Boolean = false

)