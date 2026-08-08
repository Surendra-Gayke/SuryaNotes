package com.surendra.suryanotes.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.surendra.suryanotes.domain.model.Note
import com.surendra.suryanotes.ui.components.EmptyState
import com.surendra.suryanotes.ui.home.components.NotesContent
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToEditor: (Long?) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {

    val uiState by viewModel
        .uiState
        .collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // 🔥 SEARCH STATE (UI STATE - CORRECT)
    var isSearchExpanded by remember { mutableStateOf(false) }

    // 🔥 FOCUS HANDLING
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isSearchExpanded) {
        if (isSearchExpanded) {
            focusRequester.requestFocus()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {

                is HomeEffect.NavigateToEditor -> {
                    onNavigateToEditor(effect.noteId)
                }

                HomeEffect.ShowUndoSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = "Note deleted",
                        actionLabel = "Undo",
                        duration = SnackbarDuration.Short
                    )

                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onEvent(HomeEvent.UndoDelete)
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }
        ) { padding ->

            HomeScreenContent(
                paddingValues = padding,
                uiState = uiState,
                onNoteClick = { note ->
                    viewModel.onEvent(HomeEvent.NoteClicked(note.id))
                },
                onNoteLongClick = { note ->
                    viewModel.onEvent(HomeEvent.NoteLongPressed(note))
                },
                onPinClick = { note ->
                    viewModel.onEvent(HomeEvent.TogglePin(note))
                }
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .imePadding()
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSearchExpanded) {
                TextField(
                    value = uiState.searchQuery,
                    onValueChange = {
                        viewModel.onEvent(HomeEvent.SearchQueryChanged(it))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp)
                        .background(
                            MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .focusRequester(focusRequester),
                    placeholder = { Text("Search...") },
                    singleLine = true,

                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,

                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent
                    ),
                    trailingIcon = {
                        Row {
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        viewModel.onEvent(HomeEvent.ClearSearch)
                                    }
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                            IconButton(
                                onClick = {
                                    isSearchExpanded = false
                                    viewModel.onEvent(HomeEvent.ClearSearch)
                                }
                            ) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Close")
                            }
                        }
                    }
                )

            } else {
                Surface(
                    shape = CircleShape,
                    tonalElevation = 2.dp,
                    shadowElevation = 2.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = true)
                            ) {
                                isSearchExpanded = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
            }
            // ➕ FAB
            FloatingActionButton(
                onClick = {
                    viewModel.onEvent(HomeEvent.AddNoteClicked)
                },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }
    }

    if (uiState.noteToDelete != null) {

        AlertDialog(
            onDismissRequest = {
                viewModel.onEvent(HomeEvent.DismissDeleteDialog)
            },
            title = { Text("Delete Note") },
            text = { Text("Are you sure you want to delete this note?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onEvent(HomeEvent.ConfirmDelete)
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.onEvent(HomeEvent.DismissDeleteDialog)
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (uiState.selectedNote != null) {

        AlertDialog(
            onDismissRequest = {
                viewModel.onEvent(HomeEvent.DismissActionDialog)
            },
            title = { Text("Choose Action") },
            text = {
                Column {

                    TextButton(
                        onClick = {
                            viewModel.onEvent(
                                HomeEvent.TogglePin(uiState.selectedNote!!)
                            )
                            viewModel.onEvent(HomeEvent.DismissActionDialog)
                        }
                    ) {
                        Text(
                            if (uiState.selectedNote!!.isPinned)
                                "Unpin"
                            else
                                "📌 Pin"
                        )
                    }

                    TextButton(
                        onClick = {
                            viewModel.onEvent(
                                HomeEvent.DeleteNoteClicked(uiState.selectedNote!!)
                            )
                            viewModel.onEvent(HomeEvent.DismissActionDialog)
                        }
                    ) {
                        Text("🗑️ Delete")
                    }
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
private fun HomeScreenContent(
    paddingValues: PaddingValues,
    uiState: HomeUiState,
    onNoteClick: (Note) -> Unit,
    onNoteLongClick: (Note) -> Unit,
    onPinClick: (Note) -> Unit,
) {

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {

        when {

            uiState.isLoading -> {
                EmptyState(
                    modifier = Modifier.fillMaxSize(),
                    title = "Loading...",
                    description = "Please wait."
                )
            }

            uiState.notes.isEmpty() -> {
                EmptyState(
                    modifier = Modifier.fillMaxSize(),
                    title = "No Notes Found",
                    description = "Try a different keyword."
                )
            }

            else -> {
                NotesContent(
                    notes = uiState.notes,
                    onNoteClick = onNoteClick,
                    onNoteLongClick = onNoteLongClick,
                    onPinClick = onPinClick,
                    paddingValues = paddingValues,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}