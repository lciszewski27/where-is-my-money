package dev.lciszewski27.whereismymoney.ui.settings

import androidx.compose.ui.graphics.vector.ImageVector

internal enum class SettingsPage {
    MAIN, APPEARANCE, CURRENCY, EXCHANGE_RATES, BACKUP, ABOUT, CATEGORIES
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
