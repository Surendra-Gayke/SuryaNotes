package com.surendra.suryanotes.data.repository

import com.surendra.suryanotes.data.local.note.NoteDao
import com.surendra.suryanotes.data.mapper.toDomain
import com.surendra.suryanotes.data.mapper.toEntity
import com.surendra.suryanotes.domain.model.Note
import com.surendra.suryanotes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Default implementation of [NoteRepository].
 *
 * Responsibilities:
 * - Interact with the DAO
 * - Convert Entity ↔ Domain
 * - Expose business-friendly APIs
 */
class NoteRepositoryImpl(
    private val noteDao: NoteDao
) : NoteRepository {

    override fun observeNotes(): Flow<List<Note>> {
        return noteDao
            .observeNotes()
            .map { entities ->
                entities.map { entity ->
                    entity.toDomain()
                }
            }
    }

    override suspend fun getNoteById(
        id: Long
    ): Note? {
        return noteDao
            .getNoteById(id)
            ?.toDomain()
    }

    override suspend fun createNote(
        note: Note
    ): Long {
        return noteDao.insert(
            note.toEntity()
        )
    }

    override suspend fun updateNote(
        note: Note
    ) {
        noteDao.update(
            note.toEntity()
        )
    }

    override suspend fun deleteNote(
        id: Long
    ) {
        val note = noteDao.getNoteById(id)
            ?: return

        noteDao.delete(note)
    }

}