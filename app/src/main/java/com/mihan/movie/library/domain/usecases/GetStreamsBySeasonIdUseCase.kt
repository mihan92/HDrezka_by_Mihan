package com.mihan.movie.library.domain.usecases

import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.data.models.toStreamModel
import com.mihan.movie.library.domain.ParserRepository
import com.mihan.movie.library.domain.models.StreamModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetStreamsBySeasonIdUseCase @Inject constructor(private val parserRepository: ParserRepository) {
    suspend operator fun invoke(
        translationId: String,
        videoId: String,
        season: String,
        episode: String
    ): Flow<ApiResponse<List<StreamModel>>> = flow {
        emit(ApiResponse.Loading)
        when(val result = parserRepository.getStreamsBySeasonId(translationId, videoId, season, episode)) {
            is ApiResponse.Loading -> Unit
            is ApiResponse.Error -> emit(ApiResponse.Error(result.errorMessage))
            is ApiResponse.Success -> emit(ApiResponse.Success(result.data.map { it.toStreamModel() }))
        }
    }
}