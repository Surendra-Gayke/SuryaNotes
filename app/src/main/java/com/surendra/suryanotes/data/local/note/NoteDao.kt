package com.surendra.suryanotes.data.local.note

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Room Data Access Object for NoteEntity.
 *
 * This interface defines all persistence operations for notes.
 * It should only deal with Room entities and database operations.
 */
@Dao
interface NoteDao {

    /**
     * Observe all notes ordered by last updated time.
     */
    @Query(
        """
        SELECT *
        FROM notes
        ORDER BY updatedAt DESC
        """
    )
    fun observeNotes(): Flow<List<NoteEntity>>

    /**
     * Get a single note by its id.
     */
    @Query(
        """
        SELECT *
        FROM notes
        WHERE id = :id
        """
    )
    suspend fun getNoteById(
        id: Long
    ): NoteEntity?

    /**
     * Insert a new note.
     */
    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insert(
        note: NoteEntity
    )

    /**
     * Update an existing note.
     */
    @Update
    suspend fun update(
        note: NoteEntity
    )

    /**
     * Delete a note.
     */
    @Delete
    suspend fun delete(
        note: NoteEntity
    )
}