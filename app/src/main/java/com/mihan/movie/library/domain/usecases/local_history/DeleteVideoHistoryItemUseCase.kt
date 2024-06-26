package com.mihan.movie.library.domain.usecases.local_history

import com.mihan.movie.library.domain.LocalVideoHistoryRepository
import com.mihan.movie.library.domain.models.VideoHistoryModel
import javax.inject.Inject

class DeleteVideoHistoryItemUseCase @Inject constructor(private val repository: LocalVideoHistoryRepository) {

    suspend operator fun invoke(model: VideoHistoryModel) {
        repository.deleteVideoHistoryById(model)
    }
}