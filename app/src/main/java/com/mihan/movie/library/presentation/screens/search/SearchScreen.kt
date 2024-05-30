package com.mihan.movie.library.presentation.screens.search

import android.Manifest
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.items
import androidx.tv.foundation.lazy.grid.rememberTvLazyGridState
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.mihan.movie.library.R
import com.mihan.movie.library.common.utils.VoiceRecognizerState
import com.mihan.movie.library.presentation.animation.AnimatedScreenTransitions
import com.mihan.movie.library.presentation.screens.destinations.DetailVideoScreenDestination
import com.mihan.movie.library.presentation.ui.size16dp
import com.mihan.movie.library.presentation.ui.size18sp
import com.mihan.movie.library.presentation.ui.size28dp
import com.mihan.movie.library.presentation.ui.view.MovieItem
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

private const val SEARCH_FIELD_BACKGROUND_ALPHA = 0.1f
private const val NUMBER_OF_GRID_CELLS = 6
private const val TEXT_HINT_ALPHA = 0.5f

@OptIn(ExperimentalPermissionsApi::class)
@Destination(style = AnimatedScreenTransitions::class)
@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val context = LocalContext.current
    val voicePermission = rememberPermissionState(permission = Manifest.permission.RECORD_AUDIO)
    val voiceState by searchViewModel.voiceState.collectAsStateWithLifecycle()
    val screenState by searchViewModel.screenState.collectAsStateWithLifecycle()
    Content(
        voiceState = voiceState,
        screenState = screenState,
        onVideoItemClick = { navigator.navigate(DetailVideoScreenDestination(it)) },
        buttonSearchPressed = searchViewModel::buttonSearchPressed,
        buttonVoicePressed = {
            if (!voicePermission.status.isGranted)
                voicePermission.launchPermissionRequest()
            else {
                if (!voiceState.isSpeaking) searchViewModel.startListening() else searchViewModel.stopListening()
            }
        }
    )
    if (voiceState.error.isNotEmpty())
        Toast.makeText(context, voiceState.error, Toast.LENGTH_LONG).show()
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun Content(
    voiceState: VoiceRecognizerState,
    screenState: SearchScreenState,
    onVideoItemClick: (String) -> Unit,
    buttonSearchPressed: (String) -> Unit,
    buttonVoicePressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = rememberTvLazyGridState()
    val focusRequester = remember { FocusRequester() }
    Column(modifier = modifier.fillMaxSize()) {
        SearchField(
            voiceState = voiceState,
            buttonVoicePressed = buttonVoicePressed,
            buttonSearchPressed = buttonSearchPressed
        )
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (screenState.isLoading)
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            else if (!screenState.listOfVideo.isNullOrEmpty()) {
                TvLazyVerticalGrid(
                    state = state,
                    columns = TvGridCells.Fixed(NUMBER_OF_GRID_CELLS),
                    modifier = modifier
                        .fillMaxSize()
                        .focusRequester(focusRequester)
                ) {
                    items(screenState.listOfVideo) { item ->
                        MovieItem(
                            title = item.title,
                            category = item.category,
                            imageUrl = item.imageUrl,
                            onItemClick = { onVideoItemClick(item.videoUrl) },
                        )
                    }
                }
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            } else if (screenState.listOfVideo != null && screenState.listOfVideo.isEmpty())
                TextHint(hintResId = R.string.hint_no_results)
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun SearchField(
    voiceState: VoiceRecognizerState,
    buttonVoicePressed: () -> Unit,
    buttonSearchPressed: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    var searchText by remember { mutableStateOf("") }
    if (voiceState.spokenText.isNotEmpty()) {
        searchText = voiceState.spokenText
        buttonSearchPressed(searchText)
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(size28dp)
            .focusGroup(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        VoiceSearchButton(
            isSpeaking = voiceState.isSpeaking,
            buttonPressed = { buttonVoicePressed() }
        )
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { TextHint(R.string.hint_enter_query) },
            singleLine = true,
            keyboardActions = KeyboardActions(
                onSearch = {
                    focusManager.moveFocus(FocusDirection.Right)
                    buttonSearchPressed(searchText)
                }
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.onBackground.copy(SEARCH_FIELD_BACKGROUND_ALPHA),
                focusedContainerColor = MaterialTheme.colorScheme.onBackground.copy(SEARCH_FIELD_BACKGROUND_ALPHA),
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onBackground.copy(SEARCH_FIELD_BACKGROUND_ALPHA),
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.weight(1f)
        )
        ButtonSearch { buttonSearchPressed(searchText) }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun VoiceSearchButton(
    isSpeaking: Boolean,
    modifier: Modifier = Modifier,
    buttonPressed: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    IconButton(
        onClick = buttonPressed,
        modifier = modifier
            .padding(horizontal = size16dp)
            .focusRequester(focusRequester),
        colors = ButtonDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.primary,
            focusedContentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        AnimatedContent(targetState = isSpeaking, label = "") { isSpeaking ->
            if (isSpeaking)
                Icon(
                    painter = painterResource(id = R.drawable.ic_stop),
                    contentDescription = null,
                )
            else
                Icon(
                    painter = painterResource(id = R.drawable.ic_mic),
                    contentDescription = null,
                )
        }
    }
    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun ButtonSearch(
    modifier: Modifier = Modifier,
    buttonPressed: () -> Unit
) {
    IconButton(
        onClick = buttonPressed,
        modifier = modifier.padding(horizontal = size16dp),
        colors = ButtonDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.primary,
            focusedContentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Icon(
            imageVector = Icons.Rounded.Search,
            contentDescription = null,
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TextHint(
    hintResId: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(id = hintResId),
        fontSize = size18sp,
        fontWeight = FontWeight.W600,
        color = MaterialTheme.colorScheme.onBackground.copy(TEXT_HINT_ALPHA),
        modifier = modifier
    )
}