package techtown.org.calculator

import androidx.room.Database
import androidx.room.RoomDatabase
import techtown.org.calculator.dao.HistoryDao
import techtown.org.calculator.model.History

// 데이터베이스를 만들어야함 그래서 추상클래스로 만들고 활용함, 데이터베이스 처리를 위해
// 데이터베이스임을 알려주기 위해, History를 DB로 만들것이므로 entity로 등록해서 사용함
// version도 작성해줘야함, 앱이 업데이트 하면 DB가 바뀔 수도 있음 1->2로 될 때 migration을 해주어서 데이터가 날라가지 않고 DB 버전과 구조가 바뀔 수 있으므로 이를 명시하고 버전을 체크를 해야함
// version이 바뀌면 migration 코드가 있어서 업데이트를 하므로 이를 위해서 version을 처리해줘야함
@Database(entities = [History::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    // historyDao를 가지고 있음, 이를 가져갈 수 있게 함
    // 이러면 AppDatabase를 사용할 때 historyDao를 활용할 수 있음
    abstract fun historyDao() : HistoryDao
}