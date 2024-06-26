package com.mihan.movie.library.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.common.utils.EventManager
import com.mihan.movie.library.common.utils.VoiceRecognizer
import com.mihan.movie.library.domain.usecases.parser.GetVideosByTitleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val voiceRecognizer: VoiceRecognizer,
    private val getVideosByTitleUseCase: GetVideosByTitleUseCase,
    private val eventManager: EventManager
) : ViewModel() {
    private val _screenState = MutableStateFlow(SearchScreenState())

    val screenState = _screenState.asStateFlow()
    val voiceState = voiceRecognizer.voiceRecognizerState

    fun startListening() {
        voiceRecognizer.startListening("ru")
    }

    fun stopListening() {
        voiceRecognizer.stopListening()
    }

    fun buttonSearchPressed(searchingText: String) {
        if (searchingText.isNotEmpty()) {
            viewModelScope.launch {
                getVideosByTitleUseCase(searchingText)
                    .onEach { result ->
                        when (result) {
                            is ApiResponse.Error -> {
                                _screenState.update { it.copy(isLoading = false) }
                                eventManager.sendEvent(result.errorMessage)
                            }
                            is ApiResponse.Loading -> _screenState.update { it.copy(isLoading = true) }
                            is ApiResponse.Success ->
                                _screenState.update { it.copy(isLoading = false, listOfVideo = result.data) }
                        }
                    }.last()
                voiceRecognizer.resetState()
            }
        }
    }
}