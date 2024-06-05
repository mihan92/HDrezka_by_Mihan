package com.mihan.movie.library.domain.usecases

import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.domain.ParserRepository
import com.mihan.movie.library.domain.models.VideoItemModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetVideosByTitleUseCase @Inject constructor(private val parserRepository: ParserRepository) {

    suspend operator fun invoke(videoTitle: String): Flow<ApiResponse<List<VideoItemModel>>> = flow {
            emit(ApiResponse.Loading)
            when(val result = parserRepository.getVideosByTitle(videoTitle)) {
                is ApiResponse.Loading -> Unit
                is ApiResponse.Error -> emit(ApiResponse.Error(result.errorMessage))
                is ApiResponse.Success -> emit(ApiResponse.Success(result.data))
            }
    }
}