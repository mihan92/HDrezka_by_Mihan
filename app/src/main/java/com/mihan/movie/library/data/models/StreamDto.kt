package com.mihan.movie.library.data.models

import com.mihan.movie.library.domain.models.StreamModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StreamDto(
    @SerialName("url") val url: String,
    @SerialName("quality") val quality: String
)

fun StreamDto.toStreamModel() = StreamModel(
    url = url,
    quality = quality
)
