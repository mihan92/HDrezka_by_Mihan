package com.mihan.movie.library.domain.usecases.local_history

import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.domain.LocalVideoHistoryRepository
import com.mihan.movie.library.domain.models.VideoHistoryModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetVideoHistoryByIdUseCase @Inject constructor(private val repository: LocalVideoHistoryRepository) {

    suspend operator fun invoke(filmId: String): Flow<ApiResponse<VideoHistoryModel?>> = flow {
        emit(ApiResponse.Loading)
        when (val result = repository.getVideoHistoryById(filmId)) {
            is ApiResponse.Loading -> Unit
            is ApiResponse.Error -> emit(ApiResponse.Error(result.errorMessage))
            is ApiResponse.Success -> emit(ApiResponse.Success(result.data))
        }
    }
}