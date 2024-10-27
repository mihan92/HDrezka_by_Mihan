package com.mihan.movie.library.presentation.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.DialogProperties
import com.mihan.movie.library.R
import com.mihan.movie.library.presentation.ui.size16dp
import com.mihan.movie.library.presentation.ui.size18sp
import com.mihan.movie.library.presentation.ui.theme.dialogBgColor

@Composable
fun ConfirmDeleteDialog(
    showDialogState: Boolean,
    onButtonYesPressed: () -> Unit,
    onButtonNoPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    if (showDialogState) {
        AlertDialog(
            onDismissRequest = { onButtonNoPressed() },
            dismissButton = {
                DialogButton(
                    title = stringResource(R.string.no_title),
                    isEnabled = true,
                    onButtonClick = onButtonNoPressed
                )
            },
            confirmButton = {
                DialogButton(
                    title = stringResource(R.string.yes_title),
                    isEnabled = true,
                    onButtonClick = onButtonYesPressed,
                    modifier = Modifier.focusRequester(focusRequester)
                )
            },
            containerColor = dialogBgColor,
            textContentColor = Color.White,
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = modifier,
            text = {
                Column(
                    modifier = modifier,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        stringResource(R.string.title_delete_question),
                        fontSize = size18sp,
                        fontWeight = W700,
                        textAlign = TextAlign.Center,
                        modifier = modifier.padding(bottom = size16dp)
                    )
                }
            }
        )
        LaunchedEffect(Unit) {
            runCatching {
                focusRequester.requestFocus()
            }
        }
    }
}