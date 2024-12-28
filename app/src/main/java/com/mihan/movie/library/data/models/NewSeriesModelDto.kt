package com.mihan.movie.library.data.models

import com.mihan.movie.library.domain.models.NewSeriesModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewSeriesModelDto(
    @SerialName("videoId") val videoId: String,
    @SerialName("viewDate") val viewDate: String,
    @SerialName("title") val title: String,
    @SerialName("lastInfo") val lastInfo: String,
    @SerialName("pageUrl") val pageUrl: String,
    @SerialName("posterUrl") val posterUrl: String,
)


fun NewSeriesModelDto.toNewSeriesModel() = NewSeriesModel(
    dataId = videoId,
    viewDate = viewDate,
    title = title,
    lastInfo = lastInfo,
    pageUrl = pageUrl,
    posterUrl = posterUrl,
)

