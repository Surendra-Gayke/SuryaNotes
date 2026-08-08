package com.surendra.suryanotes.domain.repository

import com.surendra.suryanotes.domain.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Defines all business operations that can be performed on Notes.
 *
 * This contract is completely independent of:
 * - Room
 * - Android
 * - Compose
 * - Network
 */
interface NoteRepository {

    /**
     * Observe all notes.
     *
     * The returned Flow emits a new list whenever the
     * underlying data changes.
     */
    fun observeNotes(): Flow<List<Note>>

    /**
     * Get a note by its id.
     */
    suspend fun getNoteById(
        id: Long
    ): Note?

    suspend fun createNote(
        note: Note
    ): Long

    suspend fun updateNote(
        note: Note
    )

    suspend fun deleteNote(
        id: Long
    )

}