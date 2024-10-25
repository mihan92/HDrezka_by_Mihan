package com.mihan.movie.library.presentation.screens.splash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mihan.movie.library.R
import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.common.DataStorePrefs
import com.mihan.movie.library.common.utils.EventManager
import com.mihan.movie.library.common.utils.IDownloadManager
import com.mihan.movie.library.common.utils.SharedPrefs
import com.mihan.movie.library.domain.usecases.parser.GetBaseUrlUseCase
import com.mihan.movie.library.domain.usecases.parser.GetNewSeriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val dataStorePrefs: DataStorePrefs,
    private val getBaseUrlUseCase: GetBaseUrlUseCase,
    private val getNewSeriesUseCase: GetNewSeriesUseCase,
    private val eventManager: EventManager,
    private val application: Application,
    private val sharedPrefs: SharedPrefs,
    downloadManager: IDownloadManager,
) : AndroidViewModel(application) {

    private val _screenState = MutableStateFlow(SplashScreenState())
    val splashScreenState = _screenState.asStateFlow()

    init {
        viewModelScope.launch {
            getBaseUrl()
            val isUserAuthorized = sharedPrefs.getUserAuthStatus()
            if (isUserAuthorized)
                getNewSeries()
            else
                dataStorePrefs.updateNewSeriesStatus(false)
            downloadManager.deleteOldApk()
            _screenState.update { SplashScreenState(toNextScreen = true) }
        }
    }

    private suspend fun getBaseUrl() {
        val isAutoUpdateEnabled = dataStorePrefs.getAutoUpdate().first()
        if (!isAutoUpdateEnabled) {
            delay(SPLASH_SCREEN_DELAY_TIME)
            return
        }
        getBaseUrlUseCase().onEach { result ->
            when (result) {
                is ApiResponse.Success -> {
                    val baseUrl = result.data.baseUrl
                    if (baseUrl != dataStorePrefs.getBaseUrl().first()) {
                        dataStorePrefs.setBaseUrl(baseUrl)
                        dataStorePrefs.updateNewSeriesStatus(false)
                        sharedPrefs.clearCookies()
                        eventManager.sendEvent(application.getString(R.string.updated_url_message, baseUrl))
                    }
                }
                is ApiResponse.Error -> eventManager.sendEvent(result.errorMessage)
                is ApiResponse.Loading -> Unit
            }
        }.last()
    }

    private suspend fun getNewSeries() {
        getNewSeriesUseCase().onEach { result ->
            when(result) {
                is ApiResponse.Loading -> Unit
                is ApiResponse.Error -> dataStorePrefs.updateNewSeriesStatus(false)
                is ApiResponse.Success -> {
                    if (result.data.isEmpty()) dataStorePrefs.updateNewSeriesStatus(false)
                    else dataStorePrefs.updateNewSeriesStatus(true)
                }
            }
        }.last()
    }

    companion object {
        private const val SPLASH_SCREEN_DELAY_TIME = 1500L
    }
}