package com.mihan.movie.library.data.models

import com.mihan.movie.library.common.Constants
import com.mihan.movie.library.domain.models.VideoHistoryModel

data class VideoHistoryModelDto(
    val videoId: String,
    val dataId: String,
    val pageUrl: String,
    val videoTitle: String,
    val posterUrl: String,
    val translatorName: String,
    val season: String,
    val episode: String,
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
