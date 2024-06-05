package com.mihan.movie.library.domain

import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.common.models.CategoryFilter
import com.mihan.movie.library.common.models.GenreFilter
import com.mihan.movie.library.common.models.MovieCollections
import com.mihan.movie.library.common.models.MoviePeriod
import com.mihan.movie.library.common.models.VideoCategory
import com.mihan.movie.library.domain.models.BaseUrlModel
import com.mihan.movie.library.domain.models.SerialModel
import com.mihan.movie.library.domain.models.StreamModel
import com.mihan.movie.library.domain.models.VideoDetailModel
import com.mihan.movie.library.domain.models.VideoInfoModel
import com.mihan.movie.library.domain.models.VideoItemModel
import com.mihan.movie.library.presentation.ui.view.TopBarItems

interface ParserRepository {
    suspend fun getListVideo(
        topBarItems: TopBarItems,
        videoCategory: VideoCategory,
        page: Int
    ): ApiResponse<List<VideoItemModel>>

    suspend fun getFilteredListVideo(
        category: CategoryFilter,
        genre: GenreFilter,
        moviePeriod: MoviePeriod,
        page: Int
    ): ApiResponse<List<VideoItemModel>>

    suspend fun getCollectionsListVideo(
        category: CategoryFilter,
        movieCollection: MovieCollections,
        page: Int
    ): ApiResponse<List<VideoItemModel>>

    suspend fun getDetailVideoByUrl(url: String): ApiResponse<VideoDetailModel>

    suspend fun getTranslationsByUrl(url: String): ApiResponse<VideoInfoModel>

    suspend fun getStreamBySeasonId(
        translationId: String,
        filmId: String,
        season: String,
        episode: String
    ): ApiResponse<StreamModel>

    suspend fun getVideosByTitle(videoTitle: String): ApiResponse<List<VideoItemModel>>

    suspend fun getBaseUrl(): ApiResponse<BaseUrlModel>

    suspend fun getSeasonsByTranslatorId(translatorId: String, filmId: String): ApiResponse<List<SerialModel>>

    suspend fun getStreamByTranslatorId(translatorId: String, filmId: String): ApiResponse<StreamModel>
}