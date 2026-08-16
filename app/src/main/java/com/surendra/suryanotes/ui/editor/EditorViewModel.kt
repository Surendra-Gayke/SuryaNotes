package com.surendra.suryanotes.ui.editor

import android.util.Log
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamedrejeb.richeditor.model.RichTextState
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

    // ✅ NEW: Rich text state
    val richTextState = RichTextState()

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

            val titleText = note?.title.orEmpty()
            val contentHtml = note?.content.orEmpty() // ⚠️ now treated as HTML

            // ✅ Load into RichTextState
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

    // ----------------------------
    // UI Actions
    // ----------------------------
    fun onTitleChange(value: TextFieldValue) {
        Log.d("Editor", "Title changed")
        _uiState.update {
            it.copy(title = value)
        }
    }

    // ❌ REMOVE onContentChange (handled by RichTextState)

    // ----------------------------
    // Formatting Actions (M2.2)
    // ----------------------------

    fun toggleBold() {
        richTextState.toggleSpanStyle(
            SpanStyle(fontWeight = FontWeight.Bold)
        )
    }

    fun toggleItalic() {
        richTextState.toggleSpanStyle(
            SpanStyle(fontStyle = FontStyle.Italic)
        )
    }

    fun toggleUnderline() {
        richTextState.toggleSpanStyle(
            SpanStyle(textDecoration = TextDecoration.Underline)
        )
    }

    fun toggleStrike() {
        richTextState.toggleSpanStyle(
            SpanStyle(textDecoration = TextDecoration.LineThrough)
        )
    }

    // ----------------------------
    // AUTO SAVE (UPDATED)
    // ----------------------------
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
}