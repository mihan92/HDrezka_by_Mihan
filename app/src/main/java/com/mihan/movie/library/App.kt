package com.mihan.movie.library

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.appmetrica.analytics.AppMetrica
import io.appmetrica.analytics.AppMetricaConfig

@HiltAndroidApp
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        initAnalytics()
    }

    private fun initAnalytics() {
        val config = AppMetricaConfig.newConfigBuilder(BuildConfig.APP_METRICA_KEY).build()
        AppMetrica.activate(this, config)
    }
}