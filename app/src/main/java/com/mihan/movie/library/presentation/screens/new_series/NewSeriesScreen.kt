package com.mihan.movie.library.presentation.screens.new_series

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
import com.mihan.movie.library.domain.models.NewSeriesModel
import com.mihan.movie.library.presentation.animation.AnimatedScreenTransitions
import com.mihan.movie.library.presentation.screens.destinations.DetailVideoScreenDestination
import com.mihan.movie.library.presentation.ui.size14sp
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
fun NewSeriesScreen(
    viewModel: NewSeriesViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    val baseUrl by viewModel.baseUrl.collectAsStateWithLifecycle()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (screenState.data.isEmpty() && !screenState.isLoading)
            EmptyListPlaceholder(text = stringResource(R.string.new_series_screen_placeholder))
        Content(
            seriesList = screenState.data,
            onButtonWatchClicked = { navigator.navigate(DetailVideoScreenDestination("$baseUrl${it.pageUrl}")) },
            onButtonDeleteClicked = { viewModel.onButtonDeleteClicked(it.dataId) }
        )
        if (screenState.isLoading) CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
    LaunchedEffect(key1 = Unit) {
        viewModel.getNewSeries()
    }
}

@Composable
private fun Content(
    seriesList: List<NewSeriesModel>,
    onButtonWatchClicked: (NewSeriesModel) -> Unit,
    onButtonDeleteClicked: (NewSeriesModel) -> Unit,
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
        items(seriesList) { item ->
            RemoteHistoryItem(
                newSeriesModel = item,
                onButtonWatchClicked = onButtonWatchClicked,
                onItemDeleteClicked = onButtonDeleteClicked
            )
        }
    }
    LaunchedEffect(key1 = seriesList) {
        if (seriesList.isNotEmpty()) {
            runCatching {
                focusRequester.requestFocus()
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun RemoteHistoryItem(
    newSeriesModel: NewSeriesModel,
    onButtonWatchClicked: (NewSeriesModel) -> Unit,
    onItemDeleteClicked: (NewSeriesModel) -> Unit,
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
            imageUrl = newSeriesModel.posterUrl,
            contentDescription = newSeriesModel.title
        )
        Column(modifier = modifier.fillMaxWidth(0.95f)) {
            Text(
                text = newSeriesModel.title,
                fontSize = size20sp,
                fontWeight = FontWeight.W700,
                modifier = modifier.padding(start = size16dp)
            )
            Text(
                text = newSeriesModel.lastInfo,
                fontSize = size14sp,
                fontWeight = FontWeight.W500,
                modifier = modifier.padding(size16dp)
            )
            RectangleButton(
                text = stringResource(id = R.string.bt_watch).uppercase(),
                onButtonClicked = { onButtonWatchClicked(newSeriesModel) },
                isFocused = { isFocused = it }
            )
        }
        ButtonDelete(
            isFocused = { isFocused = it },
            onButtonDeleteClicked = { onItemDeleteClicked(newSeriesModel) },
            modifier = modifier
        )
    }
}
