package com.mihan.movie.library.presentation.screens.new_series

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.common.Constants
import com.mihan.movie.library.common.DataStorePrefs
import com.mihan.movie.library.common.utils.EventManager
import com.mihan.movie.library.common.utils.whileUiSubscribed
import com.mihan.movie.library.domain.usecases.auth.DeleteWatchedVideoUseCase
import com.mihan.movie.library.domain.usecases.parser.GetNewSeriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewSeriesViewModel @Inject constructor(
    private val getNewSeriesUseCase: GetNewSeriesUseCase,
    private val deleteWatchedVideoUseCase: DeleteWatchedVideoUseCase,
    private val eventManager: EventManager,
    private val dataStorePrefs: DataStorePrefs
) : ViewModel() {

    private val _screenState = MutableStateFlow(NewSeriesScreenState())
    val screenState = _screenState.asStateFlow()
    val baseUrl = dataStorePrefs.getBaseUrl().stateIn(viewModelScope, whileUiSubscribed, Constants.EMPTY_STRING)

    suspend fun getNewSeries() {
        getNewSeriesUseCase().onEach { result ->
            when (result) {
                is ApiResponse.Loading -> _screenState.update { state -> state.copy(isLoading = true) }
                is ApiResponse.Error -> {
                    _screenState.update { state -> state.copy(isLoading = false) }
                    eventManager.sendEvent(result.errorMessage)
                }

                is ApiResponse.Success -> _screenState.update {
                    if (result.data.isEmpty()) {
                        dataStorePrefs.updateNewSeriesStatus(false)
                    }
                    NewSeriesScreenState(data = result.data)
                }
            }
        }.last()
    }

    fun onButtonDeleteClicked(dataId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteWatchedVideoUseCase(dataId)
            getNewSeries()
        }
    }
}