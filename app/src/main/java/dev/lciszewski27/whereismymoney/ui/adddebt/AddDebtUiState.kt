package dev.lciszewski27.whereismymoney.ui.adddebt

import dev.lciszewski27.whereismymoney.domain.model.CurrencyInfo
import dev.lciszewski27.whereismymoney.domain.model.DebtType
import dev.lciszewski27.whereismymoney.domain.model.Person

/**
 * Immutable UI state for the Add/Edit Debt bottom sheet.
 */
data class AddDebtUiState(
    val persons: List<Person> = emptyList(),
    val selectedPersonId: String? = null,
    val amountCents: Long = 0L,
    val amountText: String = "",
    val currency: String = "PLN",
    val debtType: DebtType = DebtType.THEY_OWE_ME,
    val description: String = "",
    val dueDateMillis: Long? = null,
    val selectedPersonName: String = "",
    val showNewPersonField: Boolean = false,
    val newPersonName: String = "",
    val showDatePicker: Boolean = false,
    val isEditing: Boolean = false,
    val editDebtId: String? = null
)

/**
 * Events emitted from the Add/Edit Debt bottom sheet.
 */
sealed interface AddDebtUiEvent {
    data class SelectPerson(val personId: String, val personName: String) : AddDebtUiEvent
    data class AmountChanged(val text: String) : AddDebtUiEvent
    data class CurrencyChanged(val currency: String) : AddDebtUiEvent
    data class DebtTypeChanged(val debtType: DebtType) : AddDebtUiEvent
    data class DescriptionChanged(val description: String) : AddDebtUiEvent
    data class SetDueDate(val millis: Long?) : AddDebtUiEvent
    data object ToggleDatePicker : AddDebtUiEvent
    data object ToggleNewPersonField : AddDebtUiEvent
    data class NewPersonNameChanged(val name: String) : AddDebtUiEvent
    data object SaveDebt : AddDebtUiEvent
    data object Dismiss : AddDebtUiEvent
}