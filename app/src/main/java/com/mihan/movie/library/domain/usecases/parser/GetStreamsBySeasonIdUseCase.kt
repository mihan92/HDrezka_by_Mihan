package com.mihan.movie.library.domain.usecases.parser

import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.domain.ParserRepository
import com.mihan.movie.library.domain.models.StreamModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetStreamsBySeasonIdUseCase @Inject constructor(private val parserRepository: ParserRepository) {
    suspend operator fun invoke(
        translationId: String,
        filmId: String,
        season: String,
        episode: String
    ): Flow<ApiResponse<StreamModel>> = flow {
        emit(ApiResponse.Loading)
        when(val result = parserRepository.getStreamBySeasonId(translationId, filmId, season, episode)) {
            is ApiResponse.Loading -> Unit
            is ApiResponse.Error -> emit(ApiResponse.Error(result.errorMessage))
            is ApiResponse.Success -> emit(ApiResponse.Success(result.data))
        }
    }
}