package techtown.org.bookreview

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import techtown.org.bookreview.dao.HistoryDao
import techtown.org.bookreview.dao.ReviewDao
import techtown.org.bookreview.model.History
import techtown.org.bookreview.model.Review

/*
RoomDB를 사용하기 위한 구현체 Database 만듬
검색 기록 저장하기 위한 DB
리뷰 기록 저장 및 조회를 위한 DB
DB 테이블 등 추가될 때마다 entities를 추가하면 됨
 */
@Database(entities = [History::class, Review::class], version = 1) // 여기서 Dao가 2개가 있으므로 버전관리를 계속 해줘야함, 그게 아니면 기존의 것을 삭제하고 실행해도 됨
abstract class AppDatabase: RoomDatabase() {
    // DB는 Dao에서 꺼내옴
    abstract fun historyDao(): HistoryDao
    // 리뷰에 대한 Dao
    abstract fun reviewDao(): ReviewDao
}


fun getAppDatabase(context: Context): AppDatabase {

    // 버전관리 및 마이그레이션을 하는 경우를 위한 코드
    val migartion_1_2 = object: Migration(1,2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // 어떻게 데이터 테이블이 바뀌었는지 직접 SQL문으로 작성해야함
            database.execSQL("CREATE TABLE `REVIEW` (`id` INTEGER, `review` TEXT," + "PRIMARY KEY(`id`))")
        }

    }


    return Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "BookSearchDB"
    )
        .addMigrations(migartion_1_2) // DB 최신버전 업데이트 시 활용을 함
        .build()
}