package com.mihan.movie.library.presentation.screens.favourites

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
import com.mihan.movie.library.domain.models.FavouritesModel
import com.mihan.movie.library.presentation.animation.AnimatedScreenTransitions
import com.mihan.movie.library.presentation.screens.destinations.DetailVideoScreenDestination
import com.mihan.movie.library.presentation.ui.size100dp
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
fun FavouritesScreen(
    favouritesViewModel: FavouritesViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val favouritesList by favouritesViewModel.favouritesList.collectAsStateWithLifecycle()
    val baseUrl by favouritesViewModel.baseUrl.collectAsStateWithLifecycle()
    if (favouritesList.isEmpty()) EmptyListPlaceholder()
    Content(
        favouritesList = favouritesList,
        onButtonWatchClicked = { navigator.navigate(DetailVideoScreenDestination("$baseUrl${it.videoPageUrl}")) },
        onItemDeleteClicked = favouritesViewModel::onButtonDeletePressed
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
        if (favouritesList.isNotEmpty())
            focusRequester.requestFocus()
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
        SubcomposeAsyncImage(
            model = favouritesModel.posterUrl,
            loading = { CircularProgressIndicator() },
            contentDescription = favouritesModel.videoTitle,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .clip(RoundedCornerShape(size8dp))
                .width(size100dp)
                .aspectRatio(POSTER_ASPECT_RATIO),
        )
        Column(modifier = modifier.fillMaxWidth(0.95f)) {
            Text(
                text = favouritesModel.videoTitle,
                fontSize = size20sp,
                fontWeight = FontWeight.W700,
                modifier = modifier.padding(start = size16dp)
            )
            ButtonWatch(
                onButtonWatchClicked = { onButtonWatchClicked(favouritesModel) },
                isFocused = { isFocused = it }
            )
        }
        ButtonDelete(
            onButtonDeleteClicked = { onItemDeleteClicked(favouritesModel.videoId) },
            isFocused = { isFocused = it }
        )
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
            text = stringResource(id = R.string.favourites_screen_placeholder),
            fontSize = size18sp
        )
    }
}