package com.surendra.suryanotes.ui.editor.toolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.surendra.suryanotes.ui.editor.model.EditorHeading
import com.surendra.suryanotes.ui.editor.model.RichTextToolbarEvent
import com.surendra.suryanotes.ui.editor.model.ToolbarAction
@Composable
fun ToolbarButton(

    icon: ImageVector? = null,

    painter: Painter? = null,

    selected: Boolean,

    enabled: Boolean = true,

    text: String? = null,

    contentDescription: String,

    action: ToolbarAction,

    currentTextColor: Color? = null,

    onEvent: (RichTextToolbarEvent) -> Unit,

    onPositionChanged: ((ToolbarAction, Int) -> Unit)? = null
) {

    val interactionSource = remember {
        MutableInteractionSource()
    }

    Surface(
        shape = CircleShape,
        color =
            if (selected)
                MaterialTheme.colorScheme.secondaryContainer
            else
                MaterialTheme.colorScheme.surface,

        modifier = Modifier
            .size(36.dp)
            .onGloballyPositioned { coordinates ->
                onPositionChanged?.invoke(
                    action,
                    coordinates.positionInParent().x.toInt()
                )
            }

    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(),
                    enabled = enabled,
                    onClick = {
                        onEvent(
                            RichTextToolbarEvent.Action(action)
                        )
                    }
                ),

            contentAlignment = Alignment.Center

        ) {
            when {
                text != null -> {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                icon != null -> {
                    Icon(
                        imageVector = icon,
                        contentDescription = contentDescription,
                        tint = when {
                            !enabled ->
                                MaterialTheme.colorScheme.outline

                            action == ToolbarAction.TextColor ->
                                currentTextColor ?: LocalContentColor.current

                            selected ->
                                MaterialTheme.colorScheme.primary

                            else ->
                                LocalContentColor.current
                        }
                    )
                }
                painter != null -> {
                    Icon(
                        painter = painter,
                        contentDescription = contentDescription
                    )
                }
            }
        }
    }
}