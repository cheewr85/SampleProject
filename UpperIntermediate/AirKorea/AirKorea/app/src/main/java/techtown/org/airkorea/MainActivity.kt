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