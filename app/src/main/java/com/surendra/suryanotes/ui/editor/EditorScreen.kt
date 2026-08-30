package com.surendra.suryanotes.ui.editor

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mohamedrejeb.richeditor.ui.BasicRichTextEditor
import com.surendra.suryanotes.ui.editor.toolbar.RichTextToolbar
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import com.surendra.suryanotes.ui.editor.components.HeadingPickerSheet
import com.surendra.suryanotes.ui.editor.components.TextColorPickerSheet
import com.surendra.suryanotes.ui.editor.model.RichTextToolbarEvent
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: EditorViewModel = koinViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val scrollState = rememberScrollState()
    val toolbarState by viewModel.toolbarState.collectAsStateWithLifecycle()

    val titleFocusRequester = remember { FocusRequester() }
    val contentFocusRequester = remember { FocusRequester() }

    var isInitialFocusDone by remember(isLandscape) { mutableStateOf(false) }

    var isTitleFocused by remember { mutableStateOf(false) }
    var isContentFocused by remember { mutableStateOf(false) }

    var lastFocusedField by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.isLoading, isLandscape) {
        if (uiState.isLoading) return@LaunchedEffect
        if (isInitialFocusDone) return@LaunchedEffect
        isInitialFocusDone = true

        if (lastFocusedField != null) {
            when (lastFocusedField) {
                "title" -> titleFocusRequester.requestFocus()
                "content" -> contentFocusRequester.requestFocus()
            }
            return@LaunchedEffect
        }

        if (isLandscape) return@LaunchedEffect

        when {
            uiState.noteId == null -> titleFocusRequester.requestFocus()
            uiState.title.text.isBlank() -> titleFocusRequester.requestFocus()
            else -> contentFocusRequester.requestFocus()
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow {
            viewModel.richTextState.selection

        }
            .distinctUntilChanged()
            .collect {
                viewModel.onSelectionChanged()
            }
    }

    val showTitle = !isLandscape || isTitleFocused || !isContentFocused

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = isContentFocused
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding(),
                    contentAlignment = Alignment.Center
                ) {
                    RichTextToolbar(
                        state = toolbarState,
                        onEvent = viewModel::onToolbarEvent,
                    )
                }
            }
        }

    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 24.dp,
                    bottom = 12.dp
                )
        ) {

            AnimatedVisibility(
                visible = showTitle,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                BasicTextField(
                    value = uiState.title,
                    onValueChange = viewModel::onTitleChange,
                    textStyle = MaterialTheme.typography.headlineSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .focusRequester(titleFocusRequester)
                        .onFocusChanged {
                            isTitleFocused = it.isFocused
                            if (it.isFocused) lastFocusedField = "title"
                        },
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
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                BasicRichTextEditor(
                    state = viewModel.richTextState,

                    modifier = Modifier
                        .fillMaxSize()
                        .focusRequester(contentFocusRequester)
                        .onFocusChanged {
                            isContentFocused = it.isFocused
                            if (it.isFocused) {
                                lastFocusedField = "content"
                            }
                        },

                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),

                    cursorBrush = SolidColor(
                        MaterialTheme.colorScheme.primary
                    ),

                    decorationBox = { innerTextField ->

                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (viewModel.richTextState.annotatedString.text.isEmpty()) {

                                Text(
                                    "Start writing your note...",
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
        if (toolbarState.showTextColorPicker) {
            TextColorPickerSheet(
                onColorSelected = { color ->
                    viewModel.onToolbarEvent(
                        RichTextToolbarEvent.ChangeTextColor(color)
                    )
                },
                onDismiss = {
                    viewModel.hideTextColorPicker()
                }
            )
        }

        if (toolbarState.showHeadingPicker) {
            HeadingPickerSheet(
                currentHeading = toolbarState.currentHeading,
                onHeadingSelected = { heading ->
                    viewModel.onToolbarEvent(
                        RichTextToolbarEvent.ChangeHeading(heading)
                    )
                },
                onDismiss = {
                    viewModel.hideHeadingPicker()
                }
            )
        }
    }
}