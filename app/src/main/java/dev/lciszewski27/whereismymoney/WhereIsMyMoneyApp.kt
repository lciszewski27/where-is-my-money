package dev.lciszewski27.whereismymoney

import android.app.Application
import dev.lciszewski27.whereismymoney.data.local.AppDatabase
import dev.lciszewski27.whereismymoney.data.local.BackupService
import dev.lciszewski27.whereismymoney.data.local.preferences.UserPreferencesDataStore
import dev.lciszewski27.whereismymoney.data.repository.DebtRepositoryImpl
import dev.lciszewski27.whereismymoney.domain.usecase.CurrencyConversionUseCase
import dev.lciszewski27.whereismymoney.domain.usecase.GetDashboardSummaryUseCase
import dev.lciszewski27.whereismymoney.domain.usecase.GetPersonDetailUseCase
import dev.lciszewski27.whereismymoney.domain.usecase.SettleUpUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

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

    lateinit var settleUpUseCase: SettleUpUseCase
        private set

    lateinit var repository: DebtRepositoryImpl
        private set

    lateinit var backupService: BackupService
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

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
            categoryDao = database.categoryDao(),
            paymentDao = database.paymentDao(),
            exchangeRateDao = database.exchangeRateDao(),
            currencyConversion = currencyConversion
        )

        // ── Use Cases ────────────────────────────────────────────────
        getDashboardSummaryUseCase = GetDashboardSummaryUseCase(repository)
        getPersonDetailUseCase = GetPersonDetailUseCase(repository, currencyConversion)
        settleUpUseCase = SettleUpUseCase()

        // ── Backup ───────────────────────────────────────────────────
        backupService = BackupService(
            personDao = database.personDao(),
            debtDao = database.debtDao(),
            categoryDao = database.categoryDao(),
            paymentDao = database.paymentDao(),
            exchangeRateDao = database.exchangeRateDao()
        )

        // ── Restore persisted exchange rates into the conversion engine.
        // Keeps multi-currency totals stable across process death and makes
        // rates imported via backup effective immediately.
        applicationScope.launch {
            currencyConversion.syncRates(repository.getExchangeRates())
            repository.observeExchangeRates().collect { rates ->
                currencyConversion.syncRates(rates)
            }
        }
    }
}