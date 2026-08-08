package com.surendra.suryanotes.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.surendra.suryanotes.domain.model.Note

@Composable
fun NotesContent(
    notes: List<Note>,
    onNoteClick: (Note) -> Unit,
    onNoteLongClick: (Note) -> Unit,
    onPinClick: (Note) -> Unit,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(160.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 16.dp + paddingValues.calculateTopPadding(),
            bottom = 16.dp + paddingValues.calculateBottomPadding()
        ),
        verticalItemSpacing = 12.dp,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        items(
            items = notes,
            key = { it.id }
        ) { note ->

            NoteCard(
                note = note,
                onClick = onNoteClick,
                onLongClick = onNoteLongClick,
                onPinClick = onPinClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}