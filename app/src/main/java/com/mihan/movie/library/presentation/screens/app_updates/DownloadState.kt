package com.mihan.movie.library.presentation.screens.app_updates

import com.mihan.movie.library.common.Constants

data class DownloadState(
    val isDownloading: Boolean = false,
    val downloadingProgress: Float = Constants.DEFAULT_FLOAT
)
