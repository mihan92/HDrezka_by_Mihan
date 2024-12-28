package com.mihan.movie.library.data.models

import com.mihan.movie.library.common.Constants
import com.mihan.movie.library.domain.models.Episode
import com.mihan.movie.library.domain.models.SerialModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SerialModelDto(
    @SerialName("season") val season: String = Constants.EMPTY_STRING,
    @SerialName("episodes") val episodes: List<String> = emptyList()
)

fun SerialModelDto.toSerialModel() = SerialModel(
    season = season,
    episodes = episodes.map { Episode(title = it) }
)
