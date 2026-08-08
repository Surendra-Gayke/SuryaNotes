package com.surendra.suryanotes.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surendra.suryanotes.domain.model.Note
import com.surendra.suryanotes.domain.repository.NoteRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
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

    private val effectChannel = Channel<HomeEffect>()

    val effect = effectChannel.receiveAsFlow()

    private var recentlyDeletedNote: Note? = null

    init {
        observeNotes()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {

            HomeEvent.AddNoteClicked -> {
                navigateToEditor(null)
            }

            is HomeEvent.NoteClicked -> {
                navigateToEditor(event.noteId)
            }

            is HomeEvent.DeleteNoteClicked -> {
                deleteNote(event.note)
            }

            HomeEvent.UndoDelete -> {
                restoreDeletedNote()
            }
        }
    }

    private fun navigateToEditor(noteId: Long?) {
        viewModelScope.launch {
            effectChannel.send(
                HomeEffect.NavigateToEditor(noteId)
            )
        }
    }

    private fun deleteNote(note: Note) {
        viewModelScope.launch {

            recentlyDeletedNote = note

            noteRepository.deleteNote(note.id)

            effectChannel.send(HomeEffect.ShowUndoSnackbar)
        }
    }

    private fun restoreDeletedNote() {
        viewModelScope.launch {

            recentlyDeletedNote?.let { note ->
                noteRepository.createNote(note)
                recentlyDeletedNote = null
            }
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