package com.mihan.movie.library.domain.usecases.local_history

import com.mihan.movie.library.domain.LocalVideoHistoryRepository
import com.mihan.movie.library.domain.models.VideoHistoryModel
import javax.inject.Inject

class UpdateVideoHistoryUseCase @Inject constructor(private val repository: LocalVideoHistoryRepository) {

    suspend operator fun invoke(videoHistoryModel: VideoHistoryModel) {
        repository.updateVideoHistory(videoHistoryModel)
    }
}