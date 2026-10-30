package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ClientEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.ReceiptEntity
import com.example.data.model.WorkdayEntity

@Database(
    entities = [
        ClientEntity::class,
        ProjectEntity::class,
        ReceiptEntity::class,
        WorkdayEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clientDao(): ClientDao
    abstract fun projectDao(): ProjectDao
    abstract fun receiptDao(): ReceiptDao
    abstract fun workdayDao(): WorkdayDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "project_finance_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
