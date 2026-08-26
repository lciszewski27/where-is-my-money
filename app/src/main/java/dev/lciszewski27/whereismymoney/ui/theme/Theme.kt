package dev.lciszewski27.whereismymoney.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import dev.lciszewski27.whereismymoney.ui.settings.ColorPreset

// --- DEFAULT SCHEMES (Assuming LightPrimary, etc. are defined in your Color.kt) ---

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    surfaceContainerLowest = LightSurfaceContainerLowest,
    surfaceContainerLow = LightSurfaceContainerLow,
    surfaceContainer = LightSurfaceContainer,
    surfaceContainerHigh = LightSurfaceContainerHigh,
    surfaceContainerHighest = LightSurfaceContainerHighest,
    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    surfaceContainerLowest = DarkSurfaceContainerLowest,
    surfaceContainerLow = DarkSurfaceContainerLow,
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerHigh = DarkSurfaceContainerHigh,
    surfaceContainerHighest = DarkSurfaceContainerHighest,
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant
)

val LocalAnimationsEnabled = staticCompositionLocalOf { true }

// ============================================================================
// EXPRESSIVE CUSTOM PRESETS (Fully populated for proper M3 contrast)
// Expressive uses vibrant complementary Tertiary colors to create UI tension.
// ============================================================================

// --- MONEY GREEN (Earthy Primary + Warm Orange/Rust Tertiary) ---
private val MoneyGreenLight = lightColorScheme(
    primary = Color(0xFF1B6B28),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFA4F5A9),
    onPrimaryContainer = Color(0xFF002106),
    secondary = Color(0xFF52634F),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD5E8CF),
    onSecondaryContainer = Color(0xFF101F10),
    tertiary = Color(0xFF984022),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFDBD1),
    onTertiaryContainer = Color(0xFF3B0900),
    surface = Color(0xFFF7FBF1),
    background = Color(0xFFF7FBF1),
    surfaceContainer = Color(0xFFEDF2E8)
)
private val MoneyGreenDark = darkColorScheme(
    primary = Color(0xFF89D88F),
    onPrimary = Color(0xFF003911),
    primaryContainer = Color(0xFF00531D),
    onPrimaryContainer = Color(0xFFA4F5A9),
    secondary = Color(0xFFB9CCB4),
    onSecondary = Color(0xFF243423),
    secondaryContainer = Color(0xFF3A4B38),
    onSecondaryContainer = Color(0xFFD5E8CF),
    tertiary = Color(0xFFFFB59D),
    onTertiary = Color(0xFF5C1900),
    tertiaryContainer = Color(0xFF7A2A0D),
    onTertiaryContainer = Color(0xFFFFDBD1),
    surface = Color(0xFF11140E),
    background = Color(0xFF11140E),
    surfaceContainer = Color(0xFF1D211A)
)

// --- OCEAN BLUE (Deep Blue Primary + Vibrant Coral Tertiary) ---
private val OceanBlueLight = lightColorScheme(
    primary = Color(0xFF0061A4),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD1E4FF),
    onPrimaryContainer = Color(0xFF001D36),
    secondary = Color(0xFF535F70),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD7E3F7),
    onSecondaryContainer = Color(0xFF101C2B),
    tertiary = Color(0xFFB3261E),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF9DEDC),
    onTertiaryContainer = Color(0xFF410E0B),
    surface = Color(0xFFFDFBFF),
    background = Color(0xFFFDFBFF),
    surfaceContainer = Color(0xFFF2F4FC)
)
private val OceanBlueDark = darkColorScheme(
    primary = Color(0xFF9ECAFF),
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF00497D),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = Color(0xFFBBC7DB),
    onSecondary = Color(0xFF253140),
    secondaryContainer = Color(0xFF3B4858),
    onSecondaryContainer = Color(0xFFD7E3F7),
    tertiary = Color(0xFFF2B8B5),
    onTertiary = Color(0xFF601410),
    tertiaryContainer = Color(0xFF8C1D18),
    onTertiaryContainer = Color(0xFFF9DEDC),
    surface = Color(0xFF1A1C1E),
    background = Color(0xFF1A1C1E),
    surfaceContainer = Color(0xFF202429)
)

// --- ROYAL PURPLE (Rich Purple Primary + Mint/Teal Tertiary) ---
private val RoyalPurpleLight = lightColorScheme(
    primary = Color(0xFF6D28D9),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE9D5FF),
    onPrimaryContainer = Color(0xFF2E0076),
    secondary = Color(0xFF625B71),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE8DEF8),
    onSecondaryContainer = Color(0xFF1D192B),
    tertiary = Color(0xFF006C52),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFF85F8CC),
    onTertiaryContainer = Color(0xFF002116),
    surface = Color(0xFFFFFBFE),
    background = Color(0xFFFFFBFE),
    surfaceContainer = Color(0xFFF4EFF4)
)
private val RoyalPurpleDark = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    onPrimary = Color(0xFF381E72),
    primaryContainer = Color(0xFF4F378B),
    onPrimaryContainer = Color(0xFFEADDFF),
    secondary = Color(0xFFCCC2DC),
    onSecondary = Color(0xFF332D41),
    secondaryContainer = Color(0xFF4A4458),
    onSecondaryContainer = Color(0xFFE8DEF8),
    tertiary = Color(0xFF67DBB1),
    onTertiary = Color(0xFF003829),
    tertiaryContainer = Color(0xFF00513D),
    onTertiaryContainer = Color(0xFF85F8CC),
    surface = Color(0xFF141218),
    background = Color(0xFF141218),
    surfaceContainer = Color(0xFF211F26)
)

// --- CHARCOAL (Sleek Monochromatic + Bright Indigo Tertiary) ---
private val CharcoalLight = lightColorScheme(
    primary = Color(0xFF475569),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE2E8F0),
    onPrimaryContainer = Color(0xFF0F172A),
    secondary = Color(0xFF64748B),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF1F5F9),
    onSecondaryContainer = Color(0xFF1E293B),
    tertiary = Color(0xFF4338CA),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE0E7FF),
    onTertiaryContainer = Color(0xFF1E1B4B),
    surface = Color(0xFFF8FAFC),
    background = Color(0xFFF8FAFC),
    surfaceContainer = Color(0xFFF1F5F9)
)
private val CharcoalDark = darkColorScheme(
    primary = Color(0xFFCBD5E1),
    onPrimary = Color(0xFF1E293B),
    primaryContainer = Color(0xFF334155),
    onPrimaryContainer = Color(0xFFF1F5F9),
    secondary = Color(0xFF94A3B8),
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF1E293B),
    onSecondaryContainer = Color(0xFFE2E8F0),
    tertiary = Color(0xFFA5B4FC),
    onTertiary = Color(0xFF312E81),
    tertiaryContainer = Color(0xFF3730A3),
    onTertiaryContainer = Color(0xFFE0E7FF),
    surface = Color(0xFF0F172A),
    background = Color(0xFF0F172A),
    surfaceContainer = Color(0xFF1E293B)
)

@Composable
fun WhereIsMyMoneyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    amoledMode: Boolean = false,
    animationsEnabled: Boolean = true,
    colorPreset: ColorPreset = ColorPreset.DEFAULT,
    content: @Composable () -> Unit
) {
    // 1. Resolve Color Scheme cleanly, supporting both Light & Dark variations of presets.
    var colorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        else -> {
            when (colorPreset) {
                ColorPreset.MONEY_GREEN -> if (darkTheme) MoneyGreenDark else MoneyGreenLight
                ColorPreset.OCEAN_BLUE -> if (darkTheme) OceanBlueDark else OceanBlueLight
                ColorPreset.ROYAL_PURPLE -> if (darkTheme) RoyalPurpleDark else RoyalPurpleLight
                ColorPreset.CHARCOAL -> if (darkTheme) CharcoalDark else CharcoalLight
                else -> if (darkTheme) DarkColorScheme else LightColorScheme
            }
        }
    }

    // 2. Safely apply AMOLED mode over the evaluated dark scheme
    if (darkTheme && amoledMode) {
        colorScheme = colorScheme.copy(
            background = Color.Black,
            surface = Color.Black,
            surfaceContainerLowest = Color.Black,
            surfaceContainerLow = Color(0xFF0A0A0A),
            surfaceContainer = Color(0xFF121212),
            surfaceContainerHigh = Color(0xFF1A1A1A),
            surfaceContainerHighest = Color(0xFF222222)
        )
    }

    // 3. Status Bar configuration
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Ensure these are defined in your Theme setup
        shapes = MoneyShapes,    // Ensure these are defined in your Theme setup
        content = {
            CompositionLocalProvider(LocalAnimationsEnabled provides animationsEnabled) {
                content()
            }
        }
    )
}