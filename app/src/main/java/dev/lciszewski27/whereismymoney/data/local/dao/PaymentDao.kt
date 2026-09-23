package dev.lciszewski27.whereismymoney.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.lciszewski27.whereismymoney.data.local.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(payment: PaymentEntity)

    @Query("SELECT * FROM payments ORDER BY timestamp DESC")
    suspend fun getAll(): List<PaymentEntity>

    @Query("SELECT * FROM payments WHERE personId = :personId ORDER BY timestamp DESC")
    fun observeForPerson(personId: String): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE debtId = :debtId ORDER BY timestamp DESC")
    fun observeForDebt(debtId: String): Flow<List<PaymentEntity>>

    @Query("DELETE FROM payments WHERE id = :id")
    suspend fun deleteById(id: String)
}
