package dev.lciszewski27.whereismymoney.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Expressive Material 3 shapes with distinct morphing geometry.
 * Includes the May 2025 Expressive update corner tokens.
 *
 * - extraSmall: minimal (4dp)
 * - small: subtle (8dp)
 * - medium: balanced (12dp)
 * - large: generous (16dp)
 * - largeIncreases: extra generous (20dp) — Expressive
 * - extraLarge: fully rounded (28dp)
 * - extraLargeIncreased: more rounded (32dp) — Expressive
 * - extraExtraLarge: pill-like (48dp) — Expressive
 */
val MoneyShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

/**
 * Predefined corner radii for one-off usage.
 * Mirrors the shape token scale but can be used directly.
 */
object MoneyCornerRadius {
    /** Pill / fully rounded — for buttons, chips, badges. */
    val pill = 48.dp
    /** Extra expressive rounding for large dialogs, sheets. */
    val extraExtraLarge = 48.dp
    /** Larger sheet / dialog corners — Expressive token. */
    val extraLargeIncreased = 32.dp
    /** Dialog / bottom sheet / FAB corners. */
    val extraLarge = 28.dp
    /** Large increased — expressive cards, containers. */
    val largeIncreased = 20.dp
    /** Large — cards, elevated surfaces. */
    val large = 16.dp
    /** Medium — text fields, menus, small cards. */
    val medium = 12.dp
    /** Small — chips, snackbars. */
    val small = 8.dp
    /** Extra Small — minimal rounding. */
    val extraSmall = 4.dp
}