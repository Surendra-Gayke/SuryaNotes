package com.surendra.suryanotes.data.mapper

import com.surendra.suryanotes.data.local.note.NoteEntity
import com.surendra.suryanotes.domain.model.Note

/**
 * Maps Room entities to Domain models and vice versa.
 *
 * Keeping these mappings centralized ensures that
 * the rest of the application never depends on Room.
 */

/**
 * Converts a Room entity into a Domain model.
 */
fun NoteEntity.toDomain(): Note =
    Note(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isPinned = isPinned
    )

/**
 * Domain -> Room
 */
fun Note.toEntity(): NoteEntity =
    NoteEntity(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isPinned = isPinned
    )