package com.surendra.suryanotes.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.surendra.suryanotes.ui.screen.HomeScreen

private const val HOME_ROUTE = "home"

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HOME_ROUTE
    ) {

        composable(HOME_ROUTE) {

            HomeScreen()

        }

    }

}