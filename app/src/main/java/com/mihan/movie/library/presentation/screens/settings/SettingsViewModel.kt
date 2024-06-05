package com.mihan.movie.library.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mihan.movie.library.common.Constants
import com.mihan.movie.library.common.DataStorePrefs
import com.mihan.movie.library.common.models.Colors
import com.mihan.movie.library.common.models.VideoCategory
import com.mihan.movie.library.common.models.VideoQuality
import com.mihan.movie.library.common.utils.whileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStorePrefs: DataStorePrefs
) : ViewModel() {

    private val _siteDialogState = MutableStateFlow(false)
    val siteDialogState = _siteDialogState.asStateFlow()

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

    val remoteParsing = dataStorePrefs.getRemoteParsing().stateIn(
        viewModelScope,
        whileUiSubscribed,
        false
    )

    val autoUpdate = dataStorePrefs.getAutoUpdate().stateIn(
        viewModelScope,
        whileUiSubscribed,
        true
    )

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
            }
        }
    }

    fun onButtonDialogDismissPressed() {
        _siteDialogState.update { false }
    }

    fun onSwitchPressed(isSelected: Boolean) {
        viewModelScope.launch {
            dataStorePrefs.setRemoteParsing(isSelected)
        }
    }

    fun onAutoUpdatePressed(isEnabled: Boolean) {
        viewModelScope.launch {
            dataStorePrefs.setAutoUpdate(isEnabled)
        }
    }
}