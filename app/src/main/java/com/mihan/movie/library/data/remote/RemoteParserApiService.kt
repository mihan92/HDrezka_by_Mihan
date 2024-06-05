package com.mihan.movie.library.data.remote

import com.mihan.movie.library.BuildConfig
import com.mihan.movie.library.data.models.BaseUrlModelDto
import com.mihan.movie.library.data.models.SerialModelDto
import com.mihan.movie.library.data.models.VideoDetailDto
import com.mihan.movie.library.data.models.VideoDto
import com.mihan.movie.library.data.models.VideoItemDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface RemoteParserApiService {

    @Headers("Authorization: ${BuildConfig.ACCESS_TOKEN}")
    @GET("api/listVideo")
    fun getListVideo(
        @Query("filter") filter: String,
        @Query("category") category: String,
        @Query("page") page: Int
    ): Call<List<VideoItemDto>>

    @Headers("Authorization: ${BuildConfig.ACCESS_TOKEN}")
    @GET("api/filteredListVideo")
    fun getFilteredListVideo(
        @Query("category_filter") categoryFilter: String,
        @Query("genre_filter") genreFilter: String,
        @Query("movie_period") moviePeriod: String,
        @Query("page") page: Int
    ): Call<List<VideoItemDto>>

    @Headers("Authorization: ${BuildConfig.ACCESS_TOKEN}")
    @GET("api/collectionsListVideo")
    fun getCollectionsListVideo(
        @Query("category_filter") categoryFilter: String,
        @Query("movie_collection") movieCollection: String,
        @Query("page") page: Int
    ): Call<List<VideoItemDto>>

    @Headers("Authorization: ${BuildConfig.ACCESS_TOKEN}")
    @GET("api/detailVideoInfo")
    fun getDetailVideoByUrl(
        @Query("url") url: String
    ): Call<VideoDetailDto>

    @Headers("Authorization: ${BuildConfig.ACCESS_TOKEN}")
    @GET("api/translations")
    fun getTranslationsByUrl(
        @Query("url") url: String
    ): Call<VideoDto>

    @Headers("Authorization: ${BuildConfig.ACCESS_TOKEN}")
    @GET("api/seasonListById")
    fun getSeasonsByTranslatorId(
        @Query("translatorId") translatorId: String
    ): Call<List<SerialModelDto>>

    @Headers("Authorization: ${BuildConfig.ACCESS_TOKEN}")
    @GET("api/getBaseUrl")
    fun getBaseUrl(): Call<BaseUrlModelDto>
}