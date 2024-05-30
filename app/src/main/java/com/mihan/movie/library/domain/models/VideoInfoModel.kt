package com.mihan.movie.library.domain.models

data class VideoInfoModel(
    val isVideoHasTranslations: Boolean = false,
    val isVideoHasSeries: Boolean = false,
    val translations: Map<String, String> = emptyMap()
)
