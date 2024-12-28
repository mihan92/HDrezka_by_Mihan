package com.mihan.movie.library.data.models

import com.mihan.movie.library.common.Constants
import com.mihan.movie.library.domain.models.BaseUrlModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseUrlModelDto(
    @SerialName("baseUrl") val baseUrl: String = Constants.EMPTY_STRING
)

fun BaseUrlModelDto.toBaseUrlModel() = BaseUrlModel(baseUrl)
