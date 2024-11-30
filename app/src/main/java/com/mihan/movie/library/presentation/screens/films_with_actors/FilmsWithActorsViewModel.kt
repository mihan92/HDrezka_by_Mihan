package com.mihan.movie.library.presentation.screens.films_with_actors

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.common.utils.EventManager
import com.mihan.movie.library.domain.usecases.parser.GetListVideoByActorIdUseCase
import com.ramcosta.composedestinations.generated.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilmsWithActorsViewModel @Inject constructor(
    private val getListVideoByActorIdUseCase: GetListVideoByActorIdUseCase,
    private val eventManager: EventManager,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val navArgs: FilmsWithActorsNavArgs = savedStateHandle.navArgs()
    private val _screenState = MutableStateFlow(FilmsWithActorsScreenState())

    val actorName = navArgs.actorModel.actorName
    val filmsWithActorsScreenState = _screenState.asStateFlow()

    init {
        viewModelScope.launch {
            getListVideoByActorIdUseCase(navArgs.actorModel.actorId).onEach { result ->
                when(result) {
                    is ApiResponse.Error -> {
                        _screenState.update { it.copy(isLoading = false) }
                        eventManager.sendEvent(result.errorMessage)
                    }
                    is ApiResponse.Loading -> _screenState.update { it.copy(isLoading = true) }
                    is ApiResponse.Success -> _screenState.update { it.copy(isLoading = false, data = result.data) }
                }
            }.last()
        }
    }
}