package techtown.org.calculator.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import techtown.org.calculator.model.History

// Room에 연결된 Dao
// 여기에는 이전에 만든 model 패키지에 있는 History Entity 저장을 어떻게 하고 조회는 어떻게 할 것이며 지우는 것은 어떻게 할 지에 대해서 정의를 함
// 데이터 저장, 삭제, 가져오는 기능
@Dao
interface HistoryDao {
    // History를 전부 가져오는 함수, Dao에서 Query문을 바로 작성하면됨
    @Query("SELECT * FROM history")
    fun getAll() : List<History>

    // 하나하나 저장, insert 하는 메소드, History를 받아와 저장
    @Insert
    fun insertHistory(history : History)

    // 전체삭제하는 기능, return 하는 것 없이 삭제만 하므로 직접 Query로 작성
    @Query("DELETE FROM history")
    fun deleteAll()

//    // 하나만 삭제하는 경우, History를 받아와 하나만 삭제함
//    @Delete
//    fun delete(history : History)
//
//    // SELECT 할 때 질의문을 줘서 조건에 부합한 것만 지울려고 할 때, Result 기준으로 받아오고 싶을 때
//    @Query("SELECT * FROM history WHERE result LIKE :result LIMIT 1") // 인자로 들어오는 result를 가지고 사용함, 하나만 반환하고 싶을 때 LIMIT을 검
//    fun findByResult(result : String) : History
}