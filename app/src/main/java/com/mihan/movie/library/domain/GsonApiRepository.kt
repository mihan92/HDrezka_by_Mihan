package com.mihan.movie.library.domain

import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.domain.models.ChangelogModel
import com.mihan.movie.library.domain.models.NotificationMessage

interface GsonApiRepository {
    suspend fun checkUpdates(): ChangelogModel

    suspend fun checkNotificationMessage(): ApiResponse<NotificationMessage>
}