package com.mihan.movie.library.domain.usecases

import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.data.models.toChangelogModel
import com.mihan.movie.library.domain.AppUpdateRepository
import com.mihan.movie.library.domain.models.ChangelogModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CheckUpdatesUseCase @Inject constructor(private val repository: AppUpdateRepository) {
    suspend operator fun invoke(): Flow<ApiResponse<ChangelogModel>> = flow {
        try {
            emit(ApiResponse.Loading)
            val updates = repository.checkUpdates().toChangelogModel()
            emit(ApiResponse.Success(updates))
        } catch (e: Exception) {
            emit(ApiResponse.Error(e.message ?: "CheckUpdatesUseCase error"))
        }
    }
}