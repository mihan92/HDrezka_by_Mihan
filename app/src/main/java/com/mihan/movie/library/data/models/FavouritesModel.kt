package com.mihan.movie.library.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mihan.movie.library.domain.models.FavouritesModel

@Entity(tableName = "video_favourites")
data class FavouritesDbModel(
    @PrimaryKey @ColumnInfo(name = "video_id") val videoId: String,
    @ColumnInfo(name = "video_page_url") val videoPageUrl: String,
    @ColumnInfo(name = "video_title") val videoTitle: String,
    @ColumnInfo(name = "poster_url") val posterUrl: String,
)

fun FavouritesDbModel.toFavouritesModel() = FavouritesModel(
    videoId = videoId,
    videoPageUrl = videoPageUrl,
    videoTitle = videoTitle,
    posterUrl = posterUrl
)
