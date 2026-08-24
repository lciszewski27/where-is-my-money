package dev.lciszewski27.whereismymoney.ui.person

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.lciszewski27.whereismymoney.domain.model.Debt
import dev.lciszewski27.whereismymoney.domain.repository.DebtRepository
import dev.lciszewski27.whereismymoney.domain.usecase.GetPersonDetailUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PersonDetailViewModel(
    private val personId: String,
    private val repository: DebtRepository,
    private val getPersonDetail: GetPersonDetailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PersonDetailUiState())
    val uiState: StateFlow<PersonDetailUiState> = _uiState.asStateFlow()

    private val _navigateBack = MutableSharedFlow<Unit>()
    val navigateBack: SharedFlow<Unit> = _navigateBack.asSharedFlow()

    private val _navigateToAddDebt = MutableSharedFlow<String>()
    val navigateToAddDebt: SharedFlow<String> = _navigateToAddDebt.asSharedFlow()

    private val _navigateToEditDebt = MutableSharedFlow<String>()
    val navigateToEditDebt: SharedFlow<String> = _navigateToEditDebt.asSharedFlow()

    private val _shareIntent = MutableSharedFlow<String>()
    val shareIntent: SharedFlow<String> = _shareIntent.asSharedFlow()

    init {
        observePerson()
    }

    private fun observePerson() {
        viewModelScope.launch {
            getPersonDetail(personId).collect { data ->
                _uiState.update { state ->
                    state.copy(
                        person = data.person,
                        debts = data.debts,
                        netCents = data.netCents,
                        netCurrency = data.netCurrency,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onEvent(event: PersonDetailUiEvent) {
        when (event) {
            is PersonDetailUiEvent.NavigateBack -> {
                viewModelScope.launch { _navigateBack.emit(Unit) }
            }
            is PersonDetailUiEvent.SettleAll -> {
                viewModelScope.launch {
                    repository.settleAllForPerson(personId)
                }
            }
            is PersonDetailUiEvent.SendReminder -> {
                val state = _uiState.value
                val person = state.person ?: return
                val unsettledCount = state.debts.count { !it.isSettled }
                val message = buildString {
                    appendLine("📋 Reminder from Where is my money?")
                    appendLine()
                    appendLine("Hi ${person.name},")
                    appendLine("You have $unsettledCount unsettled debt(s) with me.")
                    appendLine()
                    appendLine("Please check and settle them. 😊")
                }
                viewModelScope.launch {
                    _shareIntent.emit(message)
                }
            }
            is PersonDetailUiEvent.DeleteDebt -> {
                viewModelScope.launch {
                    repository.deleteDebt(event.debtId)
                }
            }
            is PersonDetailUiEvent.ToggleSettled -> {
                viewModelScope.launch {
                    val debt = repository.getDebt(event.debtId) ?: return@launch
                    repository.updateDebt(debt.copy(isSettled = !debt.isSettled))
                }
            }
            is PersonDetailUiEvent.AddDebt -> {
                viewModelScope.launch {
                    _navigateToAddDebt.emit(personId)
                }
            }
            is PersonDetailUiEvent.DeletePerson -> {
                viewModelScope.launch {
                    repository.deletePerson(personId)
                    _navigateBack.emit(Unit)
                }
            }
            is PersonDetailUiEvent.PartialSettle -> {
                viewModelScope.launch {
                    val debt = repository.getDebt(event.debtId) ?: return@launch
                    if (event.amountCents >= debt.amountCents) {
                        // Full settle
                        repository.updateDebt(debt.copy(isSettled = true))
                    } else if (event.amountCents > 0) {
                        // Partial settle: reduce original debt and maybe create a history record?
                        // For simplicity, we just reduce the amount of the current debt.
                        repository.updateDebt(debt.copy(amountCents = debt.amountCents - event.amountCents))
                    }
                }
            }
            is PersonDetailUiEvent.UpdatePerson -> {
                viewModelScope.launch {
                    val person = repository.getPerson(personId) ?: return@launch
                    repository.insertPerson(person.copy(name = event.name, colorSeed = event.colorSeed))
                }
            }
            is PersonDetailUiEvent.EditDebt -> {
                viewModelScope.launch {
                    _navigateToEditDebt.emit(event.debtId)
                }
            }
        }
    }
}