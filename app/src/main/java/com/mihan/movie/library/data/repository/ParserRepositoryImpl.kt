package com.mihan.movie.library.data.repository

import android.net.Uri
import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.common.Constants
import com.mihan.movie.library.common.models.CategoryFilter
import com.mihan.movie.library.common.models.GenreFilter
import com.mihan.movie.library.common.models.MovieCollections
import com.mihan.movie.library.common.models.MoviePeriod
import com.mihan.movie.library.common.models.VideoCategory
import com.mihan.movie.library.data.local.LocalRezkaParser
import com.mihan.movie.library.data.models.toBaseUrlModel
import com.mihan.movie.library.data.models.toNewSeriesModel
import com.mihan.movie.library.data.models.toSerialModel
import com.mihan.movie.library.data.models.toStreamModel
import com.mihan.movie.library.data.models.toUserInfo
import com.mihan.movie.library.data.models.toVideoDetail
import com.mihan.movie.library.data.models.toVideoHistoryModel
import com.mihan.movie.library.data.models.toVideoInfoModel
import com.mihan.movie.library.data.models.toVideoItemModel
import com.mihan.movie.library.data.remote.RemoteParserApiService
import com.mihan.movie.library.domain.ParserRepository
import com.mihan.movie.library.domain.models.BaseUrlModel
import com.mihan.movie.library.domain.models.NewSeriesModel
import com.mihan.movie.library.domain.models.SerialModel
import com.mihan.movie.library.domain.models.StreamModel
import com.mihan.movie.library.domain.models.UserInfo
import com.mihan.movie.library.domain.models.VideoDetailModel
import com.mihan.movie.library.domain.models.VideoHistoryModel
import com.mihan.movie.library.domain.models.VideoInfoModel
import com.mihan.movie.library.domain.models.VideoItemModel
import com.mihan.movie.library.presentation.ui.view.TopBarItems
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ActivityRetainedScoped
class ParserRepositoryImpl @Inject constructor(
    private val remoteParserApi: RemoteParserApiService,
    private val localParser: LocalRezkaParser,
) : ParserRepository {

    override suspend fun getListVideo(
        topBarItems: TopBarItems,
        videoCategory: VideoCategory,
        page: Int
    ): ApiResponse<List<VideoItemModel>> = withContext(Dispatchers.IO) {
        runCatching {
            remoteParserApi.getListVideo(topBarItems.section, videoCategory.genre, page).execute()
        }.fold(
            { response ->
                if (response.isSuccessful && response.body() != null) {
                    val itemList = response.body()?.map { it.toVideoItemModel() }
                    ApiResponse.Success(itemList ?: emptyList())
                } else
                    ApiResponse.Error(response.errorBody()?.string() ?: "getListVideo api error")
            },
            { error -> ApiResponse.Error("getListVideo error: ${error.message}") }
        )
    }

    override suspend fun getFilteredListVideo(
        category: CategoryFilter,
        genre: GenreFilter,
        moviePeriod: MoviePeriod,
        page: Int
    ): ApiResponse<List<VideoItemModel>> = withContext(Dispatchers.IO) {
        runCatching {
            remoteParserApi.getFilteredListVideo(category.name, genre.name, moviePeriod.name, page).execute()
        }.fold(
            { response ->
                if (response.isSuccessful && response.body() != null) {
                    val list = response.body()?.map { it.toVideoItemModel() }
                    ApiResponse.Success(list ?: emptyList())
                } else
                    ApiResponse.Error(response.errorBody()?.string() ?: "getFilteredListVideo api error")
            },
            { error -> ApiResponse.Error("getFilteredListVideo error: ${error.message}") }
        )
    }

    override suspend fun getCollectionsListVideo(
        category: CategoryFilter,
        movieCollection: MovieCollections,
        page: Int
    ): ApiResponse<List<VideoItemModel>> = withContext(Dispatchers.IO) {
        runCatching {
            remoteParserApi.getCollectionsListVideo(category.name, movieCollection.name, page).execute()
        }.fold(
            { response ->
                if (response.isSuccessful && response.body() != null) {
                    val list = response.body()?.map { it.toVideoItemModel() }
                    ApiResponse.Success(list ?: emptyList())
                } else
                    ApiResponse.Error(response.errorBody()?.string() ?: "getCollectionsListVideo api error")
            },
            { error -> ApiResponse.Error("getCollectionsListVideo error: ${error.message}") }
        )
    }

    override suspend fun getDetailVideoByUrl(url: String): ApiResponse<VideoDetailModel> = withContext(Dispatchers.IO) {
        runCatching {
            val urlPath = Uri.parse(url).path ?: Constants.EMPTY_STRING
            remoteParserApi.getDetailVideoByUrl(urlPath).execute()
        }.fold(
            { response ->
                if (response.isSuccessful && response.body() != null) {
                    val item = response.body()!!.toVideoDetail()
                    ApiResponse.Success(item)
                } else
                    ApiResponse.Error(response.errorBody()?.string() ?: "getDetailVideoByUrl api error")
            },
            { error -> ApiResponse.Error("getDetailVideoByUrl error: ${error.message}") }
        )
    }

    override suspend fun getTranslationsByUrl(url: String): ApiResponse<VideoInfoModel> = withContext(Dispatchers.IO) {
        runCatching {
            val urlPath = Uri.parse(url).path ?: Constants.EMPTY_STRING
            remoteParserApi.getTranslationsByUrl(urlPath).execute()
        }.fold(
            { response ->
                if (response.isSuccessful && response.body() != null) {
                    val videoInfoModel = response.body()!!.toVideoInfoModel()
                    ApiResponse.Success(videoInfoModel)
                } else
                    ApiResponse.Error(response.errorBody()?.string() ?: "getTranslationsByUrl api error")
            },
            { error -> ApiResponse.Error("getTranslationsByUrl error: ${error.message}") }
        )
    }

    override suspend fun getStreamBySeasonId(
        translationId: String,
        filmId: String,
        season: String,
        episode: String
    ): ApiResponse<StreamModel> =
        runCatching {
            localParser.getStreamsBySeasonId(translationId, filmId, season, episode).toStreamModel()
        }.fold(
            { result -> ApiResponse.Success(result) },
            { error -> ApiResponse.Error("getStreamsBySeasonId error: ${error.message}") }
        )

    override suspend fun getListVideoByTitle(videoTitle: String, page: String): ApiResponse<List<VideoItemModel>> =
        withContext(Dispatchers.IO) {
            runCatching {
                remoteParserApi.getListVideoByTitle(videoTitle, page).execute()
            }.fold(
                { response ->
                    if (response.isSuccessful && response.body() != null) {
                        val itemList = response.body()?.map { it.toVideoItemModel() }
                        ApiResponse.Success(itemList ?: emptyList())
                    } else
                        ApiResponse.Error(response.errorBody()?.string() ?: "getListVideoByTitle api error")
                },
                { error -> ApiResponse.Error("getListVideoByTitle error: ${error.message}") }
            )
        }

    override suspend fun getSeasonsByTranslatorId(
        translatorId: String,
        filmId: String
    ): ApiResponse<List<SerialModel>> = withContext(Dispatchers.IO) {
        runCatching {
            remoteParserApi.getSeasonsByTranslatorId(translatorId).execute()
        }.fold(
            { response ->
                if (response.isSuccessful && response.body() != null) {
                    val serialModelList = response.body()!!.map { it.toSerialModel() }
                    ApiResponse.Success(serialModelList)
                } else
                    ApiResponse.Error(response.errorBody()?.string() ?: "getSeasonsByTranslatorId api error")
            },
            { error -> ApiResponse.Error("getSeasonsByTranslatorId error: ${error.message}") }
        )
    }

    override suspend fun getBaseUrl(): ApiResponse<BaseUrlModel> =
        withContext(Dispatchers.IO) {
            runCatching {
                remoteParserApi.getBaseUrl().execute()
            }.fold(
                { response ->
                    if (response.isSuccessful && response.body() != null) {
                        val urlModel = response.body()!!.toBaseUrlModel()
                        ApiResponse.Success(urlModel)
                    } else
                        ApiResponse.Error(response.errorBody()?.string() ?: "getBaseUrl error")
                },
                { error -> ApiResponse.Error("getBaseUrl error: ${error.message}") }
            )
        }

    override suspend fun getStreamByTranslatorId(translatorId: String, filmId: String): ApiResponse<StreamModel> =
        runCatching {
            localParser.getStreamsByTranslationId(translatorId, filmId).toStreamModel()
        }.fold(
            { result -> ApiResponse.Success(result) },
            { error -> ApiResponse.Error("getStreamsByTranslatorId error: ${error.message}") }
        )

    override suspend fun getUserInfo(): ApiResponse<UserInfo> =
        runCatching {
            localParser.getUserInfo().toUserInfo()
        }.fold(
            { result -> ApiResponse.Success(result) },
            { error -> ApiResponse.Error("getUserInfo error: ${error.message}") }
        )

    override suspend fun getNewSeriesList(): ApiResponse<List<NewSeriesModel>> =
        runCatching {
            localParser.getNewSeriesList().map { it.toNewSeriesModel() }
        }.fold(
            { result -> ApiResponse.Success(result) },
            { error -> ApiResponse.Error("getNewSeriesList error: ${error.message}") }
        )

    override suspend fun getRemoteHistoryList(): ApiResponse<List<VideoHistoryModel>> =
        runCatching {
            localParser.getRemoteHistoryList().map { it.toVideoHistoryModel() }
        }.fold(
            { result -> ApiResponse.Success(result) },
            { error -> ApiResponse.Error("getRemoteHistoryList error: ${error.message}") }
        )

    override suspend fun getListVideoByActorId(actorId: String): ApiResponse<List<VideoItemModel>> =
        withContext(Dispatchers.IO) {
            runCatching {
                remoteParserApi.getListVideoByActorId(actorId).execute()
            }.fold(
                { response ->
                    if (response.isSuccessful && response.body() != null) {
                        val itemList = response.body()?.map { it.toVideoItemModel() }
                        ApiResponse.Success(itemList ?: emptyList())
                    } else
                        ApiResponse.Error(response.errorBody()?.string() ?: "getListVideoByActorId api error")
                },
                { error -> ApiResponse.Error("getListVideoByActorId error: ${error.message}") }
            )
        }
}