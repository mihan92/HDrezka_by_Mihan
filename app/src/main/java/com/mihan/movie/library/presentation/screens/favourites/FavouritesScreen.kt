package com.mihan.movie.library.presentation.screens.favourites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.mihan.movie.library.common.Constants
import com.mihan.movie.library.domain.models.FavouritesModel
import com.mihan.movie.library.presentation.animation.AnimatedScreenTransitions
import com.mihan.movie.library.presentation.screens.destinations.DetailVideoScreenDestination
import com.mihan.movie.library.presentation.ui.size16dp
import com.mihan.movie.library.presentation.ui.size20sp
import com.mihan.movie.library.presentation.ui.size8dp
import com.mihan.movie.library.presentation.ui.view.ButtonDelete
import com.mihan.movie.library.presentation.ui.view.ConfirmDeleteDialog
import com.mihan.movie.library.presentation.ui.view.EmptyListPlaceholder
import com.mihan.movie.library.presentation.ui.view.PosterView
import com.mihan.movie.library.presentation.ui.view.RectangleButton
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

private const val SELECTED_BACKGROUND_ALPHA = 0.1f

@Destination(style = AnimatedScreenTransitions::class)
@Composable
fun FavouritesScreen(
    favouritesViewModel: FavouritesViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val favouritesList by favouritesViewModel.favouritesList.collectAsStateWithLifecycle()
    val baseUrl by favouritesViewModel.baseUrl.collectAsStateWithLifecycle()
    var deleteDialogState by rememberSaveable { mutableStateOf(false) }
    var videoId by remember { mutableStateOf(Constants.EMPTY_STRING) }
    if (favouritesList.isEmpty()) EmptyListPlaceholder(text = stringResource(id = R.string.favourites_screen_placeholder))
    Content(
        favouritesList = favouritesList,
        onButtonWatchClicked = { navigator.navigate(DetailVideoScreenDestination("$baseUrl${it.videoPageUrl}")) },
        onItemDeleteClicked = { id ->
            videoId = id
            deleteDialogState = true
        }
    )
    ConfirmDeleteDialog(
        showDialogState = deleteDialogState,
        onButtonYesPressed = {
            deleteDialogState = false
            if (videoId.isNotEmpty()) favouritesViewModel.onButtonDeletePressed(videoId)
        },
        onButtonNoPressed = { deleteDialogState = false },
    )
}

@Composable
private fun Content(
    favouritesList: List<FavouritesModel>,
    onButtonWatchClicked: (FavouritesModel) -> Unit,
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
        items(favouritesList) { item ->
            FavouritesItem(
                favouritesModel = item,
                onButtonWatchClicked = onButtonWatchClicked,
                onItemDeleteClicked = onItemDeleteClicked
            )
        }
    }
    LaunchedEffect(key1 = favouritesList) {
        if (favouritesList.isNotEmpty()) {
            runCatching {
                focusRequester.requestFocus()
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun FavouritesItem(
    favouritesModel: FavouritesModel,
    onButtonWatchClicked: (FavouritesModel) -> Unit,
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
        PosterView(
            imageUrl = favouritesModel.posterUrl,
            contentDescription = favouritesModel.videoTitle
        )
        Column(modifier = modifier.fillMaxWidth(0.95f)) {
            Text(
                text = favouritesModel.videoTitle,
                fontSize = size20sp,
                fontWeight = FontWeight.W700,
                modifier = modifier.padding(start = size16dp)
            )
            RectangleButton(
                text = stringResource(id = R.string.bt_watch).uppercase(),
                onButtonClicked = { onButtonWatchClicked(favouritesModel) },
                isFocused = { isFocused = it }
            )
        }
        ButtonDelete(
            onButtonDeleteClicked = { onItemDeleteClicked(favouritesModel.videoId) },
            isFocused = { isFocused = it }
        )
    }
}