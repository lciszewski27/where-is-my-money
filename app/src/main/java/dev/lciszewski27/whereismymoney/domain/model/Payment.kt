package dev.lciszewski27.whereismymoney.domain.model

/**
 * Domain model for a single recorded payment against a debt.
 * Together these rows form the audit ledger (payment history).
 */
data class Payment(
    val id: String,
    val debtId: String,
    val personId: String,
    val amountCents: Long,
    val currency: String,
    val timestamp: Long,
    val kind: PaymentKind
) {
    companion object {
        val EMPTY = Payment(
            id = "", debtId = "", personId = "", amountCents = 0L,
            currency = "PLN", timestamp = 0L, kind = PaymentKind.PARTIAL
        )
    }
}

enum class PaymentKind {
    PARTIAL,
    FULL,
    SETTLE_ALL;

    val dbValue: String get() = name

    companion object {
        fun fromDb(value: String): PaymentKind = try {
            valueOf(value)
        } catch (e: IllegalArgumentException) {
            PARTIAL
        }
    }
}
