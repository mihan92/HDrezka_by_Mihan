package com.mihan.movie.library.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String? = null
)
