package com.surendra.suryanotes.ui.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.surendra.suryanotes.ui.editor.model.EditorHeading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeadingPickerSheet(
    currentHeading: EditorHeading,
    onHeadingSelected: (EditorHeading) -> Unit,
    onDismiss: () -> Unit
) {

    val headings = listOf(
        EditorHeading.Normal,
        EditorHeading.H1,
        EditorHeading.H2,
        EditorHeading.H3,
        EditorHeading.H4,
        EditorHeading.H5,
        EditorHeading.H6
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 420.dp),
            contentPadding = PaddingValues(
                start = 24.dp,
                end = 24.dp,
                top = 8.dp,
                bottom = 32.dp
            )
        ) {
            items(headings) { heading ->
                HeadingItem(
                    heading = heading,
                    selected = heading == currentHeading,
                    onClick = {
                        onHeadingSelected(heading)
                    }
                )
            }
        }
    }
}

@Composable
private fun HeadingItem(
    heading: EditorHeading,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (selected)
                    MaterialTheme.colorScheme.secondaryContainer
                else
                    Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(
                horizontal = 16.dp,
                vertical = 10.dp
            )
    ) {
        Text(
            text = heading.label(),
            style = heading.textStyle()
        )
    }
}

@Composable
private fun EditorHeading.textStyle() =
    when (this) {

        EditorHeading.Normal ->
            MaterialTheme.typography.bodyLarge

        EditorHeading.H1 ->
            MaterialTheme.typography.headlineLarge

        EditorHeading.H2 ->
            MaterialTheme.typography.headlineMedium

        EditorHeading.H3 ->
            MaterialTheme.typography.headlineSmall

        EditorHeading.H4 ->
            MaterialTheme.typography.titleLarge

        EditorHeading.H5 ->
            MaterialTheme.typography.titleMedium

        EditorHeading.H6 ->
            MaterialTheme.typography.titleSmall
    }

private fun EditorHeading.label(): String =
    when (this) {

        EditorHeading.Normal ->
            "Normal"

        EditorHeading.H1 ->
            "Heading 1"

        EditorHeading.H2 ->
            "Heading 2"

        EditorHeading.H3 ->
            "Heading 3"

        EditorHeading.H4 ->
            "Heading 4"

        EditorHeading.H5 ->
            "Heading 5"

        EditorHeading.H6 ->
            "Heading 6"
    }