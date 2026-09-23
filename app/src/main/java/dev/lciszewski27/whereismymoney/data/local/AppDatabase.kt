package dev.lciszewski27.whereismymoney.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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

@Database(
    entities = [
        PersonEntity::class,
        DebtEntity::class,
        CategoryEntity::class,
        PaymentEntity::class,
        ExchangeRateEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun personDao(): PersonDao
    abstract fun debtDao(): DebtDao
    abstract fun categoryDao(): CategoryDao
    abstract fun paymentDao(): PaymentDao
    abstract fun exchangeRateDao(): ExchangeRateDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Migration 1→2: Add categories table and categoryId column to debts.
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `categories` (
                        `id` TEXT NOT NULL PRIMARY KEY,
                        `name` TEXT NOT NULL,
                        `colorSeed` INTEGER NOT NULL DEFAULT 0,
                        `createdAt` INTEGER NOT NULL DEFAULT 0
                    )
                    """.trimIndent()
                )
                db.execSQL("ALTER TABLE `debts` ADD COLUMN `categoryId` TEXT DEFAULT NULL")
            }
        }

        /**
         * Migration 2→3: Add payment ledger and persisted exchange rates.
         */
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `payments` (
                        `id` TEXT NOT NULL PRIMARY KEY,
                        `debtId` TEXT NOT NULL,
                        `personId` TEXT NOT NULL,
                        `amountCents` INTEGER NOT NULL DEFAULT 0,
                        `currency` TEXT NOT NULL DEFAULT 'PLN',
                        `timestamp` INTEGER NOT NULL DEFAULT 0,
                        `kind` TEXT NOT NULL DEFAULT 'PARTIAL',
                        FOREIGN KEY(`debtId`) REFERENCES `debts`(`id`) ON DELETE CASCADE,
                        FOREIGN KEY(`personId`) REFERENCES `persons`(`id`) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_payments_debtId` ON `payments` (`debtId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_payments_personId` ON `payments` (`personId`)")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `exchange_rates` (
                        `fromCurrency` TEXT NOT NULL,
                        `toCurrency` TEXT NOT NULL,
                        `rate` REAL NOT NULL DEFAULT 1.0,
                        `updatedAt` INTEGER NOT NULL DEFAULT 0,
                        PRIMARY KEY(`fromCurrency`, `toCurrency`)
                    )
                    """.trimIndent()
                )
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "whereismymoney.db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}