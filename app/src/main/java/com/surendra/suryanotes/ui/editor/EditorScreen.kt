package com.surendra.suryanotes.ui.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: EditorViewModel = koinViewModel()
) {
    val uiState by viewModel
        .uiState
        .collectAsStateWithLifecycle()

    val titleFocusRequester = remember { FocusRequester() }
    val contentFocusRequester = remember { FocusRequester() }
    var isInitialFocusDone by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) return@LaunchedEffect
        if (isInitialFocusDone) return@LaunchedEffect
        when {
            // 🆕 New note
            uiState.noteId == null -> {
                titleFocusRequester.requestFocus()
            }
            // ✏️ Existing note
            uiState.title.text.isBlank() -> {
                titleFocusRequester.requestFocus()
            }
            else -> {
                contentFocusRequester.requestFocus()
            }
        }
        isInitialFocusDone = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("NoteCraft")
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {

            val textColor = MaterialTheme.colorScheme.onSurface

            // 🔹 TITLE
            BasicTextField(
                value = uiState.title,
                onValueChange = viewModel::onTitleChange,
                textStyle = MaterialTheme.typography.headlineSmall.copy(
                    color = textColor
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary), // 🔥 cursor color
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(titleFocusRequester)
                    .padding(bottom = 12.dp),
                decorationBox = { innerTextField ->
                    Box {
                        if (uiState.title.text.isEmpty()) {
                            Text(
                                text = "Title",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        innerTextField()
                    }
                }
            )

            // 🔹 CONTENT (SCROLLABLE + FOCUSABLE)
            val scrollState = rememberScrollState()

            BasicTextField(
                value = uiState.content,
                onValueChange = viewModel::onContentChange,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = textColor
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .focusRequester(contentFocusRequester),
                decorationBox = { innerTextField ->
                    Box {
                        if (uiState.content.text.isEmpty()) {
                            Text(
                                text = "Start writing your note...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}