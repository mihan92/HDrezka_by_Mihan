package com.mihan.movie.library.presentation.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.common.Constants
import com.mihan.movie.library.common.DataStorePrefs
import com.mihan.movie.library.common.utils.EventManager
import com.mihan.movie.library.common.utils.whileUiSubscribed
import com.mihan.movie.library.domain.models.VideoHistoryModel
import com.mihan.movie.library.domain.usecases.local_history.DeleteVideoHistoryItemUseCase
import com.mihan.movie.library.domain.usecases.local_history.GetVideoHistoryListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getVideoHistoryListUseCase: GetVideoHistoryListUseCase,
    private val deleteVideoHistoryItemUseCase: DeleteVideoHistoryItemUseCase,
    private val eventManager: EventManager,
    dataStorePrefs: DataStorePrefs
) : ViewModel() {
    private val _historyScreenState = MutableStateFlow(HistoryScreenState())
    val historyScreenState = _historyScreenState.asStateFlow()
    val baseUrl = dataStorePrefs.getBaseUrl().stateIn(viewModelScope, whileUiSubscribed, Constants.EMPTY_STRING)

    suspend fun getHistoryList() {
        getVideoHistoryListUseCase().collectLatest { result ->
            when(result) {
                is ApiResponse.Error -> {
                    _historyScreenState.update { it.copy(isLoading = false) }
                    eventManager.sendEvent(result.errorMessage)
                }
                is ApiResponse.Loading -> _historyScreenState.update { it.copy(isLoading = true) }
                is ApiResponse.Success -> {
                    _historyScreenState.update { it.copy(isLoading = false, data = result.data) }
                }
            }
        }
    }

    fun onButtonDeleteClicked(model: VideoHistoryModel) {
        viewModelScope.launch {
            deleteVideoHistoryItemUseCase(model)
            getHistoryList()
        }
    }
}