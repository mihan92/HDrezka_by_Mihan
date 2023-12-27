package com.mihan.movie.library.presentation.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.Text
import com.mihan.movie.library.R
import com.mihan.movie.library.presentation.ui.size16sp
import com.mihan.movie.library.presentation.ui.size18sp
import com.mihan.movie.library.presentation.ui.size8dp

private const val DIALOG_FRACTION = 0.8f
private const val DIALOG_RATIO = 3 / 2f

@Composable
fun FilmDialog(
    isDialogShow: State<Boolean>,
    translations: Map<String, String>,
    onTranslationItemClicked: (String) -> Unit,
    onDialogDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isDialogShow.value) {
        val focusRequester = remember { FocusRequester() }
        Dialog(onDismissRequest = onDialogDismiss) {
            Column(
                modifier = modifier
                    .fillMaxSize(DIALOG_FRACTION)
                    .aspectRatio(DIALOG_RATIO)
                    .background(Color.DarkGray),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.voicecover_title),
                    fontSize = size18sp,
                    fontWeight = FontWeight.W700,
                    modifier = modifier.padding(vertical = size8dp)
                )
                TvLazyColumn(
                    modifier = Modifier.focusRequester(focusRequester)
                ) {
                    items(translations.keys.toList()) { item ->
                        TextButton(
                            onClick = { onTranslationItemClicked(item) },
                            modifier = modifier.fillMaxWidth(),
                            shape = RectangleShape
                        ) {
                            Text(
                                text = item,
                                fontWeight = FontWeight.W700,
                                fontSize = size16sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
        LaunchedEffect(key1 = Unit) {
            focusRequester.requestFocus()
        }
    }
}