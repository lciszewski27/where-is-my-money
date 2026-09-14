package dev.lciszewski27.whereismymoney.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.lciszewski27.whereismymoney.data.local.preferences.UserPreferencesDataStore
import dev.lciszewski27.whereismymoney.domain.model.StatsSummary
import dev.lciszewski27.whereismymoney.domain.repository.DebtRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StatsViewModel(
    private val repository: DebtRepository,
    private val preferences: UserPreferencesDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            val primaryCurrency = preferences.primaryCurrency.first()
            val stats = repository.getStatsSummary(primaryCurrency)
            _uiState.update { it.copy(stats = stats, isLoading = false) }
        }
    }

    fun onEvent(event: StatsUiEvent) {
        when (event) {
            is StatsUiEvent.Refresh -> loadStats()
        }
    }
}

data class StatsUiState(
    val stats: StatsSummary = StatsSummary.EMPTY,
    val isLoading: Boolean = true
)

sealed interface StatsUiEvent {
    data object Refresh : StatsUiEvent
}