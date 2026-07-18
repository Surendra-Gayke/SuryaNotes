package com.surendra.suryanotes.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.surendra.suryanotes.core.navigation.AppNavHost
import com.surendra.suryanotes.ui.theme.NoteCraftTheme

@Composable
fun NoteCraftApp() {

    NoteCraftTheme {

        Surface {

            AppNavHost()
        }
    }
}