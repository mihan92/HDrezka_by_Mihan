package com.mihan.movie.library.presentation.ui.view

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import coil.compose.SubcomposeAsyncImage
import com.mihan.movie.library.presentation.ui.size100dp
import com.mihan.movie.library.presentation.ui.size8dp

private const val POSTER_ASPECT_RATIO = 1 / 1.4f

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PosterView(
    imageUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    SubcomposeAsyncImage(
        model = imageUrl,
        loading = { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) },
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .clip(RoundedCornerShape(size8dp))
            .width(size100dp)
            .aspectRatio(POSTER_ASPECT_RATIO),
    )
}