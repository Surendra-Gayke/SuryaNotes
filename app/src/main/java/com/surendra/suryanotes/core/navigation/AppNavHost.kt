package com.surendra.suryanotes.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.surendra.suryanotes.ui.editor.EditorScreen
import com.surendra.suryanotes.ui.home.HomeScreen

@Composable
fun AppNavHost() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Home
    ) {

        composable<Home> {

            HomeScreen(

                onNavigateToEditor = { noteId ->

                    navController.navigate(
                        Editor(noteId)
                    )

                }

            )

        }

        composable<Editor> {

            val route = it.toRoute<Editor>()

            // route is intentionally unused for now.
            // M1.4.1 will pass route.noteId to EditorViewModel
            // through SavedStateHandle.
            EditorScreen()

        }

    }

}