package com.mihan.movie.library.presentation.ui.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.MaterialTheme

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ButtonDelete(
    isFocused: (Boolean) -> Unit,
    onButtonDeleteClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onButtonDeleteClicked,
        colors = ButtonDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.primary,
            focusedContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = modifier.onFocusChanged { isFocused(it.isFocused) }
    ) {
        Icon(
            imageVector = Icons.Rounded.Delete,
            contentDescription = null,
        )
    }
}