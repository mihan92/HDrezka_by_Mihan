package com.mihan.movie.library.common.utils

import com.mihan.movie.library.common.Constants
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserAgentInterceptor @Inject constructor(): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .header("User-Agent", Constants.USER_AGENT)
            .build()

        return chain.proceed(request)
    }
}