package com.mihan.movie.library.domain.models

data class NewSeriesModel(
    val dataId: String,
    val viewDate: String,
    val title: String,
    val lastInfo: String,
    val pageUrl: String,
    val posterUrl: String,
)
