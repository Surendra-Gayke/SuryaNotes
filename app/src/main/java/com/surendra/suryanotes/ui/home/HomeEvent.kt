package com.surendra.suryanotes.ui.home

/**
 * Represents all user interactions originating from the Home screen.
 *
 * Events describe what the user did.
 * The ViewModel decides how to react.
 */
sealed interface HomeEvent {

    /**
     * User tapped the Floating Action Button.
     */
    data object AddNoteClicked : HomeEvent

    /**
     * User tapped an existing note.
     */
    data class NoteClicked(
        val noteId: Long
    ) : HomeEvent

}