package dev.lciszewski27.whereismymoney.ui.settings

import dev.lciszewski27.whereismymoney.domain.model.ExchangeRate

/**
 * UI state for the Settings screen.
 */
data class SettingsUiState(
    val primaryCurrency: String = "PLN",
    val dynamicColorEnabled: Boolean = true,
    val darkThemeMode: ThemeMode = ThemeMode.AUTO,
    val exchangeRates: List<ExchangeRate> = emptyList()
)

enum class ThemeMode(val displayName: String, val value: String) {
    AUTO("System", "auto"),
    LIGHT("Light", "light"),
    DARK("Dark", "dark")
}

sealed interface SettingsUiEvent {
    data class SetPrimaryCurrency(val currency: String) : SettingsUiEvent
    data class ToggleDynamicColor(val enabled: Boolean) : SettingsUiEvent
    data class SetThemeMode(val mode: ThemeMode) : SettingsUiEvent
    data class AddExchangeRate(val from: String, val to: String, val rate: Double) : SettingsUiEvent
    data class RemoveExchangeRate(val from: String, val to: String) : SettingsUiEvent
    data object ExportBackup : SettingsUiEvent
    data object ImportBackup : SettingsUiEvent
    data object NavigateBack : SettingsUiEvent
    data object OpenContributors : SettingsUiEvent
}