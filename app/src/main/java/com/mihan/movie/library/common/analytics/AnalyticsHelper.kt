package com.mihan.movie.library.common.analytics

import android.util.Log
import com.mihan.movie.library.BuildConfig
import io.appmetrica.analytics.AppMetrica

fun sendEvent(event: AnalyticsEvent, param: String, value: String = "") {
    AppMetrica.reportEvent(event.eventName, "{\"$param\":\"$value\"}")
    if (BuildConfig.DEBUG) Log.d("AppMetrica","send event: ${event.eventName} $param value: $value")
}