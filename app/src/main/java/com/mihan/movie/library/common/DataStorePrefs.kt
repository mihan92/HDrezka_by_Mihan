package com.mihan.movie.library.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mihan.movie.library.common.entites.Colors
import com.mihan.movie.library.common.entites.VideoCategory
import com.mihan.movie.library.common.entites.VideoQuality
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStorePrefs @Inject constructor(@ApplicationContext context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATA_STORE_NAME)
    private val dataStore = context.dataStore
    fun getAppUpdates(): Flow<Boolean> =
        dataStore.data.map { prefs ->
            prefs[APP_UPDATES_KEY] ?: false
        }

    suspend fun setAppUpdates(isUpdateAvailable: Boolean) =
        dataStore.edit { prefs ->
            prefs[APP_UPDATES_KEY] = isUpdateAvailable
        }

    fun getVideoCategory(): Flow<VideoCategory> =
        dataStore.data.map { prefs ->
            prefs[VIDEO_CATEGORY_KEY]?.let { VideoCategory.valueOf(it) } ?: DEFAULT_VIDEO_CATEGORY
        }

    suspend fun setVideoCategory(videoCategory: VideoCategory) =
        dataStore.edit { prefs ->
            prefs[VIDEO_CATEGORY_KEY] = videoCategory.name
        }

    fun getVideoQuality(): Flow<VideoQuality> =
        dataStore.data.map { prefs ->
            prefs[VIDEO_QUALITY_KEY]?.let { VideoQuality.valueOf(it) } ?: DEFAULT_VIDEO_QUALITY
        }

    suspend fun setVideoQuality(videoQuality: VideoQuality) =
        dataStore.edit { prefs ->
            prefs[VIDEO_QUALITY_KEY] = videoQuality.name
        }

    fun getBaseUrl(): Flow<String> =
        dataStore.data.map { prefs ->
            prefs[BASE_URL_KEY] ?: DEFAULT_BASE_URL
        }

    suspend fun setBaseUrl(baseUrl: String) =
        dataStore.edit { prefs ->
            prefs[BASE_URL_KEY] = baseUrl
        }

    fun getPrimaryColor(): Flow<Colors> =
        dataStore.data.map { prefs ->
            prefs[PRIMARY_COLOR_KEY]?.let { Colors.valueOf(it) } ?: DEFAULT_PRIMARY_COLOR
        }

    suspend fun setPrimaryColor(primaryColor: Colors) {
        dataStore.edit { prefs ->
            prefs[PRIMARY_COLOR_KEY] = primaryColor.name
        }
    }

    fun getRemoteParsing(): Flow<Boolean> =
        dataStore.data.map { prefs ->
            prefs[REMOTE_PARSING_KEY] ?: true
        }

    suspend fun setRemoteParsing(isSelected: Boolean) {
        dataStore.edit { prefs ->
            prefs[REMOTE_PARSING_KEY] = isSelected
        }
    }

    fun getAutoUpdate(): Flow<Boolean> =
        dataStore.data.map { prefs ->
            prefs[AUTO_UPDATE_KEY] ?: true
        }

    suspend fun setAutoUpdate(isEnabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[AUTO_UPDATE_KEY] = isEnabled
        }
    }

    companion object {
        private const val DATA_STORE_NAME = "data_store_preferences"
        private val APP_UPDATES_KEY = booleanPreferencesKey("app_updates_key")
        private val VIDEO_CATEGORY_KEY = stringPreferencesKey("video_category")
        private val VIDEO_QUALITY_KEY = stringPreferencesKey("video_quality")
        private val BASE_URL_KEY = stringPreferencesKey("base_url")
        private val PRIMARY_COLOR_KEY = stringPreferencesKey("primary_color")
        private val REMOTE_PARSING_KEY = booleanPreferencesKey("remote_parsing")
        private val AUTO_UPDATE_KEY = booleanPreferencesKey("auto_update")
        private val DEFAULT_VIDEO_CATEGORY = VideoCategory.All
        private val DEFAULT_VIDEO_QUALITY = VideoQuality.Quality1080
        private val DEFAULT_PRIMARY_COLOR = Colors.Color0
        private const val DEFAULT_BASE_URL = "https://hdrezka320wyi.org"
    }
}