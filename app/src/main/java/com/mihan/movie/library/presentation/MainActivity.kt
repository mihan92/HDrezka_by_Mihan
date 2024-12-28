package com.mihan.movie.library.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.ModalNavigationDrawer
import androidx.tv.material3.Surface
import androidx.tv.material3.rememberDrawerState
import com.mihan.movie.library.BuildConfig
import com.mihan.movie.library.R
import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.common.Constants
import com.mihan.movie.library.common.DataStorePrefs
import com.mihan.movie.library.common.analytics.AnalyticsEvent
import com.mihan.movie.library.common.analytics.sendEvent
import com.mihan.movie.library.common.extentions.logger
import com.mihan.movie.library.common.models.Colors
import com.mihan.movie.library.common.utils.AppUpdatesChecker
import com.mihan.movie.library.common.utils.EventManager
import com.mihan.movie.library.common.utils.SharedPrefs
import com.mihan.movie.library.domain.usecases.gson_repository.CheckNotificationMessageUseCase
import com.mihan.movie.library.presentation.animation.AnimatedScreenTransitions
import com.mihan.movie.library.presentation.navigation.Screens
import com.mihan.movie.library.presentation.ui.size60dp
import com.mihan.movie.library.presentation.ui.sizeEmpty
import com.mihan.movie.library.presentation.ui.theme.MovieLibraryTheme
import com.mihan.movie.library.presentation.ui.view.DrawerContent
import com.mihan.movie.library.presentation.ui.view.InformationDialog
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import dagger.hilt.android.AndroidEntryPoint
import io.appmetrica.analytics.AppMetrica
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    internal lateinit var dataStorePrefs: DataStorePrefs

    @Inject
    internal lateinit var sharedPrefs: SharedPrefs

    @Inject
    internal lateinit var appUpdatesChecker: AppUpdatesChecker

    @Inject
    internal lateinit var eventManager: EventManager

    @Inject
    internal lateinit var checkNotificationMessageUseCase: CheckNotificationMessageUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appUpdatesChecker.checkUpdates()
        onBackPressedCallback()
        registerEventManager()
        sendAppOpenEvent()
        setContent {
            val primaryColorState = dataStorePrefs.getPrimaryColor().collectAsStateWithLifecycle(Colors.Color0)
            val primaryColor by remember { primaryColorState }
            MovieLibraryTheme(selectedColor = primaryColor) {
                val appUpdateState = dataStorePrefs.getAppUpdates().collectAsStateWithLifecycle(initialValue = false)
                val hasNewSeries by dataStorePrefs.getNewSeriesStatus()
                    .collectAsStateWithLifecycle(initialValue = false)
                val isAppUpdateAvailable by remember { appUpdateState }
                val navController = rememberNavController()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination by remember { derivedStateOf { navBackStackEntry?.destination?.route } }
                var notificationDialogState by rememberSaveable { mutableStateOf(false) }
                var notificationMessage by rememberSaveable { mutableStateOf(Constants.EMPTY_STRING) }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape
                ) {
                    val screenWithDrawer = currentDestination in screensWithDrawer
                    val contentStartPadding by animateDpAsState(
                        targetValue = if (screenWithDrawer) size60dp else sizeEmpty,
                        animationSpec = tween(delayMillis = TWEEN_DURATION),
                        label = "dpAnimation"
                    )
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            AnimatedVisibility(
                                screenWithDrawer,
                                enter = fadeIn(
                                    animationSpec = tween(delayMillis = AnimatedScreenTransitions.TWEEN_DURATION)
                                ),
                                exit = fadeOut(),
                            ) {
                                DrawerContent(
                                    drawerState = drawerState,
                                    currentDestination = currentDestination,
                                    isAppUpdatesAvailable = isAppUpdateAvailable,
                                    isNewSeriesAvailable = hasNewSeries,
                                    navController = navController
                                )
                            }
                        },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .animateContentSize(tween()),
                    ) {
                        DestinationsNavHost(
                            navGraph = NavGraphs.root,
                            navController = navController,
                            modifier = Modifier.padding(start = contentStartPadding)
                        )
                    }
                }
                InformationDialog(
                    dialogTitle = stringResource(R.string.dear_users_title),
                    dialogDescription = notificationMessage,
                    showDialogState = notificationDialogState,
                    onButtonAgreePressed = { notificationDialogState = false }
                )
                LaunchedEffect(Unit) {
                    checkNotificationMessageUseCase()
                        .onEach { result ->
                            when (result) {
                                is ApiResponse.Loading -> Unit
                                is ApiResponse.Error -> logger(result.errorMessage)
                                is ApiResponse.Success -> {
                                    if (result.data.message.isNotEmpty()) {
                                        val message = dataStorePrefs.getNotificationMessage().first()
                                        if (message.contains(result.data.message, ignoreCase = true)) return@onEach
                                        notificationMessage = result.data.message
                                        notificationDialogState = true
                                        dataStorePrefs.updateNotificationMessage(result.data.message)
                                    }
                                }
                            }
                        }.last()
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
            if (currentTimeInMillis + ON_BACK_PRESSED_TIME_INTERVAL > System.currentTimeMillis())
                finish()
            else {
                Toast.makeText(this@MainActivity, getString(R.string.toast_confirm_exit), Toast.LENGTH_SHORT).show()
                currentTimeInMillis = System.currentTimeMillis()
            }
        }
    }

    private fun sendAppOpenEvent() {
        if (BuildConfig.DEBUG) return
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                delay(DELAY_TIME_BEFORE_SENDING_EVENT)
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
        private const val ON_BACK_PRESSED_TIME_INTERVAL = 3000L
        private const val DELAY_TIME_BEFORE_SENDING_EVENT = 5000L
        private const val TWEEN_DURATION = 600
    }
}


