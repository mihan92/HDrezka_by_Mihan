package com.mihan.movie.library.presentation.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults.colors
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Switch
import androidx.tv.material3.SwitchDefaults
import androidx.tv.material3.Text
import com.mihan.movie.library.BuildConfig
import com.mihan.movie.library.R
import com.mihan.movie.library.common.entites.Colors
import com.mihan.movie.library.common.entites.VideoCategory
import com.mihan.movie.library.common.entites.VideoQuality
import com.mihan.movie.library.presentation.animation.AnimatedScreenTransitions
import com.mihan.movie.library.presentation.ui.size10dp
import com.mihan.movie.library.presentation.ui.size14sp
import com.mihan.movie.library.presentation.ui.size16dp
import com.mihan.movie.library.presentation.ui.size1dp
import com.mihan.movie.library.presentation.ui.size20sp
import com.mihan.movie.library.presentation.ui.size8dp
import com.mihan.movie.library.presentation.ui.view.ChangingSiteUrlDialog
import com.mihan.movie.library.presentation.ui.view.PrimaryColorDropDownMenu
import com.mihan.movie.library.presentation.ui.view.VideoCategoryDropDownMenu
import com.mihan.movie.library.presentation.ui.view.VideoQualityDropDownMenu
import com.ramcosta.composedestinations.annotation.Destination

private const val DESCRIPTION_TITLE_ALPHA = 0.6f
private const val SELECTED_BACKGROUND_ALPHA = 0.1f

@OptIn(ExperimentalTvMaterial3Api::class)
@Destination(style = AnimatedScreenTransitions::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val focusRequester = remember { FocusRequester() }
    val videoCategory by settingsViewModel.getVideoCategory.collectAsStateWithLifecycle()
    val videoQuality by settingsViewModel.getVideoQuality.collectAsStateWithLifecycle()
    val siteUrl by settingsViewModel.getSiteUrl.collectAsStateWithLifecycle()
    val siteDialogState by settingsViewModel.siteDialogState.collectAsStateWithLifecycle()
    val primaryColor by settingsViewModel.getPrimaryColor.collectAsStateWithLifecycle()
    val isRemoteParsingSelected by settingsViewModel.remoteParsing.collectAsStateWithLifecycle()
    val isAutoUpdateEnabled by settingsViewModel.autoUpdate.collectAsStateWithLifecycle()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = size16dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VideoCategory(
                videoCategory = videoCategory,
                onCategoryItemClicked = settingsViewModel::videoCategoryChanged,
                modifier = Modifier.focusRequester(focusRequester)
            )
            VideoQuality(
                videoQuality = videoQuality,
                onQualityItemClicked = settingsViewModel::videoQualityChanged
            )
            PrimaryColor(
                primaryColor = primaryColor,
                onColorItemClicked = settingsViewModel::primaryColorChanged
            )
            AutoUpdate(
                isAutoUpdateEnabled = isAutoUpdateEnabled,
                onSwitchPressed = settingsViewModel::onAutoUpdatePressed
            )
            AnimatedVisibility(visible = !isAutoUpdateEnabled) {
                SiteUrl(onButtonClick = settingsViewModel::onButtonShowDialogClicked)
            }
            RemoteParsing(
                isRemoteParsingSelected = isRemoteParsingSelected,
                onSwitchPressed = settingsViewModel::onSwitchPressed
            )
        }
        Text(
            text = stringResource(id = R.string.app_version_title, BuildConfig.VERSION_NAME),
            color = MaterialTheme.colorScheme.onBackground.copy(DESCRIPTION_TITLE_ALPHA),
        )
    }
    ChangingSiteUrlDialog(
        isShow = siteDialogState,
        siteUrl = siteUrl,
        onButtonDismiss = settingsViewModel::onButtonDialogDismissPressed,
        onButtonConfirm = settingsViewModel::onButtonDialogConfirmPressed
    )
    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun VideoCategory(
    videoCategory: VideoCategory,
    onCategoryItemClicked: (VideoCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val backgroundColor = if (isFocused) MaterialTheme.colorScheme.onBackground.copy(SELECTED_BACKGROUND_ALPHA)
    else MaterialTheme.colorScheme.background
    Row(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(size8dp))
            .padding(size10dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TitleWithDescription(titleResId = R.string.category_title, descResiId = R.string.category_description)
        VideoCategoryDropDownMenu(
            videoCategory = videoCategory,
            onCategoryItemClicked = onCategoryItemClicked,
            isFocused = { isFocused = it }
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun VideoQuality(
    videoQuality: VideoQuality,
    onQualityItemClicked: (VideoQuality) -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val backgroundColor = if (isFocused) MaterialTheme.colorScheme.onBackground.copy(SELECTED_BACKGROUND_ALPHA)
    else MaterialTheme.colorScheme.background
    Row(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(size8dp))
            .padding(size10dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TitleWithDescription(
            titleResId = R.string.quality_title,
            descResiId = R.string.quality_description,
        )
        VideoQualityDropDownMenu(
            videoQuality = videoQuality,
            onCategoryItemClicked = onQualityItemClicked,
            isFocused = { isFocused = it },
            modifier = modifier.weight(.1f)
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun SiteUrl(
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val backgroundColor = if (isFocused) MaterialTheme.colorScheme.onBackground.copy(SELECTED_BACKGROUND_ALPHA)
    else MaterialTheme.colorScheme.background
    Row(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(size8dp))
            .padding(size10dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TitleWithDescription(titleResId = R.string.site_url_title, descResiId = R.string.site_url_description)
        Button(
            onClick = onButtonClick,
            modifier = modifier
                .onFocusChanged { isFocused = it.isFocused }
                .border(size1dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(size10dp)),
            colors = colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
                focusedContentColor = MaterialTheme.colorScheme.onBackground
            ),
        ) {
            Text(text = stringResource(id = R.string.change_title).uppercase())
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun PrimaryColor(
    primaryColor: Colors,
    onColorItemClicked: (Colors) -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val backgroundColor = if (isFocused) MaterialTheme.colorScheme.onBackground.copy(SELECTED_BACKGROUND_ALPHA)
    else MaterialTheme.colorScheme.background
    Row(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(size8dp))
            .padding(size10dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TitleWithDescription(
            titleResId = R.string.primary_color_title,
            descResiId = R.string.primary_color_description,
        )
        PrimaryColorDropDownMenu(
            primaryColor = primaryColor,
            onColorItemClicked = onColorItemClicked,
            isFocused = { isFocused = it }
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun RemoteParsing(
    isRemoteParsingSelected: Boolean,
    onSwitchPressed: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val backgroundColor = if (isFocused) MaterialTheme.colorScheme.onBackground.copy(SELECTED_BACKGROUND_ALPHA)
    else MaterialTheme.colorScheme.background
    Row(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(size8dp))
            .padding(size10dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TitleWithDescription(
            titleResId = R.string.remote_parsing_title,
            descResiId = R.string.remote_parsing_description
        )
        Switch(
            checked = isRemoteParsingSelected,
            onCheckedChange = { onSwitchPressed(it) },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.background
            ),
            modifier = modifier.onFocusChanged {
                isFocused = it.isFocused
            }
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun AutoUpdate(
    isAutoUpdateEnabled: Boolean,
    onSwitchPressed: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val backgroundColor = if (isFocused) MaterialTheme.colorScheme.onBackground.copy(SELECTED_BACKGROUND_ALPHA)
    else MaterialTheme.colorScheme.background
    Row(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(size8dp))
            .padding(size10dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TitleWithDescription(
            titleResId = R.string.auto_update_title,
            descResiId = R.string.auto_update_description
        )
        Switch(
            checked = isAutoUpdateEnabled,
            onCheckedChange = { onSwitchPressed(it) },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.background
            ),
            modifier = modifier.onFocusChanged {
                isFocused = it.isFocused
            }
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TitleWithDescription(
    titleResId: Int,
    descResiId: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(0.75f)) {
        Text(
            text = stringResource(id = titleResId),
            fontSize = size20sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.W600
        )
        Spacer(modifier = modifier.height(size10dp))
        Text(
            text = stringResource(id = descResiId),
            fontSize = size14sp,
            color = MaterialTheme.colorScheme.onBackground.copy(DESCRIPTION_TITLE_ALPHA),
            fontWeight = FontWeight.W600
        )
    }
}