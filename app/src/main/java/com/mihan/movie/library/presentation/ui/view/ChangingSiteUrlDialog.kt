package com.mihan.movie.library.presentation.ui.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import com.mihan.movie.library.R
import com.mihan.movie.library.presentation.ui.size18sp

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ChangingSiteUrlDialog(
    isShow: Boolean,
    siteUrl: String,
    onButtonDismiss: () -> Unit,
    onButtonConfirm: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    if (isShow) {
        var inputedUrl by remember { mutableStateOf(siteUrl) }
        AlertDialog(
            onDismissRequest = onButtonDismiss,
            containerColor = Color.DarkGray,
            textContentColor = Color.DarkGray,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            confirmButton = {
                DialogTextButton(
                    title = stringResource(id = R.string.confirm_title),
                    onButtonClick = { onButtonConfirm(inputedUrl) }
                )
            },
            dismissButton = {
                DialogTextButton(
                    title = stringResource(id = R.string.dismiss_title),
                    onButtonClick = onButtonDismiss
                )
            },
            title = {
                Text(
                    text = stringResource(id = R.string.site_url_title),
                    textAlign = TextAlign.Center,
                    modifier = modifier.fillMaxWidth()
                )
            },
            text = {
                TextField(
                    value = inputedUrl,
                    onValueChange = { inputedUrl = it },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = size18sp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { onButtonConfirm(inputedUrl) }
                    ),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.DarkGray,
                        focusedContainerColor = Color.DarkGray,
                        focusedIndicatorColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        cursorColor = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                )
            },
            modifier = modifier.fillMaxWidth()
        )
        LaunchedEffect(key1 = Unit) {
            focusRequester.requestFocus()
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun DialogTextButton(
    title: String,
    onButtonClick: () -> Unit,
) {
    TextButton(
        onClick = onButtonClick,
        colors = ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colorScheme.onBackground,
            containerColor = Color.DarkGray
        )
    ) {
        Text(text = title)
    }
}