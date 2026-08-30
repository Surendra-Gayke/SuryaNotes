package com.surendra.suryanotes.ui.editor.toolbar

import android.util.Log
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.FormatListBulleted
import androidx.compose.material.icons.rounded.FormatBold
import androidx.compose.material.icons.rounded.FormatColorText
import androidx.compose.material.icons.rounded.FormatItalic
import androidx.compose.material.icons.rounded.FormatListNumbered
import androidx.compose.material.icons.rounded.FormatUnderlined
import androidx.compose.material.icons.rounded.StrikethroughS
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.surendra.suryanotes.ui.editor.model.EditorHeading
import com.surendra.suryanotes.ui.editor.model.RichTextToolbarEvent
import com.surendra.suryanotes.ui.editor.model.RichTextToolbarState
import com.surendra.suryanotes.ui.editor.model.ToolbarAction

@Composable
fun RichTextToolbar(

    state: RichTextToolbarState,

    onEvent: (RichTextToolbarEvent) -> Unit,

    modifier: Modifier = Modifier
) {

    val scrollState = rememberScrollState()
    val actionPositions = remember {
        mutableStateMapOf<ToolbarAction, Int>()
    }
    val onToolbarPositionChanged: (ToolbarAction, Int) -> Unit =
        { action, x ->
            actionPositions[action] = x
        }
    var lastScrollTarget by remember {
        mutableIntStateOf(-1)
    }

    LaunchedEffect(
        state.firstSelectedAction,
        actionPositions[state.firstSelectedAction]
    ) {
        val action = state.firstSelectedAction ?: return@LaunchedEffect

        val targetX = actionPositions[action] ?: return@LaunchedEffect

        if (targetX == lastScrollTarget) {
            return@LaunchedEffect
        }
        lastScrollTarget = targetX
        scrollState.animateScrollTo(targetX)
    }

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
                .horizontalScroll(scrollState)
                .padding(horizontal = 8.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            ToolbarButton(
                icon = Icons.Rounded.FormatBold,
                selected = ToolbarAction.Bold in state.activeActions,
                contentDescription = "Bold",
                action = ToolbarAction.Bold,
                onEvent = onEvent,
                onPositionChanged = onToolbarPositionChanged
            )

            ToolbarButton(
                icon = Icons.Rounded.FormatItalic,
                selected = ToolbarAction.Italic in state.activeActions,
                contentDescription = "Italic",
                action = ToolbarAction.Italic,
                onEvent = onEvent,
                onPositionChanged = onToolbarPositionChanged
            )

            ToolbarButton(
                icon = Icons.Rounded.FormatUnderlined,
                selected = ToolbarAction.Underline in state.activeActions,
                contentDescription = "Underline",
                action = ToolbarAction.Underline,
                onEvent = onEvent,
                onPositionChanged = onToolbarPositionChanged
            )

            ToolbarButton(
                icon = Icons.Rounded.StrikethroughS,
                selected = ToolbarAction.Strike in state.activeActions,
                contentDescription = "Strikethrough",
                action = ToolbarAction.Strike,
                onEvent = onEvent,
                onPositionChanged = onToolbarPositionChanged
            )

            ToolbarButton(
                icon = Icons.Rounded.FormatColorText,
                selected = false,
                currentTextColor = state.currentTextColor,
                contentDescription = "Text Color",
                action = ToolbarAction.TextColor,
                onEvent = onEvent,
                onPositionChanged = onToolbarPositionChanged
            )

            ToolbarButton(
                text = state.currentHeading.shortLabel(),
                selected = state.currentHeading != EditorHeading.Normal,
                contentDescription = "Heading",
                action = ToolbarAction.Heading,
                onEvent = onEvent,
                onPositionChanged = onToolbarPositionChanged
            )

            ToolbarButton(
                icon = Icons.AutoMirrored.Rounded.FormatListBulleted,
                selected = ToolbarAction.BulletList in state.activeActions,
                contentDescription = "Bullet List",
                action = ToolbarAction.BulletList,
                onEvent = onEvent,
                onPositionChanged = onToolbarPositionChanged
            )

            ToolbarButton(
                icon = Icons.Rounded.FormatListNumbered,
                selected = ToolbarAction.NumberedList in state.activeActions,
                contentDescription = "Numbered List",
                action = ToolbarAction.NumberedList,
                onEvent = onEvent,
                onPositionChanged = onToolbarPositionChanged
            )
        }
    }
}
private fun EditorHeading.shortLabel(): String =
    when (this) {

        EditorHeading.Normal -> "H"

        EditorHeading.H1 -> "H1"

        EditorHeading.H2 -> "H2"

        EditorHeading.H3 -> "H3"

        EditorHeading.H4 -> "H4"

        EditorHeading.H5 -> "H5"

        EditorHeading.H6 -> "H6"
    }