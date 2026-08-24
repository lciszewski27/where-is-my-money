package dev.lciszewski27.whereismymoney.domain.model

/**
 * Exchanged rates for the Currency Engine.
 * Stored as: 1 unit of [fromCurrency] = [rate] units of [toCurrency].
 * e.g. "PLN" -> "EUR" = 0.23 means 1 PLN = 0.23 EUR.
 */
data class ExchangeRate(
    val fromCurrency: String,
    val toCurrency: String,
    val rate: Double
)