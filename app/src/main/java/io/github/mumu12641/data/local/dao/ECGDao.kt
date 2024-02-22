package io.github.mumu12641.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.mumu12641.data.local.model.ECGModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ECGDao {
    @Query("SELECT * FROM ECG_table")
    fun getAllECGModels(): Flow<List<ECGModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertECG(ecgModel: ECGModel)

    @Delete
    suspend fun deleteECG(ecgModel: ECGModel)
}