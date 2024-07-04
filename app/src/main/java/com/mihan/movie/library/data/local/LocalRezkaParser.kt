package com.mihan.movie.library.data.local

import android.net.Uri
import android.util.ArrayMap
import android.util.Patterns
import com.mihan.movie.library.common.Constants
import com.mihan.movie.library.common.DataStorePrefs
import com.mihan.movie.library.common.extentions.logger
import com.mihan.movie.library.data.models.NewSeriesModelDto
import com.mihan.movie.library.data.models.StreamDto
import com.mihan.movie.library.data.models.UserInfoDto
import com.mihan.movie.library.data.models.VideoHistoryModelDto
import com.mihan.movie.library.data.models.VideoItemDto
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.Cookie
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.json.JSONObject
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.regex.Pattern
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@ActivityRetainedScoped
class LocalRezkaParser @Inject constructor(
    private val dataStorePrefs: DataStorePrefs,
) : CoroutineScope {

    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.IO

    suspend fun getNewSeriesList(): List<NewSeriesModelDto> = withContext(Dispatchers.IO) {
        val cookies = dataStorePrefs.getCookies().first()
            .mapNotNull { cookieString -> Cookie.parse(getBaseUrl().toHttpUrl(), cookieString) }
        val cookieMap = cookies.associate { it.name to it.value }
        val document = getConnection("${getBaseUrl()}/continue/").cookies(cookieMap).get()
        val notAuthorized = document.select("div.b-info__message").text()
        if (notAuthorized.isNotEmpty()) {
            dataStorePrefs.clearCookies()
            return@withContext emptyList()
        }
        fetchNewSeriesFromDocument(document)
    }

    suspend fun getRemoteHistoryList(): List<VideoHistoryModelDto> = withContext(Dispatchers.IO) {
        val cookies = dataStorePrefs.getCookies().first()
            .mapNotNull { cookieString -> Cookie.parse(getBaseUrl().toHttpUrl(), cookieString) }
        val cookieMap = cookies.associate { it.name to it.value }
        val document = getConnection("${getBaseUrl()}/continue/").cookies(cookieMap).get()
        val notAuthorized = document.select("div.b-info__message").text()
        if (notAuthorized.isNotEmpty()) {
            dataStorePrefs.clearCookies()
            return@withContext emptyList()
        }
        fetchHistoryFromDocument(document)
    }

    suspend fun getUserInfo(): UserInfoDto = withContext(Dispatchers.IO) {
        val userId = dataStorePrefs.getUserId().first()
        if (userId.isEmpty()) return@withContext UserInfoDto()
        val cookies = dataStorePrefs.getCookies().first()
            .mapNotNull { cookieString -> Cookie.parse(getBaseUrl().toHttpUrl(), cookieString) }
        val cookieMap = cookies.associate { it.name to it.value }
        val document = getConnection("${getBaseUrl()}/user/$userId/").cookies(cookieMap).get()
        val title = document.select("input#email").attr("value").toString()
        UserInfoDto(title)
    }

    suspend fun getStreamsBySeasonId(
        translationId: String,
        videoId: String,
        season: String,
        episode: String
    ): StreamDto = withContext(Dispatchers.IO) {
        var streamList = parseSteams(getEncodedString(translationId, videoId, season, episode))
        buildList {
            var isValid = checkValidateUrl(streamList)
            while (!isValid || streamList.isEmpty()) {
                streamList = parseSteams(getEncodedString(translationId, videoId, season, episode))
                isValid = checkValidateUrl(streamList)
            }
            addAll(streamList)
        }.firstOrNull { it.quality == getVideoQuality() } ?: streamList.last()
    }

    suspend fun getVideosByTitle(videoTitle: String): List<VideoItemDto> = withContext(Dispatchers.IO) {
        buildList {
            val document = getConnection("${getBaseUrl()}$SEARCH_URL").data("q", videoTitle).post()
            val element = document.select("div.b-content__inline_item")
            for (i in 0 until element.size) {
                val title = element.select("div.b-content__inline_item-link")
                    .select("a")
                    .eq(i)
                    .text()
                val imageUrl = element.select("img")
                    .eq(i)
                    .attr("src")
                val movieUrl = element.select("div.b-content__inline_item-cover")
                    .select("a")
                    .eq(i)
                    .attr("href")
                val category = element.select("i.entity")
                    .eq(i)
                    .text()
                val movie = VideoItemDto(
                    title = title,
                    category = category,
                    imageUrl = imageUrl,
                    videoUrl = movieUrl
                )
                add(movie)
            }
        }
    }

    suspend fun getStreamsByTranslationId(translatorId: String, filmId: String): StreamDto =
        withContext(Dispatchers.IO) {
            var streamList = parseSteams(getEncodedString(translatorId, filmId))
            buildList {
                var isValid = checkValidateUrl(streamList)
                while (!isValid || streamList.isEmpty()) {
                    streamList = parseSteams(getEncodedString(translatorId, filmId))
                    isValid = checkValidateUrl(streamList)
                }
                addAll(streamList)
            }.firstOrNull { it.quality == getVideoQuality() } ?: streamList.last()
        }

    private fun fetchNewSeriesFromDocument(document: Document): List<NewSeriesModelDto> = buildList {
        val elements = document.select("div.b-videosaves__list_item:not(.watched-row) div.td.info a.new-episode.own")
        for (element in elements) {
            element.parent()?.parent()?.parent()?.let { item ->
                val videoId = item.select("div.td.controls").select("a").attr("data-id")
                val viewDate = item.select("div.td.date").text()
                val title = item.select("div.td.title").text()
                val pageUrl = item.select("a").attr("href")
                val imgUrl = item.select("a").attr("data-cover_url")
                val lastInfo = element.text()
                val model = NewSeriesModelDto(
                    videoId = videoId,
                    viewDate = viewDate,
                    title = title,
                    lastInfo = lastInfo,
                    pageUrl = Uri.parse(pageUrl).path.toString(),
                    posterUrl = imgUrl
                )
                add(model)
            }
        }
    }

    private fun fetchHistoryFromDocument(document: Document): List<VideoHistoryModelDto> = buildList {
        val elements = document.select("div.b-videosaves__list_item div.td.info")
        val seasonEpisodePattern = Pattern.compile("(\\d+) сезон (\\d+) серия")
        val translatorPattern = Pattern.compile("\\(([^)]+)\\)|([^\\s()]+)")
        for (element in elements) {
            element.parent()?.let { item ->
                val dataId = item.select("div.td.controls").select("a").attr("data-id")
                val title = item.select("div.td.title").text()
                val pageUrl = item.select("a").attr("href")
                val imgUrl = item.select("a").attr("data-cover_url")
                val videoId = extractIdFromUrl(pageUrl)

                // Удаляем вложенный элемент span.info-holder
                element.select("span.info-holder").remove()

                // Получаем текст из оставшегося элемента info
                val lastInfoNewSeries = element.text()

                val season: String
                val episode: String
                val seasonEpisodeMatcher = seasonEpisodePattern.matcher(lastInfoNewSeries)
                if (seasonEpisodeMatcher.find()) {
                    season = seasonEpisodeMatcher.group(1) ?: ""
                    episode = seasonEpisodeMatcher.group(2) ?: ""
                } else {
                    season = ""
                    episode = ""
                }

                var translatorName = ""
                val translatorMatcher = translatorPattern.matcher(lastInfoNewSeries)
                while (translatorMatcher.find()) {
                    translatorName = translatorMatcher.group(1) ?: translatorMatcher.group(2) ?: ""
                }
                // Чистим лишние слова из переводов.
                translatorName = translatorName.replace("серия", "").trim()

                val model = VideoHistoryModelDto(
                    videoId = videoId,
                    dataId = dataId,
                    pageUrl = Uri.parse(pageUrl).path.toString(),
                    videoTitle = title,
                    posterUrl = imgUrl,
                    // Если перевод пустой, то это Оригинальная озвучка
                    translatorName = translatorName.ifEmpty { "Оригинальный" },
                    season = season,
                    episode = episode,
                )
                add(model)
            }
        }
    }

    private suspend fun getEncodedString(
        translationId: String,
        videoId: String,
        season: String,
        episode: String
    ): String {
        val data: ArrayMap<String, String> = ArrayMap()
        data["id"] = videoId
        data["translator_id"] = translationId
        data["season"] = season
        data["episode"] = episode
        data["action"] = "get_stream"
        val unixTime = System.currentTimeMillis()
        val result: Document? = getConnection("${getBaseUrl()}$GET_STREAM_POST/?t=$unixTime").data(data).post()
        if (result != null) {
            val bodyString: String = result.select("body").text()
            val jsonObject = JSONObject(bodyString)
            return jsonObject.getString("url")
        }
        return Constants.EMPTY_STRING
    }

    private suspend fun checkValidateUrl(listStreams: List<StreamDto>): Boolean {
        return if (listStreams.isEmpty()) false
        else {
            val selectedStream = listStreams.firstOrNull { it.quality == getVideoQuality() } ?: listStreams.last()
            return Patterns.WEB_URL.matcher(selectedStream.url).matches()
        }
    }

    private suspend fun getEncodedString(translatorId: String, filmId: String): String {
        val data: ArrayMap<String, String> = ArrayMap()
        data["id"] = filmId
        data["translator_id"] = translatorId
        data["action"] = "get_movie"
        val unixTime = System.currentTimeMillis()
        val result: Document? = getConnection("${getBaseUrl()}$GET_STREAM_POST/?t=$unixTime").data(data).post()
        if (result != null) {
            val bodyString: String = result.select("body").text()
            val jsonObject = JSONObject(bodyString)
            if (jsonObject.getBoolean("success")) {
                return jsonObject.getString("url")
            }
        } else {
            error("Видео недоступно")
        }
        return Constants.EMPTY_STRING
    }

    private fun parseSteams(streams: String?): List<StreamDto> = buildList {
        if (streams.isNullOrEmpty()) return emptyList()
        val decodedStreams = decodeUrl(streams)
        val regex = """\[(.*?)](.*?)\.mp4""".toRegex()
        val matches = regex.findAll(decodedStreams)
        for (match in matches) {
            val quality = match.groupValues[1]
            val url = match.groupValues[2] + ".mp4"
            add(StreamDto(url, quality))
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun decodeUrl(str: String): String = runCatching {
        var result = ""
        val chunks = str.drop(2).split("//_//")
        var lengthSalt = chunks.first().length
        chunks.forEach { item ->
            val parts = item.split("=").filter { part -> part.isNotEmpty() }
            val minPart = parts.minOf { it.length }
            if (minPart < lengthSalt) lengthSalt = minPart + 1
        }
        chunks.forEachIndexed { index, item ->
            val parts = item.split("=").filter { part -> part.isNotEmpty() }
            var string = parts[0]
            if (parts.size > 1) {
                parts.forEachIndexed { i, _ ->
                    if (i % 2 != 0)
                        string = parts[i]
                }
            } else if (index > 0) {
                string = string.drop(lengthSalt)
            }
            result += string
        }
        String(Base64.decode(result))
    }.fold(
        { decodedUrl -> decodedUrl },
        { error ->
            logger(error.message.toString())
            Constants.EMPTY_STRING
        }
    )

    private suspend fun getBaseUrl() = dataStorePrefs.getBaseUrl().first()

    private suspend fun getVideoQuality() = dataStorePrefs.getVideoQuality().first().quality

    private fun getConnection(filmUrl: String): Connection = Jsoup
        .connect(filmUrl)
        .ignoreContentType(true)
        .header(APP_HEADER, REQUEST_HEADER_ENABLE_METADATA_VALUE)
        .timeout(CONNECTION_TIMEOUT)
        .followRedirects(true)

    private fun extractIdFromUrl(url: String): String {
        val idPattern = Pattern.compile("/(\\d+)-")
        val matcher = idPattern.matcher(url)
        return if (matcher.find()) {
            matcher.group(1) ?: Constants.EMPTY_STRING
        } else {
            Constants.EMPTY_STRING
        }
    }

    companion object {
        private const val CONNECTION_TIMEOUT = 15_000
        private const val REQUEST_HEADER_ENABLE_METADATA_VALUE = "1"
        private const val APP_HEADER = "X-App-Hdrezka-App"
        private const val GET_STREAM_POST = "/ajax/get_cdn_series"
        private const val SEARCH_URL = "/search/?do=search&subaction=search"
    }
}