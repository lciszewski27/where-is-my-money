package dev.lciszewski27.whereismymoney.data.local.entity

import androidx.room.Entity
import kotlinx.serialization.Serializable

/**
 * Room entity for a user-managed exchange rate.
 * Stored as: 1 unit of [fromCurrency] = [rate] units of [toCurrency].
 *
 * Previously rates lived only in memory and were lost on process death.
 * Persisting them here makes multi-currency totals (and backups) stable.
 */
@Entity(
    tableName = "exchange_rates",
    primaryKeys = ["fromCurrency", "toCurrency"]
)
@Serializable
data class ExchangeRateEntity(
    val fromCurrency: String,
    val toCurrency: String,
    val rate: Double,
    val updatedAt: Long = System.currentTimeMillis()
)
