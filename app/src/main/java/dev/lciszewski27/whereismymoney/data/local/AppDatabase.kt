package dev.lciszewski27.whereismymoney.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.lciszewski27.whereismymoney.data.local.dao.DebtDao
import dev.lciszewski27.whereismymoney.data.local.dao.PersonDao
import dev.lciszewski27.whereismymoney.data.local.entity.DebtEntity
import dev.lciszewski27.whereismymoney.data.local.entity.PersonEntity

@Database(
    entities = [PersonEntity::class, DebtEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun personDao(): PersonDao
    abstract fun debtDao(): DebtDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "whereismymoney.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}