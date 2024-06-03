package com.mihan.movie.library.domain.usecases

import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.common.models.CategoryFilter
import com.mihan.movie.library.common.models.MovieCollections
import com.mihan.movie.library.domain.ParserRepository
import com.mihan.movie.library.domain.models.VideoItemModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCollectionsListVideoUseCase @Inject constructor(private val parserRepository: ParserRepository) {
    suspend operator fun invoke(
        categoryFilter: CategoryFilter,
        movieCollection: MovieCollections,
        page: Int
    ): Flow<ApiResponse<List<VideoItemModel>>> = flow {
        emit(ApiResponse.Loading)
        when (val result = parserRepository.getCollectionsListVideo(categoryFilter, movieCollection, page)) {
            is ApiResponse.Loading -> Unit
            is ApiResponse.Success -> emit(ApiResponse.Success(result.data))
            is ApiResponse.Error -> emit(ApiResponse.Error(result.errorMessage))
        }
    }
}