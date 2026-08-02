package com.surendra.suryanotes.core.navigation

import kotlinx.serialization.Serializable

@Serializable
data object Home

@Serializable
data class Editor(
    val noteId: Long? = null
)