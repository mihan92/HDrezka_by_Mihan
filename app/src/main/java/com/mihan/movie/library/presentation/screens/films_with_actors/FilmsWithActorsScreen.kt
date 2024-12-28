package com.mihan.movie.library.presentation.screens.films_with_actors

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.mihan.movie.library.R
import com.mihan.movie.library.domain.models.VideoItemModel
import com.mihan.movie.library.presentation.navigation.AppNavGraph
import com.mihan.movie.library.presentation.ui.size10dp
import com.mihan.movie.library.presentation.ui.size16dp
import com.mihan.movie.library.presentation.ui.size18sp
import com.mihan.movie.library.presentation.ui.view.EmptyListPlaceholder
import com.mihan.movie.library.presentation.ui.view.MovieItem
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.generated.destinations.DetailVideoScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

private const val numberOfGridCells = 6

@Destination<AppNavGraph>(navArgs = FilmsWithActorsNavArgs::class)
@Composable
fun FilmsWithActorsScreen(
    viewModel: FilmsWithActorsViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val screenState by viewModel.filmsWithActorsScreenState.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(size16dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            stringResource(R.string.video_with_actor_title, viewModel.actorName),
            fontWeight = W700,
            fontSize = size18sp
        )
        if (screenState.isLoading)
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        if (!screenState.isLoading && screenState.data.isEmpty())
            EmptyListPlaceholder(stringResource(R.string.films_with_actors_screen_placeholder))
        if (screenState.data.isNotEmpty())
            Content(
                listOfVideos = screenState.data,
                onMovieItemClick = { navigator.navigate(DetailVideoScreenDestination(it.videoUrl)) }
            )
    }
}

@Composable
private fun Content(
    listOfVideos: List<VideoItemModel>,
    onMovieItemClick: (VideoItemModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = modifier.padding(bottom = size10dp)
    ) {
        val state = rememberLazyGridState()
        LazyVerticalGrid(
            state = state,
            columns = GridCells.Fixed(numberOfGridCells),
            modifier = modifier
                .fillMaxSize()
                .focusRequester(focusRequester)
        ) {
            items(listOfVideos) { item ->
                MovieItem(
                    title = item.title,
                    category = item.category,
                    imageUrl = item.imageUrl,
                    onItemClick = { onMovieItemClick(item) },
                )
            }
        }
    }
    LaunchedEffect(Unit) {
        runCatching {
            focusRequester.requestFocus()
        }
    }
}