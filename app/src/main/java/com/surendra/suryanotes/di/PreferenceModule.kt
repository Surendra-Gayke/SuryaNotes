package com.surendra.suryanotes.di

import com.surendra.suryanotes.data.preferences.dataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val preferenceModule = module {

    single {
        androidContext().dataStore
    }

}