package com.surendra.suryanotes.ui.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.surendra.suryanotes.domain.model.Note
import com.surendra.suryanotes.ui.components.EmptyState
import com.surendra.suryanotes.ui.home.components.NotesContent
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

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {

                is HomeEffect.NavigateToEditor -> {
                    onNavigateToEditor(effect.noteId)
                }

                HomeEffect.ShowUndoSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = "Note deleted",
                        actionLabel = "Undo"
                    )

                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onEvent(HomeEvent.UndoDelete)
                    }
                }
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,

        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "NoteCraft",
                        style = MaterialTheme.typography.titleLarge
                    )

                }
            )
        },

        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.onEvent(HomeEvent.AddNoteClicked)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Note"
                )
            }
        }

    ) { padding ->

        HomeScreenContent(
            paddingValues = padding,
            uiState = uiState,

            onNoteClick = { note ->
                viewModel.onEvent(
                    HomeEvent.NoteClicked(note.id)
                )
            },

            onNoteLongClick = { note ->
                viewModel.onEvent(
                    HomeEvent.DeleteNoteClicked(note)
                )
            }
        )
    }
}

@Composable
private fun HomeScreenContent(
    paddingValues: PaddingValues,
    uiState: HomeUiState,
    onNoteClick: (Note) -> Unit,
    onNoteLongClick: (Note) -> Unit
) {

    when {

        uiState.isLoading -> {
            EmptyState(
                modifier = Modifier.padding(paddingValues),
                title = "Loading...",
                description = "Please wait."
            )
        }

        uiState.notes.isEmpty() -> {
            EmptyState(
                modifier = Modifier.padding(paddingValues),
                title = "No Notes Yet",
                description = "Create your first note to get started."
            )
        }

        else -> {
            NotesContent(
                notes = uiState.notes,
                onNoteClick = onNoteClick,
                onNoteLongClick = onNoteLongClick,
                paddingValues = paddingValues
            )
        }
    }
}