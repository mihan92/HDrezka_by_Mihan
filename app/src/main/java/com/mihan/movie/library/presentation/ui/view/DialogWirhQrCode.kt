package com.mihan.movie.library.presentation.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.mihan.movie.library.R

private const val IMAGE_SIZE_FRACTION = 0.5f

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun QrCodeDialog(
    isShow: Boolean,
    onDialogDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isShow)
        BasicAlertDialog(
            onDismissRequest = onDialogDismiss,
            modifier = modifier
        ) {
            Image(
                painterResource(R.drawable.qr_code),
                null,
                modifier = modifier.fillMaxSize(IMAGE_SIZE_FRACTION)
            )
        }
}