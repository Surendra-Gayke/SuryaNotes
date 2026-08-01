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

    /**
     * Create a new note.
     */
    suspend fun createNote(
        note: Note
    )

    /**
     * Update an existing note.
     */
    suspend fun updateNote(
        note: Note
    )

    /**
     * Delete a note.
     */
    suspend fun deleteNote(
        id: Long
    )

}