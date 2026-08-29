package com.surendra.suryanotes.ui.editor

import android.util.Log
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamedrejeb.richeditor.model.RichTextState
import com.surendra.suryanotes.domain.model.Note
import com.surendra.suryanotes.domain.repository.NoteRepository
import com.surendra.suryanotes.ui.editor.model.RichTextToolbarState
import com.surendra.suryanotes.ui.editor.model.RichTextToolbarEvent
import com.surendra.suryanotes.ui.editor.richtext.RichTextController
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class EditorViewModel(
    private val noteRepository: NoteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val noteId: Long? =
        savedStateHandle["noteId"]

    val richTextState = RichTextState()
    private val _toolbarState =
        MutableStateFlow(RichTextToolbarState())

    val toolbarState: StateFlow<RichTextToolbarState> =
        _toolbarState.asStateFlow()

    fun onSelectionChanged() {
        richTextController.onSelectionChanged()
    }

    private val richTextController =
        RichTextController(
            richTextState = richTextState,
            toolbarState = _toolbarState
        )

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

    private fun loadNoteIfExists() {

        if (noteId == null) {
            _uiState.update {
                it.copy(isLoading = false)
            }
            return
        }

        viewModelScope.launch {

            val note = noteRepository.getNoteById(noteId)

            val titleText = note?.title.orEmpty()
            val contentHtml = note?.content.orEmpty()

            richTextState.setHtml(contentHtml)

            _uiState.update {
                it.copy(
                    title = TextFieldValue(
                        text = titleText,
                        selection = TextRange(titleText.length)
                    ),
                    isLoading = false
                )
            }
            Log.d(
                "Editor",
                """
                    Loaded...
                    title=${note?.title}
                    html=${note?.content}
                    """.trimIndent()
                )
        }
    }
    fun onTitleChange(value: TextFieldValue) {
        Log.d("Editor", "Title changed")
        _uiState.update {
            it.copy(title = value)
        }
    }
    private fun observeAutoSave() {

        viewModelScope.launch {

            combine(
                // Observe title changes
                uiState
                    .map { it.title.text }
                    .distinctUntilChanged(),

                // Observe Rich Editor changes
                snapshotFlow {
                    richTextState.annotatedString
                }
                    .distinctUntilChanged()

            ) { title, _ ->
                title to richTextState.toHtml()
            }
                .debounce(500)
                .filter { !uiState.value.isLoading }
                .collect { (title, html) ->

                    Log.d(
                        "Editor",
                        """
                    Saving...
                    title=$title
                    html=$html
                    noteId=${uiState.value.noteId}
                    """.trimIndent()
                    )

                    saveNote(
                        title = title,
                        contentHtml = html
                    )
                }
        }
    }

    private suspend fun saveNote(
        title: String,
        contentHtml: String
    ) {

        if (title.isBlank() && contentHtml.isBlank()) {
            return
        }

        val currentTime = System.currentTimeMillis()

        val currentNoteId = _uiState.value.noteId

        val existingNote =
            currentNoteId?.let {
                noteRepository.getNoteById(it)
            }

        val note = Note(
            id = currentNoteId ?: 0L,
            title = title,
            content = contentHtml, // ✅ store HTML
            createdAt = existingNote?.createdAt ?: currentTime,
            updatedAt = currentTime,
            isPinned = existingNote?.isPinned ?: false
        )

        if (currentNoteId == null) {

            val newId = noteRepository.createNote(note)

            _uiState.update {
                it.copy(noteId = newId)
            }

        } else {
            noteRepository.updateNote(note)
        }
        Log.d(
            "Editor", """
                Saving...
                title=$title
                html=$contentHtml
                noteId=${_uiState.value.noteId}
                """.trimIndent()
            )
    }

    fun onToolbarEvent(
        event: RichTextToolbarEvent
    ) {
        richTextController.onToolbarEvent(event)
    }

}