package com.mihan.movie.library.presentation.screens.detail

import com.mihan.movie.library.domain.models.VideoDetailModel

data class DetailScreenState(
    val isLoading: Boolean = false,
    val detailInfo: VideoDetailModel? = null,
)
