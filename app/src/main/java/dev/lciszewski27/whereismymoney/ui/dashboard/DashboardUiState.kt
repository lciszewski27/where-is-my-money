package dev.lciszewski27.whereismymoney.ui.dashboard

import dev.lciszewski27.whereismymoney.domain.model.DashboardSummary
import dev.lciszewski27.whereismymoney.domain.model.DebtItemWithPerson
import dev.lciszewski27.whereismymoney.domain.model.Person

/**
 * Immutable UI state for the Dashboard screen.
 */
data class DashboardUiState(
    val persons: List<Person> = emptyList(),
    val summary: DashboardSummary = DashboardSummary(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val filterType: DebtFilterType = DebtFilterType.ALL,
    /** Next 10 upcoming repayments sorted by nearest due date. */
    val upcomingRepayments: List<DebtItemWithPerson> = emptyList(),
    /** Chronological log of recent debt activity (max 20). */
    val recentActivity: List<DebtItemWithPerson> = emptyList()
)

enum class DebtFilterType {
    ALL, THEY_OWE_ME, I_OWE_THEM
}

/**
 * Events emitted by the Dashboard screen.
 */
sealed interface DashboardUiEvent {
    data class Search(val query: String) : DashboardUiEvent
    data class SetFilter(val filter: DebtFilterType) : DashboardUiEvent
    data object ClearSearch : DashboardUiEvent
    data object AddDebt : DashboardUiEvent
    data class OpenPerson(val personId: String) : DashboardUiEvent
    data object OpenSettings : DashboardUiEvent
    data class SettlePerson(val personId: String) : DashboardUiEvent
    data class DeletePerson(val personId: String) : DashboardUiEvent
    data class QuickAddDebt(val personId: String, val type: dev.lciszewski27.whereismymoney.domain.model.DebtType) : DashboardUiEvent
    data class CreatePerson(val name: String) : DashboardUiEvent
}