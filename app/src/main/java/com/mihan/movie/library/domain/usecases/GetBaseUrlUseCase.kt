package com.mihan.movie.library.domain.usecases

import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.domain.ParserRepository
import com.mihan.movie.library.domain.models.BaseUrlModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetBaseUrlUseCase @Inject constructor(private val parserRepository: ParserRepository) {

    suspend operator fun invoke(): Flow<ApiResponse<BaseUrlModel>> = flow {
        when(val result = parserRepository.getBaseUrl()) {
            is ApiResponse.Loading -> Unit
            is ApiResponse.Error -> emit(ApiResponse.Error(result.errorMessage))
            is ApiResponse.Success -> emit(ApiResponse.Success(result.data))
        }
    }
}