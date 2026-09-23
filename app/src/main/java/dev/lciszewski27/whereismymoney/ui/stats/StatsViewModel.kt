package dev.lciszewski27.whereismymoney.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.lciszewski27.whereismymoney.data.local.preferences.UserPreferencesDataStore
import dev.lciszewski27.whereismymoney.domain.model.SettleTransfer
import dev.lciszewski27.whereismymoney.domain.model.StatsSummary
import dev.lciszewski27.whereismymoney.domain.repository.DebtRepository
import dev.lciszewski27.whereismymoney.domain.usecase.SettleUpUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StatsViewModel(
    private val repository: DebtRepository,
    private val preferences: UserPreferencesDataStore,
    private val settleUpUseCase: SettleUpUseCase
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
            val balances = repository.observePersonsWithBalance(primaryCurrency).first()
                .filter { it.balanceCents != 0L }
                .map { person ->
                    SettleUpUseCase.Balance(
                        personId = person.id,
                        personName = person.name,
                        personColorSeed = person.colorSeed,
                        balanceCents = person.balanceCents
                    )
                }
            val suggestions = settleUpUseCase.suggest(balances, primaryCurrency)
            _uiState.update {
                it.copy(
                    stats = stats,
                    settleSuggestions = suggestions,
                    settleCurrency = primaryCurrency,
                    isLoading = false
                )
            }
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
    val settleSuggestions: List<SettleTransfer> = emptyList(),
    val settleCurrency: String = "PLN",
    val isLoading: Boolean = true
)

sealed interface StatsUiEvent {
    data object Refresh : StatsUiEvent
}