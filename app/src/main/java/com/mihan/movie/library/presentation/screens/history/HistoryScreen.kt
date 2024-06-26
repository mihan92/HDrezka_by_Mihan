package com.mihan.movie.library.presentation.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import androidx.tv.foundation.lazy.list.rememberTvLazyListState
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.mihan.movie.library.R
import com.mihan.movie.library.domain.models.VideoHistoryModel
import com.mihan.movie.library.presentation.animation.AnimatedScreenTransitions
import com.mihan.movie.library.presentation.screens.destinations.DetailVideoScreenDestination
import com.mihan.movie.library.presentation.ui.size10dp
import com.mihan.movie.library.presentation.ui.size16dp
import com.mihan.movie.library.presentation.ui.size20sp
import com.mihan.movie.library.presentation.ui.size8dp
import com.mihan.movie.library.presentation.ui.view.ButtonDelete
import com.mihan.movie.library.presentation.ui.view.EmptyListPlaceholder
import com.mihan.movie.library.presentation.ui.view.PosterView
import com.mihan.movie.library.presentation.ui.view.RectangleButton
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

private const val SELECTED_BACKGROUND_ALPHA = 0.1f

@OptIn(ExperimentalTvMaterial3Api::class)
@Destination(style = AnimatedScreenTransitions::class)
@Composable
fun HistoryScreen(
    historyViewModel: HistoryViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val historyScreenState by historyViewModel.historyScreenState.collectAsStateWithLifecycle()
    val baseUrl by historyViewModel.baseUrl.collectAsStateWithLifecycle()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (historyScreenState.data.isEmpty() && !historyScreenState.isLoading)
            EmptyListPlaceholder(text = stringResource(id = R.string.history_screen_placeholder))
        Content(
            historyList = historyScreenState.data,
            onButtonWatchClicked = { navigator.navigate(DetailVideoScreenDestination("$baseUrl${it.videoPageUrl}")) },
            onItemDeleteClicked = historyViewModel::onButtonDeleteClicked
        )
        if (historyScreenState.isLoading) CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }

    LaunchedEffect(key1 = Unit) {
        historyViewModel.getHistoryList()
    }
}

@Composable
private fun Content(
    historyList: List<VideoHistoryModel>,
    onButtonWatchClicked: (VideoHistoryModel) -> Unit,
    onItemDeleteClicked: (VideoHistoryModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val state = rememberTvLazyListState()
    val focusRequester = remember { FocusRequester() }
    TvLazyColumn(
        state = state,
        modifier = modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
    ) {
        items(historyList) { item ->
            HistoryItem(
                videoHistoryModel = item,
                onButtonWatchClicked = onButtonWatchClicked,
                onItemDeleteClicked = onItemDeleteClicked
            )
        }
    }
    LaunchedEffect(key1 = historyList) {
        if (historyList.isNotEmpty())
            focusRequester.requestFocus()
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun HistoryItem(
    videoHistoryModel: VideoHistoryModel,
    onButtonWatchClicked: (VideoHistoryModel) -> Unit,
    onItemDeleteClicked: (VideoHistoryModel) -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val backgroundColor = if (isFocused) MaterialTheme.colorScheme.onBackground.copy(SELECTED_BACKGROUND_ALPHA)
    else MaterialTheme.colorScheme.background
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(size8dp))
            .padding(size16dp)
    ) {
        PosterView(
            imageUrl = videoHistoryModel.posterUrl,
            contentDescription = videoHistoryModel.videoTitle
        )
        Column(modifier = modifier.fillMaxWidth(0.95f)) {
            Text(
                text = videoHistoryModel.videoTitle,
                fontSize = size20sp,
                fontWeight = FontWeight.W700,
                modifier = modifier.padding(start = size16dp)
            )
            if (videoHistoryModel.episode.isNotEmpty() && videoHistoryModel.season.isNotEmpty())
                SeasonAndEpisodeTitle(videoHistoryModel)
            RectangleButton(
                text = stringResource(id = R.string.bt_watch).uppercase(),
                onButtonClicked = { onButtonWatchClicked(videoHistoryModel) },
                isFocused = { isFocused = it }
            )
        }
        ButtonDelete(
            onButtonDeleteClicked = { onItemDeleteClicked(videoHistoryModel) },
            isFocused = { isFocused = it }
        )
    }
}

@Composable
private fun SeasonAndEpisodeTitle(
    videoHistoryModel: VideoHistoryModel,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.padding(start = size16dp, top = size16dp)) {
        Text(
            text = stringResource(id = R.string.season_title, videoHistoryModel.season),
            modifier = modifier.padding(end = size10dp)
        )
        Text(text = stringResource(id = R.string.episode_title, videoHistoryModel.episode))
    }
}