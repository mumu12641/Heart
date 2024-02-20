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
    @ColumnInfo(name = "time") @TypeConverters(DateConverter::class) val time: Date,
    @ColumnInfo(name = "des") val des: String?
)


class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}