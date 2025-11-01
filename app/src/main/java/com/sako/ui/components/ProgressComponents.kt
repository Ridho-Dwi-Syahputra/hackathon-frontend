package com.sako.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sako.ui.theme.ProgressBarBackground
import com.sako.ui.theme.ProgressBarFill
import com.sako.ui.theme.SakoTheme

/**
 * SAKO Progress Bar - Linear progress bar untuk progres kategori/level
 *
 * @param progress Progress value (0.0 - 1.0)
 * @param modifier Modifier untuk styling tambahan
 * @param progressColor Warna progress fill
 * @param backgroundColor Warna background
 */
@Composable
fun SakoProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    progressColor: Color = ProgressBarFill,
    backgroundColor: Color = ProgressBarBackground
) {
    LinearProgressIndicator(
        progress = progress,
        modifier = modifier
            .fillMaxWidth()
            .size(height = 8.dp, width = Dp.Unspecified),
        color = progressColor,
        trackColor = backgroundColor,
    )
}

/**
 * SAKO Progress Bar with Label - Progress bar dengan label persentase
 *
 * @param progress Progress value (0.0 - 1.0)
 * @param label Label yang ditampilkan
 * @param modifier Modifier untuk styling tambahan
 */
@Composable
fun SakoProgressBarWithLabel(
    progress: Float,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        VerticalSpacer(height = SakoSpacing.ExtraSmall)

        SakoProgressBar(progress = progress)
    }
}

/**
 * SAKO Circular Progress - Circular progress indicator untuk level status
 *
 * @param progress Progress value (0.0 - 1.0)
 * @param size Ukuran circular progress
 * @param strokeWidth Ketebalan stroke
 * @param modifier Modifier untuk styling tambahan
 * @param progressColor Warna progress
 * @param backgroundColor Warna background
 */
@Composable
fun SakoCircularProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 60.dp,
    strokeWidth: Dp = 6.dp,
    progressColor: Color = ProgressBarFill,
    backgroundColor: Color = ProgressBarBackground
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val canvasSize = this.size.minDimension
            val radius = (canvasSize / 2) - strokeWidth.toPx() / 2

            // Background circle
            drawArc(
                color = backgroundColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(
                    x = (this.size.width - canvasSize) / 2 + strokeWidth.toPx() / 2,
                    y = (this.size.height - canvasSize) / 2 + strokeWidth.toPx() / 2
                ),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )

            // Progress arc
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = Offset(
                    x = (this.size.width - canvasSize) / 2 + strokeWidth.toPx() / 2,
                    y = (this.size.height - canvasSize) / 2 + strokeWidth.toPx() / 2
                ),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }

        // Percentage text
        Text(
            text = "${(progress * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * SAKO XP Display - Component untuk menampilkan XP dengan icon
 *
 * @param xp Total XP pengguna
 * @param modifier Modifier untuk styling tambahan
 */
@Composable
fun SakoXPDisplay(
    xp: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = "$xp XP",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun SakoProgressBarPreview() {
    SakoTheme {
        SakoProgressBar(progress = 0.6f)
    }
}

@Preview(showBackground = true)
@Composable
private fun SakoProgressBarWithLabelPreview() {
    SakoTheme {
        SakoProgressBarWithLabel(
            progress = 0.75f,
            label = "Progres: 75%"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SakoCircularProgressPreview() {
    SakoTheme {
        SakoCircularProgress(progress = 0.65f)
    }
}

@Preview(showBackground = true)
@Composable
private fun SakoXPDisplayPreview() {
    SakoTheme {
        SakoXPDisplay(xp = 1250)
    }
}