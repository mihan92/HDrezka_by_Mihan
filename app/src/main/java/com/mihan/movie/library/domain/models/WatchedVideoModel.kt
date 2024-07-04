package com.mihan.movie.library.domain.models

data class WatchedVideoModel(
    val dataId: String,
    val translatorId: String,
    val season: String,
    val episode: String,
)
