package com.mihan.movie.library.data.models

import com.mihan.movie.library.domain.models.VideoInfoModel

data class VideoDto(
    val isVideoHasTranslations: Boolean = false,
    val isVideoHasSeries: Boolean = false,
    val translations: Map<String, String> = emptyMap()
)

fun VideoDto.toVideoInfoModel() = VideoInfoModel(
    isVideoHasTranslations = isVideoHasTranslations,
    isVideoHasSeries = isVideoHasSeries,
    translations = translations
)