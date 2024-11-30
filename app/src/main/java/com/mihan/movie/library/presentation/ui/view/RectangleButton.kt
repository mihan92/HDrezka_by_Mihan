package com.mihan.movie.library.presentation.ui.view

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.mihan.movie.library.presentation.ui.size16dp
import com.mihan.movie.library.presentation.ui.size8dp

@Composable
fun RectangleButton(
    text: String,
    onButtonClicked: () -> Unit,
    isFocused: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onButtonClicked,
        colors = ButtonDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.primary,
            focusedContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = ButtonDefaults.shape(RoundedCornerShape(size8dp)),
        modifier = modifier
            .padding(size16dp)
            .onFocusChanged { isFocused(it.isFocused) }
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.W700
        )
    }
}