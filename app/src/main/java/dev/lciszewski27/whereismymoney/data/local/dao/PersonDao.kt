package dev.lciszewski27.whereismymoney.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.lciszewski27.whereismymoney.data.local.entity.PersonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {

    @Query("SELECT * FROM persons ORDER BY name ASC")
    fun observeAll(): Flow<List<PersonEntity>>

    @Query("SELECT * FROM persons ORDER BY name ASC")
    suspend fun getAll(): List<PersonEntity>

    @Query("SELECT * FROM persons WHERE id = :id")
    suspend fun getById(id: String): PersonEntity?

    @Query("SELECT * FROM persons WHERE id = :id")
    fun observeById(id: String): Flow<PersonEntity?>

    @Query("SELECT * FROM persons WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun search(query: String): Flow<List<PersonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(person: PersonEntity)

    @Update
    suspend fun update(person: PersonEntity)

    @Delete
    suspend fun delete(person: PersonEntity)

    @Query("DELETE FROM persons WHERE id = :id")
    suspend fun deleteById(id: String)
}