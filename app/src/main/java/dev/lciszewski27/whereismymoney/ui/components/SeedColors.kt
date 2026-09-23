package dev.lciszewski27.whereismymoney.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

/**
 * Deterministic color roles derived from a seed value.
 * Shared by person avatars and category icons so every named thing in
 * the app gets a stable, recognizable color across light/dark themes.
 */
data class SeedColorRole(val background: Color, val content: Color)

private val seedColorRoles = listOf(
    SeedColorRole(Color(0xFF1A6B52), Color(0xFFFFFFFF)),  // teal
    SeedColorRole(Color(0xFF4A6FA5), Color(0xFFFFFFFF)),  // blue
    SeedColorRole(Color(0xFF7B52AB), Color(0xFFFFFFFF)),  // purple
    SeedColorRole(Color(0xFFC43E00), Color(0xFFFFFFFF)),  // orange
    SeedColorRole(Color(0xFF00897B), Color(0xFFFFFFFF)),  // teal
    SeedColorRole(Color(0xFF5C6BC0), Color(0xFFFFFFFF)),  // indigo
    SeedColorRole(Color(0xFFAD1457), Color(0xFFFFFFFF)),  // pink
    SeedColorRole(Color(0xFFF57F17), Color(0xFFFFFFFF)),  // amber
    SeedColorRole(Color(0xFF00695C), Color(0xFFFFFFFF)),  // dark teal
    SeedColorRole(Color(0xFF4527A0), Color(0xFFFFFFFF)),  // deep purple
    SeedColorRole(Color(0xFFBF360C), Color(0xFFFFFFFF)),  // deep orange
    SeedColorRole(Color(0xFF283593), Color(0xFFFFFFFF)),  // indigo dark
)

/**
 * Select a deterministic color role from a seed value.
 */
@Composable
fun rememberSeedColorRole(seed: Long): SeedColorRole {
    return remember(seed) {
        seedColorRoles[kotlin.math.abs(seed.toInt()) % seedColorRoles.size]
    }
}
