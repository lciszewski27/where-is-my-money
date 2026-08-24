package dev.lciszewski27.whereismymoney.data.local

import android.content.Context
import android.net.Uri
import dev.lciszewski27.whereismymoney.data.local.dao.DebtDao
import dev.lciszewski27.whereismymoney.data.local.dao.PersonDao
import dev.lciszewski27.whereismymoney.data.local.entity.DebtEntity
import dev.lciszewski27.whereismymoney.data.local.entity.PersonEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * 100% offline JSON backup/restore for the local Room data.
 */
@Serializable
data class BackupData(
    val persons: List<PersonEntity>,
    val debts: List<DebtEntity>
)

class BackupService(
    private val personDao: PersonDao,
    private val debtDao: DebtDao
) {
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    suspend fun exportToJson(): String = withContext(Dispatchers.IO) {
        val persons = personDao.getAll()
        val debts = debtDao.getAll()
        json.encodeToString(BackupData(persons, debts))
    }

    suspend fun importFromJson(jsonString: String): Int = withContext(Dispatchers.IO) {
        val backup = json.decodeFromString<BackupData>(jsonString)
        var count = 0
        for (person in backup.persons) {
            personDao.insert(person)
            count++
        }
        for (debt in backup.debts) {
            debtDao.insert(debt)
            count++
        }
        count
    }

    suspend fun exportToUri(context: Context, uri: Uri) = withContext(Dispatchers.IO) {
        val jsonString = exportToJson()
        context.contentResolver.openOutputStream(uri)?.use { out ->
            out.write(jsonString.toByteArray())
            out.flush()
        }
    }
}