package com.mihan.movie.library.domain.usecases.auth

import com.mihan.movie.library.domain.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val authRepository: AuthRepository) {

    suspend operator fun invoke(login: String, password: String): Boolean = authRepository.login(login, password)
}