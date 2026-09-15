package dev.lciszewski27.whereismymoney.ui.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.lciszewski27.whereismymoney.ui.theme.LocalAnimationsEnabled

/**
 * Determinate progress that goes wavy (Material 3 Expressive) when motion is
 * allowed, and falls back to the standard bar when reduced motion is on.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveLinearProgress(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
) {
    if (LocalAnimationsEnabled.current) {
        LinearWavyProgressIndicator(
            progress = progress,
            modifier = modifier,
            color = color,
            trackColor = trackColor,
        )
    } else {
        LinearProgressIndicator(
            progress = progress,
            modifier = modifier,
            color = color,
            trackColor = trackColor,
        )
    }
}

/**
 * Full-screen loading moment: the expressive loading indicator when motion is
 * allowed, standard circular progress with reduced motion.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveLoadingIndicator(
    modifier: Modifier = Modifier,
) {
    if (LocalAnimationsEnabled.current) {
        CircularWavyProgressIndicator(modifier = modifier)
    } else {
        CircularProgressIndicator(modifier = modifier)
    }
}
