package com.surendra.suryanotes.ui.editor.richtext

import com.surendra.suryanotes.ui.editor.model.ToolbarAction

internal object ToolbarActionOrder {

    val orderedActions = listOf(

        ToolbarAction.Bold,

        ToolbarAction.Italic,

        ToolbarAction.Underline,

        ToolbarAction.Strike,

        ToolbarAction.TextColor,

        ToolbarAction.Heading,

        ToolbarAction.BulletList,

        ToolbarAction.NumberedList
    )

    fun firstSelected(
        activeActions: Set<ToolbarAction>
    ): ToolbarAction? {

        return orderedActions.firstOrNull {
            it in activeActions
        }
    }
}