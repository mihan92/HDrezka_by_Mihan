package com.mihan.movie.library.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.ModalNavigationDrawer
import androidx.tv.material3.Surface
import androidx.tv.material3.rememberDrawerState
import com.mihan.movie.library.R
import com.mihan.movie.library.common.DataStorePrefs
import com.mihan.movie.library.common.utils.AppUpdatesChecker
import com.mihan.movie.library.presentation.navigation.Screens
import com.mihan.movie.library.presentation.screens.NavGraphs
import com.mihan.movie.library.presentation.ui.theme.MovieLibraryTheme
import com.mihan.movie.library.presentation.ui.view.DrawerContent
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    internal lateinit var navController: NavHostController

    @Inject
    internal lateinit var dataStorePrefs: DataStorePrefs

    @Inject
    internal lateinit var appUpdatesChecker: AppUpdatesChecker

    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appUpdatesChecker.checkUpdates()
        onBackPressedCallback()
        setContent {
            MovieLibraryTheme {
                val appUpdateState = dataStorePrefs.getAppUpdates().collectAsStateWithLifecycle(initialValue = false)
                val isAppUpdateAvailable by remember { appUpdateState }
                val navController by remember { derivedStateOf { this.navController } }
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination by remember { derivedStateOf { navBackStackEntry?.destination?.route } }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape
                ) {
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            AnimatedVisibility(
                                visible = currentDestination in screensWithDrawer,
                                enter = slideInHorizontally(
                                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                                ) + fadeIn(),
                                exit = slideOutHorizontally(
                                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                                ) + fadeOut()
                            ) {
                                DrawerContent(
                                    drawerState = drawerState,
                                    currentDestination = currentDestination,
                                    isAppUpdatesAvailable = isAppUpdateAvailable,
                                    navController = navController
                                )
                            }
                        },
                        modifier = Modifier.background(MaterialTheme.colorScheme.background),
                    ) {
                        DestinationsNavHost(
                            navGraph = NavGraphs.root,
                            navController = navController
                        )
                    }
                }
            }
        }
    }

    private fun onBackPressedCallback() {
        var currentTimeInMillis = System.currentTimeMillis()
        onBackPressedDispatcher.addCallback {
            if (currentTimeInMillis + TIME_INTERVAL > System.currentTimeMillis())
                finish()
            else {
                Toast.makeText(this@MainActivity, getString(R.string.toast_confirm_exit), Toast.LENGTH_SHORT).show()
                currentTimeInMillis = System.currentTimeMillis()
            }
        }
    }

    companion object {
        private val screensWithDrawer = listOf(
            Screens.Home.route,
            Screens.Search.route,
            Screens.Settings.route,
            Screens.Placeholder.route,
            Screens.AppUpdatesScreen.route
        )
        private const val TIME_INTERVAL = 3000L
    }
}


