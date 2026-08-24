package dev.lciszewski27.whereismymoney.domain.model

/**
 * Domain model for a single debt/IOU transaction.
 * Amount is stored in minor units (cents) for the given [currency].
 */
data class Debt(
    val id: String,
    val personId: String,
    val amountCents: Long,
    val currency: String,
    val type: DebtType,
    val description: String,
    val timestamp: Long,
    val dueDateMillis: Long?,
    val isSettled: Boolean
) {
    /**
     * Human-readable major-unit amount (e.g. "12.50" for 1250 cents).
     */
    val majorAmount: Double get() = amountCents / 100.0

    companion object {
        val EMPTY = Debt(
            id = "", personId = "", amountCents = 0L, currency = "PLN",
            type = DebtType.THEY_OWE_ME, description = "",
            timestamp = 0L, dueDateMillis = null, isSettled = false
        )
    }
}