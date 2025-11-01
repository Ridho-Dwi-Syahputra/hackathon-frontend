package com.sako.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.sako.R

/**
 * Background wrapper untuk screen dengan background image
 * Menampilkan background.png dengan opacity rendah
 */
@Composable
fun BackgroundImage(
    modifier: Modifier = Modifier,
    alpha: Float = 0.5f,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = alpha
        )
        
        // Content di atas background
        content()
    }
}
