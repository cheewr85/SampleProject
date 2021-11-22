## 미세먼지 앱
- 내 위치 정보를 가져와서 해당 위치에서의 미세먼지 상태 정보를 불러옴

- 공공데이터 포털 API를 활용하여서 미세먼지 상태 정보를 가져옴

- 카카오 API를 활용하여서 좌표값을 변환하여서 해당 위치에서의 미세먼지 정보를 불러올 수 있게함

- 미세먼지 위젯을 추가할 수 있게끔 위젯 또한 만듬

### 데이터 파싱
- data 패키지의 models의 내용 같은 경우 공식문서상에서 대기 오염 정보와 측정소 정보에 대해서 공공 API에서 나와있는 JSON 형태를 받아와서 쓰기 위해서 JSON 형태를 kotlin data class로 변환한 것임

- 이 부분에서는 사실상 거의 그대로 해당 데이터를 쓰기 때문에 굳이 data class를 직접 만들지 않은 것임

### API 통신
- 기본 Url 정보, Repository 부분의 경우 싱글톤으로 처리함

### Url.kt
```kotlin
package techtown.org.airkorea.data
/*
API 사용을 위한 URL을 저장한 object
 */
object Url {
    const val KAKAO_API_BASE_URL = "https://dapi.kakao.com"
    const val AIR_KOREA_API_BASE_URL = "http://apis.data.go.kr/"
}
```

### Repository.kt
```kotlin
package techtown.org.airkorea.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import techtown.org.airkorea.BuildConfig
import techtown.org.airkorea.data.models.airquality.MeasuredValues
import techtown.org.airkorea.data.models.monitoriongstation.MonitoringStation
import techtown.org.airkorea.data.services.AirKoreaApiService
import techtown.org.airkorea.data.services.KakaoLocalApiService

/*
Retrofit을 통한 통신을 위해 싱글톤으로 만든 object
 */
object Repository {

    suspend fun getNearbyMonitoringStation(latitude: Double, longitude: Double): MonitoringStation? {
        // Tm 좌표를 받음
        val tmCoordinates=  kakaoLocalApiService
            .getTmCoordinates(longitude, latitude)
            .body()
            ?.documents // x,y좌표가 리스트 형태로 되어있음
            ?.firstOrNull() // 첫 좌표 확인

        val tmX = tmCoordinates?.x
        val tmY = tmCoordinates?.y

        // KAKAO를 통해 tm 좌표로 변환후 해당 좌표에서 측정소를 가져옴
        return airKoreaApiService
            .getNearbyMonitoringStation(tmX!!, tmY!!)
            .body()
            ?.response
            ?.body
            ?.monitoringStations
            ?.minByOrNull { it.tm ?: Double.MAX_VALUE } // API 접근해서 tm 값 중 가장 적은 즉 가까운 측정소를 가져옴
    }

    suspend fun getLatestAirQualityData(stationName: String): MeasuredValues? =
        airKoreaApiService
            .getRealtimeAirQualities(stationName)
            .body()
            ?.response
            ?.body
            ?.MeasuredValues // 측정된 데이터를 불러옴
            ?.firstOrNull() // 첫 값이 없으면 끝

    // Kakao API Service
    private val kakaoLocalApiService: KakaoLocalApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Url.KAKAO_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildHttpClient()) // logging을 위해 추가
            .build()
            .create()
    }

    // Air Korea Service
    private val airKoreaApiService: AirKoreaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Url.AIR_KOREA_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildHttpClient()) // logging을 위해 추가
            .build()
            .create()
    }

    // Header를 조작할 필요 없이 logging 목적의 클라이언트
    private fun buildHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply{
                    // logging level 수준에 따라 다르게 처리해줘야함(보이는 경고의 정도 결정)
                    level = if(BuildConfig.DEBUG) {
                        // Debug일 때만 Body까지 보여줌
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            )
            .build()
}
```

### service 패키지
- 공공데이터포털 API, 카카오 API 사용을 위해서 해당 API와 통신할 부분에 대한 인터페이스 구현

### AirKoreaApiService.kt
```kotlin
package techtown.org.airkorea.data.services

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import techtown.org.airkorea.BuildConfig
import techtown.org.airkorea.data.models.airquality.AirQualityResponse
import techtown.org.airkorea.data.models.monitoriongstation.MonitoringStationsResponse

/*
근접 측정소에 해당하는 좌표, 대기오염 정보를 불러오는데 통신하는 인터페이스
 */
interface AirKoreaApiService {

    @GET(
        "B552584/MsrstnInfoInqireSvc/getNearbyMsrstnList" +
                "?serviceKey=${BuildConfig.AIR_KOREA_SERVICE_KEY}" +
                "&returnType=json"
    )
    suspend fun getNearbyMonitoringStation(
        // 좌표를 불러옴
        @Query("tmX") tmX: Double,
        @Query("tmY") tmY: Double
    ): Response<MonitoringStationsResponse>

    @GET(
        "B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty" +
                "?serviceKey=${BuildConfig.AIR_KOREA_SERVICE_KEY}" +
                "&returnType=json" +
                "&dataTerm=DAILY" +
                "&ver=1.3"
    )
    suspend fun getRealtimeAirQualities(
        @Query("stationName") stationName: String
    ): Response<AirQualityResponse>
}
```

### KakaoLocalApiService.kt
```kotlin
package techtown.org.airkorea.data.services

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import techtown.org.airkorea.BuildConfig
import techtown.org.airkorea.data.models.tmcoordinates.TmCoordinatesResponse

/*
카카오 API 사용을 통해서 좌표 변환을 위해서 통신하는 인터페이스
 */
interface KakaoLocalApiService {

    // KAKAO 좌표 변환을 위한 API 호출, tm 좌표로 변환
    @Headers("Authorization: KakaoAK ${BuildConfig.KAKAO_API_KEY}")
    @GET("v2/local/geo/transcoord.json?output_coord=TM")
    suspend fun getTmCoordinates(
        @Query("x") longitude: Double, // 경도
        @Query("y") latitude: Double // 위도
    ): Response<TmCoordinatesResponse>
}
```

## 메인화면
![one](/UpperIntermediate/AirKorea/img/one.png)

- 통신한 데이터를 바탕으로 해당 지역에 대한 미세먼지 정보를 불러오고 업데이트 할 수 있게함

- 중복되는 UI가 나타난 부분을 별도로 xml 레이아웃을 만들어서 include를 통해서 처리함, viewBinding을 사용했으므로 각각 include한 부분별로 id로 구분하였으므로 해당 UI의 데이터를 업데이트 하는 것은 직관적으로 처리할 수 있음

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.swiperefreshlayout.widget.SwipeRefreshLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/refresh"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <ProgressBar
            android:id="@+id/progressBar"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center" />

        <TextView
            android:id="@+id/errorDescriptionTextView"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:gravity="center"
            android:visibility="gone"
            android:text="예기치 못한 문제가 발생했습니다.\n잠시 후 다시 시도해주세요"
            tools:ignore="HardcodedText" />

        <androidx.constraintlayout.widget.ConstraintLayout
            android:id="@+id/contentsLayout"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:alpha="0"
            android:background="@color/gray"
            tools:context=".MainActivity">

            <TextView
                android:id="@+id/measuringStationNameTextView"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="50dp"
                android:textColor="@color/white"
                android:textSize="40sp"
                android:textStyle="bold"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toTopOf="parent"
                tools:text="강남대로" />

            <TextView
                android:id="@+id/totalGradeLabelTextView"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="6dp"
                android:textColor="@color/white"
                android:textSize="20sp"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toBottomOf="@id/measuringStationNameTextView"
                tools:text="매우 나쁨" />

            <TextView
                android:id="@+id/totalGradeEmojiTextView"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:textColor="@color/white"
                android:textSize="95sp"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toBottomOf="@id/totalGradeLabelTextView"
                tools:text="😀" />

            <TextView
                android:id="@+id/fineDustInformationTextView"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_marginTop="16dp"
                android:gravity="center"
                android:textColor="@color/white"
                android:textSize="16sp"
                app:layout_constraintEnd_toStartOf="@id/ultraFineDustInformationTextView"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toBottomOf="@id/totalGradeEmojiTextView"
                tools:text="미세먼지 : 40 😀" />

            <TextView
                android:id="@+id/ultraFineDustInformationTextView"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:gravity="center"
                android:textColor="@color/white"
                android:textSize="16sp"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toEndOf="@id/fineDustInformationTextView"
                app:layout_constraintTop_toTopOf="@id/fineDustInformationTextView"
                tools:text="초미세먼지: 10 😀" />

            <View
                android:id="@+id/upperDivider"
                android:layout_width="0dp"
                android:layout_height="1dp"
                android:layout_marginHorizontal="20dp"
                android:layout_marginTop="20dp"
                android:alpha="0.5"
                android:background="@color/white"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toBottomOf="@id/fineDustInformationTextView" />

            <LinearLayout
                android:layout_width="0dp"
                android:layout_height="0dp"
                android:layout_marginHorizontal="30dp"
                android:layout_marginVertical="10dp"
                android:orientation="vertical"
                app:layout_constraintBottom_toBottomOf="@id/lowerDivider"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toTopOf="@id/upperDivider">

                <include
                    android:id="@+id/so2Item"
                    layout="@layout/view_measured_item"
                    android:layout_width="match_parent"
                    android:layout_height="0dp"
                    android:layout_weight="1" />

                <include
                    android:id="@+id/coItem"
                    layout="@layout/view_measured_item"
                    android:layout_width="match_parent"
                    android:layout_height="0dp"
                    android:layout_weight="1" />

                <include
                    android:id="@+id/o3Item"
                    layout="@layout/view_measured_item"
                    android:layout_width="match_parent"
                    android:layout_height="0dp"
                    android:layout_weight="1" />

                <include
                    android:id="@+id/no2Item"
                    layout="@layout/view_measured_item"
                    android:layout_width="match_parent"
                    android:layout_height="0dp"
                    android:layout_weight="1" />

            </LinearLayout>

            <View
                android:id="@+id/lowerDivider"
                android:layout_width="0dp"
                android:layout_height="1dp"
                android:layout_marginHorizontal="20dp"
                android:layout_marginBottom="12dp"
                android:alpha="0.5"
                android:background="@color/white"
                app:layout_constraintBottom_toTopOf="@id/measuringStationAddressTextView"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent" />

            <TextView
                android:id="@+id/measuringStationAddressTextView"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_marginHorizontal="30dp"
                android:layout_marginBottom="20dp"
                android:maxLines="1"
                android:textColor="@color/white"
                app:autoSizeMaxTextSize="12sp"
                app:autoSizeMinTextSize="8sp"
                app:autoSizeTextType="uniform"
                app:layout_constraintBottom_toTopOf="@id/additionalInformationTextView"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                tools:text="측정소 위치: 서울시 강남대로...." />


            <TextView
                android:id="@+id/additionalInformationTextView"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:alpha="0.3"
                android:background="@color/black"
                android:drawablePadding="6dp"
                android:paddingHorizontal="16dp"
                android:paddingVertical="6dp"
                android:text="자료 출처: 환경부/한국환경공단\n 주의 사항: 해당 기관이 제공하는 자료는 인증을 받지 않은 실시간자료이므로 자료 오류 및 표출방식에 따라 값이 다를 수 있음"
                android:textColor="@color/white"
                android:textSize="10sp"
                app:drawableStartCompat="@drawable/ic_outline_info_24"
                app:layout_constraintBottom_toBottomOf="parent"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                tools:ignore="HardcodedText,SmallSp" />


        </androidx.constraintlayout.widget.ConstraintLayout>
    </FrameLayout>
</androidx.swiperefreshlayout.widget.SwipeRefreshLayout>
```

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:gravity="center"
    android:orientation="horizontal"
    tools:background="@color/gray"
    tools:layout_height="50dp">

    <TextView
        android:id="@+id/labelTextView"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:textColor="@color/white"
        android:textSize="16sp"
        tools:text="아황산가스" />

    <TextView
        android:id="@+id/gradeTextView"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:gravity="end"
        android:textColor="@color/white"
        android:textSize="16sp"
        tools:text="좋음 😀" />

    <TextView
        android:id="@+id/valueTextView"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:gravity="end"
        android:textColor="@color/white"
        android:textSize="16sp"
        tools:text="130 ppm" />

</LinearLayout>
```

### MainActivity.kt
- 앞서 Retrofit을 통해서 서버와 통신하고 해당 data model을 정의했으므로 바로 Main에서 사용할 수 있음

- 그리고 백그라운드 처리를 통한 권한 체크 역시 할 수 있음

```kotlin
package techtown.org.airkorea

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import techtown.org.airkorea.data.Repository
import techtown.org.airkorea.data.models.airquality.Grade
import techtown.org.airkorea.data.models.airquality.MeasuredValues
import techtown.org.airkorea.data.models.monitoriongstation.MonitoringStation
import techtown.org.airkorea.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var cancellationTokenSource: CancellationTokenSource? = null

    private val binding by lazy {ActivityMainBinding.inflate(layoutInflater)}
    private val scope = MainScope() // 코루틴 스코프 설정

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        bindViews()
        initVariables()
        // 앱 시작하자마자 바로 권한을 요청함
        requestLocationPermissions()
    }

    override fun onDestroy() {
        // 들어왔다가 바로 나간 경우 굳이 진행할 필요 없으므로 캔슬하면 됨
        super.onDestroy()
        cancellationTokenSource?.cancel()
        scope.cancel() // 종료시 코루틴 종료시킴
    }

    @SuppressLint("MissingPermission") // 어차피 권한이 부여된 상황에서 작업하는 것임
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // 권한이 넘어왔는지 확인하는 변수
        val locationPermissionGranted =
                requestCode == REQUEST_ACCESS_LOCATION_PERMISSIONS &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED

        // 백그라운드 권한을 확인함
        val backgroundLocationPermissionGranted =
            requestCode == REQUSET_BACKGROUND_ACCESS_LOCATION_PERMISSIONS &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED

        // 버전에 따라 다르게 처리함
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 백그라운드 권한 요청 처리
            if(!backgroundLocationPermissionGranted) {
                requestBackgroundLocationPermissions()
            } else {
                fetchAirQualityData()
            }
        } else {
            if (!locationPermissionGranted) {
                // 위치 권한이 안 넘어왔으면 바로 종료
                finish()
            } else {
                // 권한이 있으면 AirQuality를 Fetch함
                fetchAirQualityData()
            }
        }
    }

    private fun bindViews() {
        // refresh를 할 경우 데이터를 다시 받아오는 처리함
        binding.refresh.setOnRefreshListener {
            fetchAirQualityData()
        }
    }

    private fun initVariables() {
        // 현재 위치 정보를 불러오기 위한 Client 초기화
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun requestLocationPermissions() {
        // 권한 요청을 해야함, Location 관련 2개 처리해서 arrayOf로 함
        ActivityCompat.requestPermissions(
                this,
                arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ),
                REQUEST_ACCESS_LOCATION_PERMISSIONS
        )
    }

    private fun requestBackgroundLocationPermissions() {
        // 백그라운드 위치 권한을 요청함
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
            REQUSET_BACKGROUND_ACCESS_LOCATION_PERMISSIONS
        )
    }

    @SuppressLint("MissingPermission")
    private fun fetchAirQualityData() {
        // fetchData
        // 권한이 넘어오면 데이터 업데이트, 위치정보 제대로 가져오는지 확인
        cancellationTokenSource = CancellationTokenSource()

        fusedLocationProviderClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource!!.token // 생성하자마자 넘김
        ).addOnSuccessListener { location ->
            // task를 반환하므로 리스너를 추가함, location을 받음, 실제 API 받아온 상태
            scope.launch {
                binding.errorDescriptionTextView.visibility = View.GONE
                try {
                    // 코루틴 시작, GPS로 찍은 위치 정보를 Retrofit을 통해 API와 통신해 TM 좌표로 변환하고 해당 좌표에서 측정소를 가져옴
                    val monitoringStation =
                        Repository.getNearbyMonitoringStation(location.latitude, location.longitude)
                    // 해당 장소에서의 최신 대기 상태를 불러옴
                    val measuredValue =
                        Repository.getLatestAirQualityData(monitoringStation!!.stationName!!)

                    displayAirQualityData(monitoringStation, measuredValue!!)
                } catch(exception: Exception) {
                    // 에러 발생시 에러 나오는 텍스트 보이게 하고, 메인 화면을 없앰
                    binding.errorDescriptionTextView.visibility = View.VISIBLE
                    binding.contentsLayout.alpha = 0F
                } finally {
                    // 마지막 모든 작업이 끝났다면 정상적으로 처리된 것이므로 progresbar와 refresh를 없애게 처리함
                    binding.progressBar.visibility = View.GONE
                    binding.refresh.isRefreshing = false
                }
            }
        }
    }

    // 대기 환경에 대한 정보를 불러와서 보여줌
    @SuppressLint("SetTextI18n")
    fun displayAirQualityData(monitoringStation: MonitoringStation, measuredValues: MeasuredValues) {
        // 애니메이션 처리함, 로딩 이후 나타나므로 페이드인으로 나타나게 함
        binding.contentsLayout.animate()
            .alpha(1F)
            .start()

        binding.measuringStationNameTextView.text = monitoringStation.stationName
        binding.measuringStationAddressTextView.text = monitoringStation.addr

        // Grade를 사전에 설정한 enum class에서 지정을 함
        (measuredValues.khaiGrade ?: Grade.UNKNOWN).let { grade ->
            binding.root.setBackgroundResource(grade.colorResId)
            binding.totalGradeLabelTextView.text = grade.label
            binding.totalGradeEmojiTextView.text = grade.emoji
        }

        with(measuredValues) {
            binding.fineDustInformationTextView.text =
                "미세먼지: $pm10Value ㎍/㎥ ${(pm10Grade ?: Grade.UNKNOWN).emoji}"
            binding.ultraFineDustInformationTextView.text =
                "초미세먼지: $pm25Value ㎍/㎥ ${(pm25Grade ?: Grade.UNKNOWN).emoji}"

            // 각 대기별 상황 보여줌
            with(binding.so2Item) {
                labelTextView.text = "아황산가스"
                gradeTextView.text = (so2Grade ?: Grade.UNKNOWN).toString()
                valueTextView.text = "$so2Value ppm"
            }

            with(binding.coItem) {
                labelTextView.text = "일산화탄소"
                gradeTextView.text = (coGrade ?: Grade.UNKNOWN).toString()
                valueTextView.text = "$coValue ppm"
            }

            with(binding.o3Item) {
                labelTextView.text = "오존"
                gradeTextView.text = (o3Grade ?: Grade.UNKNOWN).toString()
                valueTextView.text = "$o3Value ppm"
            }

            with(binding.no2Item) {
                labelTextView.text = "이산화질소"
                gradeTextView.text = (no2Grade ?: Grade.UNKNOWN).toString()
                valueTextView.text = "$no2Value ppm"
            }
        }
    }

    companion object {
        // requestCode 상수로 정의
        private const val REQUEST_ACCESS_LOCATION_PERMISSIONS = 100
        private const val REQUSET_BACKGROUND_ACCESS_LOCATION_PERMISSIONS = 101
    }
}
```

## 위젯화면
![one](/UpperIntermediate/AirKorea/img/two.png)

- 위와 같이 위젯 화면으로써도 직접 구현할 수 있음, 레이아웃 작성은 동일하게 함

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@drawable/shape_widget_background"
    android:gravity="center"
    android:orientation="vertical"
    tools:background="@color/black"
    tools:layout_height="50dp"
    tools:layout_width="110dp">

    <TextView
        android:id="@+id/labelTextView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="미세먼지"
        android:textColor="@color/white"
        android:textSize="10sp"
        android:visibility="gone"
        tools:ignore="HardcodedText,SmallSp"
        tools:visibility="visible" />

    <TextView
        android:id="@+id/resultTextView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="로딩중"
        tools:text="☹"
        android:textColor="@color/white"
        android:textSize="15sp"
        tools:ignore="HardcodedText" />

    <TextView
        android:id="@+id/gradeLabelTextView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textColor="@color/white"
        android:textSize="12sp"
        android:visibility="gone"
        tools:text="매우 나쁨"
        tools:visibility="visible" />

</LinearLayout>
```

- 추가적으로 위젯의 형태에 대해서 xml 패키지 하위로 아래와 같이 정할 수 있음
```xml
<?xml version="1.0" encoding="utf-8"?>
<appwidget-provider xmlns:android="http://schemas.android.com/apk/res/android"
    android:initialLayout="@layout/widget_simple"
    android:minWidth="110dp"
    android:minHeight="50dp"
    android:resizeMode="none"
    android:updatePeriodMillis="3600000"
    android:widgetCategory="home_screen" />


```

### SimpleAirQualityWidgetProvider.kt
- 그리고 수명주기를 활용하고 채널을 생성해서 권한을 받아와 처리해야함, 이 경우 직접 해당 앱에 대한 권한을 All time으로 설정을 해야 위젯 상에서도 해당 내용이 나올 수 있음

- LifecycleService를 활용함

```kotlin
package techtown.org.airkorea.appwidget

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import techtown.org.airkorea.R
import techtown.org.airkorea.data.Repository
import techtown.org.airkorea.data.models.airquality.Grade

class SimpleAirQualityWidgetProvider : AppWidgetProvider() {

    // 앱 위젯에 내용을 갱신하기 위해서 오버라이딩한 함수
    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        // 서비스를 시작해줌
        ContextCompat.startForegroundService(
            context!!,
            Intent(context, UpdateWidgetService::class.java)
        )
    }

    class UpdateWidgetService : LifecycleService() {

        override fun onCreate() {
            super.onCreate()

            createChannelIfNeeded() // API 26 이상에서 채널을 만듬
            startForeground(
                NOTIFICATION_ID,
                createNotification(),
            )
        }

        // foreground 시작을 할 때 불리는 Command
        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

            // 위치를 먼저 가져옴
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // 권한이 없는 경우, 위젯을 업데이트 해 줌 그에 맞게
                val updateViews = RemoteViews(packageName, R.layout.widget_simple).apply {
                    setTextViewText(
                        R.id.resultTextView,
                        "권한 없음"
                    )
                    setViewVisibility(R.id.labelTextView, View.GONE)
                    setViewVisibility(R.id.gradeLabelTextView, View.GONE)
                }
                updateWidget(updateViews)
                stopSelf() // 권한 없으면 종료함

                return super.onStartCommand(intent, flags, startId)
            }

            LocationServices.getFusedLocationProviderClient(this).lastLocation
                .addOnSuccessListener { location ->
                    lifecycleScope.launch {
                        // try-catch로 위젯 갱신 실패시 상황도 처리함
                        try {
                            // 데이터 통신을 위해서 코루틴 시작, 정보를 가져와서 갱신을 함
                            val nearbyMonitoringStation = Repository.getNearbyMonitoringStation(
                                location.latitude,
                                location.longitude
                            )
                            val measuredValue =
                                Repository.getLatestAirQualityData(nearbyMonitoringStation!!.stationName!!)
                            // 정보를 바탕으로 위뎃을 업데이트 함
                            val updateViews =
                                RemoteViews(packageName, R.layout.widget_simple).apply {
                                    setViewVisibility(R.id.labelTextView, View.VISIBLE)
                                    setViewVisibility(R.id.gradeLabelTextView, View.VISIBLE)

                                    val currentGrade = (measuredValue?.khaiGrade ?: Grade.UNKNOWN)
                                    setTextViewText(R.id.resultTextView, currentGrade.emoji)
                                    setTextViewText(R.id.gradeLabelTextView, currentGrade.label)
                                }

                            // 업데이트 하고 끝냄
                            updateWidget(updateViews)
                        } catch (exception: Exception) {
                            exception.printStackTrace()
                        } finally {
                            stopSelf()
                        }

                    }
                }

            return super.onStartCommand(intent, flags, startId)
        }

        override fun onDestroy() {
            super.onDestroy()
            stopForeground(true)
        }

        // 채널을 만듬 서비스를 위해서
        private fun createChannelIfNeeded() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // 해당 버전 이상인 경우 채널을 만들어야함
                (getSystemService(NOTIFICATION_SERVICE) as? NotificationManager)
                    ?.createNotificationChannel(
                        NotificationChannel(
                            WIDGET_REFRESH_CHANNEL_ID,
                            "위젯 갱신 채널",
                            NotificationManager.IMPORTANCE_LOW
                        )
                    )
            }
        }

        // Notification 생성함
        private fun createNotification(): Notification =
            NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_baseline_refresh_24)
                .setChannelId(WIDGET_REFRESH_CHANNEL_ID)
                .build()

        private fun updateWidget(updateViews: RemoteViews) {
            val widgetProvider = ComponentName(this, SimpleAirQualityWidgetProvider::class.java)
            AppWidgetManager.getInstance(this).updateAppWidget(widgetProvider, updateViews)
        }
    }

    companion object {
        private const val WIDGET_REFRESH_CHANNEL_ID = "WIDGET_REFRESH_CHANNEL_ID"
        private const val NOTIFICATION_ID = 101
    }
}
```