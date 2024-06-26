package com.mihan.movie.library.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mihan.movie.library.common.Constants
import com.mihan.movie.library.domain.models.VideoHistoryModel

@Entity(tableName = "video_history")
data class VideoHistoryDbModel(
    @PrimaryKey @ColumnInfo(name = "video_id") val videoId: String,
    @ColumnInfo(name = "video_page_url") val videoPageUrl: String,
    @ColumnInfo(name = "video_title") val videoTitle: String,
    @ColumnInfo(name = "poster_url") val posterUrl: String,
    @ColumnInfo(name = "translator_name") val translatorName: String,
    @ColumnInfo(name = "translator_id") val translatorId: String,
    @ColumnInfo(name = "season") val season: String,
    @ColumnInfo(name = "episode") val episode: String,
    @ColumnInfo(name = "watching_time") val watchingTime: Long
)

fun VideoHistoryDbModel.toVideoHistoryModel() = VideoHistoryModel(
    videoId = videoId,
    dataId = Constants.EMPTY_STRING,
    videoPageUrl = videoPageUrl,
    videoTitle = videoTitle,
    posterUrl = posterUrl,
    translatorName = translatorName,
    translatorId = translatorId,
    season = season,
    episode = episode,
    watchingTime = watchingTime
)

fun VideoHistoryModel.toVideoHistoryDbModel() = VideoHistoryDbModel(
    videoId = videoId,
    videoPageUrl = videoPageUrl,
    videoTitle = videoTitle,
    posterUrl = posterUrl,
    translatorName = translatorName,
    translatorId = translatorId,
    season = season,
    episode = episode,
    watchingTime = watchingTime
)