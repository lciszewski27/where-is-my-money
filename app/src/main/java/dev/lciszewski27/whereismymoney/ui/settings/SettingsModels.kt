package dev.lciszewski27.whereismymoney.ui.settings

import androidx.compose.ui.graphics.vector.ImageVector

internal enum class SettingsPage {
    MAIN, GENERAL, APPEARANCE, CURRENCY, EXCHANGE_RATES, BACKUP, ABOUT, CATEGORIES, LICENSES
}

internal data class SettingsGroup(
    val items: List<SettingsItem>
)

internal data class SettingsItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val page: SettingsPage
)
