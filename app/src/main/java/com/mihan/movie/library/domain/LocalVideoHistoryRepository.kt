package com.mihan.movie.library.domain

import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.domain.models.VideoHistoryModel

interface LocalVideoHistoryRepository {

    suspend fun getVideoHistoryList(): ApiResponse<List<VideoHistoryModel>>

    suspend fun getVideoHistoryById(videoId: String): ApiResponse<VideoHistoryModel?>

    suspend fun updateVideoHistory(videoHistoryModel: VideoHistoryModel)

    suspend fun deleteVideoHistoryById(model: VideoHistoryModel)
}