package com.mihan.movie.library.data.repository

import android.net.Uri
import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.common.Constants
import com.mihan.movie.library.common.DataStorePrefs
import com.mihan.movie.library.common.entites.Filter
import com.mihan.movie.library.common.entites.VideoCategory
import com.mihan.movie.library.data.local.LocalRezkaParser
import com.mihan.movie.library.data.models.BaseUrlModelDto
import com.mihan.movie.library.data.models.SeasonModelDto
import com.mihan.movie.library.data.models.StreamDto
import com.mihan.movie.library.data.models.VideoDetailDto
import com.mihan.movie.library.data.models.VideoDto
import com.mihan.movie.library.data.models.VideoItemDto
import com.mihan.movie.library.data.remote.RemoteParserApiService
import com.mihan.movie.library.domain.ParserRepository
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ActivityRetainedScoped
class ParserRepositoryImpl @Inject constructor(
    private val remoteParser: RemoteParserApiService,
    private val localParser: LocalRezkaParser,
    private val dataStorePrefs: DataStorePrefs
) : ParserRepository {

    private suspend fun isRemoteParsing() = dataStorePrefs.getRemoteParsing().first()

    override suspend fun getListVideo(
        filter: Filter,
        videoCategory: VideoCategory,
        page: Int
    ): ApiResponse<List<VideoItemDto>> =
        if (isRemoteParsing()) {
            withContext(Dispatchers.IO) {
                runCatching {
                    remoteParser.getListVideo(filter.section, videoCategory.genre, page).execute()
                }.fold(
                    { response ->
                        if (response.isSuccessful && response.body() != null)
                            ApiResponse.Success(response.body() ?: emptyList())
                        else
                            ApiResponse.Error(response.errorBody()?.string() ?: "getListVideo api error")
                    },
                    { error -> ApiResponse.Error("getListVideo error: ${error.message}") }
                )
            }
        } else {
            runCatching {
                localParser.getListVideo(filter, videoCategory, page)
            }.fold(
                { result -> ApiResponse.Success(result) },
                { error -> ApiResponse.Error("getListVideo error: ${error.message}") }
            )
        }

    override suspend fun getDetailVideoByUrl(url: String): ApiResponse<VideoDetailDto> =
        if (isRemoteParsing()) {
            withContext(Dispatchers.IO) {
                runCatching {
                    val urlPath = Uri.parse(url).path ?: Constants.EMPTY_STRING
                    remoteParser.getDetailVideoByUrl(urlPath).execute()
                }.fold(
                    { response ->
                        if (response.isSuccessful && response.body() != null)
                            ApiResponse.Success(response.body()!!)
                        else
                            ApiResponse.Error(response.errorBody()?.string() ?: "getDetailVideoByUrl api error")
                    },
                    { error -> ApiResponse.Error("getDetailVideoByUrl error: ${error.message}") }
                )

            }
        } else {
            runCatching {
                localParser.getDetailVideoByUrl(url)
            }.fold(
                { result -> ApiResponse.Success(result) },
                { error -> ApiResponse.Error("getDetailVideoByUrl error: ${error.message}") }
            )
        }

    override suspend fun getTranslationsByUrl(url: String): ApiResponse<VideoDto> =
        if (isRemoteParsing())
            withContext(Dispatchers.IO) {
                runCatching {
                    val urlPath = Uri.parse(url).path ?: Constants.EMPTY_STRING
                    remoteParser.getTranslationsByUrl(urlPath).execute()
                }.fold(
                    { response ->
                        if (response.isSuccessful && response.body() != null)
                            ApiResponse.Success(response.body()!!)
                        else
                            ApiResponse.Error(response.errorBody()?.string() ?: "getTranslationsByUrl api error")
                    },
                    { error -> ApiResponse.Error("getTranslationsByUrl error: ${error.message}") }
                )
            }
        else {
            runCatching {
                localParser.getTranslationsByUrl(url)
            }.fold(
                { result -> ApiResponse.Success(result) },
                { error -> ApiResponse.Error("getTranslationsByUrl error: ${error.message}") }
            )
        }

    override suspend fun getStreamsBySeasonId(
        translationId: String,
        videoId: String,
        season: String,
        episode: String
    ): ApiResponse<List<StreamDto>> =
        if (isRemoteParsing())
            withContext(Dispatchers.IO) {
                runCatching {
                    val quality = dataStorePrefs.getVideoQuality().first().quality
                    remoteParser.getStreamsBySeasonId(translationId, videoId, season, episode, quality).execute()
                }.fold(
                    { response ->
                        if (response.isSuccessful && response.body() != null)
                            ApiResponse.Success(response.body()!!)
                        else
                            ApiResponse.Error(response.errorBody()?.string() ?: "getStreamsBySeasonId api error")
                    },
                    { error -> ApiResponse.Error("getStreamsBySeasonId error: ${error.message}") }
                )
            }
        else {
            runCatching {
                localParser.getStreamsBySeasonId(translationId, videoId, season, episode)
            }.fold(
                { result -> ApiResponse.Success(result) },
                { error -> ApiResponse.Error("getStreamsBySeasonId error: ${error.message}") }
            )
        }

    override suspend fun getVideosByTitle(videoTitle: String): ApiResponse<List<VideoItemDto>> =
        if (isRemoteParsing())
            withContext(Dispatchers.IO) {
                runCatching {
                    remoteParser.getVideosByTitle(videoTitle).execute()
                }.fold(
                    { response ->
                        if (response.isSuccessful && response.body() != null)
                            ApiResponse.Success(response.body()!!)
                        else
                            ApiResponse.Error(response.errorBody()?.string() ?: "getVideosByTitle api error")
                    },
                    { error -> ApiResponse.Error("getVideosByTitle error: ${error.message}") }
                )
            }
        else {
            runCatching {
                localParser.getVideosByTitle(videoTitle)
            }.fold(
                { result -> ApiResponse.Success(result) },
                { error -> ApiResponse.Error("getVideosByTitle error: ${error.message}") }
            )
        }

    override suspend fun getSeasonsByTranslatorId(translatorId: String): ApiResponse<List<SeasonModelDto>> =
        if (isRemoteParsing())
            withContext(Dispatchers.IO) {
                runCatching {
                    remoteParser.getSeasonsByTranslatorId(translatorId).execute()
                }.fold(
                    { response ->
                        if (response.isSuccessful && response.body() != null)
                            ApiResponse.Success(response.body()!!)
                        else
                            ApiResponse.Error(response.errorBody()?.string() ?: "getSeasonsByTranslatorId api error")
                    },
                    { error -> ApiResponse.Error("getSeasonsByTranslatorId error: ${error.message}") }
                )

            }
        else {
            runCatching {
                localParser.getSeasonsByTranslatorId(translatorId)
            }.fold(
                { result -> ApiResponse.Success(result) },
                { error -> ApiResponse.Error("getSeasonsByTranslatorId error: ${error.message}") }
            )
        }

    override suspend fun getBaseUrl(): ApiResponse<BaseUrlModelDto> =
        withContext(Dispatchers.IO) {
            runCatching {
                remoteParser.getBaseUrl().execute()
            }.fold(
                { response ->
                    if (response.isSuccessful && response.body() != null)
                        ApiResponse.Success(response.body()!!)
                    else
                        ApiResponse.Error(response.errorBody()?.string() ?: "getBaseUrl error")
                },
                { error -> ApiResponse.Error("getBaseUrl error: ${error.message}") }
            )
        }

    override suspend fun getStreamsByTranslatorId(translatorId: String): ApiResponse<List<StreamDto>> =
        if (isRemoteParsing())
            withContext(Dispatchers.IO) {
                runCatching {
                    val quality = dataStorePrefs.getVideoQuality().first().quality
                    remoteParser.getStreamsByTranslatorId(translatorId, quality).execute()
                }.fold(
                    { response ->
                        if (response.isSuccessful && response.body() != null)
                            ApiResponse.Success(response.body()!!)
                        else
                            ApiResponse.Error(response.errorBody()?.string() ?: "getStreamsByTranslatorId api error")
                    },
                    { error -> ApiResponse.Error("getStreamsByTranslatorId error: ${error.message}") }
                )
            }
        else {
            runCatching {
                localParser.getStreamsByTranslationId(translatorId)
            }.fold(
                { result -> ApiResponse.Success(result) },
                { error -> ApiResponse.Error("getStreamsByTranslatorId error: ${error.message}") }
            )
        }
}