package com.mihan.movie.library.data.repository

import com.mihan.movie.library.data.local.db.VideoHistoryDao
import com.mihan.movie.library.data.models.toVideoHistoryModel
import com.mihan.movie.library.domain.LocalVideoHistoryRepository
import com.mihan.movie.library.domain.models.VideoHistoryModel
import com.mihan.movie.library.domain.models.toVideoHistoryDbModel
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ActivityRetainedScoped
class LocalVideoVideoHistoryRepositoryImpl @Inject constructor(
    private val videoHistoryDao: VideoHistoryDao
): LocalVideoHistoryRepository {
    override fun getVideoHistoryList(): Flow<List<VideoHistoryModel>> {
        return videoHistoryDao.getVideoHistoryList().map { list -> list.map { it.toVideoHistoryModel() } }
    }

    override fun getVideoHistoryById(videoId: String): Flow<VideoHistoryModel?> {
         return videoHistoryDao.getVideoHistoryById(videoId).map { it?.toVideoHistoryModel() }
    }

    override suspend fun updateVideoHistory(videoHistoryModel: VideoHistoryModel) {
        videoHistoryDao.updateVideoHistory(videoHistoryModel.toVideoHistoryDbModel())
    }

    override suspend fun deleteVideoHistoryById(videoId: String) {
        videoHistoryDao.deleteVideoHistoryById(videoId)
    }
}