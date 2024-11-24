package com.mihan.movie.library.data.models

import com.mihan.movie.library.common.Constants.EMPTY_STRING
import com.mihan.movie.library.domain.models.VideoDetailModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoDetailDto(
    @SerialName("filmId") val filmId: String = EMPTY_STRING,
    @SerialName("title") val title: String = EMPTY_STRING,
    @SerialName("description") val description: String = EMPTY_STRING,
    @SerialName("releaseDate") val releaseDate: String = EMPTY_STRING,
    @SerialName("country") val country: String = EMPTY_STRING,
    @SerialName("ratingIMDb") val ratingIMDb: String = EMPTY_STRING,
    @SerialName("ratingKp") val ratingKp: String = EMPTY_STRING,
    @SerialName("ratingHdrezka") val ratingHdrezka: String = EMPTY_STRING,
    @SerialName("genre") val genre: String = EMPTY_STRING,
    @SerialName("actors") val actors: Map<String, String> = emptyMap(),
    @SerialName("imageUrl") val imageUrl: String = EMPTY_STRING,
)

fun VideoDetailDto.toVideoDetail() = VideoDetailModel(
    filmId = filmId,
    title = title,
    description = description,
    releaseDate = releaseDate,
    country = country,
    ratingIMDb = ratingIMDb,
    ratingKp = ratingKp,
    ratingRezka = ratingHdrezka,
    genre = genre,
    actors = actors,
    imageUrl = imageUrl,
)
