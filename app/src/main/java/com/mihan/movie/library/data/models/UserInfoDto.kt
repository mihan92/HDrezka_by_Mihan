package com.mihan.movie.library.data.models

import com.mihan.movie.library.common.Constants
import com.mihan.movie.library.domain.models.UserInfo

data class UserInfoDto(
    val userEmail: String = Constants.EMPTY_STRING
)

fun UserInfoDto.toUserInfo() = UserInfo(
    userEmail = userEmail
)
