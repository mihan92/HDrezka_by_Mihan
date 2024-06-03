package com.mihan.movie.library.domain.usecases

import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.domain.ParserRepository
import com.mihan.movie.library.domain.models.VideoDetailModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetDetailVideoByUrlUseCase @Inject constructor(private val parserRepository: ParserRepository) {
    suspend operator fun invoke(url: String): Flow<ApiResponse<VideoDetailModel>> = flow {
        emit(ApiResponse.Loading)
        when (val result = parserRepository.getDetailVideoByUrl(url)) {
            is ApiResponse.Loading -> Unit
            is ApiResponse.Success -> emit(ApiResponse.Success(result.data))
            is ApiResponse.Error -> emit(ApiResponse.Error(result.errorMessage))
        }
    }
}