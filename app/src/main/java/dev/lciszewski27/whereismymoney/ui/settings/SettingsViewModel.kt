package dev.lciszewski27.whereismymoney.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.lciszewski27.whereismymoney.data.local.preferences.UserPreferencesDataStore
import dev.lciszewski27.whereismymoney.domain.usecase.CurrencyConversionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferences: UserPreferencesDataStore,
    private val currencyConversion: CurrencyConversionUseCase
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
            val preset = try { ColorPreset.valueOf(presetStr) } catch (e: Exception) { ColorPreset.DEFAULT }
            
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
                    exchangeRates = currencyConversion.getAllRates()
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
                currencyConversion.setRate(event.from, event.to, event.rate)
                _uiState.update { it.copy(exchangeRates = currencyConversion.getAllRates()) }
            }
            is SettingsUiEvent.RemoveExchangeRate -> {
                currencyConversion.removeRate(event.from, event.to)
                _uiState.update { it.copy(exchangeRates = currencyConversion.getAllRates()) }
            }
            is SettingsUiEvent.ExportBackup -> {}
            is SettingsUiEvent.ImportBackup -> {}
            is SettingsUiEvent.NavigateBack -> {}
        }
    }
}