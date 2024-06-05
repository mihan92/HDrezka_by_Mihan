package com.mihan.movie.library.presentation.ui.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.items
import androidx.tv.foundation.lazy.list.rememberTvLazyListState
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.mihan.movie.library.R
import com.mihan.movie.library.common.models.CategoryFilter
import com.mihan.movie.library.common.models.GenreFilter
import com.mihan.movie.library.common.models.MovieCollections
import com.mihan.movie.library.common.models.MoviePeriod
import com.mihan.movie.library.presentation.ui.size110dp
import com.mihan.movie.library.presentation.ui.size140dp
import com.mihan.movie.library.presentation.ui.size16dp
import com.mihan.movie.library.presentation.ui.size180dp
import com.mihan.movie.library.presentation.ui.size2dp
import com.mihan.movie.library.presentation.ui.size4dp
import com.mihan.movie.library.presentation.ui.size8dp
import com.mihan.movie.library.presentation.ui.sizeEmpty
import com.mihan.movie.library.presentation.ui.theme.dialogBgColor
import kotlinx.coroutines.launch

private val DIALOG_WIDTH = 590.dp
private val DIALOG_HEIGHT = 310.dp

@Composable
fun FilterDialog(
    categoryFilter: CategoryFilter,
    genreFilter: GenreFilter,
    moviePeriod: MoviePeriod,
    showDialog: Boolean,
    onButtonSearchClick: (CategoryFilter, GenreFilter, MoviePeriod) -> Unit,
    onCollectionItemClick: (CategoryFilter, MovieCollections) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val moviePeriodList = MoviePeriod.entries
    val categories = CategoryFilter.entries
    val genresMap = GenreFilter.getGenresMap()
    val movieCollectionsMap = MovieCollections.getCollectionsMap()
    var selectedCategory by remember { mutableStateOf(categoryFilter) }
    val genres by remember { derivedStateOf { genresMap[selectedCategory] ?: emptyList() } }
    val movieCollections by remember { derivedStateOf { movieCollectionsMap[selectedCategory] ?: emptyList() } }
    var selectedGenre by remember { mutableStateOf(genreFilter) }
    var selectedMoviePeriod by remember { mutableStateOf(moviePeriod) }
    val categoryListState = rememberTvLazyListState()
    val genreListState = rememberTvLazyListState()
    val moviePeriodListState = rememberTvLazyListState()
    val categoryFocusRequester = remember { FocusRequester() }
    val scope = rememberCoroutineScope()

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { DialogTitle(R.string.filter_dialog_title) },
            containerColor = dialogBgColor,
            text = {
                Column(modifier = modifier.fillMaxSize()) {
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TvLazyColumn(
                            state = categoryListState,
                            modifier = modifier
                                .height(size110dp)
                                .width(size140dp),
                            verticalArrangement = Arrangement.spacedBy(size2dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            items(categories) { category ->
                                CategoryItem(
                                    itemText = stringResource(id = category.titleResId),
                                    isSelected = selectedCategory == category,
                                    onItemClick = {
                                        selectedCategory = category
                                        selectedGenre = genres.first()
                                        scope.launch {
                                            genreListState.scrollToItem(genres.indexOf(selectedGenre))
                                        }
                                    },
                                    focusRequester = if (category == selectedCategory) categoryFocusRequester else null
                                )
                            }
                        }
                        TvLazyColumn(
                            state = genreListState,
                            modifier = modifier
                                .height(size110dp)
                                .width(size180dp),
                            verticalArrangement = Arrangement.spacedBy(size2dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            items(genres) { genre ->
                                CategoryItem(
                                    itemText = genre.genreTitle,
                                    isSelected = selectedGenre == genre,
                                    onItemClick = { selectedGenre = genre },
                                    focusRequester = null
                                )
                            }
                        }
                        TvLazyColumn(
                            state = moviePeriodListState,
                            modifier = modifier
                                .height(size110dp)
                                .width(size140dp),
                            verticalArrangement = Arrangement.spacedBy(size2dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            items(moviePeriodList) { period ->
                                CategoryItem(
                                    itemText = period.value,
                                    isSelected = selectedMoviePeriod == period,
                                    onItemClick = { selectedMoviePeriod = period },
                                    focusRequester = null
                                )
                            }
                        }
                        SearchButton {
                            onButtonSearchClick(selectedCategory, selectedGenre, selectedMoviePeriod)
                        }
                    }
                    DialogTitle(
                        stringResId = R.string.dialog_title_collections,
                        modifier = modifier.padding(top = size16dp, bottom = size8dp)
                    )
                    TvLazyRow {
                        items(movieCollections) {collectionItem ->
                            CollectionItem(
                                imageResId = collectionItem.iconResId,
                                onItemClick = { onCollectionItemClick(selectedCategory, collectionItem) }
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier
                .width(DIALOG_WIDTH)
                .height(DIALOG_HEIGHT)
        )
        LaunchedEffect(key1 = Unit) {
            categoryListState.scrollToItem(categories.indexOf(selectedCategory))
            genreListState.scrollToItem(genres.indexOf(selectedGenre))
            moviePeriodListState.scrollToItem(moviePeriodList.indexOf(selectedMoviePeriod))
            categoryFocusRequester.requestFocus()
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun CategoryItem(
    itemText: String,
    isSelected: Boolean,
    onItemClick: () -> Unit,
    focusRequester: FocusRequester?,
    modifier: Modifier = Modifier
) {
    val borderColor =
        if (isSelected) BorderStroke(size2dp, MaterialTheme.colorScheme.primary)
        else BorderStroke(sizeEmpty, Color.Transparent)

    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .focusRequester(focusRequester ?: FocusRequester())
    ) {
        Text(
            text = itemText,
            modifier = modifier
                .clickable(onClick = onItemClick)
                .border(
                    border = borderColor,
                    shape = MaterialTheme.shapes.small
                )
                .clip(MaterialTheme.shapes.small)
                .padding(size8dp),
            color = Color.White
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun SearchButton(
    onButtonClick: () -> Unit
) {
    Button(
        onClick = { onButtonClick() },
        colors = ButtonDefaults.colors(
            containerColor = Color.White,
            focusedContainerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.Black,
            focusedContentColor = Color.White
        )
    ) {
        Text(text = stringResource(R.string.bt_find_title))
    }
}

@Composable
private fun DialogTitle(
    stringResId: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(stringResId),
        color = Color.White,
        modifier = modifier
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun CollectionItem(
    imageResId: Int,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onItemClick,
        shape = ButtonDefaults.shape(RoundedCornerShape(size4dp)),
        colors = ButtonDefaults.colors(
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(sizeEmpty),
        modifier = modifier.padding(horizontal = size4dp)
    ) {
        Image(painter = painterResource(id = imageResId), contentDescription = null)
    }
}