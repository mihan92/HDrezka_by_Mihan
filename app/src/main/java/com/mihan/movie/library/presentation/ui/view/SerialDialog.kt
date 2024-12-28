package com.mihan.movie.library.presentation.ui.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.tv.material3.MaterialTheme
import com.mihan.movie.library.R
import com.mihan.movie.library.domain.models.SerialModel
import com.mihan.movie.library.domain.models.VideoHistoryModel
import com.mihan.movie.library.presentation.ui.size10dp
import com.mihan.movie.library.presentation.ui.size14sp
import com.mihan.movie.library.presentation.ui.size16dp
import com.mihan.movie.library.presentation.ui.size16sp
import com.mihan.movie.library.presentation.ui.size18dp
import com.mihan.movie.library.presentation.ui.size28dp
import com.mihan.movie.library.presentation.ui.size2dp
import com.mihan.movie.library.presentation.ui.size4dp
import com.mihan.movie.library.presentation.ui.size8dp
import com.mihan.movie.library.presentation.ui.theme.dialogBgColor

private val DIALOG_WIDTH = 350.dp
private val DIALOG_HEIGHT = 300.dp

@Composable
fun SerialDialog(
    isDialogShow: State<Boolean>,
    videoHistoryModel: VideoHistoryModel?,
    translations: Map<String, String>,
    seasonsWithEpisodes: List<SerialModel>,  // Передаем уже обновленную модель
    onTranslationItemClicked: (String) -> Unit,
    onEpisodeClicked: (String, String) -> Unit,
    onDialogDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isDialogShow.value) {
        Dialog(onDismissRequest = onDialogDismiss) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier
                    .clip(RoundedCornerShape(size10dp))
                    .background(dialogBgColor)
                    .padding(vertical = size8dp)
                    .width(DIALOG_WIDTH)
                    .height(DIALOG_HEIGHT)
            ) {
                ExpandableTranslationList(
                    translations = translations.keys.toList(),
                    videoHistoryModel = videoHistoryModel,
                    onTranslationItemClicked = onTranslationItemClicked
                )
                ExpandableSeasonList(
                    seasons = seasonsWithEpisodes,
                    videoHistoryModel = videoHistoryModel,
                    onEpisodeClicked = onEpisodeClicked
                )
            }
        }
    }
}

@Composable
private fun ExpandableTranslationList(
    translations: List<String>,
    videoHistoryModel: VideoHistoryModel?,
    onTranslationItemClicked: (String) -> Unit,
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    var selectedTranslator by rememberSaveable {
        mutableStateOf(videoHistoryModel?.translatorName ?: translations.first())
    }
    Column {
        TextField(
            value = selectedTranslator,
            textStyle = TextStyle(fontSize = size16sp),
            onValueChange = {},
            readOnly = true,
            label = {
                Text(
                    text = stringResource(id = R.string.voicecover_title),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = size14sp
                )
            },
            trailingIcon = { ExpandableIcon(isExpanded = isExpanded) },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
        )
        if (isExpanded) {
            LazyColumn {
                items(translations) { translateItem ->
                    DialogText(
                        title = translateItem,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedTranslator = translateItem
                                onTranslationItemClicked(translateItem)
                                isExpanded = false
                            }
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpandableSeasonList(
    seasons: List<SerialModel>,
    videoHistoryModel: VideoHistoryModel?,
    onEpisodeClicked: (String, String) -> Unit
) {
    // Стейт для отслеживания открытого сезона
    var expandedSeason by remember { mutableStateOf<String?>(null) }

    // Сохраняем состояние выбранного эпизода для каждого сезона
    val seasonEpisodesState = remember { mutableStateMapOf<String, String?>() }

    LazyColumn {
        items(seasons) { seasonWithEpisodes ->
            // Проверяем, была ли хотя бы одна серия просмотрена в этом сезоне
            val isSeasonExpanded = expandedSeason == seasonWithEpisodes.season ||
                    videoHistoryModel?.season == seasonWithEpisodes.season

            // Проверяем, какой эпизод был просмотрен для текущего сезона
            val selectedEpisode = seasonEpisodesState[seasonWithEpisodes.season]
                ?: videoHistoryModel?.takeIf { it.season == seasonWithEpisodes.season }?.episode

            var isExpanded by remember { mutableStateOf(isSeasonExpanded) }

            SectionHeader(
                seasonTitle = seasonWithEpisodes.season,
                isExpanded = isExpanded,
                onHeaderClicked = {
                    // Если сезон еще не был открыт, открываем его, иначе закрываем
                    expandedSeason = if (isExpanded) null else seasonWithEpisodes.season
                    isExpanded = !isExpanded
                }
            )

            if (isExpanded) {
                seasonWithEpisodes.episodes.forEach { episode ->
                    DialogTextWithLoading(
                        title = episode.title,
                        isLoading = episode.isLoading,
                        selected = episode.title == selectedEpisode,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Обновляем состояние выбранного эпизода для текущего сезона
                                seasonEpisodesState[seasonWithEpisodes.season] = episode.title
                                onEpisodeClicked(seasonWithEpisodes.season, episode.title)
                            }
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    seasonTitle: String,
    isExpanded: Boolean,
    onHeaderClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onHeaderClicked() }
            .background(Color.DarkGray)
            .padding(vertical = size4dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DialogText(title = stringResource(id = R.string.season_title, seasonTitle))
        ExpandableIcon(
            isExpanded = isExpanded,
            modifier = Modifier.padding(end = size10dp)
        )
    }
}

@Composable
private fun DialogTextWithLoading(
    title: String,
    isLoading: Boolean,
    selected: Boolean = false,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    Row(
        modifier = modifier
            .padding(vertical = size8dp, horizontal = size16dp)
            .focusRequester(focusRequester)
            .focusable()
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Текст серии
        Text(
            text = "Серия $title",
            fontSize = size16sp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
        )
        // Индикатор загрузки
        AnimatedVisibility(isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(size18dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = size2dp
            )
        }
        LaunchedEffect(key1 = Unit) {
            if (selected) {
                runCatching {
                    focusRequester.requestFocus()
                }
            }
        }
    }
}

@Composable
private fun ExpandableIcon(
    isExpanded: Boolean,
    modifier: Modifier = Modifier
) {
    val icon = if (isExpanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown
    Image(
        modifier = modifier.size(size28dp),
        imageVector = icon,
        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground),
        contentDescription = null
    )
}

@Composable
private fun DialogText(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        fontSize = size16sp,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier
            .padding(vertical = size8dp, horizontal = size16dp)
            .focusable()
    )
}