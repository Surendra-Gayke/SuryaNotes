package com.surendra.suryanotes.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.surendra.suryanotes.core.navigation.AppNavHost
import com.surendra.suryanotes.ui.theme.NoteCraftTheme

/**
 * Root composable of the NoteCraft application.
 *
 * Responsibilities:
 * - Apply the application theme.
 * - Provide the root Surface.
 * - Host the application's navigation graph.
 *
 * Business logic must never be placed here.
 */
@Composable
fun NoteCraftApp() {

    NoteCraftTheme {

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AppNavHost()
        }

    }
}