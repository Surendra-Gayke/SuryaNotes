package com.surendra.suryanotes.ui.editor.model

import androidx.compose.ui.graphics.Color
import com.surendra.suryanotes.ui.editor.richtext.ToolbarActionOrder

data class RichTextToolbarState(
    val activeActions: Set<ToolbarAction> = emptySet(),
    val currentTextColor: Color? = null,
    val currentHeading: EditorHeading = EditorHeading.Normal,
    val showTextColorPicker: Boolean = false,
    val showHeadingPicker: Boolean = false
) {
    val firstSelectedAction: ToolbarAction?
        get() = ToolbarActionOrder.firstSelected(activeActions)
}