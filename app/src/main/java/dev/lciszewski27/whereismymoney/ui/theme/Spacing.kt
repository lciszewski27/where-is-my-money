package dev.lciszewski27.whereismymoney.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * M3 8dp spacing system.
 *
 * MD3 uses an 8dp baseline grid for margins, padding, and component gaps.
 * Use these constants to keep spacing consistent and adaptive.
 */
object MoneySpacing {
    /** Micro spacing — tightest internal padding (2dp). */
    val micro: Dp = 2.dp
    /** Extra extra small (4dp). */
    val xxs: Dp = 4.dp
    /** Extra small (8dp) — baseline grid unit. */
    val xs: Dp = 8.dp
    /** Small (12dp). */
    val sm: Dp = 12.dp
    /** Medium (16dp) — standard content padding. */
    val md: Dp = 16.dp
    /** Large (20dp). */
    val lg: Dp = 20.dp
    /** Extra large (24dp). */
    val xl: Dp = 24.dp
    /** Extra extra large (32dp). */
    val xxl: Dp = 32.dp
    /** Largest — section spacing (48dp). */
    val max: Dp = 48.dp
}

/**
 * Extension to make Dp values more readable.
 */
val Dp.Companion.Micro: Dp get() = 2.dp
val Dp.Companion.XXS: Dp get() = 4.dp
val Dp.Companion.XS: Dp get() = 8.dp
val Dp.Companion.SM: Dp get() = 12.dp
val Dp.Companion.MD: Dp get() = 16.dp
val Dp.Companion.LG: Dp get() = 20.dp
val Dp.Companion.XL: Dp get() = 24.dp
val Dp.Companion.XXL: Dp get() = 32.dp
val Dp.Companion.MAX: Dp get() = 48.dp