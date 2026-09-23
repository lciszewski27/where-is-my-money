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

/**
 * Circular avatar showing the first character of a person's name.
 * Color is deterministic per [colorSeed] via [rememberSeedColorRole].
 */
@Composable
fun PersonAvatar(
    name: String,
    colorSeed: Long,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    val initial = name.firstOrNull()?.uppercase() ?: "?"
    val colorRole = rememberSeedColorRole(colorSeed)

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(colorRole.background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            color = colorRole.content,
            fontWeight = FontWeight.Bold,
            fontSize = (size.value * 0.45).sp
        )
    }
}