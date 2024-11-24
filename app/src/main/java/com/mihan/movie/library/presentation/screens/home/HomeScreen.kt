package com.mihan.movie.library.presentation.screens.home

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.mihan.movie.library.R
import com.mihan.movie.library.domain.models.VideoItemModel
import com.mihan.movie.library.presentation.animation.AnimatedScreenTransitions
import com.mihan.movie.library.presentation.screens.destinations.DetailVideoScreenDestination
import com.mihan.movie.library.presentation.ui.size10dp
import com.mihan.movie.library.presentation.ui.view.FilterDialog
import com.mihan.movie.library.presentation.ui.view.InformationDialog
import com.mihan.movie.library.presentation.ui.view.MovieItem
import com.mihan.movie.library.presentation.ui.view.PageFooter
import com.mihan.movie.library.presentation.ui.view.TopAppBar
import com.mihan.movie.library.presentation.ui.view.TopBarItems
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

private const val numberOfGridCells = 6
private const val PLACEHOLDER_MESSAGE_WIDTH = .8f

@Destination(style = AnimatedScreenTransitions::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle(HomeScreenState())
    val selectedTopBarItem by viewModel.topBarState.collectAsStateWithLifecycle()
    val currentDefaultPage by viewModel.currentDefaultListPage.collectAsStateWithLifecycle()
    val currentFilteredPage by viewModel.currentFilteredListPage.collectAsStateWithLifecycle()
    val videoCategory by viewModel.videoCategoryState.collectAsStateWithLifecycle()
    val categoryFilter by viewModel.categoryFilter.collectAsStateWithLifecycle()
    val genreFilter by viewModel.genreFilter.collectAsStateWithLifecycle()
    val moviePeriod by viewModel.moviePeriod.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showFilterDialog by rememberSaveable { mutableStateOf(false) }
    var showDialogUnsupportedDevice by rememberSaveable { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            selectedTopBarItem = selectedTopBarItem,
            onItemClick = {
                viewModel.onTopBarItemClicked(it)
                if (it == TopBarItems.Filter)
                    showFilterDialog = true
            },
        )
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (screenState.isLoading)
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            else if (screenState.data.isNotEmpty()) {
                var page = if (selectedTopBarItem == TopBarItems.Filter) currentFilteredPage else currentDefaultPage
                Content(
                    listOfVideos = screenState.data,
                    navigator = navigator,
                    currentPage = page,
                    previousPageClick = { viewModel.onPageChanged(--page) },
                    nextPageClick = { viewModel.onPageChanged(++page) }
                )
            } else
                Text(
                    text = stringResource(R.string.main_screen_placeholder_message),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(PLACEHOLDER_MESSAGE_WIDTH)
                )
            FilterDialog(
                categoryFilter = categoryFilter,
                genreFilter = genreFilter,
                moviePeriod = moviePeriod,
                showDialog = showFilterDialog,
                onButtonSearchClick = { category, genre, moviePeriod ->
                    viewModel.onFilterDialogButtonSearchPressed(category, genre, moviePeriod, page = 1)
                    showFilterDialog = false
                },
                onCollectionItemClick = { category, movieCollection ->
                    viewModel.onFilterDialogCollectionItemPressed(category, movieCollection, page = 1)
                    showFilterDialog = false
                },
                onDismissRequest = {
                    viewModel.onTopBarItemClicked(TopBarItems.Watching)
                    showFilterDialog = false
                }
            )
            InformationDialog(
                dialogTitle = stringResource(R.string.attention_title),
                dialogDescription = stringResource(R.string.unsupported_device_description),
                showDialogState = showDialogUnsupportedDevice,
                onButtonAgreePressed = { showDialogUnsupportedDevice = false }
            )
        }
    }
    LaunchedEffect(key1 = currentDefaultPage, key2 = selectedTopBarItem, key3 = videoCategory) {
        if (selectedTopBarItem == TopBarItems.Filter) return@LaunchedEffect
        viewModel.getListVideo()
    }
    LaunchedEffect(Unit) {
        if (!isRunningOnTv(context) && !viewModel.isUnsupportedDeviceMessageShowed) {
            showDialogUnsupportedDevice = true
            viewModel.updateUnsupportedDeviceMessageStatus(true)
        }
    }
}

private fun isRunningOnTv(context: Context): Boolean {
    val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
    return uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
}

@Composable
private fun Content(
    listOfVideos: List<VideoItemModel>,
    navigator: DestinationsNavigator,
    currentPage: Int,
    previousPageClick: () -> Unit,
    nextPageClick: () -> Unit,
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
        runCatching {
            focusRequester.requestFocus()
        }
    }
}