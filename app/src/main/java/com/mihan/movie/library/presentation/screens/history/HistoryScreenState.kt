package com.mihan.movie.library.presentation.screens.history

import com.mihan.movie.library.domain.models.VideoHistoryModel

data class HistoryScreenState(
    val isLoading: Boolean = false,
    val data: List<VideoHistoryModel> = emptyList()
)
