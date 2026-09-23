package dev.lciszewski27.whereismymoney.ui.person

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.lciszewski27.whereismymoney.data.local.preferences.UserPreferencesDataStore
import dev.lciszewski27.whereismymoney.domain.model.Debt
import dev.lciszewski27.whereismymoney.domain.model.Payment
import dev.lciszewski27.whereismymoney.domain.model.PaymentKind
import dev.lciszewski27.whereismymoney.domain.repository.DebtRepository
import dev.lciszewski27.whereismymoney.domain.usecase.GetPersonDetailUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PersonDetailViewModel(
    private val personId: String,
    private val repository: DebtRepository,
    private val getPersonDetail: GetPersonDetailUseCase,
    private val preferences: UserPreferencesDataStore
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
            combine(
                getPersonDetail(personId),
                preferences.confirmBeforeSettle
            ) { data, confirm -> data to confirm }.collect { (data, confirm) ->
                _uiState.update { state ->
                    state.copy(
                        person = data.person,
                        debts = data.debts,
                        payments = data.payments,
                        netCents = data.netCents,
                        netCurrency = data.netCurrency,
                        confirmBeforeSettle = confirm,
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
                    val active = repository.observeDebtsForPerson(personId).first()
                        .filter { !it.isSettled }
                    repository.settleAllForPerson(personId)
                    val now = System.currentTimeMillis()
                    for (debt in active) {
                        repository.recordPayment(
                            Payment(
                                id = java.util.UUID.randomUUID().toString(),
                                debtId = debt.id,
                                personId = personId,
                                amountCents = debt.amountCents,
                                currency = debt.currency,
                                timestamp = now,
                                kind = PaymentKind.SETTLE_ALL
                            )
                        )
                    }
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
                    val settling = !debt.isSettled
                    repository.updateDebt(debt.copy(isSettled = settling))
                    if (settling) {
                        repository.recordPayment(
                            Payment(
                                id = java.util.UUID.randomUUID().toString(),
                                debtId = debt.id,
                                personId = personId,
                                amountCents = debt.amountCents,
                                currency = debt.currency,
                                timestamp = System.currentTimeMillis(),
                                kind = PaymentKind.FULL
                            )
                        )
                    }
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
                        repository.recordPayment(
                            Payment(
                                id = java.util.UUID.randomUUID().toString(),
                                debtId = debt.id,
                                personId = personId,
                                amountCents = debt.amountCents,
                                currency = debt.currency,
                                timestamp = System.currentTimeMillis(),
                                kind = PaymentKind.FULL
                            )
                        )
                    } else if (event.amountCents > 0) {
                        // Partial settle: mark the original debt as settled (preserves base amount for history/stats)
                        repository.updateDebt(debt.copy(isSettled = true))
                        repository.recordPayment(
                            Payment(
                                id = java.util.UUID.randomUUID().toString(),
                                debtId = debt.id,
                                personId = personId,
                                amountCents = event.amountCents,
                                currency = debt.currency,
                                timestamp = System.currentTimeMillis(),
                                kind = PaymentKind.PARTIAL
                            )
                        )
                        // Create a new debt with the REMAINING amount so current balance reflects correctly
                        val remainingDebt = debt.copy(
                            id = java.util.UUID.randomUUID().toString(),
                            amountCents = debt.amountCents - event.amountCents,
                            description = debt.description.ifBlank { "Remaining" },
                            timestamp = System.currentTimeMillis(),
                            isSettled = false
                        )
                        repository.insertDebt(remainingDebt)
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