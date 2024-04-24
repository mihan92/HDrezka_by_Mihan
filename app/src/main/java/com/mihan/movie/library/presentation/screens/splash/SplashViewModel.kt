package com.mihan.movie.library.presentation.screens.splash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.common.DataStorePrefs
import com.mihan.movie.library.common.entites.Filter
import com.mihan.movie.library.common.utils.EventManager
import com.mihan.movie.library.common.utils.IDownloadManager
import com.mihan.movie.library.domain.usecases.GetBaseUrlUseCase
import com.mihan.movie.library.domain.usecases.GetListVideoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val getListVideoUseCase: GetListVideoUseCase,
    private val dataStorePrefs: DataStorePrefs,
    private val getBaseUrlUseCase: GetBaseUrlUseCase,
    private val eventManager: EventManager,
    downloadManager: IDownloadManager,
    application: Application
) : AndroidViewModel(application) {

    private val _screenState = MutableStateFlow(SplashScreenState())
    val splashScreenState = _screenState.asStateFlow()

    init {
        viewModelScope.launch {
            getBaseUrl()
            getListVideo()
            downloadManager.deleteOldApk()
        }
    }

    private suspend fun getBaseUrl() {
        val isAutoUpdateEnabled = dataStorePrefs.getAutoUpdate().first()
        if (!isAutoUpdateEnabled) return
        getBaseUrlUseCase().onEach { result ->
            when (result) {
                is ApiResponse.Success -> {
                    val baseUrl = result.data.baseUrl
                    if (baseUrl != dataStorePrefs.getBaseUrl().first()) {
                        dataStorePrefs.setBaseUrl(baseUrl)
                        eventManager.sendEvent("Ссылка на сайт обновлена $baseUrl")
                    }
                }
                is ApiResponse.Error -> eventManager.sendEvent(result.errorMessage)
                is ApiResponse.Loading -> Unit
            }
        }.last()
    }

    private suspend fun getListVideo() {
            val videoCategory = dataStorePrefs.getVideoCategory().first()
            getListVideoUseCase(Filter.Watching, videoCategory, FIRST_PAGE).onEach { result ->
                when (result) {
                    is ApiResponse.Error -> _screenState.update { SplashScreenState(toNextScreen = true) }
                    is ApiResponse.Loading -> Unit
                    is ApiResponse.Success -> _screenState.update { SplashScreenState(success = true, toNextScreen = true) }
                }
            }.last()
    }

    companion object {
        private const val FIRST_PAGE = 1
    }
}