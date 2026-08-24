package dev.lciszewski27.whereismymoney.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.lciszewski27.whereismymoney.data.local.preferences.UserPreferencesDataStore
import dev.lciszewski27.whereismymoney.domain.repository.DebtRepository
import dev.lciszewski27.whereismymoney.domain.usecase.GetDashboardSummaryUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModel(
    private val repository: DebtRepository,
    private val preferences: UserPreferencesDataStore,
    private val dashboardSummaryUseCase: GetDashboardSummaryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    /** Navigation events */
    private val _navigateToAddDebt = MutableSharedFlow<Pair<String, dev.lciszewski27.whereismymoney.domain.model.DebtType>?>()
    val navigateToAddDebt: SharedFlow<Pair<String, dev.lciszewski27.whereismymoney.domain.model.DebtType>?> = _navigateToAddDebt.asSharedFlow()

    private val _navigateToPerson = MutableSharedFlow<String>()
    val navigateToPerson: SharedFlow<String> = _navigateToPerson.asSharedFlow()

    private val _navigateToSettings = MutableSharedFlow<Unit>()
    val navigateToSettings: SharedFlow<Unit> = _navigateToSettings.asSharedFlow()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                preferences.primaryCurrency,
                _uiState.map { it.searchQuery },
                _uiState.map { it.filterType }
            ) { currency, query, filter -> Triple(currency, query, filter) }
            .flatMapLatest { (currency, query, filter) ->
                val summaryFlow = dashboardSummaryUseCase(currency)
                val personsFlow = repository.observePersonsWithBalance(currency).map { list ->
                    list.filter { person ->
                        val matchesQuery = person.name.contains(query, ignoreCase = true)
                        val matchesFilter = when (filter) {
                            DebtFilterType.ALL -> true
                            DebtFilterType.THEY_OWE_ME -> person.balanceCents > 0
                            DebtFilterType.I_OWE_THEM -> person.balanceCents < 0
                        }
                        matchesQuery && matchesFilter
                    }
                }

                combine(personsFlow, summaryFlow) { persons, summary ->
                    _uiState.update { state ->
                        state.copy(
                            persons = persons,
                            summary = summary,
                            isLoading = false
                        )
                    }
                }
            }
            .collect { }
        }
    }

    fun onEvent(event: DashboardUiEvent) {
        when (event) {
            is DashboardUiEvent.Search -> {
                _uiState.update { it.copy(searchQuery = event.query) }
            }
            is DashboardUiEvent.SetFilter -> {
                _uiState.update { it.copy(filterType = event.filter) }
            }
            is DashboardUiEvent.ClearSearch -> {
                _uiState.update { it.copy(searchQuery = "") }
            }
            is DashboardUiEvent.AddDebt -> {
                viewModelScope.launch { _navigateToAddDebt.emit(null) }
            }
            is DashboardUiEvent.OpenPerson -> {
                viewModelScope.launch { _navigateToPerson.emit(event.personId) }
            }
            is DashboardUiEvent.OpenSettings -> {
                viewModelScope.launch { _navigateToSettings.emit(Unit) }
            }
            is DashboardUiEvent.SettlePerson -> {
                viewModelScope.launch {
                    repository.settleAllForPerson(event.personId)
                }
            }
            is DashboardUiEvent.DeletePerson -> {
                viewModelScope.launch {
                    repository.deletePerson(event.personId)
                }
            }
            is DashboardUiEvent.QuickAddDebt -> {
                viewModelScope.launch {
                    _navigateToAddDebt.emit(event.personId to event.type)
                }
            }
            is DashboardUiEvent.CreatePerson -> {
                viewModelScope.launch {
                    val newPerson = dev.lciszewski27.whereismymoney.domain.model.Person(
                        id = java.util.UUID.randomUUID().toString(),
                        name = event.name.trim(),
                        colorSeed = System.currentTimeMillis(),
                        createdAt = System.currentTimeMillis()
                    )
                    repository.insertPerson(newPerson)
                    _navigateToPerson.emit(newPerson.id)
                }
            }
        }
    }
}