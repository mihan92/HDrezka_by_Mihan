package com.mihan.movie.library.domain.usecases

import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.common.entites.Filter
import com.mihan.movie.library.common.entites.VideoCategory
import com.mihan.movie.library.data.models.toVideoItemModel
import com.mihan.movie.library.domain.ParserRepository
import com.mihan.movie.library.domain.models.VideoItemModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetListVideoUseCase @Inject constructor(private val parserRepository: ParserRepository) {
    suspend operator fun invoke(
        filter: Filter,
        videoCategory: VideoCategory,
        page: Int
    ): Flow<ApiResponse<List<VideoItemModel>>> = flow {
        emit(ApiResponse.Loading)
        when (val result = parserRepository.getListVideo(filter, videoCategory, page)) {
            is ApiResponse.Loading -> Unit
            is ApiResponse.Success -> emit(ApiResponse.Success(result.data.map { it.toVideoItemModel() }))
            is ApiResponse.Error -> emit(ApiResponse.Error(result.errorMessage))
        }
    }
}