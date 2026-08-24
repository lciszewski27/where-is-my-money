package dev.lciszewski27.whereismymoney.ui.person

import dev.lciszewski27.whereismymoney.domain.model.Debt
import dev.lciszewski27.whereismymoney.domain.model.Person
import dev.lciszewski27.whereismymoney.domain.usecase.PersonDetailData

/**
 * UI state for the Person Detail screen.
 */
data class PersonDetailUiState(
    val person: Person? = null,
    val debts: List<Debt> = emptyList(),
    val netCents: Long = 0L,
    val netCurrency: String = "PLN",
    val isLoading: Boolean = true
)

sealed interface PersonDetailUiEvent {
    data object NavigateBack : PersonDetailUiEvent
    data object SettleAll : PersonDetailUiEvent
    data object SendReminder : PersonDetailUiEvent
    data class DeleteDebt(val debtId: String) : PersonDetailUiEvent
    data class ToggleSettled(val debtId: String) : PersonDetailUiEvent
    data object AddDebt : PersonDetailUiEvent
}