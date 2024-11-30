package com.mihan.movie.library.presentation.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.tv.material3.MaterialTheme
import com.mihan.movie.library.common.models.VideoCategory

@Composable
fun VideoCategoryDropDownMenu(
    videoCategory: VideoCategory,
    onCategoryItemClicked: (VideoCategory) -> Unit,
    isFocused: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        DropDownHeader(
            selectedItemTitle = stringResource(id = videoCategory.titleResId),
            expanded = expanded,
            modifier = modifier
                .onFocusChanged { isFocused(it.isFocused) }
                .clickable { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = modifier
                .fillMaxWidth(fraction = 0.18f)
                .background(Color.DarkGray)
        ) {
            VideoCategory.entries.forEach {
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = it.titleResId)) },
                    onClick = {
                        onCategoryItemClicked(it)
                        expanded = false
                    },
                    colors = MenuDefaults.itemColors(textColor = MaterialTheme.colorScheme.onBackground),
                )
            }
        }
    }
}