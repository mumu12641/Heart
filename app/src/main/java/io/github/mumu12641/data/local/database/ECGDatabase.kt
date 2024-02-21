package io.github.mumu12641.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.mumu12641.data.local.dao.ECGDao
import io.github.mumu12641.data.local.model.ECGModel

@Database(entities = [ECGModel::class], version = 2)
abstract class ECGDatabase : RoomDatabase() {
    abstract fun getECGDao(): ECGDao
}