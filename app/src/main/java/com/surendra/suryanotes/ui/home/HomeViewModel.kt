package com.surendra.suryanotes.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surendra.suryanotes.domain.repository.NoteRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Home screen.
 *
 * Responsibilities:
 * • Observe notes.
 * • Own HomeUiState.
 * • Process HomeEvent.
 * • Emit HomeEffect.
 */
class HomeViewModel(
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(
            isLoading = true
        )
    )

    val uiState: StateFlow<HomeUiState> =
        _uiState.asStateFlow()

    private val effectChannel =
        Channel<HomeEffect>()

    val effect =
        effectChannel.receiveAsFlow()

    init {
        observeNotes()
    }

    /**
     * Single entry point for all user interactions.
     */
    fun onEvent(
        event: HomeEvent
    ) {

        when (event) {

            HomeEvent.AddNoteClicked -> {

                navigateToEditor(
                    noteId = null
                )

            }

            is HomeEvent.NoteClicked -> {

                navigateToEditor(
                    noteId = event.noteId
                )

            }

        }

    }

    private fun navigateToEditor(
        noteId: Long?
    ) {

        viewModelScope.launch {

            effectChannel.send(
                HomeEffect.NavigateToEditor(
                    noteId = noteId
                )
            )

        }

    }

    private fun observeNotes() {

        viewModelScope.launch {

            noteRepository
                .observeNotes()
                .collect { notes ->

                    _uiState.update { current ->

                        current.copy(
                            notes = notes,
                            isLoading = false,
                            errorMessage = null
                        )

                    }

                }

        }

    }

}