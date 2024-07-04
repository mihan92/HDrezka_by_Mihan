package com.mihan.movie.library.data.models

import com.mihan.movie.library.domain.models.NewSeriesModel

data class NewSeriesModelDto(
    val videoId: String,
    val viewDate: String,
    val title: String,
    val lastInfo: String,
    val pageUrl: String,
    val posterUrl: String,
)


fun NewSeriesModelDto.toNewSeriesModel() = NewSeriesModel(
    dataId = videoId,
    viewDate = viewDate,
    title = title,
    lastInfo = lastInfo,
    pageUrl = pageUrl,
    posterUrl = posterUrl,
)

