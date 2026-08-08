package com.surendra.suryanotes.ui.editor

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
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

                val titleText = note?.title.orEmpty()
                val contentText = note?.content.orEmpty()

                it.copy(
                    title = TextFieldValue(
                        text = titleText,
                        selection = TextRange(titleText.length)
                    ),
                    content = TextFieldValue(
                        text = contentText,
                        selection = TextRange(contentText.length)
                    ),
                    isLoading = false
                )
            }
        }
    }

    // ----------------------------
    // UI Actions
    // ----------------------------
    fun onTitleChange(value: TextFieldValue) {
        _uiState.update {
            it.copy(title = value)
        }
    }

    fun onContentChange(value: TextFieldValue) {
        _uiState.update {
            it.copy(content = value)
        }
    }

    // ----------------------------
    // AUTO SAVE (CORE LOGIC)
    // ----------------------------
    private fun observeAutoSave() {

        viewModelScope.launch {

            _uiState
                .debounce(500)
                .filter { state ->
                    !state.isLoading
                }
                .distinctUntilChangedBy {
                    it.title.text to it.content.text
                }
                .collect { state ->
                    saveNote(state)
                }
        }
    }

    private suspend fun saveNote(state: EditorUiState) {

        val title = state.title.text
        val content = state.content.text

        if (title.isBlank() && content.isBlank()) {
            return
        }

        val currentTime = System.currentTimeMillis()

        val existingNote =
            state.noteId?.let { noteRepository.getNoteById(it) }

        val note = Note(
            id = state.noteId ?: 0L,
            title = title,
            content = content,
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