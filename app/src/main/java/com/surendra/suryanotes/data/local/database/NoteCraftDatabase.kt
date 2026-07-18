package com.surendra.suryanotes.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.surendra.suryanotes.data.local.note.NoteDao
import com.surendra.suryanotes.data.local.note.NoteEntity

@Database(
    entities = [
        NoteEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class NoteCraftDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
}