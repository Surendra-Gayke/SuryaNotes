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

    data object UndoDelete : HomeEvent
}