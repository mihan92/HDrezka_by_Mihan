package com.mihan.movie.library.domain.usecases.gson_repository

import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.domain.GsonApiRepository
import com.mihan.movie.library.domain.models.NotificationMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CheckNotificationMessageUseCase @Inject constructor(private val repository: GsonApiRepository) {

    suspend operator fun invoke(): Flow<ApiResponse<NotificationMessage>> = flow {
        when(val result = repository.checkNotificationMessage()) {
            is ApiResponse.Loading -> Unit
            is ApiResponse.Error -> emit(ApiResponse.Error(result.errorMessage))
            is ApiResponse.Success -> emit(ApiResponse.Success(result.data))
        }
    }
}