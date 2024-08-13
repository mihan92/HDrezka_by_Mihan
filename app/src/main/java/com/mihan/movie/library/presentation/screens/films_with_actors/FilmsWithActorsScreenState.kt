package com.mihan.movie.library.presentation.screens.films_with_actors

import com.mihan.movie.library.domain.models.VideoItemModel

data class FilmsWithActorsScreenState(
    val isLoading: Boolean = false,
    val data: List<VideoItemModel> = emptyList()
)
