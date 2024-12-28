package com.mihan.movie.library.data.models

import com.mihan.movie.library.common.Constants
import com.mihan.movie.library.domain.models.UserInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfoDto(
    @SerialName("userEmail") val userEmail: String = Constants.EMPTY_STRING
)

fun UserInfoDto.toUserInfo() = UserInfo(
    userEmail = userEmail
)
