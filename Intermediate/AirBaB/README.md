## 에어비앤비 앱
- Naver Map API를 활용하여서 NaverMap을 띄우고 거기에 포함된 다양한 기능을 사용함

- Mock API를 통해서 서버 통신을 위한 JSON 데이터를 받아와서 활용함, 서버통신은 Retrofit을 사용함

- BottomSheetView를 활용해서 인터렉션하게 받아온 데이터를 표시할 수 있게함

- ViewPager2를 활용해서 현재 보고 있는 데이터를 표시할 수 있음

- 앱 외부로 공유할 수 있는 기능을 추가함

## 메인화면
- NaverMap을 기반으로 Mock API에 미리 정해둔 JSON 데이터를 가지고 URL을 생성하고 해당 데이터에 대해서 NaverMap에 Marker 형태로 뜨게 하면서 ViewPager2와 BottomSheetView에도 연동을 함

- 줌 기능과 현 위치를 찍는 기능에 대해서도 코드 내부로 설정을 함

![one](/Intermediate/AirBaB/img/one.png)

- 메인화면에서는 NaverMapView와 ViewPager2, 그리고 BottomSheet으로 만든 레이아웃을 include를 통해서 추가를 함
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <com.naver.maps.map.MapView
        android:id="@+id/mapView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_marginBottom="80dp" />

    <androidx.viewpager2.widget.ViewPager2
        android:id="@+id/houseViewPager"
        android:layout_width="match_parent"
        android:layout_height="100dp"
        android:layout_gravity="bottom"
        android:layout_marginBottom="120dp"
        android:orientation="horizontal" />

    <com.naver.maps.map.widget.LocationButtonView
        android:id="@+id/currentLocationButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="top|start"
        android:layout_margin="12dp"/>

    <include layout="@layout/bottom_sheet" />


</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

- 추가한 BottomSheet의 경우, layout_behavior를 설정하고 그런 다음 서버와 통신하는 데이터에 대해서는 RecyclerView를 활용함 

![one](/Intermediate/AirBaB/img/two.png)

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@drawable/top_radius_white_background"
    app:behavior_peekHeight="100dp"
    app:layout_behavior="com.google.android.material.bottomsheet.BottomSheetBehavior">

    <View
        android:layout_width="30dp"
        android:layout_height="3dp"
        android:layout_marginTop="12dp"
        android:background="#cccccc"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/bottomSheetTitleTextView"
        android:layout_width="0dp"
        android:layout_height="100dp"
        android:gravity="center"
        android:text="여러개의 숙소"
        android:textColor="@color/black"
        android:textSize="15sp"
        android:textStyle="bold"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <View
        android:id="@+id/lineView"
        android:layout_width="0dp"
        android:layout_height="1dp"
        android:background="#cccccc"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/bottomSheetTitleTextView" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/lineView" />


</androidx.constraintlayout.widget.ConstraintLayout>
```

- 추가적으로 맨 처음 그림에서 봤듯이 ViewPager2에 들어간 item, 그리고 bottomSheet에 들어갈 item 레이아웃을 아래와 같이 만들어서 활용했음

- ViewPager2에 사용할 itemView
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.cardview.widget.CardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_marginStart="30dp"
    android:layout_marginEnd="30dp"
    android:background="@color/white"
    app:cardCornerRadius="16dp"
    tools:layout_height="100dp">

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <ImageView
            android:id="@+id/thumbnailImageView"
            android:layout_width="100dp"
            android:layout_height="100dp"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent" />

        <TextView
            android:id="@+id/titleTextView"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_marginStart="12dp"
            android:layout_marginTop="12dp"
            android:layout_marginEnd="12dp"
            android:maxLines="2"
            android:textColor="@color/black"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toEndOf="@id/thumbnailImageView"
            app:layout_constraintTop_toTopOf="parent"
            tools:text="강남역" />

        <TextView
            android:id="@+id/priceTextView"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_marginStart="12dp"
            android:layout_marginEnd="12dp"
            android:maxLines="1"
            android:textColor="@color/black"
            android:textStyle="bold"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toEndOf="@id/thumbnailImageView"
            app:layout_constraintTop_toBottomOf="@id/titleTextView"
            tools:text="23,000원" />

    </androidx.constraintlayout.widget.ConstraintLayout>

</androidx.cardview.widget.CardView>
```

- BottomSheet에서 RecyclerView에 사용할 itemView
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

    <ImageView
        android:id="@+id/thumbnailImageView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:layout_margin="24dp"
        app:layout_constraintDimensionRatio="3:2"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/titleTextView"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="24dp"
        android:layout_marginTop="12dp"
        android:layout_marginEnd="24dp"
        android:textColor="@color/black"
        android:textSize="20sp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/thumbnailImageView"
        tools:text="강남역!" />

    <TextView
        android:id="@+id/priceTextView"
        app:layout_constraintTop_toBottomOf="@id/titleTextView"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        android:layout_marginTop="12dp"
        android:layout_marginStart="24dp"
        android:layout_marginEnd="24dp"
        android:textSize="20sp"
        android:textColor="@color/black"
        android:textStyle="bold"
        tools:text="23,000원"
        app:layout_constraintBottom_toBottomOf="parent"
        android:layout_marginBottom="24dp"
        android:layout_width="0dp"
        android:layout_height="wrap_content" />

</androidx.constraintlayout.widget.ConstraintLayout>
```


### HouseModel.kt
- JSON 형태로 담은 데이터의 내용을 정의함

```kotlin
package techtown.org.airbab
/*
JSON으로 저장한 내용을 받아오기 위한 데이터 클래스
 */

data class HouseModel (
    val id: Int,
    val title: String,
    val price: String,
    val imgUrl: String,
    val lat: Double,
    val lng: Double
)
```

### HouseDto.kt
- 어댑터 활용시 그리고 통신시 리스트 형태로 받아오게끔 JSON을 만들었기 때문에 만든 클래스

```kotlin
package techtown.org.airbab
/*
리스트형태로 JSON을 저장했으므로 List형태로 받기 위한 데이터 클래스
 */

data class HouseDto (
    val items: List<HouseModel>
)
```

### HouseService.kt
- 우선 메인 클래스 전 서버 통신에 대한 클래스 먼저 정의했음

- 이 API 그리고 baseUrl등은 Mock을 활용하여 JSON 데이터를 담아서 만든 주소임

```kotlin
package techtown.org.airbab

import retrofit2.Call
import retrofit2.http.GET

interface HouseService {
    @GET("/v3/63233eee-52e1-41b4-aa7f-98b47809ca8f") // GET할 주소 입력
    fun getHouseList(): Call<HouseDto> // JSON에 items의 리스트에 데이터를 담았으므로 Callback으로 Dto로 List를 받고 거기에 있는 model을 통해서 데이터 접근
}
```

### HouseViewPagerAdapter.kt
- ViewPager2 어댑터 클래스를 만듬, ViewPager2 어댑터는 리사이클러뷰 어댑터와 유사함

- ListAdapter를 활용해서 씀, ViewPager2에 처리할 데이터와 내용을 처리함

```kotlin
package techtown.org.airbab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

/*
ViewPager2 어댑터 클래스 리사이클러뷰 어댑터와 유사함, ListAdapter를 사용해서 씀
HouseModel을 공유하기 위해서 클릭 리스너를 달아둠
 */

class HouseViewPagerAdapter(val itemClicked: (HouseModel) -> Unit) : ListAdapter<HouseModel, HouseViewPagerAdapter.ItemViewHolder>(differ) {

    inner class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(houseModel: HouseModel) {
            // inflater를 통해서 가져온 itemView에 뷰들을 찾아서 정의함
            val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
            val priceTextView = view.findViewById<TextView>(R.id.priceTextView)
            val thumbnailImageView = view.findViewById<ImageView>(R.id.thumbnailImageView)

            // houseModel에 있는 내용을 각각 연결함
            titleTextView.text = houseModel.title
            priceTextView.text = houseModel.price

            view.setOnClickListener {
                // 클릭 처리를 위해서 Model을 그대로 넘김
                itemClicked(houseModel)
            }

            Glide
                    .with(thumbnailImageView.context)
                    .load(houseModel.imgUrl)
                    .into(thumbnailImageView)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // View를 생성하기 위해서 Inflater를 설정함 ItemViewHolder로 감싸서 view가 내부 클래스로 들어감
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(inflater.inflate(R.layout.item_house_detail_for_viewpager, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(currentList[position]) // position에 있는 것을 bind 해줌
    }

    companion object {
        val differ = object: DiffUtil.ItemCallback<HouseModel>() {
            override fun areItemsTheSame(oldItem: HouseModel, newItem: HouseModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: HouseModel, newItem: HouseModel): Boolean {
                return oldItem == newItem
            }

        }
    }
}
```

### HouseListAdapter.kt
- BottomSheetDialog에 있는 리사이클러뷰에서 활용할 어댑터

```kotlin
package techtown.org.airbab

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

/*
BottomSheetDialog를 위한 리사이클러뷰 어댑터, ListAdapter를 사용해서 씀
 */

class HouseListAdapter : ListAdapter<HouseModel, HouseListAdapter.ItemViewHolder>(differ) {

    inner class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(houseModel: HouseModel) {
            // inflater를 통해서 가져온 itemView에 뷰들을 찾아서 정의함
            val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
            val priceTextView = view.findViewById<TextView>(R.id.priceTextView)
            val thumbnailImageView = view.findViewById<ImageView>(R.id.thumbnailImageView)

            // houseModel에 있는 내용을 각각 연결함
            titleTextView.text = houseModel.title
            priceTextView.text = houseModel.price

            Glide
                    .with(thumbnailImageView.context)
                    .load(houseModel.imgUrl)
                    .transform(CenterCrop(), RoundedCorners(dpToPx(thumbnailImageView.context, 12))) // 이미지 설정 변경함
                    .into(thumbnailImageView)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // View를 생성하기 위해서 Inflater를 설정함 ItemViewHolder로 감싸서 view가 내부 클래스로 들어감
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(inflater.inflate(R.layout.item_house, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(currentList[position]) // position에 있는 것을 bind 해줌
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        // 핸드폰마다 해상도가 달라서 글라이드에선 dp가 아닌 픽셀로 들어가기 때문에, 이를 변환해줘야함 dp를 그에 맞는 픽셀로
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics).toInt()
    }

    companion object {
        val differ = object: DiffUtil.ItemCallback<HouseModel>() {
            override fun areItemsTheSame(oldItem: HouseModel, newItem: HouseModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: HouseModel, newItem: HouseModel): Boolean {
                return oldItem == newItem
            }

        }
    }
}
```

### MainActivity.kt
- NaverMap을 활용했기 때문에 해당 기능을 구현하고 메소드를 가져와서 사용함

- Map의 기본적인 기능과 줌 설정 그리고 위치 설정, 현위치 가져오는 것등 다양한 처리를 함, NaverMapAPI에 있는 다양한 기능 활용 가능함

- 그리고 앞서 정의한 어댑터를 활용하는 것과 Mock으로 만든 API를 기반으로 데이터를 받아와서 적용함

- 그리고 마커에 있는 데이터와 ViewPager2에 있는 데이터를 연동해서 클릭시 해당 위치로 이동하게끔 처리를 함

- 그리고 mapView에 대해서 액티비티 생명주기에 맞게 연결해주도록 똑같이 생명주기 메소드를 오버라이딩하여서 mapView에 대해서 설정을 함

```kotlin
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
```