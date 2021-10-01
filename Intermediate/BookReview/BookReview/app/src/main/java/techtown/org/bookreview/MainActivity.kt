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