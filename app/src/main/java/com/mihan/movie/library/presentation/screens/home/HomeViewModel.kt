package com.mihan.movie.library.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.common.DataStorePrefs
import com.mihan.movie.library.common.extentions.logger
import com.mihan.movie.library.common.models.CategoryFilter
import com.mihan.movie.library.common.models.GenreFilter
import com.mihan.movie.library.common.models.MovieCollections
import com.mihan.movie.library.common.models.MoviePeriod
import com.mihan.movie.library.common.models.VideoCategory
import com.mihan.movie.library.common.utils.EventManager
import com.mihan.movie.library.common.utils.whileUiSubscribed
import com.mihan.movie.library.domain.usecases.parser.GetCollectionsListVideoUseCase
import com.mihan.movie.library.domain.usecases.parser.GetListFilteredVideoUseCase
import com.mihan.movie.library.domain.usecases.parser.GetListVideoUseCase
import com.mihan.movie.library.presentation.ui.view.TopBarItems
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getListVideoUseCase: GetListVideoUseCase,
    private val getListFilteredVideoUseCase: GetListFilteredVideoUseCase,
    private val getCollectionsListVideoUseCase: GetCollectionsListVideoUseCase,
    private val eventManager: EventManager,
    dataStorePrefs: DataStorePrefs
) : ViewModel() {
    private val _screenState = MutableSharedFlow<HomeScreenState>(replay = 1)
    private val _topBarState = MutableStateFlow(TopBarItems.Watching)
    private val _currentDefaultListPage = MutableStateFlow(FIRST_PAGE)
    private val _currentFilteredListPage = MutableStateFlow(FIRST_PAGE)
    private val _categoryFilter = MutableStateFlow(CategoryFilter.FILMS)
    private val _genreFilter = MutableStateFlow(GenreFilter.ANY_GENRE)
    private val _moviePeriod = MutableStateFlow(MoviePeriod.ALL_TIME)
    private val _isMovieCollectionSelected = MutableStateFlow(false)
    private val _movieCollection = MutableStateFlow(MovieCollections.NETFLIX)

    val screenState = _screenState.asSharedFlow()
    val topBarState = _topBarState.asStateFlow()
    val currentDefaultListPage = _currentDefaultListPage.asStateFlow()
    val currentFilteredListPage = _currentFilteredListPage.asStateFlow()
    val categoryFilter = _categoryFilter.asStateFlow()
    val genreFilter = _genreFilter.asStateFlow()
    val moviePeriod = _moviePeriod.asStateFlow()
    val videoCategoryState = dataStorePrefs.getVideoCategory().stateIn(
        viewModelScope,
        whileUiSubscribed,
        VideoCategory.All
    )

    suspend fun getListVideo() {
        getListVideoUseCase.invoke(_topBarState.value, videoCategoryState.first(), _currentDefaultListPage.value)
            .collect { result ->
                when (result) {
                    is ApiResponse.Error -> {
                        eventManager.sendEvent(result.errorMessage)
                        _screenState.emit(HomeScreenState(isLoading = false))
                    }

                    is ApiResponse.Loading -> {
                        _screenState.emit(HomeScreenState(isLoading = true))
                    }

                    is ApiResponse.Success -> {
                        _screenState.emit(HomeScreenState(data = result.data))
                    }
                }
            }
    }

    fun onFilterDialogButtonSearchPressed(
        category: CategoryFilter,
        genre: GenreFilter,
        moviePeriod: MoviePeriod,
        page: Int
    ) {
        _isMovieCollectionSelected.update { false }
        _categoryFilter.update { category }
        _genreFilter.update { genre }
        _moviePeriod.update { moviePeriod }
        _currentFilteredListPage.update { page }
        getListFilteredVideo()
    }

    fun onFilterDialogCollectionItemPressed(category: CategoryFilter, movieCollection: MovieCollections, page: Int) {
        _isMovieCollectionSelected.update { true }
        _categoryFilter.update { category }
        _movieCollection.update { movieCollection }
        _currentFilteredListPage.update { page }
        getCollectionsListVideo()
    }

    fun onTopBarItemClicked(topBarItems: TopBarItems) {
        _currentDefaultListPage.value = FIRST_PAGE
        _currentFilteredListPage.value = FIRST_PAGE
        _topBarState.value = topBarItems
    }

    fun onPageChanged(page: Int) {
        if (_isMovieCollectionSelected.value) {
            logger("onPageChanged collections $page")
            _currentFilteredListPage.value = page
            getCollectionsListVideo()
        }else if (_topBarState.value == TopBarItems.Filter) {
            logger("onPageChanged filtered $page")
            _currentFilteredListPage.value = page
            getListFilteredVideo()
        } else
            _currentDefaultListPage.value = page
    }

    private fun getListFilteredVideo() {
        viewModelScope.launch {
            getListFilteredVideoUseCase.invoke(
                _categoryFilter.value,
                _genreFilter.value,
                _moviePeriod.value,
                _currentFilteredListPage.value
            )
                .collect { result ->
                    when (result) {
                        is ApiResponse.Error -> {
                            eventManager.sendEvent(result.errorMessage)
                            _screenState.emit(HomeScreenState(isLoading = false))
                        }

                        is ApiResponse.Loading -> {
                            _screenState.emit(HomeScreenState(isLoading = true))
                        }

                        is ApiResponse.Success -> {
                            _screenState.emit(HomeScreenState(data = result.data))
                        }
                    }
                }
        }
    }

    private fun getCollectionsListVideo() {
        viewModelScope.launch {
            getCollectionsListVideoUseCase.invoke(
                _categoryFilter.value,
                _movieCollection.value,
                _currentFilteredListPage.value
            ).collect { result ->
                when (result) {
                    is ApiResponse.Error -> {
                        eventManager.sendEvent(result.errorMessage)
                        _screenState.emit(HomeScreenState(isLoading = false))
                    }

                    is ApiResponse.Loading -> {
                        _screenState.emit(HomeScreenState(isLoading = true))
                    }

                    is ApiResponse.Success -> {
                        _screenState.emit(HomeScreenState(data = result.data))
                    }
                }
            }
        }
    }

    companion object {
        private const val FIRST_PAGE = 1
    }
}