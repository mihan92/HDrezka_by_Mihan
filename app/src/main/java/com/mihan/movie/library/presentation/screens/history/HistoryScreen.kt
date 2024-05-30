package com.mihan.movie.library.presentation.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import androidx.tv.foundation.lazy.list.rememberTvLazyListState
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.SubcomposeAsyncImage
import com.mihan.movie.library.R
import com.mihan.movie.library.domain.models.VideoHistoryModel
import com.mihan.movie.library.presentation.animation.AnimatedScreenTransitions
import com.mihan.movie.library.presentation.screens.destinations.DetailVideoScreenDestination
import com.mihan.movie.library.presentation.ui.size100dp
import com.mihan.movie.library.presentation.ui.size10dp
import com.mihan.movie.library.presentation.ui.size16dp
import com.mihan.movie.library.presentation.ui.size18sp
import com.mihan.movie.library.presentation.ui.size20sp
import com.mihan.movie.library.presentation.ui.size8dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

private const val POSTER_ASPECT_RATIO = 1 / 1.4f
private const val SELECTED_BACKGROUND_ALPHA = 0.1f

@Destination(style = AnimatedScreenTransitions::class)
@Composable
fun HistoryScreen(
    historyViewModel: HistoryViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val historyList by historyViewModel.historyList.collectAsStateWithLifecycle()
    val baseUrl by historyViewModel.baseUrl.collectAsStateWithLifecycle()
    if (historyList.isEmpty()) EmptyListPlaceholder()
    Content(
        historyList = historyList,
        onButtonWatchClicked = { navigator.navigate(DetailVideoScreenDestination("$baseUrl${it.videoPageUrl}")) },
        onItemDeleteClicked = historyViewModel::onButtonDeleteClicked
    )
}

@Composable
private fun Content(
    historyList: List<VideoHistoryModel>,
    onButtonWatchClicked: (VideoHistoryModel) -> Unit,
    onItemDeleteClicked: (String) -> Unit,
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
    onItemDeleteClicked: (String) -> Unit,
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
        SubcomposeAsyncImage(
            model = videoHistoryModel.posterUrl,
            loading = { CircularProgressIndicator() },
            contentDescription = videoHistoryModel.videoTitle,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .clip(RoundedCornerShape(size8dp))
                .width(size100dp)
                .aspectRatio(POSTER_ASPECT_RATIO),
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
            ButtonWatch(
                onButtonWatchClicked = { onButtonWatchClicked(videoHistoryModel) },
                isFocused = { isFocused = it }
            )
        }
        ButtonDelete(
            onButtonDeleteClicked = { onItemDeleteClicked(videoHistoryModel.videoId) },
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

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun ButtonWatch(
    onButtonWatchClicked: () -> Unit,
    isFocused: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onButtonWatchClicked,
        colors = ButtonDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.primary,
            focusedContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = ButtonDefaults.shape(RoundedCornerShape(size8dp)),
        modifier = modifier
            .padding(size16dp)
            .onFocusChanged { isFocused(it.isFocused) }
    ) {
        Text(
            text = stringResource(id = R.string.bt_watch).uppercase(),
            fontWeight = FontWeight.W700
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun ButtonDelete(
    isFocused: (Boolean) -> Unit,
    onButtonDeleteClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onButtonDeleteClicked,
        colors = ButtonDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.primary,
            focusedContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = modifier.onFocusChanged { isFocused(it.isFocused) }
    ) {
        Icon(
            imageVector = Icons.Rounded.Delete,
            contentDescription = null,
        )
    }
}


@Composable
private fun EmptyListPlaceholder() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.history_screen_placeholder),
            fontSize = size18sp
        )
    }
}