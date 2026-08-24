package dev.lciszewski27.whereismymoney.domain.repository

import dev.lciszewski27.whereismymoney.domain.model.DashboardSummary
import dev.lciszewski27.whereismymoney.domain.model.Debt
import dev.lciszewski27.whereismymoney.domain.model.DebtType
import dev.lciszewski27.whereismymoney.domain.model.Person
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for all person and debt data.
 */
interface DebtRepository {

    // ── Persons ──────────────────────────────────────────────────────

    fun observePersons(): Flow<List<Person>>
    fun observePersonsWithBalance(primaryCurrency: String): Flow<List<Person>>
    fun observePerson(id: String): Flow<Person?>
    suspend fun getPerson(id: String): Person?
    fun searchPersons(query: String): Flow<List<Person>>
    suspend fun insertPerson(person: Person)
    suspend fun deletePerson(id: String)

    // ── Debts ────────────────────────────────────────────────────────

    fun observeAllDebts(): Flow<List<Debt>>
    fun observeDebtsForPerson(personId: String): Flow<List<Debt>>
    fun observeActiveDebts(): Flow<List<Debt>>
    fun observeDebtsByType(type: DebtType): Flow<List<Debt>>
    suspend fun getDebt(id: String): Debt?
    suspend fun insertDebt(debt: Debt)
    suspend fun updateDebt(debt: Debt)
    suspend fun deleteDebt(id: String)
    suspend fun settleAllForPerson(personId: String)

    // ── Aggregates ───────────────────────────────────────────────────

    fun observeDashboardSummary(primaryCurrency: String): Flow<DashboardSummary>
    suspend fun getActiveCurrencies(): List<String>
}