package dev.lciszewski27.whereismymoney.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.lciszewski27.whereismymoney.data.local.entity.DebtEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtDao {

    @Query("SELECT * FROM debts ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<DebtEntity>>

    @Query("SELECT * FROM debts WHERE personId = :personId ORDER BY timestamp DESC")
    fun observeByPerson(personId: String): Flow<List<DebtEntity>>

    @Query("SELECT * FROM debts ORDER BY timestamp DESC")
    suspend fun getAll(): List<DebtEntity>

    @Query("SELECT * FROM debts WHERE id = :id")
    suspend fun getById(id: String): DebtEntity?

    @Query("SELECT * FROM debts WHERE id = :id")
    fun observeById(id: String): Flow<DebtEntity?>

    @Query("SELECT * FROM debts WHERE type = :type AND isSettled = 0 ORDER BY timestamp DESC")
    fun observeByType(type: String): Flow<List<DebtEntity>>

    @Query("SELECT * FROM debts WHERE isSettled = 0 ORDER BY timestamp DESC")
    fun observeActive(): Flow<List<DebtEntity>>

    @Query("SELECT SUM(amountCents) FROM debts WHERE type = 'THEY_OWE_ME' AND isSettled = 0")
    fun observeTotalReceivablesCents(): Flow<Long?>

    @Query("SELECT SUM(amountCents) FROM debts WHERE type = 'I_OWE_THEM' AND isSettled = 0")
    fun observeTotalPayablesCents(): Flow<Long?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(debt: DebtEntity)

    @Update
    suspend fun update(debt: DebtEntity)

    @Delete
    suspend fun delete(debt: DebtEntity)

    @Query("DELETE FROM debts WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE debts SET isSettled = 1 WHERE personId = :personId")
    suspend fun settleAllForPerson(personId: String)

    @Query("SELECT COUNT(*) FROM debts WHERE personId = :personId AND isSettled = 0")
    suspend fun countActiveForPerson(personId: String): Int

    @Query("SELECT DISTINCT currency FROM debts WHERE isSettled = 0")
    fun observeActiveCurrencies(): Flow<List<String>>

    @Query("SELECT DISTINCT currency FROM debts WHERE isSettled = 0")
    suspend fun getActiveCurrencies(): List<String>
}