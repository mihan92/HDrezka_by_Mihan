package com.mihan.movie.library.data.local.cache

import com.mihan.movie.library.common.extentions.logger
import javax.inject.Inject

/**
 * Класс, который создает кэш данных на время работы приложения.
 * Это избавляет от повторных запросов парсинга данных с сайта и мгновенно возвращает данные из кэша.
 */

class CachingManager<K, V> @Inject constructor() {

    private val cacheMap = mutableMapOf<K, V>()

    fun getCachingData(key: K): V? {
        if (cacheMap[key] != null) logger("key $key -- was getting from cache")
        return cacheMap[key]
    }

    fun putToCache(key: K, value: V) {
        logger("key $key -- has been added to cache")
        cacheMap[key] = value
    }
}