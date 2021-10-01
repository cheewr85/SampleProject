## 도서 목록 앱
- 인터파크 Open API를 활용하여 베스트셀러 정보를 가져와서 리사이클러뷰로 나타냄

- 마찬가지로 Open API를 활용하여 검색기능을 추가해서 검색한 책에 대해서 목록을 가져와서 리사이클러뷰로 나타냄

- 베스트셀러, 검색할 때 나온 책을 누르면 상세화면으로 넘어가 해당 책에 대한 내용을 보여줌

- 검색기록, 그리고 상세화면 누르고 하단의 개인 리뷰를 누르는 부분에 대해서 RoomDB를 활용해서 LocalDB에 저장해둠

- API 활용을 위한 key는 인터파크 가입 후 발급 받음, 현재 올라와 있는 키는 현재 사용하지 않음

## 메인화면

- 맨 위 검색할 수 있게 EditText와 베스트셀러를 보여주는 리사이클러뷰 존재함

- 여기서 검색기록을 나타내기 위한 리사이클러뷰는 현재 visibility를 gone으로 설정하였고 검색기록을 볼 시에만 보이게끔 함

- 검색기록은 단순하게 내가 입력한 검색기록에 대해서 저장을 하고 버튼을 누르면 삭제할 수 있게 구현함

![one](/Intermediate/BookReview/img/one.png)

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <EditText
        android:id="@+id/searchEditText"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:lines="1"
        android:inputType="text"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"/>

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/bookRecyclerView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        tools:listitem="@layout/item_book"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toBottomOf="@id/searchEditText"
        app:layout_constraintBottom_toBottomOf="parent"/>

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/historyRecyclerView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:background="@color/white"
        android:visibility="gone"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toBottomOf="@id/searchEditText"
        app:layout_constraintBottom_toBottomOf="parent"/>

</androidx.constraintlayout.widget.ConstraintLayout>
```

- 베스트셀러를 보여주기 위한 리사이클러뷰 item에 대한 xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:padding="16dp">

    <ImageView
        android:id="@+id/coverImageView"
        android:layout_width="100dp"
        android:layout_height="100dp"
        android:background="@drawable/background_gray_stroke_radius_16"
        android:padding="8dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/titleTextView"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="12dp"
        android:ellipsize="end"
        android:lines="1"
        android:textColor="@color/black"
        android:textSize="16sp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toEndOf="@id/coverImageView"
        app:layout_constraintTop_toTopOf="parent"
        tools:text="안드로이드 마스터하기" />

    <TextView
        android:id="@+id/descriptionTextView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:layout_marginTop="12dp"
        android:ellipsize="end"
        android:maxLines="3"
        android:textSize="12sp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="@id/titleTextView"
        app:layout_constraintTop_toBottomOf="@id/titleTextView" />


</androidx.constraintlayout.widget.ConstraintLayout>
```

- 검색기록에 대해서 보여주기 위한 item에 대한 xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

    <TextView
        android:id="@+id/historyKeywordTextView"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginTop="8dp"
        android:layout_marginBottom="8dp"
        android:textColor="@color/black"
        android:textSize="16sp"
        android:layout_marginEnd="8dp"
        app:layout_constraintEnd_toStartOf="@id/historyKeywordDeleteButton"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <ImageButton
        android:id="@+id/historyKeywordDeleteButton"
        android:layout_width="12dp"
        android:layout_height="12dp"
        android:layout_marginEnd="16dp"
        android:src="@drawable/ic_baseline_clear_24"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

### 구현 코드
- 먼저 ViewBinding을 활용함, Retrofit을 통해서 사용할 Open API와 통신을 하는데, 이렇게 ViewBinding을 활용하는 이유는 서버와 통신해서 바로바로 업데이트를 반영해주기 위해서 binding을 활용함

- 여기서 binding 사용시 해당 xml에 대해서 동일한 이름명으로 불러온다면 알아서 databinding을 활용하여서 불러와서 연결할 수 있음, 이 방식은 기존에 일반적으로 액티비티를 단순하게 생성하여 쓰는 것보다 편함

- 뷰를 직접 참조하여서 사용하는 방식이 아니라서 널의 안전하고 xml 파일에서 참조하는 뷰와 바로 일치하기 때문에 사용 편의성과 컴파일도 빠르게 됨

- 메인 코드 이전 API 사용을 위해서 Retrofit을 활용하였고 이에 대해서 apiKey를 별도 xml로 빼둔 뒤 사용해서 받아오고자 하는 요소에 대해서 Query와 HTTP 요청을 한 뒤 사전에 만든 DTO로 콜백을 함

### BookService.kt
- api 패키지 안에 존재, 활용할 URL을 value에 넣어서 API 활용함
```kotlin
package techtown.org.bookreview.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import techtown.org.bookreview.model.BestSellerDto
import techtown.org.bookreview.model.SearchBookDto

/*
베스트셀러 API, 탐색하는 Search API 사용, 둘 다 GET 형식으로 가져옴(GET으로 데이터 요청시 서버에서 반환할 때 Http 형식으로 반환함, URL에 다 넣어서 반환함)
POST는 요청할 때, 새로운 데이터를 만들 때, CREATE 할 때(데이터가 커서 Http Body에 넣어서 전달함)
 */

interface BookService {
    // 데이터만 가져오면 되므로 GET만 사용, base URL 말고 GET을 API에 대해서만 value로 쓰면 됨, output은 JSON으로 고정할 것임
    // search API
    @GET("/api/search.api?output=json")
    // 그런 다음 가져오기 위한 함수 정의 가져올 params을 넣음, key, query 필요
    fun getBooksByName(
        @Query("key") apiKey: String,
        @Query("query") keyword: String
    ):Call<SearchBookDto>

    // 베스트셀러 API, 고정시킬 요소는 고정시켜둠
    @GET("/api/bestSeller.api?output=json&categoryId=100")
    fun getBestSellerBooks(
            @Query("key") apiKey: String
    ):Call<BestSellerDto>
}
```

- 아래 파일은 model 패키지 하위에 존재함
### BestSellerDto.kt
- SerializedName을 통해서 GET으로 받은 JSON 데이터에서 해당 value에 대해서 매칭을 시켜서 받음
```kotlin
package techtown.org.bookreview.model

import com.google.gson.annotations.SerializedName

/*
Book data class는 단순히 JSON에서 받은 결과 중 item에 있는 특정 값만 받은 것임
실제 GET한 전체 JSON데이터에서 쓰기 위해선 전체 모델이 필요함
실제 전체 JSON 데이터 중 여러가지를 사용함
 */
class BestSellerDto(
    // BestSellerDto로 전체 API JSON을 받아오고 해당 JSON에서 title, item이 매칭이 되고 그 매칭된 값에서 item에 있는 내용 title, description등을 쓸 것이므로 Book객체의 List 형태로 받음
    // 그러면 알아서 Book 데이터에 item을 불러온걸 바탕으로 맵핑이 됨
    @SerializedName("title") val title: String,
    @SerializedName("item") val books: List<Book>
)
```
### SearchBookDto.kt
```kotlin
package techtown.org.bookreview.model

import com.google.gson.annotations.SerializedName

data class SearchBookDto(
    @SerializedName("title") val title: String,
    @SerializedName("item") val books: List<Book>
)
```

### Book.kt
- 그리고 GET으로 받은 내용에 대해서 JSON데이터 중 쓸 자료에 대해서 맵핑해서 가져오기 위해서 데이터 클래스를 만듬, 여기서 상세화면으로 넘어갈 때 인텐트로 넘길 것인데, 일일이 모든 값을 다 인텐트로 넘기기 번거롭기 때문에 직렬화를 하여서 데이터 객체 자체를 넘기기 위해서 Parcelable 처리를 함
```kotlin
package techtown.org.bookreview.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/*
Retrofit을 통해서 받은 데이터에 대한 Call 리턴 값으로 활용한 Book 데이터
 */
@Parcelize // Book 클래스 자체를 직렬화로 넘기기 위해서 선언
data class Book(
    // API를 통해서 GET한 데이터 중 활용할 item만을 뽑음
    // GET을 통해 받은 JSON value의 값 중 itemId 값을 받과 매칭하기 위해서 어노테이션 사용, 서버에선 itemId로 현재 Book data class에선 id의 값으로 맵핑이 되서 데이터 가져옴
    @SerializedName("itemId") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("coverSmallUrl") val coverSmallUrl: String
): Parcelable // 직렬화 가능하게 붙임
```

- 그리고 리사이클러뷰를 그려서 붙여주기 위해서 어댑터를 만듬, 검색기록과 베스트셀러 리스트를 보여주기 위한 어댑터 모두

### BookAdapter.kt
- 이때 매개변수로 아이템 클릭 리스너를 넘겨서 아이템 클릭시 상세화면으로 넘어갈 수 있게끔 처리를 함
- 그리고 ViewBinding을 활용하였기 때문에 해당 xml을 바로 불러와서 내부 아이템에 대해서 설정을 함, 통신을 받아서 넘겨받은 Book 데이터 클래스를 가지고
```kotlin
package techtown.org.bookreview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import techtown.org.bookreview.databinding.ItemBookBinding
import techtown.org.bookreview.model.Book


class BookAdapter(private val itemClickedListener: (Book) -> Unit): ListAdapter<Book, BookAdapter.BookItemViewHolder>(diffUtil) { // 아이템 클릭시 상세화면으로 넘어가서 보여주기 위해서 클릭이벤트 메인에서 넘겨 받아 처리함
    // View Binding을 활용하여 RecyclerView를 사용함
    // View Binding을 위해서 RecyclerView에 들어갈 아이템에 대해서 item_book.xml로 추가함, 그래서 자동으로 트랙킹해서 ItemBookBinding을 import 할 수 있음
    // 여기서 root, ItemBookBinding은 item_book.xml이 해당됨
    inner class BookItemViewHolder(private val binding: ItemBookBinding): RecyclerView.ViewHolder(binding.root){ // 미리 몇 개 만들어진 ViewHolder를 재활용 하는것이 RecyclerView임, 미리 View를 Hold해서 ViewHolder임

        // 위에서 ListAdapter에서 Book 클래스를 가져오게 함
        fun bind(bookModel: Book) {
            // binding을 통해서 item_book에 있는 View에 접근함
            binding.titleTextView.text = bookModel.title
            // 설명 추가
            binding.descriptionTextView.text = bookModel.description

            binding.root.setOnClickListener {
                itemClickedListener(bookModel) // bookModel의 내용을 받아옴
            }

            // 이미지 추가, Url을 쉽게 불러와서 쓸 수 있음
            Glide
                .with(binding.coverImageView.context) // 이미지 context 불러오고
                .load(bookModel.coverSmallUrl) // Url을 그 위에 로드하고
                .into(binding.coverImageView) // 그 안에 추가함
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookItemViewHolder {
        // 미리 만들어진 ViewHolder가 없을 경우에 새로 생성을 하는 함수
        // inner class로 BookItemViewHolder에 매개변수로 ItemBookBinding을 받아야함
        // 그리고 inflate를 해줘야함, context는 adapter여서 this를 줄 수 없기 때문에 View에 해당하는 것을 주기 위해서 parent 불러옴
        // 이러면 LayoutInflater로 불러온 뒤 ViewHolder를 생성
        return BookItemViewHolder(ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: BookItemViewHolder, position: Int) {
        // 실제 ViewHolder가 그려지게 됐을 때 데이터를 그려주게 되는 데이터를 바인드하게 되는 함수
        holder.bind(currentList[position]) // ListAdapter에 Data에 리스트는 미리 저장되어 있어서 currentList로 접근 가능
    }

    // 이미 View에 올라와 있을 때 같은 값을 할당할 필요가 없으므로 이를 판단해주는 object
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Book>(){
            // 아래의 두 기준으로 리사이클러뷰가 데이터를 업데이트 할 건지 지울건지 새로 import할 건지 알려줌
            override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
                // oldItem과 newItem이 같은지 다른지 확인
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
                // 안에 있는 content가 같은지 다른지 확인
                return oldItem.id == newItem.id
            }

        }
    }

}
```

### HistoryAdapter.kt
- 같은 리사이클러뷰이기 때문에 구현은 비슷하게 함
```kotlin
package techtown.org.bookreview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import techtown.org.bookreview.databinding.ItemHistoryBinding
import techtown.org.bookreview.model.History

class HistoryAdapter(val historyDeleteClickedListener: (String) -> Unit): ListAdapter<History, HistoryAdapter.HistoryItemViewHolder>(diffUtil) { // 삭제버튼 이벤트 정의를 위해 메인에서 함수를 던져서 가져와 처리하기 위해 매개변수 추가
    // View Binding을 활용하여 RecyclerView를 사용함
    // View Binding을 위해서 RecyclerView에 들어갈 아이템에 대해서 item_history.xml로 추가함, 그래서 자동으로 트랙킹해서 ItemBookBinding을 import 할 수 있음
    // 여기서 root, ItemBookBinding은 item_history.xml이 해당됨
    inner class HistoryItemViewHolder(private val binding: ItemHistoryBinding): RecyclerView.ViewHolder(binding.root){ // 미리 몇 개 만들어진 ViewHolder를 재활용 하는것이 RecyclerView임, 미리 View를 Hold해서 ViewHolder임

        // 위에서 ListAdapter에서 History 클래스를 가져오게 함
        fun bind(historyModel: History) {
            binding.historyKeywordTextView.text = historyModel.keyword

            // 삭제에 대해서 keyword를 넘기고 이벤트 처리를 함
            binding.historyKeywordDeleteButton.setOnClickListener {
                historyDeleteClickedListener(historyModel.keyword.orEmpty())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryItemViewHolder {
        // 미리 만들어진 ViewHolder가 없을 경우에 새로 생성을 하는 함수
        // inner class로 HistoryItemViewHolder에 매개변수로 ItemHistoryBinding을 받아야함
        // 그리고 inflate를 해줘야함, context는 adapter여서 this를 줄 수 없기 때문에 View에 해당하는 것을 주기 위해서 parent 불러옴
        // 이러면 LayoutInflater로 불러온 뒤 ViewHolder를 생성
        return HistoryItemViewHolder(ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: HistoryItemViewHolder, position: Int) {
        // 실제 ViewHolder가 그려지게 됐을 때 데이터를 그려주게 되는 데이터를 바인드하게 되는 함수
        holder.bind(currentList[position]) // ListAdapter에 Data에 리스트는 미리 저장되어 있어서 currentList로 접근 가능
    }

    // 이미 View에 올라와 있을 때 같은 값을 할당할 필요가 없으므로 이를 판단해주는 object
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<History>(){
            // 아래의 두 기준으로 리사이클러뷰가 데이터를 업데이트 할 건지 지울건지 새로 import할 건지 알려줌
            override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
                // oldItem과 newItem이 같은지 다른지 확인
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
                // 안에 있는 content가 같은지 다른지 확인
                return oldItem.keyword == newItem.keyword
            }

        }
    }

}
```

- MainActivity 활용하기 전, 검색기록에 대해서 찾고 삭제하기 위한 DB를 만들어둠

- 이 과정은 먼저 Entity를 만듬, 이 개념은 실제 DB의 내용과 유사함, 쿼리문을 활용하고 DB에서 사용하는 용어와 개념을 사용함

- 먼저 검색기록에 대해서는 String만 저장하므로 해당하는 테이블에 대한 Entity를 만듬
### History.kt
```kotlin
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
```
- 그리고 이 DB를 활용할 Dao를 구현해서 쿼리문을 통해서 해당 함수에 대한 기능을 정의함
### HistoryDao.kt
```kotlin
package techtown.org.bookreview.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import techtown.org.bookreview.model.History

@Dao
interface HistoryDao {
    // Local DB 활용할 함수를 정의함

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
```
- 그리고 이를 실질적으로 사용하기 위한 Database 구현체를 만듬, 이 구현체를 Main에서 정의한 후 위에서 정의한 Dao를 통해서 Local DB를 미리 정의한 Entity의 범위내에서 Local DB로써 활용할 수 있음
### AppDatabase.kt
- 밑에서 설명할 것이지만 단순히 검색기록 뿐 아니라 상세화면 넘어갔을 때 내부의 리뷰를 저장하는 DB 또한 포함함, 초기에는 History에 대해서만 있고 추후에 추가한 것이라 Migartion 코드가 존재함
```kotlin
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
    ).addMigrations(migartion_1_2) // DB 최신버전 업데이트 시 활용을 함
     .build()
}
```

### MainActivity.kt
- 앞서 정의한 DB, 그리고 Retrofit을 활용한 통신을 쓰기 위한 api를 Main에서 정의한 후 위에서 짠대로 역할에 맞게 기능을 사용하면 됨

- 리사이클러뷰의 경우도 해당 View를 불러온 뒤 어댑터를 통해서 구현을 함
```kotlin
package techtown.org.bookreview

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import techtown.org.bookreview.adapter.BookAdapter
import techtown.org.bookreview.adapter.HistoryAdapter
import techtown.org.bookreview.api.BookService
import techtown.org.bookreview.databinding.ActivityMainBinding
import techtown.org.bookreview.model.BestSellerDto
import techtown.org.bookreview.model.History
import techtown.org.bookreview.model.SearchBookDto

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding // binding 사용 위해 전역변수 선언
    private lateinit var adapter: BookAdapter // 어댑터 전역변수 선언
    private lateinit var historyAdapter: HistoryAdapter // 검색기록 역시 리사이클러뷰이므로 어댑터 선언해서 사용
    private lateinit var bookService: BookService

    // DB를 활용하기 위해서 선언함
   private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 리사이클러뷰를 가져오기 위해서 ViewBinding을 함(레이아웃 이름과 유사해야함)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root) // ViewBinding을 사용하므로 기존 레이아웃 말고 binding.root으로 함

        initBookRecyclerView() // 리사이클러뷰 초기화
        initHistoryRecyclerView() // 검색기록 리사이클러뷰 초기화

//        // AppDatabase,DB를 빌드함
//        db = Room.databaseBuilder(
//            applicationContext,
//            AppDatabase::class.java,
//            "BookSearchDB"
//        ).build()

        // 하지만 DB를 추가하거나 바뀔 때 이 부분에 대해서 마이그레이션과 버전관리를 위해서 db 빌드의 코드를 전역으로 사용함
        db = getAppDatabase(this)

        // BookService는 인터페이스이므로 API call 할 때 실제 사용하는 것이 아닌 동작 정의이므로 아래와 같이 구현체를 만들어줘야함
        val retrofit = Retrofit.Builder()
            .baseUrl("https://book.interpark.com") // 사용할 baseURL 설정
            .addConverterFactory(GsonConverterFactory.create()) // GSON으로 변환하기 위한 라이브러리 추가했으므로 바로 변환
            .build() // 빌드하면 됨

        // 앞서 말했듯이 인터페이스를 구현체로 만들기 위해서 retrofit으로 create 함
        bookService = retrofit.create(BookService::class.java)

        // API Key가 필요하므로 앞서 설정한 Key를 그대로 받음
        bookService.getBestSellerBooks(getString(R.string.interparkAPIKey))
            .enqueue(object : Callback<BestSellerDto> {
                // 큐에 넣고 반환을 함, 이전에 만들었던 Dto를 반환함, 그리고 object에 필요한 부분을 구현해야함
                // 구현하는 것은 응답 성공과 실패에 대한 구현임, onResponse는 성공, onFailure는 실패
                override fun onResponse(
                    call: Call<BestSellerDto>,
                    response: Response<BestSellerDto>
                ) {

                    if (response.isSuccessful.not()) {
                        // 만약 실패시 예외처리
                        Log.e(TAG, "Not Success")
                        return
                    }

                    // 정상적으로 받았다면 Dto에서 body에서 값을 로그를 찍어봄
                    response.body()?.let {
                        // List로 바꿔서 데이터를 넘김(데이터를 그릴 수 있게 됨)
                        adapter.submitList(it.books)
                    }



                }

                override fun onFailure(call: Call<BestSellerDto>, t: Throwable) {

                    Log.e(TAG, t.toString())
                }

            })



    }

    private fun search(keyword: String){
        // retrofit 통신을 통해서 입력한 책에 대해서 정보를 가져옴 API를 활용해서
        bookService.getBooksByName(getString(R.string.interparkAPIKey), keyword)
            .enqueue(object : Callback<SearchBookDto> {
                // 큐에 넣고 반환을 함, 이전에 만들었던 Dto를 반환함, 그리고 object에 필요한 부분을 구현해야함
                // 구현하는 것은 응답 성공과 실패에 대한 구현임, onResponse는 성공, onFailure는 실패
                override fun onResponse(
                    call: Call<SearchBookDto>,
                    response: Response<SearchBookDto>
                ) {

                    hideHistoryView() // search가 일어난 시점에 hide 시킴
                    // DB에 검색기록 저장
                    saveSearchKeyword(keyword)

                    if (response.isSuccessful.not()) {
                        // 만약 실패시 예외처리
                        Log.e(TAG, "Not Success")
                        return
                    }

                    // List로 바꿔서 데이터를 넘김(데이터를 그릴 수 있게 됨)
                    adapter.submitList(response.body()?.books.orEmpty())



                }

                override fun onFailure(call: Call<SearchBookDto>, t: Throwable) {
                    hideHistoryView()
                    Log.e(TAG, t.toString())
                }

            })

    }

    private fun initBookRecyclerView() {
        // 어댑터 연결함
        adapter = BookAdapter(itemClickedListener = {
            val intent = Intent(this, DetailActivity::class.java) // 클릭리스너를 넣었으므로 아이템 클릭시 인텐트로 넘어가게 함
            // data 클래스의 내용을 하나씩 넘길 수 있지만 클래스 자체를 직렬화해서 넘김, 해당 클래스를 Parcelize 설정해서 가능함
            intent.putExtra("bookModel", it)
            startActivity(intent)
        })

        binding.bookRecyclerView.layoutManager = LinearLayoutManager(this) // 리사이클러뷰 실제로 어떻게 그려질지 설정(context 설정)
        binding.bookRecyclerView.adapter = adapter // 어댑터 연결
    }

    private fun initHistoryRecyclerView() {
        historyAdapter = HistoryAdapter(historyDeleteClickedListener = {
            deleteSearchKeyword(it) // 메인에서 db를 핸들링하기 때문에 이를 함수로 만들고 이벤트 처리를 어댑터에다가 넘김
        })

        // 리사이클러뷰 레이아웃 설정 어댑터 설정
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.historyRecyclerView.adapter = historyAdapter
        initSearchEditText()
    }

    // search를 하기 위한 함수
    private fun initSearchEditText() {
        // 엔터키를 누르면 검색할 수 있도록 EditText를 통해 타자로 입력한 정보를 가져옴
        // KeyDown, KeyUp 클릭인지, LongPress인지 결정을 함(MotionEvent Action에서)
        binding.searchEditText.setOnKeyListener { v, keyCode, event ->
            // 엔터키를 잡아서 입력값을 가지고 검색을 함
            if(keyCode == KeyEvent.KEYCODE_ENTER && event.action == MotionEvent.ACTION_DOWN) {
                // search하도록 함
                search(binding.searchEditText.text.toString())
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        binding.searchEditText.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_DOWN) { // 검색기록 보여주게 함
                showHistoryView()
            }
            return@setOnTouchListener false
        }
    }

    // 검색기록은 초기에는 보이지 않고 검색기록 확인시에만 보이게 하기 위해서 아래와 같이 보여주는 가리는 함수를 정의함
    private fun showHistoryView() {
        // DB에서 데이터를 가져오고 이를 어댑터에 연결해주는 게 필요함
        Thread {
            val keywords = db.historyDao().getAll().reversed() // 최신순서대로 보이기 위해서 역순으로 함

            // UI 갱신 위해 스레드 열어서 처리
            runOnUiThread{
                binding.historyRecyclerView.isVisible = true
                historyAdapter.submitList(keywords.orEmpty()) // DB 가져온 리스트 보내줌
            }
        }.start()

    }

    private fun hideHistoryView() {
        binding.historyRecyclerView.isVisible = false
    }


    private fun saveSearchKeyword(keyword: String) {
        // DB에 저장하는 함수, historyDao 활용, 여기서 Thread를 활용해서 처리함
        Thread {
            db.historyDao().insertHistory(History(null,keyword))
        }.start()
    }

    private fun deleteSearchKeyword(keyword: String) {
        Thread {
            db.historyDao().delete(keyword) // db 삭제함
            // View 삭제하고 갱신함
            showHistoryView()
        }.start()
    }

    companion object {

        private const val TAG = "MainActivity"
    }
}
```

## 상세화면
- MainActivity에 있는 혹은 검색을 해서 나온 결과에 대해서 아이템 클릭 시 해당 책에 대한 상세 내용을 받음, 이는 앞서 정의한 Book 데이터 클래스르 통해서 정보를 받아 온 뒤 binding한 상세화면에 나타내면 됨

- 구현 화면은 아래와 같음

![one](/Intermediate/BookReview/img/two.png)
![one](/Intermediate/BookReview/img/three.png)

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".DetailActivity">

    <ScrollView
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent">

        <androidx.constraintlayout.widget.ConstraintLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content">

            <TextView
                android:id="@+id/titleTextView"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_margin="16dp"
                android:gravity="center"
                android:textColor="@color/black"
                android:textSize="24sp"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toTopOf="parent" />

            <ImageView
                android:id="@+id/coverImageView"
                android:layout_width="300dp"
                android:layout_height="300dp"
                android:layout_marginTop="16dp"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toBottomOf="@id/titleTextView" />

            <TextView
                android:id="@+id/descriptionTextView"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_margin="16dp"
                android:textColor="@color/black"
                android:textSize="16sp"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toBottomOf="@id/coverImageView" />

            <EditText
                android:id="@+id/reviewEditText"
                android:layout_width="0dp"
                android:layout_height="300dp"
                app:layout_constraintBottom_toTopOf="@id/saveButton"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toBottomOf="@id/descriptionTextView" />

            <Button
                android:id="@+id/saveButton"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:text="저장하기"
                app:layout_constraintBottom_toBottomOf="parent"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent" />

        </androidx.constraintlayout.widget.ConstraintLayout>

    </ScrollView>

</androidx.constraintlayout.widget.ConstraintLayout>
```

- 그리고 Review에 대해서도 DB를 활용하여 저장하므로 위와 비슷한 로직으로 Entity, Dao를 구현하고 위에서 보여준 AppDatabase에 추가한 것임

### Review.kt
```kotlin
package techtown.org.bookreview.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/*
Review에 대한 정보가 있는 테이블
 */
@Entity
data class Review(
    @PrimaryKey val id: Int?,
    @ColumnInfo(name = "review") val review: String?
)
```

### ReviewDao.kt
```kotlin
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
```

### DetailActivity.kt
- 상세화면은 인텐트로 넘겨받은 데이터를 해당 화면에 그려주는 것과 DB를 활용해서 리뷰를 저장하는 정도의 기능이 있음
```kotlin
package techtown.org.bookreview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Room
import com.bumptech.glide.Glide
import techtown.org.bookreview.databinding.ActivityDetailBinding
import techtown.org.bookreview.model.Book
import techtown.org.bookreview.model.Review

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    // DB를 활용하기 위해서 선언함
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        // AppDatabase,DB를 빌드함
//        db = Room.databaseBuilder(
//            applicationContext,
//            AppDatabase::class.java,
//            "BookSearchDB"
//        ).build()

        // 하지만 DB를 추가하거나 바뀔 때 이 부분에 대해서 마이그레이션과 버전관리를 위해서 db 빌드의 코드를 전역으로 사용함
        db = getAppDatabase(this)

        // 메인에서 이미지, title, description을 인텐트를 통해서 넘겨받음
        // 이때 Parcelable한 Book 모델 클래스를 넘겼으므로 해당 클래스를 그대로 받아옴
        val model = intent.getParcelableExtra<Book>("bookModel")

        binding.titleTextView.text = model?.title.orEmpty()
        binding.descriptionTextView.text = model?.description.orEmpty()

        Glide.with(binding.coverImageView.context)
            .load(model?.coverSmallUrl.orEmpty())
            .into(binding.coverImageView)

        // Review를 가져오기 위한 처리
        Thread {
            val review = db.reviewDao().getOneReview(model?.id?.toInt() ?: 0)
            runOnUiThread{
                binding.reviewEditText.setText(review?.review.orEmpty())
            }
        }.start()

        binding.saveButton.setOnClickListener {
            Thread {
                // 저장하기 버튼을 누르면 리뷰에 대한 내용이 저장됨, 해당 DB에 맞게
                db.reviewDao().saveReview(
                    Review(model?.id?.toInt() ?: 0,
                    binding.reviewEditText.text.toString()
                    )
                )
            }.start()
        }
    }
}
```