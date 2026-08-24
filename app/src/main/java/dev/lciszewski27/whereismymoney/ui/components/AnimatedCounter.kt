package dev.lciszewski27.whereismymoney.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

/**
 * Animated counter that slides numbers up/down when the amount changes.
 * Uses spring physics for fluid motion.
 */
@Composable
fun AnimatedAmountText(
    amountCents: Long,
    currencySymbol: String,
    modifier: Modifier = Modifier,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.headlineMedium,
    fontWeight: FontWeight = FontWeight.Bold
) {
    val displayText = formatCents(amountCents, currencySymbol)

    AnimatedContent(
        targetState = displayText,
        modifier = modifier,
        transitionSpec = {
            val direction = if (targetState > initialState) -1 else 1
            ContentTransform(
                targetContentEnter = slideInVertically(
                    animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
                    initialOffsetY = { it * direction }
                ) + fadeIn(animationSpec = tween(150)),
                initialContentExit = slideOutVertically(
                    animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
                    targetOffsetY = { it * -direction }
                ) + fadeOut(animationSpec = tween(150))
            )
        },
        label = "AnimatedAmount"
    ) { text ->
        Text(
            text = text,
            style = style,
            fontWeight = fontWeight,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Simple integer/text animated counter without currency.
 */
@Composable
fun AnimatedCounter(
    targetValue: Int,
    modifier: Modifier = Modifier,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.titleLarge,
    fontWeight: FontWeight = FontWeight.Bold
) {
    AnimatedContent(
        targetState = targetValue,
        modifier = modifier,
        transitionSpec = {
            val direction = if (targetState > initialState) -1 else 1
            ContentTransform(
                targetContentEnter = slideInVertically(
                    animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
                    initialOffsetY = { it * direction }
                ) + fadeIn(animationSpec = tween(150)),
                initialContentExit = slideOutVertically(
                    animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
                    targetOffsetY = { it * -direction }
                ) + fadeOut(animationSpec = tween(150))
            )
        },
        label = "AnimatedCounter"
    ) { value ->
        Text(
            text = value.toString(),
            style = style,
            fontWeight = fontWeight,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Format cents to a display string with currency symbol.
 */
fun formatCents(cents: Long, symbol: String): String {
    val sign = if (cents < 0) "-" else ""
    val abs = kotlin.math.abs(cents)
    val major = abs / 100
    val minor = abs % 100
    return "$sign$major.${minor.toString().padStart(2, '0')}$symbol"
}