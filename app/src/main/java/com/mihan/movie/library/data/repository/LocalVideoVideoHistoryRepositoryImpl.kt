package com.mihan.movie.library.data.repository

import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.common.utils.SharedPrefs
import com.mihan.movie.library.data.local.db.VideoHistoryDao
import com.mihan.movie.library.data.models.toVideoHistoryDbModel
import com.mihan.movie.library.data.models.toVideoHistoryModel
import com.mihan.movie.library.domain.AuthRepository
import com.mihan.movie.library.domain.LocalVideoHistoryRepository
import com.mihan.movie.library.domain.ParserRepository
import com.mihan.movie.library.domain.models.VideoHistoryModel
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ActivityRetainedScoped
class LocalVideoVideoHistoryRepositoryImpl @Inject constructor(
    private val videoHistoryDao: VideoHistoryDao,
    private val authRepository: AuthRepository,
    private val parserRepository: ParserRepository,
    private val sharedPrefs: SharedPrefs,
) : LocalVideoHistoryRepository {

    private val isUserAuthorized = sharedPrefs.getUserAuthStatus()

    override suspend fun getVideoHistoryList(): ApiResponse<List<VideoHistoryModel>> {
        return if (!isUserAuthorized) {
            val list = videoHistoryDao.getVideoHistoryList().map { list ->
                list.map { it.toVideoHistoryModel() }
            }.firstOrNull() ?: emptyList()
            ApiResponse.Success(list)
        } else {
            return when (val result = parserRepository.getRemoteHistoryList()) {
                is ApiResponse.Error -> ApiResponse.Error(result.errorMessage)
                is ApiResponse.Loading -> ApiResponse.Loading
                is ApiResponse.Success -> ApiResponse.Success(result.data)
            }
        }
    }

    override suspend fun getVideoHistoryById(videoId: String): ApiResponse<VideoHistoryModel?> {
        return if (!isUserAuthorized) {
            val model = videoHistoryDao.getVideoHistoryById(videoId).map { it?.toVideoHistoryModel() }.firstOrNull()
            ApiResponse.Success(model)
        } else {
            return when (val result = parserRepository.getRemoteHistoryList()) {
                is ApiResponse.Error -> ApiResponse.Error(result.errorMessage)
                is ApiResponse.Loading -> ApiResponse.Loading
                is ApiResponse.Success -> {
                    val videoModel = result.data.firstOrNull { it.videoId == videoId }
                    ApiResponse.Success(videoModel)
                }
            }
        }
    }

    override suspend fun updateVideoHistory(videoHistoryModel: VideoHistoryModel) {
        videoHistoryDao.updateVideoHistory(videoHistoryModel.toVideoHistoryDbModel())
    }

    override suspend fun deleteVideoHistoryById(model: VideoHistoryModel) {
        val isUserAuthorized = sharedPrefs.getUserAuthStatus()
        if (isUserAuthorized)
            authRepository.deleteWatchedVideo(model.dataId)
        else
            videoHistoryDao.deleteVideoHistoryById(model.videoId)
    }
}