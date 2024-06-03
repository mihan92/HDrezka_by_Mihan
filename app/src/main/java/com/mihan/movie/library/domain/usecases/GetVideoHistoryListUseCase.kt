package com.mihan.movie.library.domain.usecases

import com.mihan.movie.library.domain.LocalVideoHistoryRepository
import com.mihan.movie.library.domain.models.VideoHistoryModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetVideoHistoryListUseCase @Inject constructor(private val repository: LocalVideoHistoryRepository) {

    operator fun invoke(): Flow<List<VideoHistoryModel>> {
         return repository.getVideoHistoryList()
    }
}