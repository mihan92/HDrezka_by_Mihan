package com.mihan.movie.library.presentation.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.W500
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.DialogProperties
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import com.mihan.movie.library.R
import com.mihan.movie.library.presentation.ui.size16dp
import com.mihan.movie.library.presentation.ui.size16sp
import com.mihan.movie.library.presentation.ui.size18sp
import com.mihan.movie.library.presentation.ui.theme.dialogBgColor

@Composable
fun InformationDialog(
    dialogTitle: String,
    dialogDescription: String,
    showDialogState: Boolean,
    onButtonAgreePressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (showDialogState)
        AlertDialog(
            onDismissRequest = { onButtonAgreePressed() },
            confirmButton = { ConfirmButton(onButtonAgreePressed) },
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
                        dialogTitle,
                        fontSize = size18sp,
                        fontWeight = W700,
                        textAlign = TextAlign.Center,
                        modifier = modifier.padding(bottom = size16dp)
                    )
                    Text(
                        dialogDescription,
                        fontSize = size16sp,
                        fontWeight = W500,
                        textAlign = TextAlign.Justify
                    )
                }
            }
        )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun ConfirmButton(
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onButtonClick,
        colors = ButtonDefaults.colors(
            containerColor = Color.White,
            focusedContainerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
    ) {
        Text(
            stringResource(R.string.understand_title),
            color = Color.Black
        )
    }
}