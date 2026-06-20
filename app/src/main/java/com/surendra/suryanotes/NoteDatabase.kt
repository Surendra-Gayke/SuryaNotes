package com.surendra.suryanotes

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NoteDatabase(context: Context) {

    companion object {
        private const val PREFS_NAME = "notes_database"
        private const val KEY_NOTES = "notes_list"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getAllNotes(): MutableList<Note> {
        val json = prefs.getString(KEY_NOTES, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Note>>() {}.type
        val notes: MutableList<Note> = gson.fromJson(json, type) ?: mutableListOf()
        notes.sortByDescending { it.updatedAt }
        return notes
    }

    fun saveNote(note: Note) {
        val notes = getAllNotes()
        val existingIndex = notes.indexOfFirst { it.id == note.id }

        if (existingIndex >= 0) {
            note.updatedAt = System.currentTimeMillis()
            notes[existingIndex] = note
        } else {
            note.createdAt = System.currentTimeMillis()
            note.updatedAt = System.currentTimeMillis()
            notes.add(0, note)
        }

        saveAllNotes(notes)
    }

    fun deleteNote(noteId: String) {
        val notes = getAllNotes()
        notes.removeAll { it.id == noteId }
        saveAllNotes(notes)
    }

    fun getNoteById(noteId: String): Note? {
        return getAllNotes().find { it.id == noteId }
    }

    fun searchNotes(query: String): MutableList<Note> {
        if (query.isBlank()) return getAllNotes()

        val lowerQuery = query.lowercase().trim()
        return getAllNotes().filter { note ->
            note.title.lowercase().contains(lowerQuery) ||
                    note.content.lowercase().contains(lowerQuery)
        }.toMutableList()
    }

    private fun saveAllNotes(notes: List<Note>) {
        val json = gson.toJson(notes)
        prefs.edit().putString(KEY_NOTES, json).apply()
    }
}