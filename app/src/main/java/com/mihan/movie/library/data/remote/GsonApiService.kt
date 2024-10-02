package com.mihan.movie.library.data.remote

import com.mihan.movie.library.data.models.ChangelogDto
import com.mihan.movie.library.data.models.NotificationMessageDto
import retrofit2.Call
import retrofit2.http.GET

interface GsonApiService {
    @GET("/v/movielibrary")
    suspend fun checkUpdates(): ChangelogDto

    @GET("/v/movielibrary_notification")
    fun checkNotificationMessage(): Call<NotificationMessageDto>
}