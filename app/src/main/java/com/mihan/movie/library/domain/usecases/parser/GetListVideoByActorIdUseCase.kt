package com.mihan.movie.library.domain.usecases.parser

import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.domain.ParserRepository
import com.mihan.movie.library.domain.models.VideoItemModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetListVideoByActorIdUseCase @Inject constructor(private val parserRepository: ParserRepository) {
    suspend operator fun invoke(actorId: String): Flow<ApiResponse<List<VideoItemModel>>> = flow {
        emit(ApiResponse.Loading)
        when(val result = parserRepository.getListVideoByActorId(actorId)){
            is ApiResponse.Loading -> Unit
            is ApiResponse.Error -> emit(ApiResponse.Error(result.errorMessage))
            is ApiResponse.Success -> emit(ApiResponse.Success(result.data))
        }
    }
}