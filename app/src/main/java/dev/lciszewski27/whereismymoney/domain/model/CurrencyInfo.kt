package dev.lciszewski27.whereismymoney.domain.model

/**
 * Metadata about a supported currency.
 */
data class CurrencyInfo(
    val code: String,       // ISO 4217 3-letter code
    val name: String,       // Full name, e.g. "Polish Zloty"
    val symbol: String,     // Currency symbol, e.g. "zł", "$", "€"
    val minorPerMajor: Int  // e.g. 100 for most currencies, 1000 for others
) {
    companion object {
        val AVAILABLE = listOf(
            CurrencyInfo("PLN", "Polish Zloty", "zł", 100),
            CurrencyInfo("EUR", "Euro", "€", 100),
            CurrencyInfo("USD", "US Dollar", "$", 100),
            CurrencyInfo("GBP", "British Pound", "£", 100),
            CurrencyInfo("CHF", "Swiss Franc", "₣", 100),
            CurrencyInfo("CZK", "Czech Koruna", "Kč", 100),
            CurrencyInfo("JPY", "Japanese Yen", "¥", 1),
            CurrencyInfo("CNY", "Chinese Yuan", "¥", 100),
            CurrencyInfo("SEK", "Swedish Krona", "kr", 100),
            CurrencyInfo("NOK", "Norwegian Krone", "kr", 100),
            CurrencyInfo("DKK", "Danish Krone", "kr", 100),
            CurrencyInfo("HUF", "Hungarian Forint", "Ft", 1),
            CurrencyInfo("RON", "Romanian Leu", "lei", 100),
            CurrencyInfo("BGN", "Bulgarian Lev", "лв", 100),
            CurrencyInfo("TRY", "Turkish Lira", "₺", 100),
            CurrencyInfo("AUD", "Australian Dollar", "A$", 100),
            CurrencyInfo("CAD", "Canadian Dollar", "CA$", 100),
        )

        fun fromCode(code: String): CurrencyInfo =
            AVAILABLE.firstOrNull { it.code == code } ?: CurrencyInfo(code, code, code, 100)
    }
}