package com.surendra.suryanotes.ui.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.surendra.suryanotes.domain.model.Note
import com.surendra.suryanotes.ui.components.EmptyState
import com.surendra.suryanotes.ui.home.components.NotesContent
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    onNavigateToEditor: (Long?) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {

    val uiState by viewModel
        .uiState
        .collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {

        viewModel.effect.collectLatest { effect ->

            when (effect) {

                is HomeEffect.NavigateToEditor -> {

                    onNavigateToEditor(
                        effect.noteId
                    )

                }

            }

        }

    }

    Scaffold(

        floatingActionButton = {

            FloatingActionButton(

                onClick = {

                    viewModel.onEvent(
                        HomeEvent.AddNoteClicked
                    )

                }

            ) {

                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Create Note"
                )

            }

        }

    ) { padding ->

        HomeScreenContent(

            modifier = Modifier
                .padding(padding),

            uiState = uiState,

            onNoteClick = { note ->

                viewModel.onEvent(

                    HomeEvent.NoteClicked(
                        noteId = note.id
                    )

                )

            }

        )

    }

}

@Composable
private fun HomeScreenContent(

    modifier: Modifier = Modifier,

    uiState: HomeUiState,

    onNoteClick: (Note) -> Unit

) {

    when {

        uiState.isLoading -> {

            EmptyState(

                modifier = modifier,

                title = "Loading...",

                description = "Please wait."

            )

        }

        uiState.notes.isEmpty() -> {

            EmptyState(

                modifier = modifier,

                title = "No Notes Yet",

                description = "Create your first note to get started."

            )

        }

        else -> {

            NotesContent(

                modifier = modifier,

                notes = uiState.notes,

                onNoteClick = onNoteClick

            )

        }

    }

}