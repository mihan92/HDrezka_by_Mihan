package com.mihan.movie.library.domain.usecases.auth

import com.mihan.movie.library.domain.AuthRepository
import javax.inject.Inject

class MarkAsViewedUseCase @Inject constructor(private val authRepository: AuthRepository) {

    suspend operator fun invoke(dataId: String) = authRepository.markAsViewed(dataId)
}