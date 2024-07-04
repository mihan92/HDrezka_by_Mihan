package com.mihan.movie.library.domain.usecases.parser

import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.common.models.VideoCategory
import com.mihan.movie.library.domain.ParserRepository
import com.mihan.movie.library.domain.models.VideoItemModel
import com.mihan.movie.library.presentation.ui.view.TopBarItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetListVideoUseCase @Inject constructor(private val parserRepository: ParserRepository) {
    suspend operator fun invoke(
        topBarItems: TopBarItems,
        videoCategory: VideoCategory,
        page: Int
    ): Flow<ApiResponse<List<VideoItemModel>>> = flow {
        emit(ApiResponse.Loading)
        when (val result = parserRepository.getListVideo(topBarItems, videoCategory, page)) {
            is ApiResponse.Loading -> Unit
            is ApiResponse.Success -> emit(ApiResponse.Success(result.data))
            is ApiResponse.Error -> emit(ApiResponse.Error(result.errorMessage))
        }
    }
}