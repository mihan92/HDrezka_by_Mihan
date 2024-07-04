package com.mihan.movie.library.presentation.screens.new_series

import com.mihan.movie.library.domain.models.NewSeriesModel

data class NewSeriesScreenState(
    val isLoading: Boolean = false,
    val data: List<NewSeriesModel> = emptyList()
)
