package com.mihan.movie.library.domain.models

import com.mihan.movie.library.common.Constants

data class SerialModel(
    val season: String = Constants.EMPTY_STRING,
    val episodes: List<String> = emptyList()
)
