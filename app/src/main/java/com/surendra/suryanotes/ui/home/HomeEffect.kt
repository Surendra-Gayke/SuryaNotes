package com.surendra.suryanotes.ui.home

/**
 * One-time UI effects emitted by HomeViewModel.
 *
 * Effects represent transient actions that should never be
 * stored inside HomeUiState.
 */
sealed interface HomeEffect {

    /**
     * Navigate to the Editor screen.
     *
     * null  -> Create new note
     * noteId -> Edit existing note
     */
    data class NavigateToEditor(
        val noteId: Long? = null
    ) : HomeEffect

}