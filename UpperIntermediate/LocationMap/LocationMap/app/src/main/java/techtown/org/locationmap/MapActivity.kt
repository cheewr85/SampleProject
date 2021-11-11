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