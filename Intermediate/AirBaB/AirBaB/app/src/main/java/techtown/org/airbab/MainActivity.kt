package techtown.org.airbab

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import com.naver.maps.map.widget.LocationButtonView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), OnMapReadyCallback, Overlay.OnClickListener { // 마커 클릭 리스너를 위해서 상속함

    // NaverMap 전역 선언
    private lateinit var naverMap: NaverMap
    // locationSource
    private lateinit var locationSource: FusedLocationSource
    // mapView를 가져옴
    private val mapView: MapView by lazy {
        findViewById(R.id.mapView)
    }

    // ViewPager2 가져옴
    private val viewPager: ViewPager2 by lazy {
        findViewById(R.id.houseViewPager)
    }
    private val viewPagerAdapter = HouseViewPagerAdapter(itemClicked = {
        // 아이템 클릭시 이벤트 처리 구현, 외부로 공유할 것임, 인텐트 활용
        val intent = Intent()
                .apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT,"[지금 이 가격에 예약하세요!!] ${it.title} ${it.price} 사진보기 : ${it.imgUrl}") // 텍스트 값으로 정보를 내보냄(원래는 Url로 보내긴 함)
                    type = "text/plain"
                }
        // chooser를 통해 text/plain으로 모두 받게됨, 공유를 해서 위의 정보를 보낼 수 있음
        startActivity(Intent.createChooser(intent, null))
    })

    // 메인에 include된 레이아웃이므로 가져올 수 있음
    private val recyclerView: RecyclerView by lazy {
        findViewById(R.id.recyclerView)
    }
    private val recyclerAdapter = HouseListAdapter()

    // 현재 위치 버튼이 가려지므로 직접 View를 만들어서 NaverMap에 연결해 조정함
    private val currentLocationButton: LocationButtonView by lazy {
        findViewById(R.id.currentLocationButton)
    }

    // 숙소의 개수대로 Text 업데이트 위해서 BottomSheetTitle을 가져옴
    private val bottomSheetTitleTextView: TextView by lazy {
        findViewById(R.id.bottomSheetTitleTextView)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // mapView와 onCreate를 연결시켜줌
        mapView.onCreate(savedInstanceState)

        // NaverMap 객체를 가져와서 다양한 처리를 할 수 있음
        // 아래처럼 콜백 형태가 아닌 람다형태로도 가능함
        // mapReady에 onMapReadyCallback의 구현체가 필요하므로 이를 Main에서 구현해서 구현체가 됐으므로 this를 사용함
        mapView.getMapAsync(this)

        // ViewPager어댑터 연결함
        viewPager.adapter = viewPagerAdapter

        // RecyclerView 어댑터와 레이아웃 매니저 연결
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // ViewPager와 marker를 연동함, 이를 연동하기 위해서 callback을 붙임
        // ViewPager 이동시 해당 마커의 위치로 이동함
        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {

            // 모든 구현체를 구현할 필요 없음, 새로운 페이지가 선택됐을 때 활용하는 메소드 사용
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // 선택된 viewPager 아이템 연결, 선택된 아이템 가져옴
                val selectedHouseModel = viewPagerAdapter.currentList[position]
                // 선택된 아이템에서 저장된 위경도를 바탕으로 선택한 곳으로 이동, 애니메이션도 추가함
                val cameraUpdate = CameraUpdate.scrollTo(LatLng(selectedHouseModel.lat, selectedHouseModel.lng))
                        .animate(CameraAnimation.Easing)
                naverMap.moveCamera(cameraUpdate)
            }
        })
    }

    // NaverMap 객체를 가져오기 위한 콜백을 구현함
    override fun onMapReady(map: NaverMap) {
        // naverMap을 가져와 여러가지 설정을 쓸 수 있음
        naverMap = map

        // Zoom의 범주를 정함
        naverMap.maxZoom = 18.0
        naverMap.minZoom = 10.0

        // 초기 위치를 설정할 수 있음, cameraUpdate 객체를 통해 설정, 위도 경도 넣어서 주소로 이동함
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.49795584729596, 127.0276022610788))
        naverMap.moveCamera(cameraUpdate)

        // 현위치를 얻어오기 위해서 버튼을 비활성화함, naverMap 활용해서, 권한이 있어야 현 위치를 가져올 수 있음
        // 현위치 버튼을 가져와서 조정하여 활성화시킴
        val uiSetting = naverMap.uiSettings
        uiSetting.isLocationButtonEnabled = false
        currentLocationButton.map = naverMap

        // 권한 요청시 단순하게 uses-permission이외에 사용자에게서 권한을 가져와야함, 그러기 위해서 팝업을 띄울 수 있으나 이번엔 구글 라이브러리 활용함
        // 로케이션 코드를 받아오고 처리함
        locationSource = FusedLocationSource(this@MainActivity, LOCATION_PERMISSION_REQUEST_CODE)
        naverMap.locationSource = locationSource

        // onCreate에서 지도를 다 그린 뒤에 Retrofit을 통해서 서버와 통신해서 마커를 찍기 위해서 이곳에서 Retrofit 초기화 및 연결
        getHouseListFromAPI()
    }

    private fun getHouseListFromAPI() {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://run.mocky.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build() // retrofit 객체 완성

        // API 호출
        retrofit.create(HouseService::class.java).also{
            // 만든 이후 함수 호출 후 object 구현해서 콜백 받음(데이터)
            it.getHouseList()
                    .enqueue(object: Callback<HouseDto> {
                        override fun onResponse(call: Call<HouseDto>, response: Response<HouseDto>) {
                            if(response.isSuccessful.not()) {
                                // 실패처리
                                return
                            }

                            response.body()?.let{ dto ->
                                // API에 있는 JSON 데이터가 정상적으로 받아서 내려옴
                                updateMarker(dto.items)
                                viewPagerAdapter.submitList(dto.items) // 데이터를 Viewpager에다가도 연결함
                                recyclerAdapter.submitList(dto.items) // 데이터 리사이클러뷰에다가 연결

                                bottomSheetTitleTextView.text = "${dto.items.size}개의 숙소" // 개수만큼 가져오게끔 갱신함
                            }
                        }

                        override fun onFailure(call: Call<HouseDto>, t: Throwable) {
                            // 실패 처리 구현
                        }

                    })
        }

    }

    private fun updateMarker(houses: List<HouseModel>) {
        // 코드 차원에서 줄이기 위해서 함수로 뺌
        houses.forEach { house ->
            // DTO 리스트 item이 리스트로 되어 있으므로 리스트를 하나씩 꺼내와서 it(house)으로 던져줌
            // 그리고 이를 마커로 찍어줌, 지도에 꽂을 마커를 설정함
            val marker = Marker()
            marker.position = LatLng(house.lat, house.lng) // 마커에 위치 정보를 설정함
            // 마커의 클릭 리스너 구현, 눌렀을 때 해당 마커 위치로 가게끔 설정함
            marker.onClickListener = this
            marker.map = naverMap // naverMap에 설정함
            // marker에 대한 특징을 설정할 수 있음, 다양하게 활용할 수 있음
            marker.tag = house.id
            marker.icon = MarkerIcons.BLACK
            marker.iconTintColor = Color.RED
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return
        }

        if(locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            // 구글 라이브러리에 권한 팝업을 사용함
            if(!locationSource.isActivated) {
                // 권한이 거부됨을 알려줌
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }

    }

    // 각각 액티비티 생명주기에 맞게 mapView를 연결해줌, 호출주기에 연결을 해줌
    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    companion object {
        // location 번호를 넘김
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    override fun onClick(overlay: Overlay): Boolean {
        // 마커 클릭시 이벤트 처리하는 리스너
        overlay.tag

        // houseModel의 id 값을 저장한 것을 이용해서 ViewPager 아이템 중 어디에 위치하는지 찾음
        val selectedModel = viewPagerAdapter.currentList.firstOrNull {
            // 제일 먼저 나오는 아이템 반환하고 없으면 널 반환함
            // 아이템을 마커와 비교함
            it.id == overlay.tag
        }

        selectedModel?.let{
            // 만약 널이 아니라면 위치를 찾음, 그리고 마커 클릭시 해당 아이템으로 ViewPager를 이동하게 함
            val position = viewPagerAdapter.currentList.indexOf(it)
            viewPager.currentItem = position
        }

        return true
    }
}