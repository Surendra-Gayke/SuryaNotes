package com.surendra.suryanotes.di

import androidx.room.Room
import com.surendra.suryanotes.core.constants.DatabaseConstants
import com.surendra.suryanotes.data.local.database.NoteCraftDatabase
import com.surendra.suryanotes.data.repository.NoteRepositoryImpl
import com.surendra.suryanotes.domain.repository.NoteRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {

    /**
     * Room Database
     */
    single {
        Room.databaseBuilder(
            androidContext(),
            NoteCraftDatabase::class.java,
            DatabaseConstants.DATABASE_NAME
        ).build()
    }

    /**
     * DAO
     */
    single {
        get<NoteCraftDatabase>().noteDao()
    }

    /**
     * Repository
     */
    single<NoteRepository> {
        NoteRepositoryImpl(
            noteDao = get()
        )
    }

}