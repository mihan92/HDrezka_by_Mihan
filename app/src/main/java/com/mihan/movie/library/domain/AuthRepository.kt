package com.mihan.movie.library.domain

import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.domain.models.WatchedVideoModel

interface AuthRepository {

    suspend fun login(login: String, password: String): ApiResponse<Boolean>

    suspend fun logout()

    suspend fun sendWatchedVideo(watchedVideoModel: WatchedVideoModel)

    suspend fun deleteWatchedVideo(dataId: String)

}