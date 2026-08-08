package com.surendra.suryanotes.ui.home

/**
 * One-time UI effects emitted by HomeViewModel.
 *
 * Effects represent transient actions that should never be
 * stored inside HomeUiState.
 */
sealed interface HomeEffect {

    data class NavigateToEditor(
        val noteId: Long? = null
    ) : HomeEffect

    data object ShowUndoSnackbar : HomeEffect
}