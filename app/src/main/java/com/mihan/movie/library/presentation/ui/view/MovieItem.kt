package com.mihan.movie.library.presentation.ui.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import coil.compose.SubcomposeAsyncImage
import com.mihan.movie.library.R
import com.mihan.movie.library.common.models.VideoCategory
import com.mihan.movie.library.presentation.ui.size10dp
import com.mihan.movie.library.presentation.ui.size18sp
import com.mihan.movie.library.presentation.ui.size2dp
import com.mihan.movie.library.presentation.ui.size4dp
import com.mihan.movie.library.presentation.ui.size6dp
import com.mihan.movie.library.presentation.ui.size8dp

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MovieItem(
    title: String,
    category: String,
    imageUrl: String,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var borderColor by remember { mutableStateOf(Color.Transparent) }
    val primaryColor = MaterialTheme.colorScheme.primary
    var isFocused by remember { mutableStateOf(false) }
    Card(
        modifier = modifier
            .aspectRatio(2 / 3f)
            .padding(size10dp)
            .onFocusChanged {
                isFocused = it.isFocused
                borderColor = if (it.isFocused) primaryColor else Color.Transparent
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        onClick = onItemClick,
        shape = RoundedCornerShape(size8dp),
        border = BorderStroke(size2dp, borderColor)
    ) {
        Box(
            contentAlignment = Alignment.TopEnd
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                SubcomposeAsyncImage(
                    model = imageUrl,
                    loading = { LoadingIndicator() },
                    error = { Image(painter = painterResource(R.drawable.no_image_placeholder), null) },
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(RoundedCornerShape(size8dp))
                        .weight(1f),
                )
                VideoTitle(
                    isFocused = isFocused,
                    title = title
                )
            }
            if (category.isNotEmpty())
                Category(
                    filmCategory = category,
                    modifier = Modifier.padding(top = size6dp)
                )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun VideoTitle(
    title: String,
    isFocused: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = size18sp,
            fontWeight = FontWeight.W700,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            textAlign = TextAlign.Center,
            modifier = if (isFocused) {
                modifier
                    .basicMarquee()
                    .padding(size4dp)
            } else {
                modifier
                    .padding(size4dp)
            }
        )
    }
}


@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun Category(
    filmCategory: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                VideoCategory.getColorFromCategory(filmCategory),
                RoundedCornerShape(topStart = size6dp, bottomStart = size6dp)
            )
            .padding(horizontal = size10dp, vertical = size4dp)
    ) {
        Text(
            text = filmCategory,
            fontWeight = FontWeight.W700,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun LoadingIndicator(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}