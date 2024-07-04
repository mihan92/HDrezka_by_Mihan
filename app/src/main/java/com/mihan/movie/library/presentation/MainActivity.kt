package com.mihan.movie.library.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.ModalNavigationDrawer
import androidx.tv.material3.Surface
import androidx.tv.material3.rememberDrawerState
import com.mihan.movie.library.BuildConfig
import com.mihan.movie.library.R
import com.mihan.movie.library.common.DataStorePrefs
import com.mihan.movie.library.common.analytics.AnalyticsEvent
import com.mihan.movie.library.common.analytics.sendEvent
import com.mihan.movie.library.common.extentions.logger
import com.mihan.movie.library.common.models.Colors
import com.mihan.movie.library.common.utils.AppUpdatesChecker
import com.mihan.movie.library.common.utils.EventManager
import com.mihan.movie.library.presentation.navigation.Screens
import com.mihan.movie.library.presentation.screens.NavGraphs
import com.mihan.movie.library.presentation.ui.theme.MovieLibraryTheme
import com.mihan.movie.library.presentation.ui.view.DrawerContent
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint
import io.appmetrica.analytics.AppMetrica
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    internal lateinit var navController: NavHostController

    @Inject
    internal lateinit var dataStorePrefs: DataStorePrefs

    @Inject
    internal lateinit var appUpdatesChecker: AppUpdatesChecker

    @Inject
    internal lateinit var eventManager: EventManager

    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appUpdatesChecker.checkUpdates()
        onBackPressedCallback()
        registerEventManager()
        sendNewUserEvent()
        sendAppOpenEvent()
        setContent {
            val primaryColorState = dataStorePrefs.getPrimaryColor().collectAsStateWithLifecycle(Colors.Color0)
            val primaryColor by remember { primaryColorState }
            MovieLibraryTheme(selectedColor = primaryColor) {
                val appUpdateState = dataStorePrefs.getAppUpdates().collectAsStateWithLifecycle(initialValue = false)
                val isUserAuthorized by dataStorePrefs.getUserAuthorizationStatus().collectAsStateWithLifecycle(false)
                val hasNewSeries by dataStorePrefs.getNewSeriesStatus().collectAsStateWithLifecycle(initialValue = false)
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
                            if (currentDestination in screensWithDrawer)
                                DrawerContent(
                                    drawerState = drawerState,
                                    currentDestination = currentDestination,
                                    isAppUpdatesAvailable = isAppUpdateAvailable,
                                    isNewSeriesAvailable = isUserAuthorized && hasNewSeries,
                                    navController = navController
                                )
                        },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .animateContentSize(tween()),
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

    private fun registerEventManager() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                eventManager.eventsFlow.onEach { eventMessage ->
                    logger("event message $eventMessage")
                    Toast.makeText(this@MainActivity, eventMessage, Toast.LENGTH_LONG).show()
                }.launchIn(this)
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

    private fun sendNewUserEvent() {
        if (BuildConfig.DEBUG) return
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                val isUserHasRegisteredStatus = dataStorePrefs.getNewUserRegisterStatus().first()
                if (isUserHasRegisteredStatus) return@repeatOnLifecycle
                val deviceId = AppMetrica.getDeviceId(this@MainActivity) ?: return@repeatOnLifecycle
                sendEvent(AnalyticsEvent.DEVICES, deviceId)
                dataStorePrefs.updateNewUserRegisterStatus(true)
            }
        }
    }

    private fun sendAppOpenEvent() {
        if (BuildConfig.DEBUG) return
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                val deviceId = AppMetrica.getDeviceId(this@MainActivity) ?: return@repeatOnLifecycle
                sendEvent(AnalyticsEvent.SESSIONS, deviceId)
            }
        }
    }

    companion object {
        private val screensWithDrawer = listOf(
            Screens.Home.route,
            Screens.Search.route,
            Screens.Settings.route,
            Screens.HistoryScreen.route,
            Screens.NewSeriesScreen.route,
            Screens.AppUpdatesScreen.route,
            Screens.FavouritesScreen.route,
        )
        private const val TIME_INTERVAL = 3000L
    }
}


