package com.mihan.movie.library.presentation.navigation

import androidx.navigation.NavOptionsBuilder
import com.ramcosta.composedestinations.generated.destinations.SplashScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavOptionsBuilder

fun DestinationsNavOptionsBuilder.popUpToExit() {
    popUpTo(SplashScreenDestination) {
        inclusive = true
        saveState = false
    }
}

fun NavOptionsBuilder.popUpToMain() {
    popUpTo(Screens.Home.route) {
        inclusive = false
    }
    launchSingleTop = true
}