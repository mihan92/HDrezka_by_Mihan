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
        val reversedCookies = cookies.reversed()
        val phpId = reversedCookies.find { it.name == PHP_ID }?.let { "${it.name}=${it.value}" }
        val userId = reversedCookies.find { it.name == USER_ID }?.let { "${it.name}=${it.value}" }
        val pass = reversedCookies.find { it.name == PASS }?.let { "${it.name}=${it.value}" }
        val userIdValue = extractDleUserIdFromCookies(cookies)
        val cookieSet = setOfNotNull(phpId, userId, pass)
        sharedPrefs.saveCookies(cookieSet)
        if (userIdValue.isNotEmpty()) sharedPrefs.saveUserId(userIdValue)
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
        private const val PASS = "dle_password"
        private const val PHP_ID = "PHPSESSID"
    }
}