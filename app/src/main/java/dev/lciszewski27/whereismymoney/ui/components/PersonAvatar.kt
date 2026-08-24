package dev.lciszewski27.whereismymoney.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Dynamic avatar color palette derived from Material You color roles.
 * These stay consistent across light/dark and adapt to the wallpaper.
 */
private val avatarColorRoles = listOf(
    AvatarColorRole(Color(0xFF1A6B52), Color(0xFFFFFFFF)),  // teal
    AvatarColorRole(Color(0xFF4A6FA5), Color(0xFFFFFFFF)),  // blue
    AvatarColorRole(Color(0xFF7B52AB), Color(0xFFFFFFFF)),  // purple
    AvatarColorRole(Color(0xFFC43E00), Color(0xFFFFFFFF)),  // orange
    AvatarColorRole(Color(0xFF00897B), Color(0xFFFFFFFF)),  // teal
    AvatarColorRole(Color(0xFF5C6BC0), Color(0xFFFFFFFF)),  // indigo
    AvatarColorRole(Color(0xFFAD1457), Color(0xFFFFFFFF)),  // pink
    AvatarColorRole(Color(0xFFF57F17), Color(0xFFFFFFFF)),  // amber
    AvatarColorRole(Color(0xFF00695C), Color(0xFFFFFFFF)),  // dark teal
    AvatarColorRole(Color(0xFF4527A0), Color(0xFFFFFFFF)),  // deep purple
    AvatarColorRole(Color(0xFFBF360C), Color(0xFFFFFFFF)),  // deep orange
    AvatarColorRole(Color(0xFF283593), Color(0xFFFFFFFF)),  // indigo dark
)

private data class AvatarColorRole(val background: Color, val content: Color)

/**
 * Select a deterministic color role from a seed value.
 */
private fun seedToColorRole(seed: Long): AvatarColorRole {
    return avatarColorRoles[kotlin.math.abs(seed.toInt()) % avatarColorRoles.size]
}

/**
 * Circular avatar showing the first character of a person's name.
 * Uses Material You-inspired color palette.
 */
@Composable
fun PersonAvatar(
    name: String,
    colorSeed: Long,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    val initial = name.firstOrNull()?.uppercase() ?: "?"
    val colorRole = remember(colorSeed) { seedToColorRole(colorSeed) }

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