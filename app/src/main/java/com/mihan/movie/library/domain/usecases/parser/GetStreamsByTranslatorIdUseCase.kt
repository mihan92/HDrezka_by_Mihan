package com.mihan.movie.library.domain.usecases.parser

import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.domain.ParserRepository
import com.mihan.movie.library.domain.models.StreamModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetStreamsByTranslatorIdUseCase @Inject constructor(private val parserRepository: ParserRepository) {
    suspend operator fun invoke(
        translatorId: String,
        filmId: String
    ): Flow<ApiResponse<StreamModel>> = flow {
        emit(ApiResponse.Loading)
        when(val result = parserRepository.getStreamByTranslatorId(translatorId, filmId)) {
            is ApiResponse.Loading -> Unit
            is ApiResponse.Error -> emit(ApiResponse.Error(result.errorMessage))
            is ApiResponse.Success -> emit(ApiResponse.Success(result.data))
        }
    }
}