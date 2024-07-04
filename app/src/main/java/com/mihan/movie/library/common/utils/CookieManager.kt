package com.mihan.movie.library.common.utils

import com.mihan.movie.library.common.Constants
import com.mihan.movie.library.common.DataStorePrefs
import com.mihan.movie.library.di.IODispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import javax.inject.Inject


class CookieManager @Inject constructor(
    private val dataStorePrefs: DataStorePrefs,
    @IODispatcher private val scope: CoroutineScope
) : CookieJar {

    private val tempCookieList = mutableListOf<String>()

    init {
        scope.launch {
            dataStorePrefs.getCookies().collect { cookies ->
                tempCookieList.clear()
                tempCookieList.addAll(cookies)
            }
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return tempCookieList.mapNotNull { cookieString -> Cookie.parse(url, cookieString) }
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        if (!url.encodedPath.contains(ROUTE_FOR_COOKIES)) return // Забираем куки только со страницы авторизации
        scope.launch {
            val userId = extractDleUserIdFromCookies(cookies)
            val cookieSet =
                cookies.filterNot { it.value == DELETED_COOKIES }.map { cookie -> cookie.toString() }.toSet()
            dataStorePrefs.saveCookies(cookieSet)
            if (userId.isNotEmpty()) dataStorePrefs.saveUserId(userId)
        }
    }


    private fun extractDleUserIdFromCookies(cookies: List<Cookie>): String {
        for (cookie in cookies) {
            if (cookie.name == USER_ID) {
                if (cookie.value == DELETED_COOKIES) continue
                return cookie.value
            }
        }
        return Constants.EMPTY_STRING
    }

    companion object {
        private const val ROUTE_FOR_COOKIES = "login"
        private const val DELETED_COOKIES = "deleted"
        private const val USER_ID = "dle_user_id"
    }
}