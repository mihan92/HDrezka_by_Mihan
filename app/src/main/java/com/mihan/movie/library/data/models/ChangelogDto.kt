package com.mihan.movie.library.data.models

import com.mihan.movie.library.common.Constants
import com.mihan.movie.library.domain.models.ChangelogModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChangelogDto(
    @SerialName("latestVersion") val latestVersion: String = Constants.EMPTY_STRING,
    @SerialName("latestVersionCode") val latestVersionCode: Int = Constants.DEFAULT_INT,
    @SerialName("apkUrl") val apkUrl: String = Constants.EMPTY_STRING,
    @SerialName("releaseNotes") val releaseNotes: List<String> = emptyList()
)

fun ChangelogDto.toChangelogModel() = ChangelogModel(
    latestVersion = latestVersion,
    latestVersionCode = latestVersionCode,
    apkUrl = apkUrl,
    releaseNotes = releaseNotes
)
