package com.surendra.suryanotes.data.preferences

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.surendra.suryanotes.core.constants.PreferenceConstants

val Context.dataStore by preferencesDataStore(
    name = PreferenceConstants.PREFERENCES_NAME
)