package dev.lciszewski27.whereismymoney.ui.theme

import androidx.compose.ui.graphics.Color

// ── Light fallback palette (vibrant, finance-oriented) ──────────────
val LightPrimary = Color(0xFF1A6B52)          // Deep teal-green
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFFA7F5D2)
val LightOnPrimaryContainer = Color(0xFF002117)
val LightSecondary = Color(0xFF4D6358)
val LightOnSecondary = Color(0xFFFFFFFF)
val LightSecondaryContainer = Color(0xFFCFE9DB)
val LightOnSecondaryContainer = Color(0xFF0A1F17)
val LightTertiary = Color(0xFF3C6471)
val LightOnTertiary = Color(0xFFFFFFFF)
val LightTertiaryContainer = Color(0xFFBEE9F8)
val LightOnTertiaryContainer = Color(0xFF001F27)
val LightBackground = Color(0xFFFBFDF8)
val LightOnBackground = Color(0xFF191C1A)
val LightSurface = Color(0xFFFBFDF8)
val LightOnSurface = Color(0xFF191C1A)
val LightSurfaceVariant = Color(0xFFDCE5DB)
val LightOnSurfaceVariant = Color(0xFF414942)
val LightSurfaceContainerLowest = Color(0xFFFFFFFF)
val LightSurfaceContainerLow = Color(0xFFF5F8F2)
val LightSurfaceContainer = Color(0xFFEFF2EC)
val LightSurfaceContainerHigh = Color(0xFFE9ECE7)
val LightSurfaceContainerHighest = Color(0xFFE3E6E1)
val LightSurfaceDim = Color(0xFFDBDED9)
val LightSurfaceBright = Color(0xFFFBFDF8)
val LightError = Color(0xFFBA1A1A)
val LightOnError = Color(0xFFFFFFFF)
val LightErrorContainer = Color(0xFFFFDAD6)
val LightOnErrorContainer = Color(0xFF410002)
val LightOutline = Color(0xFF717972)
val LightOutlineVariant = Color(0xFFC0C9BE)

// ── Dark fallback palette ───────────────────────────────────────────
val DarkPrimary = Color(0xFF8BD9B8)
val DarkOnPrimary = Color(0xFF003828)
val DarkPrimaryContainer = Color(0xFF00513C)
val DarkOnPrimaryContainer = Color(0xFFA7F5D2)
val DarkSecondary = Color(0xFFB3CCB9)
val DarkOnSecondary = Color(0xFF1F3529)
val DarkSecondaryContainer = Color(0xFF354B3E)
val DarkOnSecondaryContainer = Color(0xFFCEE8D4)
val DarkTertiary = Color(0xFF92CDDC)
val DarkOnTertiary = Color(0xFF003640)
val DarkTertiaryContainer = Color(0xFF214C58)
val DarkOnTertiaryContainer = Color(0xFFBEE9F8)
val DarkBackground = Color(0xFF191C1A)
val DarkOnBackground = Color(0xFFE1E3DD)
val DarkSurface = Color(0xFF191C1A)
val DarkOnSurface = Color(0xFFE1E3DD)
val DarkSurfaceVariant = Color(0xFF414942)
val DarkOnSurfaceVariant = Color(0xFFC0C9BE)
val DarkSurfaceContainerLowest = Color(0xFF141715)
val DarkSurfaceContainerLow = Color(0xFF1C1F1D)
val DarkSurfaceContainer = Color(0xFF202321)
val DarkSurfaceContainerHigh = Color(0xFF2A2E2B)
val DarkSurfaceContainerHighest = Color(0xFF353936)
val DarkSurfaceDim = Color(0xFF191C1A)
val DarkSurfaceBright = Color(0xFF3F4340)
val DarkError = Color(0xFFFFB4AB)
val DarkOnError = Color(0xFF690005)
val DarkErrorContainer = Color(0xFF93000A)
val DarkOnErrorContainer = Color(0xFFFFDAD6)
val DarkOutline = Color(0xFF8A9389)
val DarkOutlineVariant = Color(0xFF414942)

/**
 * Semantic tone roles used throughout the app.
 * These map to MD3 color scheme roles so they adapt to dynamic color / dark theme.
 *
 * - positive (receivables / they owe me) → tertiary
 * - negative (payables / I owe them) → error
 * - settled / neutral → outline
 */
object MoneySemanticColors {
    val positive: Color get() = Color.Unspecified // placeholder; use MaterialTheme.colorScheme.tertiary
    val negative: Color get() = Color.Unspecified // placeholder; use MaterialTheme.colorScheme.error
    val settled: Color get() = Color.Unspecified  // placeholder; use MaterialTheme.colorScheme.outline
}