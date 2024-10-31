package com.mihan.movie.library.data.repository

import com.mihan.movie.library.common.ApiResponse
import com.mihan.movie.library.common.DataStorePrefs
import com.mihan.movie.library.common.extentions.logger
import com.mihan.movie.library.common.utils.EventManager
import com.mihan.movie.library.common.utils.SharedPrefs
import com.mihan.movie.library.data.remote.AuthApiService
import com.mihan.movie.library.domain.AuthRepository
import com.mihan.movie.library.domain.models.WatchedVideoModel
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.safety.Safelist
import javax.inject.Inject


@ActivityRetainedScoped
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val dataStorePrefs: DataStorePrefs,
    private val sharedPrefs: SharedPrefs,
    private val eventManager: EventManager
) : AuthRepository {

    override suspend fun login(login: String, password: String): ApiResponse<Boolean> = withContext(Dispatchers.IO) {
        runCatching {
            val url = dataStorePrefs.getBaseUrl().first() + LOGIN_ROUTE
            authApiService.login(url, login, password, 0).execute()
        }.fold(
            { response ->
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    when {
                        loginResponse.success -> {
                            logger("Login successful!")
                            ApiResponse.Success(true)
                        }

                        loginResponse.message != null -> {
                            try {
                                val messageWithoutHtml = Jsoup.clean(loginResponse.message.toString(), Safelist.none())
                                ApiResponse.Error(messageWithoutHtml)
                            } catch (e: Exception) {
                                ApiResponse.Error(e.message.toString())
                            }
                        }

                        else -> {
                            ApiResponse.Success(false)
                        }
                    }
                } else {
                    ApiResponse.Error("Login response error: ${response.errorBody()?.string()}")
                }
            },
            { error ->
                ApiResponse.Error("Login error: ${error.message}")
            }
        )
    }

    override suspend fun logout() {
        runCatching {
            withContext(Dispatchers.IO) {
                val url = dataStorePrefs.getBaseUrl().first() + LOGOUT_ROUTE
                authApiService.logout(url).execute()
                sharedPrefs.clearCookies()
            }
        }.onFailure { error -> eventManager.sendEvent("Logout error: ${error.message}") }
    }

    override suspend fun sendWatchedVideo(watchedVideoModel: WatchedVideoModel) {
        runCatching {
            withContext(Dispatchers.IO) {
                val url = dataStorePrefs.getBaseUrl().first() + SAVE_ROUTE
                authApiService.sendWatchingVideo(
                    url = url,
                    dataId = watchedVideoModel.dataId,
                    translatorId = watchedVideoModel.translatorId,
                    season = watchedVideoModel.season,
                    episode = watchedVideoModel.episode
                ).execute()
            }
        }.onFailure { error -> eventManager.sendEvent("sendWatchedVideo error: ${error.message}") }
    }

    override suspend fun deleteWatchedVideo(dataId: String) {
        runCatching {
            withContext(Dispatchers.IO) {
                val url = dataStorePrefs.getBaseUrl().first() + DELETE_ROUTE
                authApiService.deleteWatchedVideo(url, dataId).execute()
            }
        }.onFailure { error -> eventManager.sendEvent("deleteWatchedVideo error: ${error.message}") }
    }

    companion object {
        private const val LOGIN_ROUTE = "/ajax/login/"
        private const val SAVE_ROUTE = "/ajax/send_save/"
        private const val DELETE_ROUTE = "/engine/ajax/cdn_saves_remove.php"
        private const val LOGOUT_ROUTE = "/logout/"
    }
}