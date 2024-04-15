package com.mihan.movie.library.presentation.ui.theme

import androidx.compose.runtime.Composable
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme
import com.mihan.movie.library.common.entites.Colors

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MovieLibraryTheme(
    selectedColor: Colors,
    content: @Composable () -> Unit,
) {
    val colorScheme = darkColorScheme(
        primary = selectedColor.primaryColor,
        onPrimary = selectedColor.onPrimaryColor,
        secondary = secondaryColor,
        background = backgroundColor,
        onBackground = onBackgroundColor
    )
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}