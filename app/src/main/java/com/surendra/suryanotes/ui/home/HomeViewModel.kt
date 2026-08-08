package com.surendra.suryanotes.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surendra.suryanotes.domain.model.Note
import com.surendra.suryanotes.domain.repository.NoteRepository
import kotlinx.coroutines.FlowPreview
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

    private var recentlyDeletedNote: Note? = null

    private val searchQueryFlow = MutableStateFlow("")

    val uiState: StateFlow<HomeUiState> =
        _uiState.asStateFlow()

    private val effectChannel =
        Channel<HomeEffect>()

    val effect =
        effectChannel.receiveAsFlow()

    init {
        observeNotes()
    }

    fun onEvent(
        event: HomeEvent
    ) {
        when (event) {

            HomeEvent.AddNoteClicked -> {
                navigateToEditor(null)
            }

            is HomeEvent.NoteClicked -> {
                navigateToEditor(event.noteId)
            }

            is HomeEvent.DeleteNoteClicked -> {
                _uiState.update { current ->
                    current.copy(
                        noteToDelete = event.note
                    )
                }
            }

            is HomeEvent.SearchQueryChanged -> {
                searchQueryFlow.value = event.query

                _uiState.update { current ->
                    current.copy(searchQuery = event.query)
                }
            }

            is HomeEvent.TogglePin -> {
                togglePin(event.note)
            }

            is HomeEvent.NoteLongPressed -> {
                _uiState.update {
                    it.copy(selectedNote = event.note)
                }
            }

            HomeEvent.ConfirmDelete -> {
                deleteSelectedNote()
            }

            HomeEvent.DismissDeleteDialog -> {
                clearDeleteState()
            }

            HomeEvent.UndoDelete -> {
                restoreDeletedNote()
            }

            HomeEvent.DismissActionDialog -> {
                _uiState.update {
                    it.copy(selectedNote = null)
                }
            }

            HomeEvent.ToggleSearch -> {
                _uiState.update {
                    it.copy(
                        isSearchActive = !it.isSearchActive
                    )
                }
            }

            HomeEvent.ClearSearch -> {
                searchQueryFlow.value = ""

                _uiState.update {
                    it.copy(
                        searchQuery = "",
                        isSearchActive = false
                    )
                }
            }
        }
    }

    private fun deleteSelectedNote() {

        val note = _uiState.value.noteToDelete
            ?: return

        viewModelScope.launch {

            recentlyDeletedNote = note

            noteRepository.deleteNote(note.id)

            _uiState.update { current ->
                current.copy(
                    noteToDelete = null
                )
            }

            effectChannel.send(
                HomeEffect.ShowUndoSnackbar
            )
        }
    }

    private fun restoreDeletedNote() {

        val note = recentlyDeletedNote
            ?: return

        viewModelScope.launch {

            noteRepository.createNote(note)

            recentlyDeletedNote = null
        }
    }

    private fun clearDeleteState() {
        _uiState.update { current ->
            current.copy(
                noteToDelete = null
            )
        }
    }

    private fun togglePin(note: Note) {
        viewModelScope.launch {

            val updatedNote = note.copy(
                isPinned = !note.isPinned,
                updatedAt = System.currentTimeMillis()
            )

            noteRepository.updateNote(updatedNote)
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

    @OptIn(FlowPreview::class)
    private fun observeNotes() {
        viewModelScope.launch {
            combine(
                noteRepository.observeNotes(),

                searchQueryFlow
                    .debounce(300)

            ) { notes, query ->

                val filtered = if (query.isBlank()) {
                    notes
                } else {
                    notes.filter {
                        it.title.contains(query, ignoreCase = true) ||
                                it.content.contains(query, ignoreCase = true)
                    }
                }

                filtered.sortedWith(
                    compareByDescending<Note> { it.isPinned }
                        .thenByDescending { it.updatedAt }
                )

            }.collect { filteredNotes ->

                _uiState.update { current ->
                    current.copy(
                        notes = filteredNotes,
                        isLoading = false,
                        errorMessage = null
                    )
                }

            }
        }
    }
}