package com.mihan.movie.library.presentation.screens.home

import com.mihan.movie.library.domain.models.VideoItemModel

data class HomeScreenState(
    val isLoading: Boolean = false,
    val data: List<VideoItemModel> = emptyList()
)
