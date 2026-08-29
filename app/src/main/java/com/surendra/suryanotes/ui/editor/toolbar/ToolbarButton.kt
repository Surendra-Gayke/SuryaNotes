package com.surendra.suryanotes.ui.editor.toolbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.surendra.suryanotes.ui.editor.model.RichTextToolbarEvent
import com.surendra.suryanotes.ui.editor.model.ToolbarAction
@Composable
fun ToolbarButton(

    icon: ImageVector,

    selected: Boolean,

    contentDescription: String,

    action: ToolbarAction,

    onEvent: (RichTextToolbarEvent) -> Unit
) {

    Surface(
        shape = CircleShape,
        color =
            if (selected)
                MaterialTheme.colorScheme.secondaryContainer
            else
                MaterialTheme.colorScheme.surface,

        modifier = Modifier.size(36.dp)
    ) {

        Box(
            modifier = Modifier
                .size(36.dp)
                .clickable {
                    onEvent(
                        RichTextToolbarEvent(action = action)
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(

                imageVector = icon,
                contentDescription = contentDescription
            )
        }
    }
}