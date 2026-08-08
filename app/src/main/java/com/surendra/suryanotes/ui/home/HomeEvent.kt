package com.surendra.suryanotes.ui.home

import com.surendra.suryanotes.domain.model.Note

sealed interface HomeEvent {

    data object AddNoteClicked : HomeEvent

    data class NoteClicked(
        val noteId: Long
    ) : HomeEvent

    data class DeleteNoteClicked(
        val note: Note
    ) : HomeEvent

    data class SearchQueryChanged(
        val query: String
    ) : HomeEvent

    data object UndoDelete : HomeEvent
    object ConfirmDelete : HomeEvent
    object DismissDeleteDialog : HomeEvent
    data class TogglePin(val note: Note) : HomeEvent
    data class NoteLongPressed(val note: Note) : HomeEvent
    object DismissActionDialog : HomeEvent

    object ToggleSearch : HomeEvent
    object ClearSearch : HomeEvent

}