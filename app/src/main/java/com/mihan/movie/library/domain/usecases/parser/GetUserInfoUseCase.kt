package com.mihan.movie.library.domain.usecases.parser

import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.domain.ParserRepository
import com.mihan.movie.library.domain.models.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(private val parserRepository: ParserRepository) {

    suspend operator fun invoke(): Flow<ApiResponse<UserInfo>> = flow {
        emit(ApiResponse.Loading)
        when (val result = parserRepository.getUserInfo()) {
            is ApiResponse.Loading -> Unit
            is ApiResponse.Success -> emit(ApiResponse.Success(result.data))
            is ApiResponse.Error -> emit(ApiResponse.Error(result.errorMessage))
        }
    }
}