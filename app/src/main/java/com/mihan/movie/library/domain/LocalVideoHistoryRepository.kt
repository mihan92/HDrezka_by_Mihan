package com.mihan.movie.library.domain

import com.mihan.movie.library.domain.models.VideoHistoryModel
import kotlinx.coroutines.flow.Flow

interface LocalVideoHistoryRepository {

    fun getVideoHistoryList(): Flow<List<VideoHistoryModel>>

    fun getVideoHistoryById(videoId: String): Flow<VideoHistoryModel?>

    suspend fun updateVideoHistory(videoHistoryModel: VideoHistoryModel)

    suspend fun deleteVideoHistoryById(videoId: String)
}