package dev.lciszewski27.whereismymoney.ui.settings

import dev.lciszewski27.whereismymoney.domain.model.Category
import dev.lciszewski27.whereismymoney.domain.model.ExchangeRate

/**
 * UI state for the Settings screen.
 */
data class SettingsUiState(
    val primaryCurrency: String = "PLN",
    val dynamicColorEnabled: Boolean = true,
    val darkThemeMode: ThemeMode = ThemeMode.AUTO,
    val amoledModeEnabled: Boolean = false,
    val animationsEnabled: Boolean = true,
    val colorPreset: ColorPreset = ColorPreset.DEFAULT,
    val appFont: AppFont = AppFont.QUICKSAND,
    val defaultDebtTypeName: String = "THEY_OWE_ME",
    val defaultCategoryId: String = "",
    val categories: List<Category> = emptyList(),
    val personSortOrder: PersonSortOrder = PersonSortOrder.NAME,
    val confirmBeforeSettle: Boolean = true,
    val startScreen: AppStartScreen = AppStartScreen.DASHBOARD,
    val exchangeRates: List<ExchangeRate> = emptyList(),
    val isDropdownExpanded: Boolean = false,
    val isThemeDropdownExpanded: Boolean = false,
    val isFontDropdownExpanded: Boolean = false,
    val isDefaultTypeDropdownExpanded: Boolean = false,
    val isDefaultCategoryDropdownExpanded: Boolean = false,
    val isSortOrderDropdownExpanded: Boolean = false,
    val isStartScreenDropdownExpanded: Boolean = false
)

enum class AppFont(val displayName: String, val value: String) {
    QUICKSAND("Quicksand", "quicksand"),
    SYSTEM("System", "system")
}

enum class ThemeMode(val displayName: String, val value: String) {
    AUTO("System", "auto"),
    LIGHT("Light", "light"),
    DARK("Dark", "dark")
}

enum class ColorPreset(val displayName: String) {
    DEFAULT("Default"),
    MONEY_GREEN("Money Green"),
    OCEAN_BLUE("Ocean Blue"),
    ROYAL_PURPLE("Royal Purple"),
    CHARCOAL("Charcoal")
}

enum class PersonSortOrder(val displayName: String, val value: String) {
    NAME("Name (A–Z)", "name"),
    BALANCE("Highest balance first", "balance"),
    RECENT("Recently active first", "recent");

    companion object {
        fun fromValue(value: String): PersonSortOrder =
            entries.find { it.value == value } ?: NAME
    }
}

enum class AppStartScreen(val displayName: String, val value: String) {
    DASHBOARD("Dashboard", "dashboard"),
    STATS("Statistics", "stats");

    companion object {
        fun fromValue(value: String): AppStartScreen =
            entries.find { it.value == value } ?: DASHBOARD
    }
}

sealed interface SettingsUiEvent {
    data class SetPrimaryCurrency(val currency: String) : SettingsUiEvent
    data class ToggleDynamicColor(val enabled: Boolean) : SettingsUiEvent
    data class SetThemeMode(val mode: ThemeMode) : SettingsUiEvent
    data class ToggleAmoledMode(val enabled: Boolean) : SettingsUiEvent
    data class ToggleAnimations(val enabled: Boolean) : SettingsUiEvent
    data class SetColorPreset(val preset: ColorPreset) : SettingsUiEvent
    data class AddExchangeRate(val from: String, val to: String, val rate: Double) : SettingsUiEvent
    data class RemoveExchangeRate(val from: String, val to: String) : SettingsUiEvent
    data object ExportBackup : SettingsUiEvent
    data object ImportBackup : SettingsUiEvent
    data object NavigateBack : SettingsUiEvent
    data object DismissDropdown : SettingsUiEvent
    data object ToggleDropdown : SettingsUiEvent

    data object ToggleThemeDropdown : SettingsUiEvent
    data object DismissThemeDropdown : SettingsUiEvent

    data class SetAppFont(val font: AppFont) : SettingsUiEvent
    data object ToggleFontDropdown : SettingsUiEvent
    data object DismissFontDropdown : SettingsUiEvent

    data class SetDefaultDebtType(val typeName: String) : SettingsUiEvent
    data object ToggleDefaultTypeDropdown : SettingsUiEvent
    data object DismissDefaultTypeDropdown : SettingsUiEvent

    data class SetDefaultCategory(val categoryId: String) : SettingsUiEvent
    data object ToggleDefaultCategoryDropdown : SettingsUiEvent
    data object DismissDefaultCategoryDropdown : SettingsUiEvent

    data class SetPersonSortOrder(val order: PersonSortOrder) : SettingsUiEvent
    data object ToggleSortOrderDropdown : SettingsUiEvent
    data object DismissSortOrderDropdown : SettingsUiEvent

    data class ToggleConfirmBeforeSettle(val enabled: Boolean) : SettingsUiEvent

    data class SetStartScreen(val screen: AppStartScreen) : SettingsUiEvent
    data object ToggleStartScreenDropdown : SettingsUiEvent
    data object DismissStartScreenDropdown : SettingsUiEvent
}