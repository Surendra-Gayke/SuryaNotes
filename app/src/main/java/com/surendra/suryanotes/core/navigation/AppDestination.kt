package com.surendra.suryanotes.core.navigation

import kotlinx.serialization.Serializable

/**
 * Root destinations for the application.
 *
 * Every destination must be @Serializable.
 * No string routes are allowed.
 */
@Serializable
data object Home