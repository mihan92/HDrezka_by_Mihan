package com.mihan.movie.library.data.models

import com.mihan.movie.library.common.Constants.EMPTY_STRING
import com.mihan.movie.library.domain.models.VideoDetailModel

data class VideoDetailDto(
    val filmId: String = EMPTY_STRING,
    val title: String = EMPTY_STRING,
    val description: String = EMPTY_STRING,
    val releaseDate: String = EMPTY_STRING,
    val country: String = EMPTY_STRING,
    val ratingIMDb: String = EMPTY_STRING,
    val ratingKp: String = EMPTY_STRING,
    val ratingHdrezka: String = EMPTY_STRING,
    val genre: String = EMPTY_STRING,
    val actors: String = EMPTY_STRING,
    val imageUrl: String = EMPTY_STRING,
    val errorMessage: String = EMPTY_STRING
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
    errorMessage = errorMessage
)
