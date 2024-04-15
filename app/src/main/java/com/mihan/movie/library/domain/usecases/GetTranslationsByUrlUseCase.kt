package com.mihan.movie.library.domain.usecases

import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.data.models.toVideoModel
import com.mihan.movie.library.domain.ParserRepository
import com.mihan.movie.library.domain.models.VideoModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetTranslationsByUrlUseCase @Inject constructor(private val parserRepository: ParserRepository) {
    suspend operator fun invoke(filmUrl: String): Flow<ApiResponse<VideoModel>> = flow {
        emit(ApiResponse.Loading)
        when (val result = parserRepository.getTranslationsByUrl(filmUrl)) {
            is ApiResponse.Loading -> Unit
            is ApiResponse.Error -> emit(ApiResponse.Error(result.errorMessage))
            is ApiResponse.Success -> emit(ApiResponse.Success(result.data.toVideoModel()))
        }
    }
}