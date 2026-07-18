package com.surendra.suryanotes.di

import androidx.room.Room
import com.surendra.suryanotes.core.constants.DatabaseConstants
import com.surendra.suryanotes.data.local.database.NoteCraftDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {

    single {
        Room.databaseBuilder(
            androidContext(),
            NoteCraftDatabase::class.java,
            DatabaseConstants.DATABASE_NAME
        ).build()
    }

    single {
        get<NoteCraftDatabase>().noteDao()
    }
}