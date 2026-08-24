package dev.lciszewski27.whereismymoney.domain.usecase

import dev.lciszewski27.whereismymoney.domain.model.CurrencyInfo
import dev.lciszewski27.whereismymoney.domain.model.ExchangeRate

/**
 * Pure computation engine for currency conversions.
 * All exchange rates are stored locally and managed by the user.
 * Falls back to 1:1 when no rate is configured.
 */
class CurrencyConversionUseCase {

    private val rates = mutableListOf<ExchangeRate>()

    /**
     * Convert [cents] in [fromCurrency] to the equivalent cents in [toCurrency].
     * Fallback to 1:1 if no rate is found.
     */
    fun convert(cents: Long, fromCurrency: String, toCurrency: String): Long {
        if (fromCurrency == toCurrency) return cents
        val rate = findRate(fromCurrency, toCurrency) ?: 1.0
        return (cents * rate).toLong()
    }

    /**
     * Return the rate from [from] to [to]; null = fallback to 1:1.
     */
    fun findRate(from: String, to: String): Double? {
        rates.firstOrNull { it.fromCurrency == from && it.toCurrency == to }?.let { return it.rate }
        rates.firstOrNull { it.fromCurrency == to && it.toCurrency == from }?.let { return 1.0 / it.rate }
        return null
    }

    fun setRate(from: String, to: String, rate: Double) {
        rates.removeAll { it.fromCurrency == from && it.toCurrency == to }
        rates.add(ExchangeRate(from, to, rate))
    }

    fun removeRate(from: String, to: String) {
        rates.removeAll { it.fromCurrency == from && it.toCurrency == to }
    }

    fun getAllRates(): List<ExchangeRate> = rates.toList()

    /** Format amount with the proper currency symbol. */
    fun formatAmount(cents: Long, currency: String, includeCode: Boolean = false): String {
        val info = CurrencyInfo.fromCode(currency)
        val major = cents / 100.0
        val formatted = String.format("%.2f", major).replace(',', '.')
        return if (includeCode) "$formatted ${info.code}" else "$formatted ${info.symbol}"
    }
}