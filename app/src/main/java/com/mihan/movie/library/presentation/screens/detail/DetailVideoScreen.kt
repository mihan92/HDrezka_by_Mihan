package com.mihan.movie.library.presentation.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.SubcomposeAsyncImage
import com.mihan.movie.library.R
import com.mihan.movie.library.domain.models.ActorModel
import com.mihan.movie.library.domain.models.VideoDetailModel
import com.mihan.movie.library.presentation.navigation.AppNavGraph
import com.mihan.movie.library.presentation.ui.size16dp
import com.mihan.movie.library.presentation.ui.size18dp
import com.mihan.movie.library.presentation.ui.size18sp
import com.mihan.movie.library.presentation.ui.size1dp
import com.mihan.movie.library.presentation.ui.size26sp
import com.mihan.movie.library.presentation.ui.size28dp
import com.mihan.movie.library.presentation.ui.size4dp
import com.mihan.movie.library.presentation.ui.size50dp
import com.mihan.movie.library.presentation.ui.size60dp
import com.mihan.movie.library.presentation.ui.size8dp
import com.mihan.movie.library.presentation.ui.view.FilmDialog
import com.mihan.movie.library.presentation.ui.view.SerialDialog
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.generated.destinations.FilmsWithActorsScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

private val POSTER_WIDTH = 350.dp
private val POSTER_HEIGHT = 420.dp
private const val DARKENING_SCREEN_DURING_LOADING_PROCESS = 0.5f


@Destination<AppNavGraph>(navArgs = DetailScreenNavArgs::class)
@Composable
fun DetailVideoScreen(
    detailViewModel: DetailViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
) {
    val screenState by detailViewModel.screenState.collectAsStateWithLifecycle()
    val filmDialogState = detailViewModel.showFilmDialog.collectAsStateWithLifecycle()
    val serialDialogState = detailViewModel.showSerialDialog.collectAsStateWithLifecycle()
    val videoInfo by detailViewModel.videoInfo.collectAsStateWithLifecycle()
    val listOfSeasons by detailViewModel.listOfSeasons.collectAsStateWithLifecycle()
    val videoHistoryModel by detailViewModel.videoHistoryModel.collectAsStateWithLifecycle()
    val isVideoHasFavourites by detailViewModel.isVideoHasFavourites.collectAsStateWithLifecycle()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        screenState.detailInfo?.let { detailModel ->
            Content(
                videoDetailModel = detailModel,
                isVideoHasFavourites = isVideoHasFavourites,
                onButtonWatchClick = detailViewModel::onButtonWatchClicked,
                onButtonFavouritesClick = detailViewModel::onButtonFavouritesClicked,
                onActorClick = { actorModel ->
                    if (actorModel.actorId.isNotEmpty()) {
                        navigator.navigate(FilmsWithActorsScreenDestination(actorModel))
                    }
                }
            )
        }
        if (screenState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(DARKENING_SCREEN_DURING_LOADING_PROCESS)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
        FilmDialog(
            isDialogShow = filmDialogState,
            translations = videoInfo.translations,
            onTranslationItemClicked = { translate ->
                val selectedTranslate = videoInfo.translations.getValue(translate)
                detailViewModel.selectTranslateForFilms(selectedTranslate)
            },
            onDialogDismiss = detailViewModel::onDialogDismiss
        )
        SerialDialog(
            isDialogShow = serialDialogState,
            videoHistoryModel = videoHistoryModel,
            translations = videoInfo.translations,
            seasons = listOfSeasons,
            onTranslationItemClicked = { translate ->
                val translatorId = videoInfo.translations.getValue(translate)
                detailViewModel.selectTranslateForSerials(translatorId)
            },
            onEpisodeClicked = { season, episode ->
                detailViewModel.onEpisodeClicked(season, episode)
            },
            onDialogDismiss = detailViewModel::onDialogDismiss
        )
    }
}

@Composable
private fun Content(
    videoDetailModel: VideoDetailModel,
    isVideoHasFavourites: Boolean,
    onButtonWatchClick: () -> Unit,
    onButtonFavouritesClick: () -> Unit,
    onActorClick: (ActorModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = modifier //Для фокуса, чтобы скроллилась страница вверх из самого нижнего состояния
                .fillMaxWidth()
                .height(size1dp)
                .focusable()
        )
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(vertical = size50dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Bottom
        ) {
            Poster(videoDetailModel)
            VideoInfo(
                videoDetailModel = videoDetailModel,
                isVideoHasFavourites = isVideoHasFavourites,
                onButtonWatchClick = onButtonWatchClick,
                onButtonFavouritesClick = onButtonFavouritesClick,
                onActorClick = onActorClick
            )
        }
        Text(
            text = videoDetailModel.description,
            textAlign = TextAlign.Justify,
            fontSize = size18sp,
            modifier = Modifier
                .padding(horizontal = size16dp)
                .focusable()
                .focusRequester(focusRequester)
        )
    }
}

@Composable
private fun VideoTitle(
    videoDetailModel: VideoDetailModel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = videoDetailModel.title,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = size26sp,
            modifier = modifier.padding(bottom = size16dp)
        )
    }
}

@Composable
private fun Poster(
    videoDetailModel: VideoDetailModel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(POSTER_WIDTH)
            .height(POSTER_HEIGHT)
            .padding(start = size50dp),
        contentAlignment = Alignment.Center
    ) {
        SubcomposeAsyncImage(
            model = videoDetailModel.imageUrl,
            loading = { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) },
            contentDescription = videoDetailModel.title,
            contentScale = ContentScale.FillBounds,
        )
    }
}

@Composable
private fun VideoInfo(
    videoDetailModel: VideoDetailModel,
    isVideoHasFavourites: Boolean,
    onButtonWatchClick: () -> Unit,
    onButtonFavouritesClick: () -> Unit,
    onActorClick: (ActorModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        VideoTitle(videoDetailModel = videoDetailModel)
        ButtonsSection(
            isVideoHasFavourites = isVideoHasFavourites,
            onButtonWatchClick = onButtonWatchClick,
            onButtonFavouritesClick = onButtonFavouritesClick
        )
        RatingInfo(videoDetailModel)
        ItemInfo(title = stringResource(id = R.string.release_date_title, videoDetailModel.releaseDate))
        ItemInfo(title = stringResource(id = R.string.country_title, videoDetailModel.country))
        ItemInfo(title = stringResource(id = R.string.genre_title, videoDetailModel.genre))
        if (videoDetailModel.actors.isNotEmpty())
            ActorsList(
                actorsTitleResId = R.string.actors_title,
                videoDetailModel.actors,
                onActorClick = onActorClick
            )
    }
}

@Composable
private fun RatingInfo(
    videoDetailModel: VideoDetailModel,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.padding(start = size28dp)) {
        ItemRatingInfo(stringResource(id = R.string.ratings_title))
        if (videoDetailModel.ratingIMDb.isNotEmpty())
            ItemRatingInfo(stringResource(id = R.string.rating_imdb, videoDetailModel.ratingIMDb))
        if (videoDetailModel.ratingKp.isNotEmpty())
            ItemRatingInfo(stringResource(id = R.string.rating_kp, videoDetailModel.ratingKp))
        if (videoDetailModel.ratingRezka.isNotEmpty())
            ItemRatingInfo(stringResource(id = R.string.rating_rezka, videoDetailModel.ratingRezka))
    }
}

@Composable
private fun ButtonsSection(
    isVideoHasFavourites: Boolean,
    onButtonFavouritesClick: () -> Unit,
    onButtonWatchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(size60dp)
            .background(MaterialTheme.colorScheme.onBackground.copy(0.2f)),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
        ) {
            Button(
                shape = ButtonDefaults.shape(RoundedCornerShape(size4dp)),
                onClick = onButtonWatchClick,
                colors = ButtonDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    contentColor = MaterialTheme.colorScheme.background,
                    focusedContainerColor = MaterialTheme.colorScheme.primary,
                    focusedContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = modifier
                    .padding(start = size28dp)
                    .focusRequester(focusRequester)
            ) {
                Text(
                    text = stringResource(id = R.string.bt_watch).uppercase(),
                    fontWeight = FontWeight.W700,
                )
            }
            Button(
                shape = ButtonDefaults.shape(RoundedCornerShape(size4dp)),
                onClick = onButtonFavouritesClick,
                colors = ButtonDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    contentColor = MaterialTheme.colorScheme.background,
                    focusedContainerColor = MaterialTheme.colorScheme.primary,
                    focusedContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = modifier
                    .padding(start = size28dp)
                    .focusRequester(focusRequester)
            ) {
                if (isVideoHasFavourites)
                    Text(
                        text = stringResource(id = R.string.delete_from_favourites_title).uppercase(),
                        fontWeight = FontWeight.W700,
                    )
                else
                    Text(
                        text = stringResource(id = R.string.add_to_favourites_title).uppercase(),
                        fontWeight = FontWeight.W700,
                    )
            }
        }
        LaunchedEffect(key1 = Unit) {
            focusRequester.requestFocus()
        }
    }
}

@Composable
private fun ItemInfo(
    title: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = size28dp)
    ) {
        Spacer(modifier = Modifier.height(size16dp))
        Text(
            text = title,
            fontSize = size18sp
        )
    }
}

@Composable
private fun ItemRatingInfo(
    title: String
) {
    Column(
        modifier = Modifier.padding(end = size18dp)
    ) {
        Spacer(modifier = Modifier.height(size16dp))
        Text(
            text = title,
            fontSize = size18sp
        )
    }
}

@Composable
private fun ActorsList(
    actorsTitleResId: Int,
    actors: Map<String, String>,
    onActorClick: (ActorModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Spacer(modifier = Modifier.height(size16dp))
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = size28dp)
    ) {
        Text(
            stringResource(actorsTitleResId),
            modifier = modifier.padding(end = size8dp)
        )
        LazyRow {
            items(actors.entries.toList()) { item ->
                ActorItem(
                    item.value,
                    onItemClick = {
                        val actorModel = ActorModel(item.key, item.value)
                        onActorClick(actorModel)
                    },
                )
            }
        }
    }
}

@Composable
private fun ActorItem(
    title: String,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var focused by remember { mutableStateOf(false) }
    val textColor = if (focused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground

    Text(
        text = "$title, ",
        color = textColor,
        modifier = modifier
            .onFocusChanged {
                focused = it.isFocused
            }
            .clickable(onClick = onItemClick)
    )
}