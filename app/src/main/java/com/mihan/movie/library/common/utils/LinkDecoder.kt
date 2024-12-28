package com.mihan.movie.library.common.utils

import android.util.Base64
import com.mihan.movie.library.data.models.StreamDto

object LinkDecoder {

    private val garbage = listOf(
        "@@", "@#", "@!", "@^", "@$", "#@", "##", "#!", "#^", "#$", "!@", "!#", "!!", "!^", "!$",
        "^@", "^#", "^!", "^^", "^$", "$@", "$#", "$!", "$^", "$$", "@@@", "@@#", "@@!", "@@^", "@@$",
        "@#@", "@##", "@#!", "@#^", "@#$", "@!@", "@!#", "@!!", "@!^", "@!$", "@^@", "@^#", "@^!",
        "@^^", "@^$", "@$@", "@$#", "@$!", "@$^", "@$$", "#@@", "#@#", "#@!", "#@^", "#@$", "##@",
        "###", "##!", "##^", "##$", "#!@", "#!#", "#!!", "#!^", "#!$", "#^@", "#^#", "#^!", "#^^",
        "#^$", "#$@", "#$#", "#$!", "#$^", "#$$", "!@@", "!@#", "!@!", "!@^", "!@$", "!#@", "!##",
        "!#!", "!#^", "!#$", "!!@", "!!#", "!!!", "!!^", "!!$", "!^@", "!^#", "!^!", "!^^", "!^$",
        "!$@", "!$#", "!$!", "!$^", "!$$", "^@@", "^@#", "^@!", "^@^", "^@$", "^#@", "^##", "^#!",
        "^#^", "^#$", "^!@", "^!#", "^!!", "^!^", "^!$", "^^@", "^^#", "^^!", "^^^", "^^$", "^$@",
        "^$#", "^$!", "^$^", "^$$", "$@@", "$@#", "$@!", "$@^", "$@$", "$#@", "$##", "$#!", "$#^",
        "$#$", "$!@", "$!#", "$!!", "$!^", "$!$", "$^@", "$^#", "$^!", "$^^", "$^$", "$$@", "$$#",
        "$$!", "$$^", "$$$"
    )

    /**
     * Публичная функция, которая принимает закодированные данные
     * и возвращает список объектов StreamDto.
     */
    fun getDecodedLinks(encodedData: String): List<StreamDto> {
        val decodedString = decode(encodedData)

        return decodedString.split(",").mapNotNull { group ->
            val quality = Regex("\\[.*]").find(group)?.value?.removeSurrounding("[", "]") ?: return@mapNotNull null
            group.substringAfter("] ").split(" or ").mapNotNull { url ->
                if (url.contains("hls")) {
                    val cleanedUrl = url.substringAfter("]").substringBefore(":manifest.m3u8") // Удаляем "[качество]" и ":manifest.m3u8"
                    StreamDto(url = cleanedUrl, quality = quality)
                } else {
                    null
                }
            }
        }.flatten()
    }

    /**
     * Приватная функция для очистки и раскодирования данных.
     */
    private fun decode(data: String): String {
        var clearData = data
            .replace("#h", "") // Удаляем из начала строки #h
            .replace("//_//", "") // Убираем символы //_//

        garbage.forEach { item ->
            val encodedGarbage = Base64.encodeToString(item.toByteArray(), Base64.NO_WRAP)
            clearData = clearData.replace(encodedGarbage, "")
        }

        return String(Base64.decode(clearData, Base64.DEFAULT))
    }
}