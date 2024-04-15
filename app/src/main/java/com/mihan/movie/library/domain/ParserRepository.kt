package com.mihan.movie.library.domain

import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.common.entites.Filter
import com.mihan.movie.library.common.entites.VideoCategory
import com.mihan.movie.library.data.models.BaseUrlModelDto
import com.mihan.movie.library.data.models.SeasonModelDto
import com.mihan.movie.library.data.models.StreamDto
import com.mihan.movie.library.data.models.VideoDetailDto
import com.mihan.movie.library.data.models.VideoDto
import com.mihan.movie.library.data.models.VideoItemDto

interface ParserRepository {
    suspend fun getListVideo(
        filter: Filter,
        videoCategory: VideoCategory,
        page: Int
    ): ApiResponse<List<VideoItemDto>>

    suspend fun getDetailVideoByUrl(url: String): ApiResponse<VideoDetailDto>

    suspend fun getTranslationsByUrl(url: String): ApiResponse<VideoDto>

    suspend fun getStreamsBySeasonId(
        translationId: String,
        videoId: String,
        season: String,
        episode: String
    ): ApiResponse<List<StreamDto>>

    suspend fun getVideosByTitle(videoTitle: String): ApiResponse<List<VideoItemDto>>

    suspend fun getSeasonsByTranslatorId(translatorId: String): ApiResponse<List<SeasonModelDto>>

    suspend fun getBaseUrl(): ApiResponse<BaseUrlModelDto>

    suspend fun getStreamsByTranslatorId(translatorId: String): ApiResponse<List<StreamDto>>
}