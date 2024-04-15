package com.mihan.movie.library.presentation.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.items
import androidx.tv.foundation.lazy.grid.rememberTvLazyGridState
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import com.mihan.movie.library.domain.models.VideoItemModel
import com.mihan.movie.library.presentation.animation.AnimatedScreenTransitions
import com.mihan.movie.library.presentation.screens.destinations.DetailVideoScreenDestination
import com.mihan.movie.library.presentation.ui.size10dp
import com.mihan.movie.library.presentation.ui.view.MovieItem
import com.mihan.movie.library.presentation.ui.view.PageFooter
import com.mihan.movie.library.presentation.ui.view.TopAppBar
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

private const val numberOfGridCells = 6

@OptIn(ExperimentalTvMaterial3Api::class)
@Destination(style = AnimatedScreenTransitions::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle(HomeScreenState())
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()
    val currentPage by viewModel.pageState.collectAsStateWithLifecycle()
    val videoCategory by viewModel.videoCategoryState.collectAsStateWithLifecycle()
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            currentFilterSection = filterState,
            onItemClick = viewModel::onTopBarItemClicked
        )
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (screenState.isLoading)
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            if (screenState.data.isNotEmpty()) {
                var page = currentPage
                Content(
                    listOfVideos = screenState.data,
                    navigator = navigator,
                    currentPage = currentPage,
                    previousPageClick = { viewModel.onPageChanged(--page) },
                    nextPageClick = { viewModel.onPageChanged(++page) }
                )
            }
        }
    }
    LaunchedEffect(key1 = currentPage, key2 = filterState, key3 = videoCategory) {
        viewModel.getListVideo()
    }
}

@Composable
private fun Content(
    listOfVideos: List<VideoItemModel>,
    navigator: DestinationsNavigator,
    currentPage: Int,
    previousPageClick: () -> Unit,
    nextPageClick: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.padding(bottom = size10dp)
    ) {
        val state = rememberTvLazyGridState()
        TvLazyVerticalGrid(
            state = state,
            columns = TvGridCells.Fixed(numberOfGridCells),
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester)
        ) {
            items(listOfVideos) { item ->
                MovieItem(
                    title = item.title,
                    category = item.category,
                    imageUrl = item.imageUrl,
                    onItemClick = { navigator.navigate(DetailVideoScreenDestination(item.videoUrl)) },
                )
            }
        }
        AnimatedVisibility(
            visible = !state.canScrollForward,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            PageFooter(
                currentPage = currentPage,
                listSize = listOfVideos.size,
                previousPageClick = previousPageClick,
                nextPageClick = nextPageClick
            )
        }
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}