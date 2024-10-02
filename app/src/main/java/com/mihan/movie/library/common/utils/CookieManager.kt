package com.mihan.movie.library.common.utils

import com.mihan.movie.library.common.Constants
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject


class CookieManager @Inject constructor(
    private val sharedPrefs: SharedPrefs,
) : CookieJar, Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val cookies = sharedPrefs.getCookies()
        // Добавляем куки в заголовок запроса, если они есть
        val newRequest = if (cookies.isNotEmpty()) {
            originalRequest.newBuilder()
                .header("Cookie", cookies.joinToString("; "))
                .build()
        } else {
            originalRequest
        }
        return chain.proceed(newRequest)
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookies = sharedPrefs.getCookies()
        return cookies.mapNotNull { cookieString -> Cookie.parse(url, cookieString) }
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        if (!url.encodedPath.contains(ROUTE_FOR_COOKIES)) return // Забираем куки только со страницы авторизации
        val userId = extractDleUserIdFromCookies(cookies)
        //val cookieSet = cookies.filterNot { it.value == DELETED_COOKIES }.map { cookie -> cookie.toString() }.toSet()
        val cookieSet = cookies.takeLast(3).map { cookie -> cookie.toString() }.toSet()
        sharedPrefs.saveCookies(cookieSet)
        if (userId.isNotEmpty()) sharedPrefs.saveUserId(userId)
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