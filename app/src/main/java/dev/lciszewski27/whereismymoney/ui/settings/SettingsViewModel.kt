package dev.lciszewski27.whereismymoney.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.lciszewski27.whereismymoney.data.local.preferences.UserPreferencesDataStore
import dev.lciszewski27.whereismymoney.domain.repository.DebtRepository
import dev.lciszewski27.whereismymoney.domain.usecase.CurrencyConversionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferences: UserPreferencesDataStore,
    private val currencyConversion: CurrencyConversionUseCase,
    private val repository: DebtRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            val currency = preferences.primaryCurrency.first()
            val dynamic = preferences.dynamicColorEnabled.first()
            val theme = preferences.darkThemeEnabled.first()
            val amoled = preferences.amoledModeEnabled.first()
            val animations = preferences.animationsEnabled.first()
            val presetStr = preferences.colorPreset.first()
            val preset = try {
                ColorPreset.valueOf(presetStr)
            } catch (e: Exception) {
                ColorPreset.DEFAULT
            }
            val fontStr = preferences.appFont.first()
            val font = try {
                AppFont.entries.find { it.value == fontStr } ?: AppFont.QUICKSAND
            } catch (e: Exception) {
                AppFont.QUICKSAND
            }
            val defaultDebtType = preferences.defaultDebtType.first()
            val defaultCategoryId = preferences.defaultCategoryId.first()
            val categories = repository.getCategories()
            val sortOrder = PersonSortOrder.fromValue(preferences.personSortOrder.first())
            val confirmSettle = preferences.confirmBeforeSettle.first()

            val mode = when (theme) {
                "light" -> ThemeMode.LIGHT
                "dark" -> ThemeMode.DARK
                else -> ThemeMode.AUTO
            }

            _uiState.update { state ->
                state.copy(
                    primaryCurrency = currency,
                    dynamicColorEnabled = dynamic,
                    darkThemeMode = mode,
                    amoledModeEnabled = amoled,
                    animationsEnabled = animations,
                    colorPreset = preset,
                    appFont = font,
                    defaultDebtTypeName = defaultDebtType,
                    defaultCategoryId = defaultCategoryId,
                    categories = categories,
                    personSortOrder = sortOrder,
                    confirmBeforeSettle = confirmSettle,
                    exchangeRates = repository.getExchangeRates()
                )
            }
        }
    }

    fun onEvent(event: SettingsUiEvent) {
        when (event) {
            is SettingsUiEvent.SetPrimaryCurrency -> {
                viewModelScope.launch {
                    preferences.setPrimaryCurrency(event.currency)
                    _uiState.update { it.copy(primaryCurrency = event.currency) }
                }
            }

            is SettingsUiEvent.ToggleDynamicColor -> {
                viewModelScope.launch {
                    preferences.setDynamicColorEnabled(event.enabled)
                    _uiState.update { it.copy(dynamicColorEnabled = event.enabled) }
                }
            }

            is SettingsUiEvent.SetThemeMode -> {
                viewModelScope.launch {
                    preferences.setDarkThemeEnabled(event.mode.value)
                    _uiState.update { it.copy(darkThemeMode = event.mode) }
                }
            }

            is SettingsUiEvent.ToggleAmoledMode -> {
                viewModelScope.launch {
                    preferences.setAmoledModeEnabled(event.enabled)
                    _uiState.update { it.copy(amoledModeEnabled = event.enabled) }
                }
            }

            is SettingsUiEvent.ToggleAnimations -> {
                viewModelScope.launch {
                    preferences.setAnimationsEnabled(event.enabled)
                    _uiState.update { it.copy(animationsEnabled = event.enabled) }
                }
            }

            is SettingsUiEvent.SetColorPreset -> {
                viewModelScope.launch {
                    preferences.setColorPreset(event.preset.name)
                    _uiState.update { it.copy(colorPreset = event.preset) }
                }
            }

            is SettingsUiEvent.AddExchangeRate -> {
                viewModelScope.launch {
                    // Persists to Room; the app-level collector re-syncs the
                    // in-memory conversion engine automatically.
                    repository.setExchangeRate(event.from, event.to, event.rate)
                    _uiState.update { it.copy(exchangeRates = repository.getExchangeRates()) }
                }
            }

            is SettingsUiEvent.RemoveExchangeRate -> {
                viewModelScope.launch {
                    repository.removeExchangeRate(event.from, event.to)
                    _uiState.update { it.copy(exchangeRates = repository.getExchangeRates()) }
                }
            }

            is SettingsUiEvent.ExportBackup -> {}
            is SettingsUiEvent.ImportBackup -> {}
            is SettingsUiEvent.NavigateBack -> {}
            is SettingsUiEvent.DismissDropdown -> {
                _uiState.update { it.copy(isDropdownExpanded = false) }
            }

            is SettingsUiEvent.ToggleDropdown -> {
                _uiState.update { it.copy(isDropdownExpanded = !it.isDropdownExpanded) }
            }

            is SettingsUiEvent.DismissThemeDropdown -> {
                _uiState.update { it.copy(isThemeDropdownExpanded = false) }
            }

            is SettingsUiEvent.ToggleThemeDropdown -> {
                _uiState.update { it.copy(isThemeDropdownExpanded = !it.isThemeDropdownExpanded) }
            }

            is SettingsUiEvent.SetAppFont -> {
                viewModelScope.launch {
                    preferences.setAppFont(event.font.value)
                    _uiState.update { it.copy(appFont = event.font) }
                }
            }

            is SettingsUiEvent.ToggleFontDropdown -> {
                _uiState.update { it.copy(isFontDropdownExpanded = !it.isFontDropdownExpanded) }
            }

            is SettingsUiEvent.DismissFontDropdown -> {
                _uiState.update { it.copy(isFontDropdownExpanded = false) }
            }

            is SettingsUiEvent.SetDefaultDebtType -> {
                viewModelScope.launch {
                    preferences.setDefaultDebtType(event.typeName)
                    _uiState.update { it.copy(defaultDebtTypeName = event.typeName) }
                }
            }

            is SettingsUiEvent.ToggleDefaultTypeDropdown -> {
                _uiState.update { it.copy(isDefaultTypeDropdownExpanded = !it.isDefaultTypeDropdownExpanded) }
            }

            is SettingsUiEvent.DismissDefaultTypeDropdown -> {
                _uiState.update { it.copy(isDefaultTypeDropdownExpanded = false) }
            }

            is SettingsUiEvent.SetDefaultCategory -> {
                viewModelScope.launch {
                    preferences.setDefaultCategoryId(event.categoryId)
                    _uiState.update { it.copy(defaultCategoryId = event.categoryId) }
                }
            }

            is SettingsUiEvent.ToggleDefaultCategoryDropdown -> {
                _uiState.update { it.copy(isDefaultCategoryDropdownExpanded = !it.isDefaultCategoryDropdownExpanded) }
            }

            is SettingsUiEvent.DismissDefaultCategoryDropdown -> {
                _uiState.update { it.copy(isDefaultCategoryDropdownExpanded = false) }
            }

            is SettingsUiEvent.SetPersonSortOrder -> {
                viewModelScope.launch {
                    preferences.setPersonSortOrder(event.order.value)
                    _uiState.update { it.copy(personSortOrder = event.order) }
                }
            }

            is SettingsUiEvent.ToggleSortOrderDropdown -> {
                _uiState.update { it.copy(isSortOrderDropdownExpanded = !it.isSortOrderDropdownExpanded) }
            }

            is SettingsUiEvent.DismissSortOrderDropdown -> {
                _uiState.update { it.copy(isSortOrderDropdownExpanded = false) }
            }

            is SettingsUiEvent.ToggleConfirmBeforeSettle -> {
                viewModelScope.launch {
                    preferences.setConfirmBeforeSettle(event.enabled)
                    _uiState.update { it.copy(confirmBeforeSettle = event.enabled) }
                }
            }
        }
    }
}