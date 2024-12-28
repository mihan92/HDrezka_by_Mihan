package com.mihan.movie.library.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.common.Constants
import com.mihan.movie.library.common.DataStorePrefs
import com.mihan.movie.library.common.models.Colors
import com.mihan.movie.library.common.models.VideoCategory
import com.mihan.movie.library.common.models.VideoQuality
import com.mihan.movie.library.common.utils.EventManager
import com.mihan.movie.library.common.utils.SharedPrefs
import com.mihan.movie.library.common.utils.whileUiSubscribed
import com.mihan.movie.library.domain.models.UserInfo
import com.mihan.movie.library.domain.usecases.auth.LoginUseCase
import com.mihan.movie.library.domain.usecases.auth.LogoutUseCase
import com.mihan.movie.library.domain.usecases.parser.GetNewSeriesUseCase
import com.mihan.movie.library.domain.usecases.parser.GetUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getNewSeriesUseCase: GetNewSeriesUseCase,
    private val dataStorePrefs: DataStorePrefs,
    private val sharedPrefs: SharedPrefs,
    private val eventManager: EventManager
) : ViewModel() {

    private val _siteDialogState = MutableStateFlow(false)
    private val _userInfo = MutableStateFlow(UserInfo())
    private val _isUserAuthorized = MutableStateFlow(false)
    private val _settingsScreenState = MutableStateFlow(SettingsScreenState())
    val isUserAuthorized = _isUserAuthorized.asStateFlow()
    val siteDialogState = _siteDialogState.asStateFlow()
    val userInfo = _userInfo.asStateFlow()
    val settingsScreenState = _settingsScreenState.asStateFlow()

    val getVideoCategory = dataStorePrefs.getVideoCategory().stateIn(
        viewModelScope,
        whileUiSubscribed,
        VideoCategory.All
    )
    val getVideoQuality = dataStorePrefs.getVideoQuality().stateIn(
        viewModelScope,
        whileUiSubscribed,
        VideoQuality.Quality4k
    )
    val getSiteUrl = dataStorePrefs.getBaseUrl().stateIn(
        viewModelScope,
        whileUiSubscribed,
        Constants.EMPTY_STRING
    )

    val getPrimaryColor = dataStorePrefs.getPrimaryColor().stateIn(
        viewModelScope,
        whileUiSubscribed,
        Colors.Color0
    )

    val autoUpdate = dataStorePrefs.getAutoUpdate().stateIn(
        viewModelScope,
        whileUiSubscribed,
        true
    )

    init {
        viewModelScope.launch {
            if (sharedPrefs.getUserAuthStatus()) {
                _isUserAuthorized.update { true }
                getUserInfo()
            }
        }
    }

    fun login(loginAndPass: Pair<String, String>) {
        viewModelScope.launch {
            loginUseCase(loginAndPass.first, loginAndPass.second)
                .onEach { result ->
                    when (result) {
                        is ApiResponse.Error -> {
                            _settingsScreenState.update { SettingsScreenState(loginLoadingState = false) }
                            eventManager.sendEvent(result.errorMessage)
                        }
                        is ApiResponse.Loading -> {
                            _settingsScreenState.update { SettingsScreenState(loginLoadingState = true) }
                        }
                        is ApiResponse.Success -> {
                            _settingsScreenState.update { it.copy(loginLoadingState = false, isLoginSuccess = true) }
                            getUserInfo()
                            getNewSeries()
                        }
                    }
                }.launchIn(this)
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            dataStorePrefs.updateNewSeriesStatus(false)
            _isUserAuthorized.update { false }
        }
    }

    private suspend fun getUserInfo() {
        val isAuthorized = sharedPrefs.getUserAuthStatus()
        if (!isAuthorized) return
        getUserInfoUseCase().collect { result ->
            when (result) {
                is ApiResponse.Error -> eventManager.sendEvent(result.errorMessage)
                is ApiResponse.Loading -> Unit
                is ApiResponse.Success -> {
                    _isUserAuthorized.update { true }
                    _userInfo.update { result.data }
                }
            }
        }
    }

    private suspend fun getNewSeries() {
        val isAuthorized = sharedPrefs.getUserAuthStatus()
        if (!isAuthorized) return
        getNewSeriesUseCase().onEach { result ->
            when (result) {
                is ApiResponse.Loading -> Unit
                is ApiResponse.Error -> dataStorePrefs.updateNewSeriesStatus(false)
                is ApiResponse.Success -> {
                    if (result.data.isEmpty()) dataStorePrefs.updateNewSeriesStatus(false)
                    else dataStorePrefs.updateNewSeriesStatus(true)
                }
            }
        }.last()
    }

    fun videoCategoryChanged(videoCategory: VideoCategory) {
        viewModelScope.launch {
            dataStorePrefs.setVideoCategory(videoCategory)
        }
    }

    fun videoQualityChanged(videoQuality: VideoQuality) {
        viewModelScope.launch {
            dataStorePrefs.setVideoQuality(videoQuality)
        }
    }

    fun primaryColorChanged(selectedColor: Colors) {
        viewModelScope.launch {
            dataStorePrefs.setPrimaryColor(selectedColor)
        }
    }

    fun onButtonShowDialogClicked() {
        _siteDialogState.update { true }
    }

    fun onButtonDialogConfirmPressed(url: String) {
        _siteDialogState.update { false }
        viewModelScope.launch {
            if (getSiteUrl.value != url) {
                dataStorePrefs.setBaseUrl(url)
                sharedPrefs.clearCookies()
            }
        }
    }

    fun onButtonDialogDismissPressed() {
        _siteDialogState.update { false }
    }

    fun onAutoUpdatePressed(isEnabled: Boolean) {
        viewModelScope.launch {
            dataStorePrefs.setAutoUpdate(isEnabled)
        }
    }
}