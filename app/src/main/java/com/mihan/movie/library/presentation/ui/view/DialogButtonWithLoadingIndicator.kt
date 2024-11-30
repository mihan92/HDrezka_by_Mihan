package com.mihan.movie.library.presentation.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.tv.material3.MaterialTheme
import com.mihan.movie.library.presentation.ui.size10dp
import com.mihan.movie.library.presentation.ui.theme.dialogBgColor

@Composable
fun DialogButtonWithLoadingIndicator(
    title: String,
    isEnabled: Boolean,
    isLoading: Boolean,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!isLoading)
        TextButton(
            enabled = isEnabled,
            onClick = onButtonClick,
            shape = RoundedCornerShape(size10dp),
            colors = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.onBackground,
                containerColor = dialogBgColor
            ),
            modifier = modifier
        ) {
            Text(text = title)
        }
    else
        Box(
            modifier = modifier.fillMaxHeight(0.05f),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = modifier.fillMaxSize(0.1f)
            )
        }

}