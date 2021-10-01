package techtown.org.bookreview.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import techtown.org.bookreview.model.Review

/*
Review를 불러오는 기능을 하는 Dao
 */
@Dao
interface ReviewDao {

    // 해당 id에 대해서 리뷰를 가져옴
    @Query("SELECT * FROM review WHERE id ==:id")
    fun getOneReview(id: Int): Review

    // 리뷰를 저장함, 하지만 똑같은 id를 가진 리뷰가 있다면 교체함
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveReview(review: Review)
}