package com.mihan.movie.library.data.models

import com.mihan.movie.library.common.Constants
import com.mihan.movie.library.domain.models.VideoHistoryModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoHistoryModelDto(
    @SerialName("videoId") val videoId: String,
    @SerialName("dataId") val dataId: String,
    @SerialName("pageUrl") val pageUrl: String,
    @SerialName("videoTitle") val videoTitle: String,
    @SerialName("posterUrl") val posterUrl: String,
    @SerialName("translatorName") val translatorName: String,
    @SerialName("season") val season: String,
    @SerialName("episode") val episode: String,
)

fun VideoHistoryModelDto.toVideoHistoryModel() = VideoHistoryModel(
    videoId = videoId,
    dataId = dataId,
    videoPageUrl = pageUrl,
    videoTitle = videoTitle,
    posterUrl = posterUrl,
    translatorName = translatorName,
    translatorId = Constants.EMPTY_STRING,
    season = season,
    episode = episode,
    watchingTime = 0L,
)
