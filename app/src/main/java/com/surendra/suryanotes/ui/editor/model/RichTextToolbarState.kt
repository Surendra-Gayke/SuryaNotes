package com.surendra.suryanotes.ui.editor.model

data class RichTextToolbarState(
    val activeActions: Set<ToolbarAction> = emptySet()
)