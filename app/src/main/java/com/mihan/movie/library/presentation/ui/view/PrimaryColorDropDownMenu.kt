package com.mihan.movie.library.presentation.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MenuDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.tv.material3.MaterialTheme
import com.mihan.movie.library.common.models.Colors
import com.mihan.movie.library.presentation.ui.size100dp
import com.mihan.movie.library.presentation.ui.size16dp
import com.mihan.movie.library.presentation.ui.size20dp
import com.mihan.movie.library.presentation.ui.sizeEmpty

@Composable
fun PrimaryColorDropDownMenu(
    primaryColor: Colors,
    onColorItemClicked: (Colors) -> Unit,
    isFocused: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        DropDownHeader(
            selectedColor = primaryColor.primaryColor,
            expanded = expanded,
            modifier = modifier
                .onFocusChanged { isFocused(it.isFocused) }
                .clickable { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = modifier
                .fillMaxWidth(fraction = 0.12f)
                .background(Color.DarkGray),
            offset = DpOffset(size16dp, sizeEmpty)
        ) {
            Colors.entries.forEach {
                DropdownMenuItem(
                    text = { Box(
                        modifier = modifier
                                .width(size100dp)
                                .height(size20dp)
                                .background(it.primaryColor)
                        )
                    },
                    onClick = {
                        onColorItemClicked(it)
                        expanded = false
                    },
                    colors = MenuDefaults.itemColors(textColor = MaterialTheme.colorScheme.onBackground),
                )
            }
        }
    }
}