package com.mihan.movie.library.presentation.screens.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mihan.movie.library.common.Constants
import com.mihan.movie.library.common.DataStorePrefs
import com.mihan.movie.library.common.utils.whileUiSubscribed
import com.mihan.movie.library.domain.models.FavouritesModel
import com.mihan.movie.library.domain.usecases.favourites.DeleteFromFavouritesUseCase
import com.mihan.movie.library.domain.usecases.favourites.GetFavoritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val getFavouritesUseCase: GetFavoritesUseCase,
    private val deleteFromFavouritesUseCase: DeleteFromFavouritesUseCase,
    private val dataStorePrefs: DataStorePrefs
): ViewModel() {

    private val _favouritesList = MutableStateFlow<List<FavouritesModel>>(emptyList())

    val favouritesList = _favouritesList.asStateFlow()
    val baseUrl = dataStorePrefs.getBaseUrl().stateIn(viewModelScope, whileUiSubscribed, Constants.EMPTY_STRING)

    init {
        getFavourites()
    }

    fun onButtonDeletePressed(videoId: String) {
        viewModelScope.launch {
            deleteFromFavouritesUseCase(videoId)
        }
    }

    private fun getFavourites() {
        getFavouritesUseCase().onEach { list ->
            _favouritesList.update { list.reversed() }
        }.launchIn(viewModelScope)
    }
}