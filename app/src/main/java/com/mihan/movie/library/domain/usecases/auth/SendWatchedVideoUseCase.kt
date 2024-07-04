package com.mihan.movie.library.domain.usecases.auth

import com.mihan.movie.library.domain.AuthRepository
import com.mihan.movie.library.domain.models.WatchedVideoModel
import javax.inject.Inject

class SendWatchedVideoUseCase @Inject constructor(private val authRepository: AuthRepository) {

    suspend operator fun invoke(model: WatchedVideoModel) = authRepository.sendWatchedVideo(model)
}