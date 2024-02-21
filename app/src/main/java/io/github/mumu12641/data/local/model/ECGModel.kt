package io.github.mumu12641.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.Date

@Entity(tableName = "ECG_table")
data class ECGModel(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "path") val path: String,
    @ColumnInfo(name = "time")  val time: String,
    @ColumnInfo(name = "des") val des: String?
)


