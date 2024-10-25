package com.mihan.movie.library.data.models

import com.mihan.movie.library.domain.models.NotificationMessage

data class NotificationMessageDto(
    val message: String
)

fun NotificationMessageDto.toNotificationMessage() = NotificationMessage(
    message = message
)
