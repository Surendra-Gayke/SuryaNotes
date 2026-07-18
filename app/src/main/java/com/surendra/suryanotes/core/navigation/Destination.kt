package com.surendra.suryanotes.core.navigation

sealed interface Destination {

    data object Home : Destination

}