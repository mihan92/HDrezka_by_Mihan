package com.mihan.movie.library.data.models

import com.mihan.movie.library.domain.models.NotificationMessage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationMessageDto(
    @SerialName("message") val message: String
)

fun NotificationMessageDto.toNotificationMessage() = NotificationMessage(
    message = message
)
