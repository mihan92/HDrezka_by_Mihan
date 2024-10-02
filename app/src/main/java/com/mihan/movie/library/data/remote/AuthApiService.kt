package com.mihan.movie.library.data.remote

import com.mihan.movie.library.data.models.LoginResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface AuthApiService {

    @POST
    fun login(
        @Url url: String,
        @Query("login_name") loginName: String,
        @Query("login_password") loginPassword: String,
        @Query("login_not_save") loginNotSave: Int,
    ): Call<LoginResponse>

    @POST
    fun logout(@Url url: String): Call<ResponseBody>

    @POST
    fun sendWatchingVideo(
        @Url url: String,
        @Query("post_id") dataId: String,
        @Query("translator_id") translatorId: String,
        @Query("season") season: String,
        @Query("episode") episode: String
    ): Call<ResponseBody>

    @POST
    fun deleteWatchedVideo(
        @Url url: String,
        @Query("id") dataId: String,
    ): Call<ResponseBody>
}