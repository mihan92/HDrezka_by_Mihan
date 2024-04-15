package com.mihan.movie.library.presentation.screens.search

import com.mihan.movie.library.common.Constants
import com.mihan.movie.library.domain.models.VideoItemModel

data class SearchScreenState(
    val isLoading: Boolean = false,
    val listOfVideo: List<VideoItemModel>? = null,
)
