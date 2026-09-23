package dev.lciszewski27.whereismymoney.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.lciszewski27.whereismymoney.data.local.entity.ExchangeRateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeRateDao {

    @Query("SELECT * FROM exchange_rates ORDER BY fromCurrency ASC, toCurrency ASC")
    fun observeAll(): Flow<List<ExchangeRateEntity>>

    @Query("SELECT * FROM exchange_rates ORDER BY fromCurrency ASC, toCurrency ASC")
    suspend fun getAll(): List<ExchangeRateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(rate: ExchangeRateEntity)

    @Query("DELETE FROM exchange_rates WHERE fromCurrency = :from AND toCurrency = :to")
    suspend fun delete(from: String, to: String)
}
