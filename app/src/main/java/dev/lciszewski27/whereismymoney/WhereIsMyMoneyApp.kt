package dev.lciszewski27.whereismymoney

import android.app.Application
import dev.lciszewski27.whereismymoney.data.local.AppDatabase
import dev.lciszewski27.whereismymoney.data.local.BackupService
import dev.lciszewski27.whereismymoney.data.local.preferences.UserPreferencesDataStore
import dev.lciszewski27.whereismymoney.data.repository.DebtRepositoryImpl
import dev.lciszewski27.whereismymoney.domain.usecase.CurrencyConversionUseCase
import dev.lciszewski27.whereismymoney.domain.usecase.GetDashboardSummaryUseCase
import dev.lciszewski27.whereismymoney.domain.usecase.GetPersonDetailUseCase

/**
 * Application-level dependency container.
 * In a larger app, consider Hilt/Dagger-KSP or Koin.
 * For this 100% local app, manual DI keeps it clean and traceable.
 */
class WhereIsMyMoneyApp : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var preferences: UserPreferencesDataStore
        private set

    lateinit var currencyConversion: CurrencyConversionUseCase
        private set

    lateinit var getDashboardSummaryUseCase: GetDashboardSummaryUseCase
        private set

    lateinit var getPersonDetailUseCase: GetPersonDetailUseCase
        private set

    lateinit var repository: DebtRepositoryImpl
        private set

    lateinit var backupService: BackupService
        private set

    override fun onCreate() {
        super.onCreate()

        // ── Data Layer ───────────────────────────────────────────────
        database = AppDatabase.getInstance(this)
        preferences = UserPreferencesDataStore(this)

        // ── Domain Layer ─────────────────────────────────────────────
        currencyConversion = CurrencyConversionUseCase()

        // ── Repository (bridges data & domain) ───────────────────────
        repository = DebtRepositoryImpl(
            personDao = database.personDao(),
            debtDao = database.debtDao(),
            currencyConversion = currencyConversion
        )

        // ── Use Cases ────────────────────────────────────────────────
        getDashboardSummaryUseCase = GetDashboardSummaryUseCase(repository)
        getPersonDetailUseCase = GetPersonDetailUseCase(repository)

        // ── Backup ───────────────────────────────────────────────────
        backupService = BackupService(
            personDao = database.personDao(),
            debtDao = database.debtDao()
        )
    }
}