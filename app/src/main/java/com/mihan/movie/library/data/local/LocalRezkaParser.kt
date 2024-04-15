package com.mihan.movie.library.data.local

import android.util.ArrayMap
import android.util.Patterns
import com.mihan.movie.library.common.Constants
import com.mihan.movie.library.common.DataStorePrefs
import com.mihan.movie.library.common.entites.Filter
import com.mihan.movie.library.common.entites.VideoCategory
import com.mihan.movie.library.common.extentions.logger
import com.mihan.movie.library.data.models.SeasonModelDto
import com.mihan.movie.library.data.models.StreamDto
import com.mihan.movie.library.data.models.VideoDetailDto
import com.mihan.movie.library.data.models.VideoDto
import com.mihan.movie.library.data.models.VideoItemDto
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@ActivityRetainedScoped
class LocalRezkaParser @Inject constructor(
    private val dataStorePrefs: DataStorePrefs
) : CoroutineScope {
    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.IO
    private var filmId: String = Constants.EMPTY_STRING
    private suspend fun getBaseUrl() = dataStorePrefs.getBaseUrl().first()

    /**
     * Кэшируем список фильмов после первой загрузки, чтобы при каждом обращении не качать его снова.
     * Если текущие данные совпадают, то отдаем лист с кэшированными данными, если нет, качаем порцию фильмов снова.
     */
    private val cashedListVideo = mutableListOf<VideoItemDto>()
    private var currentPage = -1
    private var currentFilter = Filter.Watching
    private var currentVideoCategory = VideoCategory.All
    private var currentBaseUrl = Constants.EMPTY_STRING

    private fun getConnection(filmUrl: String): Connection = Jsoup
        .connect(filmUrl)
        .ignoreContentType(true)
        .header(APP_HEADER, REQUEST_HEADER_ENABLE_METADATA_VALUE)
        .timeout(CONNECTION_TIMEOUT)
        .followRedirects(true)

    private suspend fun getUrl(page: Int, section: String, genre: String): String {
        return if (page > 1)
            "${getBaseUrl()}/page/$page/?filter=$section$genre"
        else
            "${getBaseUrl()}/?filter=$section$genre"
    }

    /**
     * Из главной страницы получаем список видосов
     */
    suspend fun getListVideo(
        filter: Filter,
        videoCategory: VideoCategory,
        page: Int,
    ): List<VideoItemDto> =
        withContext(Dispatchers.IO) {
            if (currentFilter == filter
                && currentVideoCategory == videoCategory
                && currentPage == page
                && getBaseUrl() == currentBaseUrl
                && cashedListVideo.isNotEmpty()
            ) {
                cashedListVideo.toList()
            } else {
                cashedListVideo.clear()
                val document = getConnection(getUrl(page, filter.section, videoCategory.genre)).get()
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
                    cashedListVideo.add(movie)
                }
                currentFilter = filter
                currentVideoCategory = videoCategory
                currentPage = page
                currentBaseUrl = getBaseUrl()
                cashedListVideo.toList()
            }
        }

    /**
     * Получаем всю детальную инфу о видосе и выводим на экране детальной информации
     */
    suspend fun getDetailVideoByUrl(url: String): VideoDetailDto = withContext(Dispatchers.IO) {
        val document = getConnection(url).get()
        filmId = document.select("div.b-userset__fav_holder").attr("data-post_id")
        val element = document.select("div.b-post")
        val title = element.select("div.b-post__title").text()
        val desc = element.select("div.b-post__description_text").text()
        val ratingHdRezka = element.select("span.num").text() + element.select("average").text()
        val imageUrl = element.select("div.b-sidecover a").attr("href")
        val table = element.select("table.b-post__info").select("td")
        val errorMessage = element.select("span.b-player__restricted__block_message")
            .text()
            .substringBefore("Подробнее")

        val actors = table.last()?.text()
            ?.substringAfter(":")
            ?.trim()
            ?.split(",")
            ?.take(5)
            ?.joinToString()

        VideoDetailDto(
            videoId = filmId,
            title = title,
            description = desc,
            releaseDate = getTableValueByName(table, "дата выхода"),
            country = getTableValueByName(table, "страна"),
            ratingIMDb = getRatingByName(table.text(), "IMDb"),
            ratingKp = getRatingByName(table.text(), "Кинопоиск"),
            ratingHdrezka = ratingHdRezka,
            genre = getTableValueByName(table, "жанр"),
            actors = actors ?: Constants.EMPTY_STRING,
            imageUrl = imageUrl,
            errorMessage = errorMessage
        )
    }

    private fun getRatingByName(searchingString: String, searchingText: String): String {
        var value = ""
        val index = searchingString.indexOf(searchingText)
        if (index != -1) {
            val startIndex = index + searchingText.length + 2
            value = searchingString.substring(startIndex, startIndex + 3)
        }
        return value
    }

    private fun getTableValueByName(table: Elements, searchingText: String): String {
        var value = ""
        table.forEachIndexed { index, elem ->
            if (elem.text().contains(searchingText, true))
                value = table[index + 1].text()
        }
        return value
    }

    /**
     * Проходим по странице и ищем переводы, если переводы не найдены, значит это оригинальная озвучка без переводов.
     * Добавляем ее вручную
     */
    private fun getTranslations(document: Document): Map<String, String> {
        val mapOfTranslations = mutableMapOf<String, String>()
        val translators = document.select(".b-translator__item")
        for (element in translators) {
            if (element.hasClass("b-prem_translator")) continue
            val country = element.select("img").attr("alt")
            var name = element.attr("title")
            val translatorId = element.attr("data-translator_id")
            if (country.isNotEmpty()) {
                name += "($country)"
            }
            mapOfTranslations[name] = translatorId
        }
        if (mapOfTranslations.isEmpty()) {
            val translator = document
                .select("script")
                .map(Element::data)
                .firstOrNull { "sof.tv" in it }
                ?.split(",")
                ?.map(String::trim)
                ?.take(2)
                ?.last() ?: error("Ожидаем в хорошем качестве...")
            mapOfTranslations[RUSSIAN_TRANSLATOR_NAME] = translator
        }
        return mapOfTranslations.toMap()
    }

    suspend fun getTranslationsByUrl(url: String): VideoDto = withContext(Dispatchers.IO) {
        val document = getConnection(url).get()
        val isVideoHasTranslation = document.select("div.b-translators__block").hasText()
        val isVideoHasSeries = document.select("ul.b-simple_episodes__list").hasText()
        val translations = getTranslations(document)
        VideoDto(
            videoId = filmId,
            isVideoHasTranslations = isVideoHasTranslation,
            isVideoHasSeries = isVideoHasSeries,
            translations = translations
        )
    }

    suspend fun getStreamsBySeasonId(
        translationId: String,
        videoId: String,
        season: String,
        episode: String
    ): List<StreamDto> = withContext(Dispatchers.IO) {
        val list = mutableListOf<StreamDto>()
        var streams = parseSteams(getEncodedString(translationId, videoId, season, episode))
        var isValid = checkValidateUrl(streams)
        while (!isValid || streams.isEmpty()) {
            streams = parseSteams(getEncodedString(translationId, videoId, season, episode))
            isValid = checkValidateUrl(streams)
        }
        list.addAll(streams)

        list.toList()
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

    suspend fun getVideosByTitle(videoTitle: String): List<VideoItemDto> = withContext(Dispatchers.IO) {
        val list = mutableListOf<VideoItemDto>()
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
            list.add(movie)
        }
        list.toList()
    }

    suspend fun getSeasonsByTranslatorId(translatorId: String): List<SeasonModelDto> =
        withContext(Dispatchers.IO) {
            val list = mutableListOf<SeasonModelDto>()
            val data: ArrayMap<String, String> = ArrayMap()
            data["id"] = filmId
            data["translator_id"] = translatorId
            data["action"] = "get_episodes"
            val unixTime = System.currentTimeMillis()
            val result: Document? = getConnection("${getBaseUrl()}$GET_STREAM_POST/?t=$unixTime").data(data).post()
            if (result != null) {
                val bodyString: String = result.select("body").text()
                val jsonObject = JSONObject(bodyString)
                if (jsonObject.getBoolean("success")) {
                    val map = parseSeasons(Jsoup.parse(jsonObject.getString("episodes")))
                    map.entries.forEach {
                        list.add(SeasonModelDto(it.key, it.value))
                    }
                }
            } else {
                error("result is null")
            }
            list.toList()
        }

    private suspend fun checkValidateUrl(listStreams: List<StreamDto>): Boolean {
        return if (listStreams.isEmpty()) false
        else {
            val selectedQuality = dataStorePrefs.getVideoQuality().first()
            val selectedStream = listStreams.firstOrNull { it.quality == selectedQuality.quality } ?: listStreams.last()
            return Patterns.WEB_URL.matcher(selectedStream.url).matches()
        }
    }

    suspend fun getStreamsByTranslationId(translatorId: String): List<StreamDto> =
        withContext(Dispatchers.IO) {
            val listOfStreams = mutableListOf<StreamDto>()
            var streams = parseSteams(getEncodedString(translatorId))
            var isValid = checkValidateUrl(streams)
            while (!isValid || streams.isEmpty()) {
                streams = parseSteams(getEncodedString(translatorId))
                isValid = checkValidateUrl(streams)
            }
            listOfStreams.addAll(streams)

            listOfStreams.toList()
        }

    private suspend fun getEncodedString(translatorId: String): String {
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

    private fun parseSteams(streams: String?): List<StreamDto> {
        val parsedStreams = mutableListOf<StreamDto>()
        if (!streams.isNullOrEmpty()) {
            val decodedStreams = decodeUrl(streams)
            val split: Array<String> = decodedStreams.split(",").toTypedArray()
            for (str in split) {
                try {
                    if (str.contains(" or ")) {
                        parsedStreams.add(
                            StreamDto(
                                str.split(" or ").toTypedArray()[1],
                                str.substring(1, str.indexOf("]"))
                            )
                        )
                    } else {
                        parsedStreams.add(
                            StreamDto(
                                str.substring(str.indexOf("]") + 1),
                                str.substring(1, str.indexOf("]"))
                            )
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    return emptyList()
                }
            }
        }
        return parsedStreams
    }

    private fun parseSeasons(document: Document): Map<String, List<String>> {
        val seasonList = mutableMapOf<String, List<String>>()
        val seasons = document.select("ul.b-simple_episodes__list")
        for (season in seasons) {
            val n = season.attr("id").replace("simple-episodes-list-", "")
            val episodesList: ArrayList<String> = ArrayList()
            val episodes = season.select("li.b-simple_episode__item")
            for (episode in episodes) {
                episodesList.add(episode.attr("data-episode_id"))
            }
            seasonList[n] = episodesList
        }
        return seasonList.toMap()
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun decodeUrl(str: String): String {
        return try {
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
        } catch (e: Exception) {
            e.printStackTrace()
            str
        }
    }

    companion object {
        private const val CONNECTION_TIMEOUT = 15_000
        private const val REQUEST_HEADER_ENABLE_METADATA_VALUE = "1"
        private const val APP_HEADER = "X-App-Hdrezka-App"
        private const val GET_STREAM_POST = "/ajax/get_cdn_series"
        private const val RUSSIAN_TRANSLATOR_NAME = "Оригинальный"
        private const val SEARCH_URL = "/search/?do=search&subaction=search"
    }
}