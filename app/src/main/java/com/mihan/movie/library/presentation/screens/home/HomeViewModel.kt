package com.mihan.movie.library.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mihan.movie.library.common.DataStorePrefs
import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.common.entites.Filter
import com.mihan.movie.library.common.entites.VideoCategory
import com.mihan.movie.library.common.listeners.EventManager
import com.mihan.movie.library.common.utils.whileUiSubscribed
import com.mihan.movie.library.domain.usecases.GetListVideoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getListVideoUseCase: GetListVideoUseCase,
    private val eventManager: EventManager,
    dataStorePrefs: DataStorePrefs
) : ViewModel() {
    private val _screenState = MutableSharedFlow<HomeScreenState>(replay = 1)
    private val _filterState = MutableStateFlow(Filter.Watching)
    private val _pageState = MutableStateFlow(FIRST_PAGE)
    val screenState = _screenState.asSharedFlow()
    val filterState = _filterState.asStateFlow()
    val pageState = _pageState.asStateFlow()
    val videoCategoryState = dataStorePrefs.getVideoCategory().stateIn(
        viewModelScope,
        whileUiSubscribed,
        VideoCategory.All
    )

    suspend fun getListVideo() {
        getListVideoUseCase.invoke(_filterState.value, videoCategoryState.first(), _pageState.value)
            .onEach { result ->
                when (result) {
                    is ApiResponse.Error -> {
                        eventManager.sendEvent(result.errorMessage)
                        _screenState.emit(HomeScreenState(isLoading = false))
                    }
                    is ApiResponse.Loading -> _screenState.emit(HomeScreenState(isLoading = true))
                    is ApiResponse.Success -> _screenState.emit(HomeScreenState(data = result.data))
                }
            }.last()
    }

    fun onTopBarItemClicked(filter: Filter) {
        _pageState.value = FIRST_PAGE
        _filterState.value = filter
    }

    fun onPageChanged(page: Int) {
        _pageState.value = page
    }

    companion object {
        private const val FIRST_PAGE = 1
    }
}