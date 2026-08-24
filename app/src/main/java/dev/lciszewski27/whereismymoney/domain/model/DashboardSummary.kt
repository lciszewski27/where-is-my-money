package dev.lciszewski27.whereismymoney.domain.model

/**
 * Aggregated dashboard summary computed from all unsettled debts.
 */
data class DashboardSummary(
    val totalReceivablesCents: Long = 0L,
    val totalPayablesCents: Long = 0L,
    val primaryCurrency: String = "PLN",
    /** Receivables converted to primary currency minus payables converted. */
    val netBalanceCents: Long = 0L,
    val totalActiveDebts: Int = 0,
    val activeCurrencies: Set<String> = emptySet()
) {
    val netBalanceMajor: Double get() = netBalanceCents / 100.0
    val totalReceivablesMajor: Double get() = totalReceivablesCents / 100.0
    val totalPayablesMajor: Double get() = totalPayablesCents / 100.0
}