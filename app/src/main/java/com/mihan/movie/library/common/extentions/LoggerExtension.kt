package com.mihan.movie.library.common.extentions

import android.util.Log
import com.mihan.movie.library.BuildConfig

fun Any.logger(message: String, tag: String = this.javaClass.simpleName) {
    if (BuildConfig.DEBUG) Log.d(tag, message)
}