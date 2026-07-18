package com.surendra.suryanotes

import android.app.Application
import com.surendra.suryanotes.di.appModule
import com.surendra.suryanotes.di.databaseModule
import com.surendra.suryanotes.di.preferenceModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class NoteCraftApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {

            androidContext(this@NoteCraftApplication)

            modules(
                appModule,
                databaseModule,
                preferenceModule
            )
        }
    }
}