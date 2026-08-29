package com.surendra.suryanotes.ui.editor.toolbar

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FormatBold
import androidx.compose.material.icons.rounded.FormatItalic
import androidx.compose.material.icons.rounded.FormatUnderlined
import androidx.compose.material.icons.rounded.StrikethroughS
import androidx.compose.material.icons.rounded.FormatColorText
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.surendra.suryanotes.ui.editor.model.RichTextToolbarEvent
import com.surendra.suryanotes.ui.editor.model.RichTextToolbarState
import com.surendra.suryanotes.ui.editor.model.ToolbarAction

@Composable
fun RichTextToolbar(

    state: RichTextToolbarState,

    onEvent: (RichTextToolbarEvent) -> Unit,

    modifier: Modifier = Modifier
) {

    Surface(
        modifier = modifier
            .wrapContentWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 3.dp,
        shadowElevation = 3.dp
    ) {

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            ToolbarButton(
                icon = Icons.Rounded.FormatBold,
                selected = ToolbarAction.Bold in state.activeActions,
                contentDescription = "Bold",
                action = ToolbarAction.Bold,
                onEvent = onEvent
            )

            ToolbarButton(
                icon = Icons.Rounded.FormatItalic,
                selected = ToolbarAction.Italic in state.activeActions,
                contentDescription = "Italic",
                action = ToolbarAction.Italic,
                onEvent = onEvent
            )

            ToolbarButton(
                icon = Icons.Rounded.FormatUnderlined,
                selected = ToolbarAction.Underline in state.activeActions,
                contentDescription = "Underline",
                action = ToolbarAction.Underline,
                onEvent = onEvent
            )

            ToolbarButton(
                icon = Icons.Rounded.StrikethroughS,
                selected = ToolbarAction.Strike in state.activeActions,
                contentDescription = "Strikethrough",
                action = ToolbarAction.Strike,
                onEvent = onEvent
            )

            ToolbarButton(
                icon = Icons.Rounded.FormatColorText,
                selected = false,
                currentTextColor = state.currentTextColor,
                contentDescription = "Text Color",
                action = ToolbarAction.TextColor,
                onEvent = onEvent
            )
        }
    }
}