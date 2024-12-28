package com.mihan.movie.library.common.utils

import android.content.Context
import android.content.SharedPreferences
import com.mihan.movie.library.common.Constants
import javax.inject.Inject

class SharedPrefs @Inject constructor(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    fun saveCookies(cookies: Set<String>) {
        with(sharedPreferences.edit()) {
            putStringSet(COOKIES_KEY, cookies)
            putBoolean(AUTH_STATUS_KEY, true)
            apply()
        }
    }

    fun getCookies(): Set<String> = sharedPreferences.getStringSet(COOKIES_KEY, emptySet<String>()) ?: emptySet()

    fun clearCookies() {
        with(sharedPreferences.edit()) {
            remove(COOKIES_KEY)
            remove(USER_ID_KEY)
            putBoolean(AUTH_STATUS_KEY, false)
            apply()
        }
    }

    fun saveUserId(userId: String) {
        with(sharedPreferences.edit()) {
            putString(USER_ID_KEY, userId)
            apply()
        }
    }

    fun getUserId(): String = sharedPreferences.getString(USER_ID_KEY, Constants.EMPTY_STRING) ?: Constants.EMPTY_STRING

    fun getUserAuthStatus(): Boolean = sharedPreferences.getBoolean(AUTH_STATUS_KEY, false)

    fun isUnsupportedDeviceMessageShowed(): Boolean = sharedPreferences.getBoolean(UNSUPPORTED_DEVICE_KEY, false)

    fun updateUnsupportedDeviceMessageStatus(isShowed: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(UNSUPPORTED_DEVICE_KEY, isShowed)
            apply()
        }
    }

    companion object {
        private const val COOKIES_KEY = "cookies"
        private const val USER_ID_KEY = "userId"
        private const val AUTH_STATUS_KEY = "auth_status"
        private const val SHARED_PREFS_NAME = "MovieLibraryPrefs"
        private const val UNSUPPORTED_DEVICE_KEY = "UnsupportedDeviceKey"
    }
}