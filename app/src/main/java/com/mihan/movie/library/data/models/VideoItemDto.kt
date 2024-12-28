package com.mihan.movie.library.data.models

import com.mihan.movie.library.common.Constants.EMPTY_STRING
import com.mihan.movie.library.domain.models.VideoItemModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoItemDto(
    @SerialName("title") val title: String = EMPTY_STRING,
    @SerialName("category") val category: String = EMPTY_STRING,
    @SerialName("imageUrl") val imageUrl: String = EMPTY_STRING,
    @SerialName("videoUrl") val videoUrl: String = EMPTY_STRING
)


fun VideoItemDto.toVideoItemModel() = VideoItemModel(
    title = title,
    category = category,
    imageUrl = imageUrl,
    videoUrl = videoUrl
)