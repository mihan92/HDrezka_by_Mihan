package com.mihan.movie.library.presentation.ui.view

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.tv.material3.MaterialTheme
import com.mihan.movie.library.presentation.ui.size10dp
import com.mihan.movie.library.presentation.ui.theme.dialogBgColor

@Composable
fun DialogButton(
    title: String,
    isEnabled: Boolean,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
}