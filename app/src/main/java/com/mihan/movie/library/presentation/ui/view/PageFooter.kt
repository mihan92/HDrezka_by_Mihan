package com.mihan.movie.library.presentation.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.tv.material3.IconButton
import androidx.tv.material3.MaterialTheme
import com.mihan.movie.library.R
import com.mihan.movie.library.presentation.ui.size16dp
import com.mihan.movie.library.presentation.ui.size40dp

private const val FULL_LIST_SIZE = 36

@Composable
fun PageFooter(
    currentPage: Int,
    listSize: Int,
    previousPageClick: () -> Unit,
    nextPageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var focusedArrowBackColor by remember { mutableStateOf(Color.White) }
    var focusedArrowNextColor by remember { mutableStateOf(Color.White) }
    Row(
        modifier = modifier
            .height(size40dp)
            .background(MaterialTheme.colorScheme.background.copy(0.8f), CircleShape),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = previousPageClick,
            enabled = currentPage > 1,
            modifier = modifier.onFocusChanged { focusedArrowBackColor = if (it.isFocused) Color.Black else Color.White }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                tint = focusedArrowBackColor,
                contentDescription = null
            )
        }
        Text(
            text = stringResource(id = R.string.current_page_title, currentPage),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.W700,
            modifier = modifier.padding(horizontal = size16dp)
        )
        IconButton(
            onClick = nextPageClick,
            enabled = listSize >= FULL_LIST_SIZE,
            modifier = modifier.onFocusChanged { focusedArrowNextColor = if (it.isFocused) Color.Black else Color.White }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                tint = focusedArrowNextColor,
                contentDescription = null
            )
        }
    }
}