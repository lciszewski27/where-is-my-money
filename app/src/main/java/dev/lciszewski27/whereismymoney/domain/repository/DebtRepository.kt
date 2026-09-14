package dev.lciszewski27.whereismymoney.domain.repository

import dev.lciszewski27.whereismymoney.domain.model.Category
import dev.lciszewski27.whereismymoney.domain.model.DashboardSummary
import dev.lciszewski27.whereismymoney.domain.model.Debt
import dev.lciszewski27.whereismymoney.domain.model.DebtType
import dev.lciszewski27.whereismymoney.domain.model.Person
import dev.lciszewski27.whereismymoney.domain.model.StatsMonthlyTrend
import dev.lciszewski27.whereismymoney.domain.model.StatsSummary
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for all person, debt, and category data.
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

    // ── Categories ───────────────────────────────────────────────────

    fun observeCategories(): Flow<List<Category>>
    suspend fun getCategories(): List<Category>
    suspend fun insertCategory(category: Category)
    suspend fun deleteCategory(id: String)

    // ── Aggregates ───────────────────────────────────────────────────

    fun observeDashboardSummary(primaryCurrency: String): Flow<DashboardSummary>
    suspend fun getActiveCurrencies(): List<String>

    // ── Stats ────────────────────────────────────────────────────────

    suspend fun getStatsSummary(primaryCurrency: String): StatsSummary
    fun observeAllDebtsAscending(): Flow<List<Debt>>
}