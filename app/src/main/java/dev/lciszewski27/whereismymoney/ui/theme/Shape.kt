package dev.lciszewski27.whereismymoney.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Expressive Material 3 shapes with distinct morphing geometry.
 * - extraLarge: pill / fully rounded
 * - large: generous rounding
 * - medium: balanced
 * - small: subtle
 * - extraSmall: minimal
 */
val MoneyShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

/** Predefined corner radii for one-off usage. */
object MoneyCornerRadius {
    val pill = 48.dp
    val xl = 28.dp
    val lg = 20.dp
    val md = 14.dp
    val sm = 8.dp
}