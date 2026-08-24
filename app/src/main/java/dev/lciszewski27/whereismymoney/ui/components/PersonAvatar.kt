package dev.lciszewski27.whereismymoney.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

private val avatarColors = listOf(
    0xFF1A6B52, 0xFF4A6Fa5, 0xFF9C27B0, 0xFFE65100,
    0xFF00897B, 0xFF5C6BC0, 0xFFD81B60, 0xFFF57F17,
    0xFF00695C, 0xFF4527A0, 0xFFC62828, 0xFF283593
)

/**
 * Generate a deterministic color from a seed.
 */
fun seedToColor(seed: Long): Long {
    return avatarColors[abs(seed.toInt()) % avatarColors.size]
}

/**
 * Circular avatar showing the first character of a person's name.
 */
@Composable
fun PersonAvatar(
    name: String,
    colorSeed: Long,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    val initial = name.firstOrNull()?.uppercase() ?: "?"
    val bgColor = seedToColor(colorSeed)

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(androidx.compose.ui.graphics.Color(bgColor)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            color = androidx.compose.ui.graphics.Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = (size.value * 0.45).sp
        )
    }
}