package com.mihan.movie.library.domain.usecases

import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.data.models.toSeasonModel
import com.mihan.movie.library.domain.ParserRepository
import com.mihan.movie.library.domain.models.SeasonModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetSeasonsByTranslatorIdUseCase @Inject constructor(private val parserRepository: ParserRepository) {

    suspend operator fun invoke(translatorId: String): Flow<ApiResponse<List<SeasonModel>>> = flow {
        emit(ApiResponse.Loading)
        when(val result = parserRepository.getSeasonsByTranslatorId(translatorId)) {
            is ApiResponse.Loading -> Unit
            is ApiResponse.Error -> emit(ApiResponse.Error(result.errorMessage))
            is ApiResponse.Success -> emit(ApiResponse.Success(result.data.map { it.toSeasonModel() }))
        }
    }
}