package com.mihan.movie.library.domain.models


data class VideoHistoryModel(
    val videoId: String,
    val dataId: String,
    val videoPageUrl: String,
    val videoTitle: String,
    val posterUrl: String,
    val translatorName: String,
    val translatorId: String,
    val season: String,
    val episode: String,
    val watchingTime: Long
)
