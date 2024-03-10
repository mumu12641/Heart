package io.github.mumu12641.data.local.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.mumu12641.data.local.dao.ECGDao
import io.github.mumu12641.data.local.model.ECGModel

@Database(
    entities = [ECGModel::class], version = 2, exportSchema = true, autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
abstract class ECGDatabase : RoomDatabase() {
    abstract fun getECGDao(): ECGDao
}