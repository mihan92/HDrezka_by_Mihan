package com.mihan.movie.library.common.analytics

import com.mihan.movie.library.BuildConfig
import io.appmetrica.analytics.AppMetrica

fun sendEvent(event: AnalyticsEvent, param: String, value: String = "") {
    if (BuildConfig.DEBUG) return
    AppMetrica.reportEvent(event.eventName, "{\"$param\":\"$value\"}")
}