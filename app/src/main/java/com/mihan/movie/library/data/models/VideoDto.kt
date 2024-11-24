package com.mihan.movie.library.data.models

import com.mihan.movie.library.domain.models.VideoInfoModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoDto(
    @SerialName("isVideoHasTranslations") val isVideoHasTranslations: Boolean = false,
    @SerialName("isVideoHasSeries") val isVideoHasSeries: Boolean = false,
    @SerialName("translations") val translations: Map<String, String> = emptyMap()
)

fun VideoDto.toVideoInfoModel() = VideoInfoModel(
    isVideoHasTranslations = isVideoHasTranslations,
    isVideoHasSeries = isVideoHasSeries,
    translations = translations
)