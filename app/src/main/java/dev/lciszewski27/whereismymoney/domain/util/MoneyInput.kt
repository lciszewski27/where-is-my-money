package dev.lciszewski27.whereismymoney.domain.util

/**
 * Shared helpers for parsing/formatting monetary input.
 *
 * Amounts are stored as [Long] in minor units (cents). These helpers are
 * pure Kotlin so both the add-debt sheet and the partial-payoff dialog
 * share a single implementation.
 */
object MoneyInput {

    /**
     * Parse user input like "12.50", "12,5" or ".99" to cents.
     * Returns 0 for unparseable input. Handles a leading minus sign so
     * that "-5.20" parses to -520 (not -480).
     */
    fun parseInputToCents(input: String): Long {
        val normalized = input.trim().replace(',', '.')
        if (normalized.isEmpty()) return 0L
        val negative = normalized.startsWith("-")
        val unsigned = if (negative) normalized.drop(1) else normalized
        val parts = unsigned.split(".")
        val total = when {
            parts.size == 1 -> (parts[0].toLongOrNull() ?: 0L) * 100
            parts.size == 2 -> {
                val major = parts[0].toLongOrNull() ?: 0L
                val minor = parts[1].take(2).padEnd(2, '0').toLongOrNull() ?: 0L
                major * 100 + minor
            }
            else -> 0L
        }
        return if (negative) -total else total
    }

    /** Format cents for an input field, e.g. 1250 -> "12.50". */
    fun formatCentsForInput(cents: Long): String {
        val major = cents / 100
        val minor = cents % 100
        return "$major.${minor.toString().padStart(2, '0')}"
    }
}
