package io.github.mumu12641.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ECG_table")
data class ECGModel(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "pcmPath") val pcmPath: String,
    @ColumnInfo(name = "wavPath") val wavPath: String,
    @ColumnInfo(name = "jpgPath") val jpgPath: String,
    @ColumnInfo(name = "txtPath") val txtPath: String,
    @ColumnInfo(name = "time") val time: String,
    @ColumnInfo(name = "des") var des: String?
)


