package dev.lciszewski27.whereismymoney.ui.adddebt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.lciszewski27.whereismymoney.data.local.preferences.UserPreferencesDataStore
import dev.lciszewski27.whereismymoney.domain.model.Debt
import dev.lciszewski27.whereismymoney.domain.model.DebtType
import dev.lciszewski27.whereismymoney.domain.model.Person
import dev.lciszewski27.whereismymoney.domain.repository.DebtRepository
import dev.lciszewski27.whereismymoney.domain.util.MoneyInput
import dev.lciszewski27.whereismymoney.domain.util.SplitCalculator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddDebtViewModel(
    private val repository: DebtRepository,
    private val preferences: UserPreferencesDataStore,
    private val editDebtId: String? = null,
    private val initialPersonId: String? = null,
    private val initialDebtType: dev.lciszewski27.whereismymoney.domain.model.DebtType? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddDebtUiState())
    val uiState: StateFlow<AddDebtUiState> = _uiState.asStateFlow()

    private val _dismiss = MutableSharedFlow<Unit>()
    val dismiss: SharedFlow<Unit> = _dismiss.asSharedFlow()

    init {
        loadData()
        if (editDebtId != null) {
            viewModelScope.launch {
                loadDebtForEdit(editDebtId)
            }
        } else {
            // Apply initial values for new debt
            _uiState.update { state ->
                state.copy(debtType = initialDebtType ?: state.debtType)
            }

            if (initialPersonId != null) {
                viewModelScope.launch {
                    val person = repository.getPerson(initialPersonId)
                    if (person != null) {
                        _uiState.update { state ->
                            state.copy(
                                selectedPersonId = person.id,
                                selectedPersonName = person.name
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            val persons = repository.observePersons().first()
            val primaryCurrency = preferences.primaryCurrency.first()
            val categories = repository.observeCategories().first()

            _uiState.update { state ->
                state.copy(
                    persons = persons,
                    currency = primaryCurrency,
                    categories = categories
                )
            }
        }
    }

    private suspend fun loadDebtForEdit(debtId: String) {
        val debt = repository.getDebt(debtId) ?: return
        _uiState.update { state ->
            state.copy(
                isEditing = true,
                editDebtId = debt.id,
                selectedPersonId = debt.personId,
                amountCents = debt.amountCents,
                amountText = MoneyInput.formatCentsForInput(debt.amountCents),
                currency = debt.currency,
                debtType = debt.type,
                description = debt.description,
                dueDateMillis = debt.dueDateMillis,
                selectedCategoryId = debt.categoryId
            )
        }
    }

    fun onEvent(event: AddDebtUiEvent) {
        when (event) {
            is AddDebtUiEvent.SelectPerson -> {
                _uiState.update { it.copy(selectedPersonId = event.personId, selectedPersonName = event.personName) }
            }
            is AddDebtUiEvent.SelectCategory -> {
                _uiState.update { it.copy(selectedCategoryId = event.categoryId) }
            }
            is AddDebtUiEvent.AmountChanged -> {
                val cleaned = event.text.filter { c -> c.isDigit() || c == ',' || c == '.' }
                _uiState.update { it.copy(amountText = cleaned) }
                // Parse to cents
                val cents = MoneyInput.parseInputToCents(cleaned)
                _uiState.update { it.copy(amountCents = cents) }
            }
            is AddDebtUiEvent.CurrencyChanged -> {
                _uiState.update { it.copy(currency = event.currency) }
            }
            is AddDebtUiEvent.DebtTypeChanged -> {
                _uiState.update { it.copy(debtType = event.debtType) }
            }
            is AddDebtUiEvent.DescriptionChanged -> {
                _uiState.update { it.copy(description = event.description) }
            }
            is AddDebtUiEvent.SetDueDate -> {
                _uiState.update { it.copy(dueDateMillis = event.millis, showDatePicker = false) }
            }
            is AddDebtUiEvent.ToggleDatePicker -> {
                _uiState.update { it.copy(showDatePicker = !it.showDatePicker) }
            }
            is AddDebtUiEvent.ToggleNewPersonField -> {
                _uiState.update { it.copy(showNewPersonField = !it.showNewPersonField) }
            }
            is AddDebtUiEvent.NewPersonNameChanged -> {
                _uiState.update { it.copy(newPersonName = event.name) }
            }
            is AddDebtUiEvent.SaveDebt -> saveDebt()
            is AddDebtUiEvent.Dismiss -> viewModelScope.launch { _dismiss.emit(Unit) }
            is AddDebtUiEvent.ToggleSplitMode -> {
                _uiState.update { state ->
                    val enabling = !state.splitMode
                    state.copy(
                        splitMode = enabling,
                        // Pre-select the already chosen person when entering split mode.
                        splitPersonIds = if (enabling) {
                            state.selectedPersonId?.let { setOf(it) } ?: emptySet()
                        } else state.splitPersonIds
                    )
                }
            }
            is AddDebtUiEvent.ToggleSplitPerson -> {
                _uiState.update { state ->
                    val updated = if (event.personId in state.splitPersonIds) {
                        state.splitPersonIds - event.personId
                    } else {
                        state.splitPersonIds + event.personId
                    }
                    state.copy(splitPersonIds = updated)
                }
            }
        }
    }

    /**
     * Per-person shares for the current split selection, in selection order.
     * Used by the sheet preview and by [saveDebt].
     */
    fun currentSplitShares(): List<Pair<String, Long>> {
        val state = _uiState.value
        val ids = state.persons
            .filter { it.id in state.splitPersonIds }
            .map { it.id }
        val shares = SplitCalculator.equalShares(state.amountCents, ids.size)
        return ids.zip(shares)
    }

    private fun saveDebt() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.amountCents <= 0) return@launch

            // ── Group split: one debt per selected person ────────────
            if (state.splitMode && !state.isEditing) {
                val shares = currentSplitShares()
                if (shares.isEmpty()) return@launch
                val now = System.currentTimeMillis()
                for ((personId, shareCents) in shares) {
                    if (shareCents <= 0) continue
                    repository.insertDebt(
                        Debt(
                            id = java.util.UUID.randomUUID().toString(),
                            personId = personId,
                            amountCents = shareCents,
                            currency = state.currency,
                            type = state.debtType,
                            description = state.description.trim(),
                            timestamp = now,
                            dueDateMillis = state.dueDateMillis,
                            isSettled = false,
                            categoryId = state.selectedCategoryId
                        )
                    )
                }
                _dismiss.emit(Unit)
                return@launch
            }

            if (state.selectedPersonId == null && state.newPersonName.isBlank()) return@launch

            val personId = if (state.selectedPersonId != null) {
                state.selectedPersonId
            } else {
                // Create new person
                val newPerson = Person(
                    id = java.util.UUID.randomUUID().toString(),
                    name = state.newPersonName.trim(),
                    colorSeed = System.currentTimeMillis(),
                    createdAt = System.currentTimeMillis()
                )
                repository.insertPerson(newPerson)
                newPerson.id
            }

            val debt = Debt(
                id = state.editDebtId ?: java.util.UUID.randomUUID().toString(),
                personId = personId,
                amountCents = state.amountCents,
                currency = state.currency,
                type = state.debtType,
                description = state.description.trim(),
                timestamp = System.currentTimeMillis(),
                dueDateMillis = state.dueDateMillis,
                isSettled = false,
                categoryId = state.selectedCategoryId
            )

            if (state.isEditing) {
                repository.updateDebt(debt)
            } else {
                repository.insertDebt(debt)
            }

            _dismiss.emit(Unit)
        }
    }
}