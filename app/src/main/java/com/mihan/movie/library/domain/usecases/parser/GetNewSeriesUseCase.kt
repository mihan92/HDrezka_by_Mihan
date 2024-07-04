package com.mihan.movie.library.domain.usecases.parser

import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.domain.ParserRepository
import com.mihan.movie.library.domain.models.NewSeriesModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetNewSeriesUseCase @Inject constructor(private val parserRepository: ParserRepository) {

    suspend operator fun invoke(): Flow<ApiResponse<List<NewSeriesModel>>> = flow {
        emit(ApiResponse.Loading)
        when (val result = parserRepository.getNewSeriesList()) {
            is ApiResponse.Loading -> Unit
            is ApiResponse.Success -> emit(ApiResponse.Success(result.data))
            is ApiResponse.Error -> emit(ApiResponse.Error(result.errorMessage))
        }
    }
}