package com.mihan.movie.library.presentation.screens.app_updates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.mihan.movie.library.R
import com.mihan.movie.library.domain.models.ChangelogModel
import com.mihan.movie.library.presentation.animation.AnimatedScreenTransitions
import com.mihan.movie.library.presentation.ui.size18sp
import com.mihan.movie.library.presentation.ui.size20dp
import com.mihan.movie.library.presentation.ui.size28dp
import com.mihan.movie.library.presentation.ui.size40dp
import com.mihan.movie.library.presentation.ui.size6dp
import com.ramcosta.composedestinations.annotation.Destination

private const val PROGRESS_BACKGROUND_ALPHA = 0.2f

@Destination(style = AnimatedScreenTransitions::class)
@Composable
fun AppUpdatesScreen(
    appUpdatesViewModel: AppUpdatesViewModel = hiltViewModel()
) {
    val changelogModel = appUpdatesViewModel.changelogModel
    val downloadState by appUpdatesViewModel.downloadState.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!downloadState.isDownloading)
            Content(
                changelogModel = changelogModel,
                focusRequester = focusRequester,
                onButtonUpdatePressed = appUpdatesViewModel::buttonUpdatePressed
            )
        else
            DownloadingProgress(downloadingProgress = downloadState.downloadingProgress)
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun Content(
    changelogModel: ChangelogModel?,
    focusRequester: FocusRequester,
    onButtonUpdatePressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    var coordinatesSize by remember { mutableStateOf(IntSize.Zero) }
    val isButtonCanFocus by remember {
        derivedStateOf {
            coordinatesSize.width > 0 && coordinatesSize.height > 0
        }
    }
    changelogModel?.let { model ->
        Column(
            modifier = modifier.fillMaxWidth(0.6f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.new_version_is_available, model.latestVersion),
                fontSize = size18sp,
                fontWeight = FontWeight.W900
            )
            Spacer(modifier = modifier.height(size28dp))
            ReleaseNotes(listOfNotes = model.releaseNotes)
            Spacer(modifier = modifier.height(size28dp))
            Button(
                onClick = onButtonUpdatePressed,
                colors = ButtonDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.onBackground,
                    contentColor = MaterialTheme.colorScheme.background,
                    focusedContainerColor = MaterialTheme.colorScheme.primary,
                    focusedContentColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = modifier
                    .onGloballyPositioned { coordinates ->
                        val newSize = coordinates.size
                        if (newSize != coordinatesSize) {
                            coordinatesSize = newSize
                        }
                    }
                    .focusRequester(focusRequester)
            ) {
                Text(text = stringResource(id = R.string.update_title))
            }
        }
    }
    LaunchedEffect(key1 = isButtonCanFocus) {
        if (isButtonCanFocus)
            focusRequester.requestFocus()
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun DownloadingProgress(
    downloadingProgress: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.onBackground.copy(PROGRESS_BACKGROUND_ALPHA),
                RoundedCornerShape(size20dp)
            )
            .padding(size40dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.downloading_title),
            fontSize = size18sp,
            fontWeight = FontWeight.W900
        )
        Spacer(modifier = Modifier.height(size20dp))
        Text(
            text = "${downloadingProgress.toInt()} %",
            fontSize = size18sp,
            fontWeight = FontWeight.W900
        )
    }
}

@Composable
private fun ReleaseNotes(
    listOfNotes: List<String>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(listOfNotes) {
            Text(
                text = it,
                fontSize = size18sp,
                textAlign = TextAlign.Justify,
                modifier = modifier.padding(vertical = size6dp)
            )
        }
    }
}