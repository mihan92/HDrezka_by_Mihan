package com.mihan.movie.library.domain.models

data class VideoDetailModel(
    val filmId: String,
    val title: String,
    val description: String,
    val releaseDate: String,
    val country: String,
    val ratingIMDb: String,
    val ratingKp: String,
    val ratingRezka: String,
    val genre: String,
    val actors: String,
    val imageUrl: String,
)
