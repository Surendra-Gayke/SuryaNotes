package com.surendra.suryanotes.domain.model

/**
 * Pure business representation of a Note.
 *
 * This model is completely independent of:
 * - Room
 * - Android Framework
 * - Compose
 * - Serialization
 */
data class Note(

    val id: Long = 0L,

    val title: String,

    val content: String,

    val createdAt: Long,

    val updatedAt: Long,

    val isPinned: Boolean = false

)