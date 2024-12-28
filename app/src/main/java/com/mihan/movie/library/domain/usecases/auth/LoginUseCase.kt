package com.mihan.movie.library.domain.usecases.auth

import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.domain.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val authRepository: AuthRepository) {

    suspend operator fun invoke(login: String, password: String): Flow<ApiResponse<Boolean>> = flow {
        emit(ApiResponse.Loading)
        when(val result = authRepository.login(login, password)) {
            ApiResponse.Loading -> Unit
            is ApiResponse.Error -> emit(ApiResponse.Error(result.errorMessage))
            is ApiResponse.Success -> emit(ApiResponse.Success(result.data))
        }
    }
}