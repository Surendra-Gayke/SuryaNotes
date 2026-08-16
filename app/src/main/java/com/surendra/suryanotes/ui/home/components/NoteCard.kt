package com.surendra.suryanotes.ui.home.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.surendra.suryanotes.core.util.toNotePreview
import com.surendra.suryanotes.domain.model.Note

@Composable
fun NoteCard(
    note: Note,
    onClick: (Note) -> Unit,
    onLongClick: (Note) -> Unit,
    onPinClick: (Note) -> Unit,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onClick(note) },
                onLongClick = { onLongClick(note) }
            ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (note.title.isNotBlank()) {
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        modifier = Modifier.weight(1f) // pushes icon to right
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                IconButton(
                    onClick = {
                        onPinClick(note)
                    }
                ) {
                    Icon(
                        imageVector = if (note.isPinned)
                            Icons.Default.PushPin
                        else
                            Icons.Outlined.PushPin,
                        contentDescription = "Pin Note",
                        tint = if (note.isPinned)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = note.content.toNotePreview(),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = formatDate(note.updatedAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

        }
    }
}

private fun formatDate(timestamp: Long): String {
    val formatter = java.text.SimpleDateFormat(
        "dd MMM, hh:mm a",
        java.util.Locale.getDefault()
    )
    return formatter.format(java.util.Date(timestamp))
}