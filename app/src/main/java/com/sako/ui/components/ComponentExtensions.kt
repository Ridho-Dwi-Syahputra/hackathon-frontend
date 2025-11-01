package com.sako.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Vertical Spacer - Helper untuk spacer vertikal dengan height
 *
 * @param height Height dari spacer
 */
@Composable
fun VerticalSpacer(height: Dp) {
    Spacer(modifier = Modifier.height(height))
}

/**
 * Horizontal Spacer - Helper untuk spacer horizontal dengan width
 *
 * @param width Width dari spacer
 */
@Composable
fun HorizontalSpacer(width: Dp) {
    Spacer(modifier = Modifier.width(width))
}

/**
 * Standard vertical spacers untuk konsistensi spacing
 */
object SakoSpacing {
    val ExtraSmall = 4.dp
    val Small = 8.dp
    val Medium = 16.dp
    val Large = 24.dp
    val ExtraLarge = 32.dp
    val Huge = 48.dp
}

/**
 * Extension functions untuk Modifier
 */

/**
 * Conditional modifier - Apply modifier hanya jika condition true
 */
fun Modifier.conditional(
    condition: Boolean,
    modifier: Modifier.() -> Modifier
): Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}