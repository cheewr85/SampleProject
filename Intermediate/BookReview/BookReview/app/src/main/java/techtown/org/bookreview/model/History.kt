package techtown.org.bookreview.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class History (
    // PrimaryKey와 ColumnInfo 추가함
    @PrimaryKey   val uid: Int?,
    @ColumnInfo(name = "keyword") val keyword: String?
)