package dev.lciszewski27.whereismymoney.data.repository

import dev.lciszewski27.whereismymoney.data.local.dao.DebtDao
import dev.lciszewski27.whereismymoney.data.local.dao.PersonDao
import dev.lciszewski27.whereismymoney.data.local.entity.DebtEntity
import dev.lciszewski27.whereismymoney.data.local.entity.PersonEntity
import dev.lciszewski27.whereismymoney.domain.model.DashboardSummary
import dev.lciszewski27.whereismymoney.domain.model.Debt
import dev.lciszewski27.whereismymoney.domain.model.DebtType
import dev.lciszewski27.whereismymoney.domain.model.Person
import dev.lciszewski27.whereismymoney.domain.repository.DebtRepository
import dev.lciszewski27.whereismymoney.domain.usecase.CurrencyConversionUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class DebtRepositoryImpl(
    private val personDao: PersonDao,
    private val debtDao: DebtDao,
    private val currencyConversion: CurrencyConversionUseCase
) : DebtRepository {

    // ── mapping helpers ──────────────────────────────────────────────

    private fun PersonEntity.toDomain(): Person = Person(
        id = id, name = name, colorSeed = colorSeed, createdAt = createdAt
    )

    private fun Person.toEntity(): PersonEntity = PersonEntity(
        id = id, name = name, colorSeed = colorSeed, createdAt = createdAt
    )

    private fun DebtEntity.toDomain(): Debt = Debt(
        id = id, personId = personId, amountCents = amountCents,
        currency = currency, type = DebtType.fromDb(type),
        description = description, timestamp = timestamp,
        dueDateMillis = dueDateMillis, isSettled = isSettled
    )

    private fun Debt.toEntity(): DebtEntity = DebtEntity(
        id = id, personId = personId, amountCents = amountCents,
        currency = currency, type = type.dbValue,
        description = description, timestamp = timestamp,
        dueDateMillis = dueDateMillis, isSettled = isSettled
    )

    // ── Persons ──────────────────────────────────────────────────────

    override fun observePersons(): Flow<List<Person>> =
        personDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observePersonsWithBalance(primaryCurrency: String): Flow<List<Person>> {
        return combine(
            personDao.observeAll(),
            debtDao.observeActive()
        ) { personEntities, debtEntities ->
            val debtsByPerson = debtEntities.groupBy { it.personId }
            
            personEntities.map { personEntity ->
                val personDebts = debtsByPerson[personEntity.id] ?: emptyList()
                var balance = 0L
                
                personDebts.forEach { debtEntity ->
                    val converted = currencyConversion.convert(
                        cents = debtEntity.amountCents,
                        fromCurrency = debtEntity.currency,
                        toCurrency = primaryCurrency
                    )
                    if (debtEntity.type == DebtType.THEY_OWE_ME.dbValue) {
                        balance += converted
                    } else {
                        balance -= converted
                    }
                }
                
                personEntity.toDomain().copy(
                    balanceCents = balance,
                    currency = primaryCurrency
                )
            }
        }
    }

    override fun observePerson(id: String): Flow<Person?> =
        personDao.observeById(id).map { it?.toDomain() }

    override suspend fun getPerson(id: String): Person? =
        personDao.getById(id)?.toDomain()

    override fun searchPersons(query: String): Flow<List<Person>> =
        personDao.search(query).map { list -> list.map { it.toDomain() } }

    override suspend fun insertPerson(person: Person) =
        personDao.insert(person.toEntity())

    override suspend fun deletePerson(id: String) =
        personDao.deleteById(id)

    // ── Debts ────────────────────────────────────────────────────────

    override fun observeAllDebts(): Flow<List<Debt>> =
        debtDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeDebtsForPerson(personId: String): Flow<List<Debt>> =
        debtDao.observeByPerson(personId).map { list -> list.map { it.toDomain() } }

    override fun observeActiveDebts(): Flow<List<Debt>> =
        debtDao.observeActive().map { list -> list.map { it.toDomain() } }

    override fun observeDebtsByType(type: DebtType): Flow<List<Debt>> =
        debtDao.observeByType(type.dbValue).map { list -> list.map { it.toDomain() } }

    override suspend fun getDebt(id: String): Debt? =
        debtDao.getById(id)?.toDomain()

    override suspend fun insertDebt(debt: Debt) =
        debtDao.insert(debt.toEntity())

    override suspend fun updateDebt(debt: Debt) =
        debtDao.update(debt.toEntity())

    override suspend fun deleteDebt(id: String) =
        debtDao.deleteById(id)

    override suspend fun settleAllForPerson(personId: String) =
        debtDao.settleAllForPerson(personId)

    // ── Aggregates ───────────────────────────────────────────────────

    override fun observeDashboardSummary(primaryCurrency: String): Flow<DashboardSummary> {
        return debtDao.observeActive().map { entities ->
            val debts = entities.map { it.toDomain() }
            var receivables = 0L
            var payables = 0L

            for (debt in debts) {
                val converted = currencyConversion.convert(
                    cents = debt.amountCents,
                    fromCurrency = debt.currency,
                    toCurrency = primaryCurrency
                )
                when (debt.type) {
                    DebtType.THEY_OWE_ME -> receivables += converted
                    DebtType.I_OWE_THEM -> payables += converted
                }
            }

            DashboardSummary(
                totalReceivablesCents = receivables,
                totalPayablesCents = payables,
                primaryCurrency = primaryCurrency,
                netBalanceCents = receivables - payables,
                totalActiveDebts = debts.size,
                activeCurrencies = debts.map { it.currency }.toSet()
            )
        }
    }

    override suspend fun getActiveCurrencies(): List<String> {
        return debtDao.getActiveCurrencies()
    }
}