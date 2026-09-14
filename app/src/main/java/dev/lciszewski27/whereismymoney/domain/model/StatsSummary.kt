package dev.lciszewski27.whereismymoney.domain.model

/**
 * Monthly debt trend data point for charts.
 */
data class StatsMonthlyTrend(
    val yearMonth: String, // "2025-01"
    val year: Int,
    val month: Int,
    val receivablesCents: Long,
    val payablesCents: Long,
    val netCents: Long
)

/**
 * Comprehensive stats summary for the Stats page.
 */
data class StatsSummary(
    val totalActiveCents: Long,
    val totalSettledCents: Long,
    val totalReceivablesCents: Long,
    val totalPayablesCents: Long,
    val activeDebtCount: Int,
    val settledDebtCount: Int,
    val monthlyTrends: List<StatsMonthlyTrend>,
    val activeDebts: List<Debt>,
    val settledDebts: List<Debt>,
    val primaryCurrency: String
) {
    companion object {
        val EMPTY = StatsSummary(
            totalActiveCents = 0L,
            totalSettledCents = 0L,
            totalReceivablesCents = 0L,
            totalPayablesCents = 0L,
            activeDebtCount = 0,
            settledDebtCount = 0,
            monthlyTrends = emptyList(),
            activeDebts = emptyList(),
            settledDebts = emptyList(),
            primaryCurrency = "PLN"
        )
    }
}