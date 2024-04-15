package com.mihan.movie.library.presentation.screens.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.mihan.movie.library.R
import com.mihan.movie.library.presentation.animation.AnimatedScreenTransitions
import com.mihan.movie.library.presentation.navigation.popUpToExit
import com.mihan.movie.library.presentation.screens.destinations.HomeScreenDestination
import com.mihan.movie.library.presentation.ui.size100dp
import com.mihan.movie.library.presentation.ui.size20dp
import com.mihan.movie.library.presentation.ui.size32sp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalTvMaterial3Api::class)
@RootNavGraph(start = true)
@Destination(style = AnimatedScreenTransitions::class)
@Composable
fun SplashScreen(
    navigator: DestinationsNavigator,
    splashViewModel: SplashViewModel = hiltViewModel()
) {
    val screenState by splashViewModel.splashScreenState.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painterResource(id = R.drawable.ic_movie),
            contentDescription = null,
            modifier = Modifier
                .size(size100dp)
                .padding(bottom = size20dp)
        )
        Text(
            text = stringResource(id = R.string.app_name),
            fontSize = size32sp,
            fontWeight = FontWeight.W700
        )
    }
    if (screenState.toNextScreen)
        navigateToNextScreen(navigator)
}

fun navigateToNextScreen(navigator: DestinationsNavigator) {
    navigator.navigate(HomeScreenDestination) {
        popUpToExit()
    }
}

