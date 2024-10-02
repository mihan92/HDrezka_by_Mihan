package com.mihan.movie.library.data.repository

import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.data.models.toChangelogModel
import com.mihan.movie.library.data.models.toNotificationMessage
import com.mihan.movie.library.data.remote.GsonApiService
import com.mihan.movie.library.domain.GsonApiRepository
import com.mihan.movie.library.domain.models.ChangelogModel
import com.mihan.movie.library.domain.models.NotificationMessage
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ActivityRetainedScoped
class GsonApiRepositoryImpl @Inject constructor(
    private val gsonApiService: GsonApiService
) : GsonApiRepository {
    override suspend fun checkUpdates(): ChangelogModel {
        return gsonApiService.checkUpdates().toChangelogModel()
    }

    override suspend fun checkNotificationMessage(): ApiResponse<NotificationMessage> = withContext(Dispatchers.IO) {
        runCatching {
            gsonApiService.checkNotificationMessage().execute()
        }.fold(
            { response ->
                if (response.isSuccessful && response.body() != null) {
                    val message = response.body()!!.toNotificationMessage()
                    ApiResponse.Success(message)
                } else
                    ApiResponse.Error(response.errorBody()?.string() ?: "checkNotificationMessage api error")
            },
            { error -> ApiResponse.Error("checkNotificationMessage error ${error.message}") }
        )
    }
}