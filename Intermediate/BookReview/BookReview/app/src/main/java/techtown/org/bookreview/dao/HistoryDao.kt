package techtown.org.bookreview.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import techtown.org.bookreview.model.History

@Dao
interface HistoryDao {
    // API 활용할 함수를 정의함

    // 모든 데이터를 불러옴
    @Query("SELECT * FROM history")
    fun getAll(): List<History>

    // 하나만 추가함
    @Insert
    fun insertHistory(history: History)

    // 키워드를 가져와서 해당 키워드를 지움
    @Query("DELETE FROM history WHERE keyword == :keyword")
    fun delete(keyword: String)

}