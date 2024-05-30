package com.mihan.movie.library.data.models

import com.mihan.movie.library.common.Constants
import com.mihan.movie.library.domain.models.SerialModel

data class SeasonModelDto(
    val season: String = Constants.EMPTY_STRING,
    val episodes: List<String> = emptyList()
)

fun SeasonModelDto.toSeasonModel() = SerialModel(
    season = season,
    episodes = episodes
)
