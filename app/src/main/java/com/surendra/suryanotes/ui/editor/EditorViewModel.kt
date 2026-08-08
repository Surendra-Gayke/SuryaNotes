package com.surendra.suryanotes.ui.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surendra.suryanotes.domain.model.Note
import com.surendra.suryanotes.domain.repository.NoteRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class EditorViewModel(
    private val noteRepository: NoteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val noteId: Long? =
        savedStateHandle["noteId"]

    private val _uiState = MutableStateFlow(
        EditorUiState(
            noteId = noteId,
            isLoading = true
        )
    )

    val uiState: StateFlow<EditorUiState> =
        _uiState.asStateFlow()

    init {
        loadNoteIfExists()
        observeAutoSave()
    }

    // ----------------------------
    // Load existing note
    // ----------------------------
    private fun loadNoteIfExists() {

        if (noteId == null) {
            _uiState.update {
                it.copy(isLoading = false)
            }
            return
        }

        viewModelScope.launch {

            val note = noteRepository.getNoteById(noteId)

            _uiState.update {
                it.copy(
                    title = note?.title.orEmpty(),
                    content = note?.content.orEmpty(),
                    isLoading = false
                )
            }
        }
    }

    // ----------------------------
    // UI Actions
    // ----------------------------
    fun onTitleChange(newTitle: String) {
        _uiState.update {
            it.copy(title = newTitle)
        }
    }

    fun onContentChange(newContent: String) {
        _uiState.update {
            it.copy(content = newContent)
        }
    }

    // ----------------------------
    // AUTO SAVE (CORE LOGIC)
    // ----------------------------
    private fun observeAutoSave() {

        viewModelScope.launch {

            _uiState
                .debounce(500) // wait for typing pause
                .filter { state ->
                    !state.isLoading
                }
                .distinctUntilChangedBy {
                    it.title to it.content
                }
                .collect { state ->
                    saveNote(state)
                }
        }
    }

    private suspend fun saveNote(state: EditorUiState) {

        if (state.title.isBlank() && state.content.isBlank()) {
            return
        }

        val currentTime = System.currentTimeMillis()

        val existingNote =
            state.noteId?.let { noteRepository.getNoteById(it) }

        val note = Note(
            id = state.noteId ?: 0L,
            title = state.title,
            content = state.content,
            createdAt = existingNote?.createdAt ?: currentTime,
            updatedAt = currentTime,
            isPinned = existingNote?.isPinned ?: false
        )

        if (state.noteId == null) {
            val newId = noteRepository.createNote(note)
            _uiState.update {
                it.copy(noteId = newId)
            }

        } else {
            noteRepository.updateNote(note)
        }
    }
}