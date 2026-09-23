package dev.lciszewski27.whereismymoney.data.local

import android.content.Context
import android.net.Uri
import dev.lciszewski27.whereismymoney.data.local.dao.CategoryDao
import dev.lciszewski27.whereismymoney.data.local.dao.DebtDao
import dev.lciszewski27.whereismymoney.data.local.dao.ExchangeRateDao
import dev.lciszewski27.whereismymoney.data.local.dao.PaymentDao
import dev.lciszewski27.whereismymoney.data.local.dao.PersonDao
import dev.lciszewski27.whereismymoney.data.local.entity.CategoryEntity
import dev.lciszewski27.whereismymoney.data.local.entity.DebtEntity
import dev.lciszewski27.whereismymoney.data.local.entity.ExchangeRateEntity
import dev.lciszewski27.whereismymoney.data.local.entity.PaymentEntity
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
    val debts: List<DebtEntity>,
    val categories: List<CategoryEntity> = emptyList(),
    val payments: List<PaymentEntity> = emptyList(),
    val exchangeRates: List<ExchangeRateEntity> = emptyList()
)

class BackupService(
    private val personDao: PersonDao,
    private val debtDao: DebtDao,
    private val categoryDao: CategoryDao,
    private val paymentDao: PaymentDao,
    private val exchangeRateDao: ExchangeRateDao
) {
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    suspend fun exportToJson(): String = withContext(Dispatchers.IO) {
        val persons = personDao.getAll()
        val debts = debtDao.getAll()
        val categories = categoryDao.getAll()
        val payments = paymentDao.getAll()
        val exchangeRates = exchangeRateDao.getAll()
        json.encodeToString(BackupData(persons, debts, categories, payments, exchangeRates))
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
        for (category in backup.categories) {
            categoryDao.insert(category)
            count++
        }
        // Payments reference debts/persons, so they must come after.
        for (payment in backup.payments) {
            paymentDao.insert(payment)
            count++
        }
        for (rate in backup.exchangeRates) {
            exchangeRateDao.upsert(rate)
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