package dev.lciszewski27.whereismymoney.domain.model

/**
 * Presentation-ready debt item with resolved person name.
 * Used for upcoming repayments and activity history lists.
 */
data class DebtItemWithPerson(
    val debt: Debt,
    val personName: String,
    val personColorSeed: Long
) {
    val id: String get() = debt.id
    val personId: String get() = debt.personId
}