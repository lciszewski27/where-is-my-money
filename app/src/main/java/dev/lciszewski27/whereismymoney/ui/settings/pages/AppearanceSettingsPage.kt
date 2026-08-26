package dev.lciszewski27.whereismymoney.ui.settings.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.lciszewski27.whereismymoney.ui.settings.ColorPreset
import dev.lciszewski27.whereismymoney.ui.settings.SettingsUiEvent
import dev.lciszewski27.whereismymoney.ui.settings.SettingsUiState
import dev.lciszewski27.whereismymoney.ui.settings.ThemeMode
import dev.lciszewski27.whereismymoney.ui.settings.components.SettingsToggle

@Composable
internal fun AppearanceSettingsPage(
    uiState: SettingsUiState,
    onEvent: (SettingsUiEvent) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Material You Toggle
        SettingsToggle(
            title = "Material You",
            subtitle = "Dynamic colors from wallpaper",
            icon = Icons.Filled.Palette,
            checked = uiState.dynamicColorEnabled,
            onCheckedChange = { onEvent(SettingsUiEvent.ToggleDynamicColor(it)) }
        )

        if (!uiState.dynamicColorEnabled) {
            Text(
                "Color Presets",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ColorPreset.entries.forEach { preset ->
                    val isSelected = uiState.colorPreset == preset
                    FilledTonalButton(
                        onClick = { onEvent(SettingsUiEvent.SetColorPreset(preset)) },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            preset.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        HorizontalDivider()

        // Theme Mode
        Text(
            "Theme",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThemeMode.entries.forEach { mode ->
                val isSelected = uiState.darkThemeMode == mode
                FilledTonalButton(
                    onClick = { onEvent(SettingsUiEvent.SetThemeMode(mode)) },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        mode.displayName,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        if (uiState.darkThemeMode != ThemeMode.LIGHT) {
            SettingsToggle(
                title = "AMOLED Pure Black",
                subtitle = "Save battery with true black",
                icon = Icons.Filled.DarkMode,
                checked = uiState.amoledModeEnabled,
                onCheckedChange = { onEvent(SettingsUiEvent.ToggleAmoledMode(it)) }
            )
        }

        HorizontalDivider()

        SettingsToggle(
            title = "Enable Animations",
            subtitle = "Smooth transitions between screens",
            icon = Icons.Filled.Animation,
            checked = uiState.animationsEnabled,
            onCheckedChange = { onEvent(SettingsUiEvent.ToggleAnimations(it)) }
        )
    }
}
