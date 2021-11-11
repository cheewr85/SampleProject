## 위치검색 지도 앱
- SK Open API TMAP을 활용하여 지도 검색 기능을 통해서 검색한 위치에 대한 정보를 불러옴

- 그리고 API에서 받아온 데이터를 기반으로 Google Map을 활용하여 검색한 위치를 나오게 함, 마커로 표시함

- 그리고 Google Map에서 내 위치를 가져오는 버튼을 통해 현재 내 위치를 불러오고 내 위치로 마커를 찍고 변환함

- API에서 데이터를 받아오는데 Retrofit을 활용하는데 이때 Main 쓰레드에서 불러오지 않고 비동기 처리를 하기 위해서 Coroutine을 활용하여 데이터 처리를 함

- Okhttp를 활용하여서 빌드를 하고 처리를 하면서 Logging을 함

- ViewBinding을 활용함

### 메인화면
![one](/UpperIntermediate/LocationMap/img/one.png)

- EditText로 입력받고 검색을 함, RecyclerView에 검색한 결과를 TMAP에서 받아온 데이터 소스에서 직접 위치에 찍고 리사이클러뷰에 담기 위한 Entity에 연결한 결과값이 나옴

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <EditText
        android:id="@+id/searchBarInputView"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:hint="@string/please_input_search_keyword"
        app:layout_constraintEnd_toStartOf="@id/searchButton"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <Button
        android:id="@+id/searchButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/search"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <!-- xml 상에서 layoutmanager 설정함-->
    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/searchBarInputView"
        app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager"/>

    <TextView
        android:id="@+id/emptyResultTextView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/empty_result_text"
        android:visibility="gone"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        tools:visibility="visible" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

- 리사이클러뷰 item_view
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools">

    <TextView
        android:id="@+id/textTextView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        tools:text="제목"
        android:layout_marginTop="8dp"
        android:layout_marginStart="8dp"/>

    <TextView
        android:id="@+id/subtextTextView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        tools:text="부제목"
        app:layout_constraintStart_toStartOf="@id/textTextView"
        app:layout_constraintTop_toBottomOf="@id/textTextView"
        app:layout_constraintBottom_toBottomOf="parent"
        android:layout_marginBottom="8dp"
        android:layout_marginTop="4dp"/>

    <View
        android:id="@+id/dividerView"
        android:layout_width="0dp"
        android:layout_height="1dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        android:background="@color/black"/>

</androidx.constraintlayout.widget.ConstraintLayout>
```

### Data Class
- TMAP을 활용하기 위해서 거기서 받아올 수 있는 데이터에 대해서 미리 패키지를 구분하여서 응답 데이터로 만들어둠

- 이 데이터는 검색시 검색어 기준으로 검색한 위치와 상세주소를 그리고 위도 경도를 받아와서 지도에 띄우기 위한 데이터를 주로 씀

- 그 이외에 모든 데이터를 다 쓰는 것은 아니지만 문서상 API 사용 기준에 맞게 관련 데이터들 역시 같이 다 처리한 것임

### Poi.kt
```kotlin
package techtown.org.locationmap.response.search

data class Poi(
    //POI 의  id
    val id: String? = null,

    //POI 의 name
    val name: String? = null,

    //POI 에 대한 전화번호
    val telNo: String? = null,

    //시설물 입구 위도 좌표
    val frontLat: Float = 0.0f,

    //시설물 입구 경도 좌표
    val frontLon: Float = 0.0f,

    //중심점 위도 좌표
    val noorLat: Float = 0.0f,

    //중심점 경도 좌표
    val noorLon: Float = 0.0f,

    //표출 주소 대분류명
    val upperAddrName: String? = null,

    //표출 주소 중분류명
    val middleAddrName: String? = null,

    //표출 주소 소분류명
    val lowerAddrName: String? = null,

    //표출 주소 세분류명
    val detailAddrName: String? = null,

    //본번
    val firstNo: String? = null,

    //부번
    val secondNo: String? = null,

    //도로명
    val roadName: String? = null,

    //건물번호 1
    val firstBuildNo: String? = null,

    //건물번호 2
    val secondBuildNo: String? = null,

    //업종 대분류명
    val mlClass: String? = null,

    //거리(km)
    val radius: String? = null,

    //업소명
    val bizName: String? = null,

    //시설목적
    val upperBizName: String? = null,

    //시설분류
    val middleBizName: String? = null,

    //시설이름 ex) 지하철역 병원 등
    val lowerBizName: String? = null,

    //상세 이름
    val detailBizName: String? = null,

    //길안내 요청 유무
    val rpFlag: String? = null,

    //주차 가능유무
    val parkFlag: String? = null,

    //POI 상세정보 유무
    val detailInfoFlag: String? = null,

    //소개 정보
    val desc: String? = null
)
```

### Pois.kt
```kotlin
package techtown.org.locationmap.response.search

data class Pois(
    val poi: List<Poi>
)
```

### SearchPoiInfo.kt
```kotlin
package techtown.org.locationmap.response.search

data class SearchPoiInfo(
    val totalCount: String,
    val count: String,
    val page: String,
    val pois: Pois
)
```

### SearchResponse.kt
```kotlin
package techtown.org.locationmap.response.search

data class SearchResponse(
    val searchPoiInfo: SearchPoiInfo
)
```

### AddressInfo.kt
```kotlin
package techtown.org.locationmap.response.address

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/*
내 위치에 대한 정보를 받아오는 데이터 클래스
 */
data class AddressInfo(
    @SerializedName("fullAddress")
    @Expose
    val fullAddress: String?,
    @SerializedName("addressType")
    @Expose
    val addressType: String?,
    @SerializedName("city_do")
    @Expose
    val cityDo: String?,
    @SerializedName("gu_gun")
    @Expose
    val guGun: String?,
    @SerializedName("eup_myun")
    @Expose
    val eupMyun: String?,
    @SerializedName("adminDong")
    @Expose
    val adminDong: String?,
    @SerializedName("adminDongCode")
    @Expose
    val adminDongCode: String?,
    @SerializedName("legalDong")
    @Expose
    val legalDong: String?,
    @SerializedName("legalDongCode")
    @Expose
    val legalDongCode: String?,
    @SerializedName("ri")
    @Expose
    val ri: String?,
    @SerializedName("bunji")
    @Expose
    val bunji: String?,
    @SerializedName("roadName")
    @Expose
    val roadName: String?,
    @SerializedName("buildingIndex")
    @Expose
    val buildingIndex: String?,
    @SerializedName("buildingName")
    @Expose
    val buildingName: String?,
    @SerializedName("mappingDistance")
    @Expose
    val mappingDistance: String?,
    @SerializedName("roadCode")
    @Expose
    val roadCode: String?
)
```

### AddressInfoResponse.kt
```kotlin
package techtown.org.locationmap.response.address

data class AddressInfoResponse(
    val addressInfo: AddressInfo
)
```

### model
- 위와 같이 API와 통신하여 받은 데이터를 검색화면, 지도화면에 응용하기 위해서 필요한 것을 가공해서 처리하기 위한 Entity임

- 이를 인텐트로 처리해서 데이터를 주고 받을 수 있고 그리고 위에서 받아온 Response 결과 중 필요한 부분만을 활용하기 위해서 쓸 수 있음

### LocationLatLngEntity.kt
```kotlin
package techtown.org.locationmap.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/*
위도 경도에 대한 정보를 담는 데이터 클래스
 */
@Parcelize
data class LocationLatLngEntity (
    val latitude: Float,
    val longitude: Float
): Parcelable
```

### SearchResultEntity.kt
```kotlin
package techtown.org.locationmap.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/*
검색한 결과에 대한 데이터를 담는 데이터 클래스
인텐트에 담아서 데이트를 넘겨주기 위해서 Parcelize 사용함
 */
@Parcelize
data class SearchResultEntity (
    val fullAddress: String,
    val name: String,
    val locationLatLng: LocationLatLngEntity
): Parcelable
```

### SearchRecyclerAdapter.kt
- 위에서 데이터 처리를 가지고 이제 RecyclerView에 보여주기 위해서 어댑터 처리를 함, Entity를 통해서 그 결과만을 보여줌

```kotlin
package techtown.org.locationmap

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import techtown.org.locationmap.databinding.ViewholderSearchResultItemBinding
import techtown.org.locationmap.model.SearchResultEntity

class SearchRecyclerAdapter: RecyclerView.Adapter<SearchRecyclerAdapter.SearchResultItemViewHolder>(){

    private var searchResultList: List<SearchResultEntity> = listOf()
    private lateinit var searchResultClickListener: (SearchResultEntity) -> Unit


    class SearchResultItemViewHolder(private val binding: ViewholderSearchResultItemBinding, val searchResultClickListener: (SearchResultEntity) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        // ViewHolder 생성 및 검색 결과를 받기 때문에 리스너를 통해서 검색결과 받아옴
        fun bindData(data: SearchResultEntity) = with(binding) {
            textTextView.text = data.name
            subtextTextView.text = data.fullAddress
        }

        fun bindViews(data: SearchResultEntity) {
            binding.root.setOnClickListener {
                searchResultClickListener(data)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultItemViewHolder {
        // item으로 쓸 레이아웃을 itemViewHolder에 연결해서 binding 작업을 위해서 선언하고 처리함
        // view 객체를 만들어서 viewholder에 넣어줌
        val view = ViewholderSearchResultItemBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return SearchResultItemViewHolder(view, searchResultClickListener)

    }

    override fun onBindViewHolder(holder: SearchResultItemViewHolder, position: Int) {
        // 해당 위치에 있는 데이터를 넣게끔 설정
        holder.bindData(searchResultList[position])
        holder.bindViews(searchResultList[position])
    }

    override fun getItemCount(): Int = searchResultList.size

    fun setSearchResultList(searchResultList: List<SearchResultEntity>, searchResultClickListener:(SearchResultEntity) -> Unit) {
        this.searchResultList = searchResultList
        this.searchResultClickListener = searchResultClickListener
        notifyDataSetChanged() // 데이터 반영함
    }
}
```

### utillity & Key & Url
- MainActivity에서 다 활용하기 전, Retrofit 생성, 그리고 TMAP Url, ApiKey등을 별도의 클래스로 만들어서 처리를 함

- 그리고 Retrofit의 경우 ApiService 인터페이스를 만들어서 위치 정보 그리고 지도에 표시하기 위한 정보를 받아오는 인터페이스를 만듬

### URL.kt & Key.kt(생략)
```kotlin
package techtown.org.locationmap
/*
TMap의 서비스를 쓰기 위해서 기본 URL 정보
 */
object Url {
    const val TMAP_URL = "https://apis.openapi.sk.com"

    const val GET_TMAP_LOCATION = "/tmap/pois"

    const val GET_TMAP_REVERSE_GEO_CODE = "/tmap/geo/reversegeocoding"
}
```

### RetrofitUtil.kt
```kotlin
package techtown.org.locationmap.utillity


import techtown.org.locationmap.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import techtown.org.locationmap.Url
import java.util.concurrent.TimeUnit
/*
Retrofit 통신과 Logging을 확인하기 위해서 만든 기본 설정 및 함수들
 */
object RetrofitUtil {
    val apiService: ApiService by lazy { getRetrofit().create(ApiService::class.java) }

    private fun getRetrofit(): Retrofit {

        return Retrofit.Builder()
            .baseUrl(Url.TMAP_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildOkHttpClient())
            .build()
    }

    private fun buildOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            interceptor.level = HttpLoggingInterceptor.Level.NONE
        }
        return OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
    }
}
```

### ApiService.kt
```kotlin
package techtown.org.locationmap.utillity

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import techtown.org.locationmap.Key
import techtown.org.locationmap.Url
import techtown.org.locationmap.response.address.AddressInfoResponse
import techtown.org.locationmap.response.search.SearchResponse
/*
Header와 GET 메소드를 호출해서 TMap에 대한 API 서비스를 활용할 것임, Url, Key를 받고 쓰기 위한 쿼리파라미터를 씀
 */
interface ApiService {

    @GET(Url.GET_TMAP_LOCATION)
    suspend fun getSearchLocation(
        @Header("appKey") appKey: String = Key.TMAP_API,
        @Query("version") version: Int = 1,
        @Query("callback") callback: String? = null,
        @Query("count") count: Int = 20,
        @Query("searchKeyword") keyword: String,
        @Query("areaLLCode") areaLLCode: String? = null,
        @Query("areaLMCode") areaLMCode: String? = null,
        @Query("resCoordType") resCoordType: String? = null,
        @Query("searchType") searchType: String? = null,
        @Query("multiPoint") multiPoint: String? = null,
        @Query("searchtypCd") searchtypCd: String? = null,
        @Query("radius") radius: String? = null,
        @Query("reqCoordType") reqCoordType: String? = null,
        @Query("centerLon") centerLon: String? = null,
        @Query("centerLat") centerLat: String? = null
    ): Response<SearchResponse>

    @GET(Url.GET_TMAP_REVERSE_GEO_CODE)
    suspend fun getReverseGeoCode(
        @Header("appKey") appKey: String = Key.TMAP_API,
        @Query("version") version: Int = 1,
        @Query("callback") callback: String? = null,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("coordType") coordType: String? = null,
        @Query("addressType") addressType: String? = null
    ): Response<AddressInfoResponse>
}
```

### MainActivity.kt
- 여기서 Coroutine을 활용하여서 통신에 대해서 비동기 처리를 하고 나머지는 뷰 초기화와 앞서 만든 클래스를 활용하여 데이터, 지도 처리를 마무리함
```kotlin
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
```

### 지도화면 
![one](/UpperIntermediate/LocationMap/img/two.png)

- 검색한 데이터를 지도에 표시함 GoogleMap을 활용하여서

- 그리고 플로팅 버튼을 통해서 현재 내 위치도 표시하게끔 처리함

```kotlin
package techtown.org.locationmap

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.*
import techtown.org.locationmap.databinding.ActivityMapBinding
import techtown.org.locationmap.model.LocationLatLngEntity
import techtown.org.locationmap.model.SearchResultEntity
import techtown.org.locationmap.utillity.RetrofitUtil
import kotlin.coroutines.CoroutineContext

class MapActivity : AppCompatActivity(), OnMapReadyCallback, CoroutineScope {

    private lateinit var job: Job

    // 비동기 처리를 위한 코루틴 설정
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var binding: ActivityMapBinding
    private lateinit var map: GoogleMap
    private var currentSelectMarker: Marker? = null

    private lateinit var searchResult: SearchResultEntity

    // 위치 정보를 불러올때 관리해주는 유틸리티 매니저
    private lateinit var locationManager: LocationManager

    private lateinit var myLocationListener: MyLocationListener

    companion object {
        const val SEARCH_RESULT_EXTRA_KEY = "SEARCH_RESULT_EXTRA_KEY"
        const val CAMERA_ZOOM_LEVEL = 17f
        const val PERMISSION_REQUEST_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        job = Job()

        if (::searchResult.isInitialized.not()) {
            // 초기에 값이 없다면 그 값을 가져와서 넣어줌
            intent?.let {
                // 인텐트로 넘겨진 값을 받아옴
                searchResult = it.getParcelableExtra<SearchResultEntity>(SEARCH_RESULT_EXTRA_KEY)
                    ?: throw Exception("데이터가 존재하지 않습니다.")
                setupGoogleMap() // 구글맵 가져와서 지정함
            }
        }
        bindViews()
    }

    private fun bindViews() = with(binding) {
        currentLocationButton.setOnClickListener {
            getMyLocation()
        }
    }

    private fun setupGoogleMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this) // 구글맵 지도 객체를 가져와서 사용함
    }

    override fun onMapReady(map: GoogleMap) {
        // 구글맵 객체를 가져와서 사용함
        this.map = map
        currentSelectMarker = setUpMarker(searchResult)

        currentSelectMarker?.showInfoWindow()
    }

    private fun setUpMarker(searchResult: SearchResultEntity): Marker? {
        // 마커를 지정함, searchResult에서 넘겨받은 값을 받아서 지정함
        val positionLatLng = LatLng(
            searchResult.locationLatLng.latitude.toDouble(),
            searchResult.locationLatLng.longitude.toDouble()
        )
        // 마커설정을 아래와 같이함
        val markerOptions = MarkerOptions().apply {
            position(positionLatLng)
            title(searchResult.name)
            snippet(searchResult.fullAddress)
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(positionLatLng, CAMERA_ZOOM_LEVEL))

        return map.addMarker(markerOptions)
    }

    private fun getMyLocation() {
        if (::locationManager.isInitialized.not()) {
            // locationManager가 초기화되지 않았다면 초기화 시킴
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }

        // Gps 사용 가능 여부 확인함함
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (isGpsEnabled) {
            // 사용 가능하면 권한을 불러올 것임, 권한 체크를 먼저함
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) { // 권한이 없는 경우 권한확인을 함
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    PERMISSION_REQUEST_CODE
                )
            } else {
                // 권한이 있는 경우
                setMyLocationListener()
            }
        }
    }

    @SuppressLint("MissingPermission") // 권한처리는 위에서 했으므로 suppress함
    private fun setMyLocationListener() {
        // 내 위치를 바로 불러오는 함수
        val minTime = 1500L
        val minDistance = 100f

        if (::myLocationListener.isInitialized.not()) {
            myLocationListener = MyLocationListener()
        }
        with(locationManager) {
            // 위치정보 요청해서 가져옴, GPS 기능 가져와서 불러옴
            requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTime, minDistance, myLocationListener
            )
            requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTime, minDistance, myLocationListener
            )

        }
    }

    private fun onCurrentLocationChanged(locationLatLngEntity: LocationLatLngEntity) {
        // 포지션 값 기준으로 내 위치로 옮김
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    locationLatLngEntity.latitude.toDouble(),
                    locationLatLngEntity.longitude.toDouble()
                ), CAMERA_ZOOM_LEVEL
            )
        )

        // 내 위치 정보를 가져옴
        loadReverseGeoInfomation(locationLatLngEntity)
        removeLocationListener() // 내 위치가 바뀌면 이전에 내 위치를 불러오는 리스너가 필요 없으므로 제거함
    }

    private fun loadReverseGeoInfomation(locationLatLngEntity: LocationLatLngEntity) {
        // 내 위치 정보를 불러옴 AdderssInfo를 활용하여 역으로 위치 정보에 대해서 API에서 받아옴
        // 내 위치를 받아서 처리하는 것을 코루틴을 활용함
        launch(coroutineContext) {
            try {
                withContext(Dispatchers.IO) {
                    val response = RetrofitUtil.apiService.getReverseGeoCode(
                        lat = locationLatLngEntity.latitude.toDouble(),
                        lon = locationLatLngEntity.longitude.toDouble()
                    )
                    if (response.isSuccessful) {
                        // 성공적으로 받았으면 response에서 데이터를 꺼내줌
                        val body = response.body()
                        withContext(Dispatchers.Main) {
                            body?.let {
                                currentSelectMarker = setUpMarker(SearchResultEntity(
                                    fullAddress = it.addressInfo.fullAddress ?: "주소 정보 없음",
                                    name = "내 위치",
                                    locationLatLng = locationLatLngEntity
                                ))
                                currentSelectMarker?.showInfoWindow()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@MapActivity, "검색하는 과정에서 에러가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun removeLocationListener() {
        if (::locationManager.isInitialized && ::myLocationListener.isInitialized) {
            locationManager.removeUpdates(myLocationListener)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // 권한을 받은 것을 체크함
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // 권한을 받았는지 확인한 후 위치 체크
                setMyLocationListener()
            } else {
                // 권한을 받지 못한 경우 체크
                Toast.makeText(this, "권한을 받지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    inner class MyLocationListener : LocationListener {

        override fun onLocationChanged(location: Location) {
            // 현재 위치 콜백을 받아서 처리함
            val locationLatLngEntity = LocationLatLngEntity(
                location.latitude.toFloat(),
                location.longitude.toFloat()
            )
            onCurrentLocationChanged(locationLatLngEntity)
        }

    }
}
```