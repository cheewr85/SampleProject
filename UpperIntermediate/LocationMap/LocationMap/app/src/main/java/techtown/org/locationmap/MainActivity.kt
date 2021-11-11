package techtown.org.locationmap

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import kotlinx.coroutines.*
import techtown.org.locationmap.MapActivity.Companion.SEARCH_RESULT_EXTRA_KEY
import techtown.org.locationmap.databinding.ActivityMainBinding
import techtown.org.locationmap.model.LocationLatLngEntity
import techtown.org.locationmap.model.SearchResultEntity
import techtown.org.locationmap.response.search.Poi
import techtown.org.locationmap.response.search.Pois
import techtown.org.locationmap.utillity.RetrofitUtil
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var job: Job

    // 비동기 처리를 위한 코루틴 설정
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    // 뷰 바인딩을 바탕으로 뷰 객체를 가져와서 사용
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: SearchRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Job 객체 초기화
       job = Job()

        initAdapter()
        initViews()
        bindViews()
        initData()

    }

    private fun initViews() = with(binding) {
        // View를 초기화 하는 함수 with을 통해 초기화
        emptyResultTextView.isVisible = false
        recyclerView.adapter = adapter
    }

    private fun bindViews() = with(binding) {
        searchButton.setOnClickListener {
            // searchbutton 클릭한 시점에 검색을 할 수 있게함, 이 시점에 데이터도 가져와야함 edittext에서
            searchKeyword(searchBarInputView.text.toString())
        }
    }

    private fun initAdapter() {
        // 어댑터를 초기화하는 함수
        adapter = SearchRecyclerAdapter()
    }

    private fun initData() {
        adapter.notifyDataSetChanged()
    }

    private fun setData(pois: Pois) {
        // 서버에서 받아온 데이터에서 Poi에 대해서 값을 설정함
        val dataList = pois.poi.map {
            SearchResultEntity(
                name = it.name ?: "빌딩명 없음",
                fullAddress = makeMainAddress(it),
                locationLatLng = LocationLatLngEntity(
                    it.noorLat,
                    it.noorLon
                )
            )
        }
        adapter.setSearchResultList(dataList) {
            Toast.makeText(this,"빌딩이름 : ${it.name} 주소 : ${it.fullAddress} 위도/경도 : ${it.locationLatLng}", Toast.LENGTH_SHORT).show()
            startActivity(
                Intent(this, MapActivity::class.java).apply {
                    putExtra(SEARCH_RESULT_EXTRA_KEY, it)
                } // 인텐트로 구글맵 보이게 실행함, 그러면서 위치 정보에 대해서 담아서 보내줌
            )
        }
    }

    private fun searchKeyword(keywordString: String) {
        // 비동기 프로그램 진행함 io 쓰레드로 바뀌었다가 메인 쓰레드로 바꿈
        launch(coroutineContext) { // 메인 쓰레드에서 먼저 시작함을 알림, 위에서 선언한 context로
            try {
                // IO 쓰레드로 전환
                withContext(Dispatchers.IO) {
                    // API 호출해서 가져옴
                    val response = RetrofitUtil.apiService.getSearchLocation(
                        keyword = keywordString
                    )

                    if (response.isSuccessful) {
                        // 성공 결과의 body 값을 넣어서 활용함
                        val body = response.body()
                        withContext(Dispatchers.Main) {
                            // Main 쓰레드로 바꾸어줌
                            body?.let{ searchResponse ->
                                setData(searchResponse.searchPoiInfo.pois)
                            }
                        }
                    }
                }
            } catch(e: Exception) {

            }
        }
    }

    // 주소지마다 주소값이 다를 수 있기 때문에 그를 꺼내 쓸 수 있는 함수
    private fun makeMainAddress(poi: Poi): String =
        if (poi.secondNo?.trim().isNullOrEmpty()) {
            (poi.upperAddrName?.trim() ?: "") + " " +
                    (poi.middleAddrName?.trim() ?: "") + " " +
                    (poi.lowerAddrName?.trim() ?: "") + " " +
                    (poi.detailAddrName?.trim() ?: "") + " " +
                    poi.firstNo?.trim()
        } else {
            (poi.upperAddrName?.trim() ?: "") + " " +
                    (poi.middleAddrName?.trim() ?: "") + " " +
                    (poi.lowerAddrName?.trim() ?: "") + " " +
                    (poi.detailAddrName?.trim() ?: "") + " " +
                    (poi.firstNo?.trim() ?: "") + " " +
                    poi.secondNo?.trim()
        }
}