## 유튜브 앱
- 유튜브와 유사한 방식으로 동영상 리스트가 보여지고 있고 동영상을 누른다면 동영상이 ExoPlayer를 바탕으로 재생이 됨

- MotionLayout을 활용하여서 유튜브와 같이 화면 전환 애니메이션을 구현함

- Mocky를 활용하여 동영상에 대한 정보를 API로 만들고 Retrofit을 통해서 통신하여서 UI를 그림

## 메인화면
![one](/UpperIntermediate/NewTube/img/one.png)

- 가장 기본적인 홈 버튼의 bottom_nav 메뉴와 영상의 리스트를 보여주는 RecyclerView에 FrameLayout이 있음

- FrameLayout에서 fragment를 통해서 영상 클릭시 나타나는 영상과 그 하단의 영상 리스트들이 담겨 있음

- 기존에는 위의 사진과 같이 유튜브에서 영상이 실행중이면서 리스트를 볼 때와 같이 하단에 있음 하지만 영상 클릭시 FrameLayout에 채워지기 때문에 Fragment로 채워짐

- 이 부분에 대해서는 MotionLayout을 통해서 처리하였음

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.motion.widget.MotionLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:id="@+id/mainMotionLayout"
    android:layout_height="match_parent"
    app:layoutDescription="@xml/activity_main_scene"
    tools:context=".MainActivity">

    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/mainBottomNavigationView"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:menu="@menu/bottom_nav_menu" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/mainRecyclerView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <FrameLayout
        android:id="@+id/fragmentContainer"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.motion.widget.MotionLayout>
```

- 세부적인 애니메이션 처리와 설정에 대해서는 MotionLayout을 사용하였기 때문에 xml로 생성된 부분에서 직접 체크하면서 커스텀하였음

```xml
<?xml version="1.0" encoding="utf-8"?>
<MotionScene 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:motion="http://schemas.android.com/apk/res-auto">

    <Transition
        motion:constraintSetEnd="@+id/end"
        motion:constraintSetStart="@id/start"
        motion:duration="1000">
       <KeyFrameSet>
       </KeyFrameSet>
    </Transition>

    <ConstraintSet android:id="@+id/start">
        <Constraint
            android:id="@+id/mainBottomNavigationView"
            motion:layout_constraintEnd_toEndOf="parent"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            motion:layout_constraintBottom_toBottomOf="parent"
            motion:layout_constraintStart_toStartOf="parent" />
    </ConstraintSet>

    <ConstraintSet android:id="@+id/end">
        <Constraint
            android:id="@+id/mainBottomNavigationView"
            motion:layout_constraintEnd_toEndOf="parent"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:translationY="56dp"
            motion:layout_constraintBottom_toBottomOf="parent"
            motion:layout_constraintStart_toStartOf="parent" />
    </ConstraintSet>
</MotionScene>
```

- 각각 RecyclerView에 들어갈 item의 xml, bottomNavigation에서의 xml을 위의 예시에 맞게 작성함

### VideoDto.kt
- Retrofit을 통해서 API와 통신을 위해서 아래와 같이 Dto, Model, Service를 구분해서 활용함
```kotlin
package techtown.org.newtube.dto

import techtown.org.newtube.model.VideoModel

/*
VideoModel이라는 Array를 Dto로 받아서 사용하기 위한 클래스
 */
data class VideoDto (
    val videos: List<VideoModel>
    )
```
### VideoModel.kt
```kotlin
package techtown.org.newtube.model
/*
dummy video에 대한 HTTP Response에 대한 데이터를 사용하기 위한 데이터 클래스(모델 파일)
 */
data class VideoModel(
    val title: String,
    val sources: String,
    val subtitle: String,
    val thumb: String,
    val description: String
)
```
### VideoService.kt
```kotlin
package techtown.org.newtube.service

import retrofit2.Call
import retrofit2.http.GET
import techtown.org.newtube.dto.VideoDto

/*
Retrofit을 사용하기 위한 인터페이스
 */
interface VideoService {

    // mocky로 생성한 주소를 GET을 통해서 데이터를 받아옴
    @GET("/v3/3ec6eb0e-1489-4680-a30b-a5fd6c2a0b96")
    fun listVideos(): Call<VideoDto>
}
```

### VideoAdapter.kt
- 리사이클러뷰를 활용하기 때문에 해당 item을 보여주기 위해 어댑터를 생성함 
```kotlin
package techtown.org.newtube.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import techtown.org.newtube.R
import techtown.org.newtube.model.VideoModel

/*
Main, Fragment에 둘 다 활용할 어댑터
 */

class VideoAdapter(val callback: (String, String) -> Unit): ListAdapter<VideoModel, VideoAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val view: View): RecyclerView.ViewHolder(view) {

        fun bind(item: VideoModel) {
            // 아이템을 불러온 뒤 VideoModel에서의 데이터 연결함
            val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
            val subTitleTextView = view.findViewById<TextView>(R.id.subTitleTextView)
            val thumbnailImageView = view.findViewById<ImageView>(R.id.thumbnailImageView)

            titleTextView.text = item.title
            subTitleTextView.text = item.subtitle
            Glide.with(thumbnailImageView.context)
                .load(item.thumb)
                .into(thumbnailImageView)

            view.setOnClickListener {
                // Url과 title을 줌
                callback(item.sources, item.title)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // 만든 item 레이아웃 연결함
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_video,parent ,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 현재 position을 bind함
        return holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<VideoModel>() {
            // 고유한 값을 별도로 만들지 않았으므로 단순히 아이템 비교를 통해서 처리함
            override fun areItemsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
                return oldItem == newItem
            }

        }
    }


}
```

### MainActivity.kt
- FrameLayout에 fragment를 할당시키고, 어댑터와 리사이클러뷰를 연결하며 Retrofit을 통해서 서버와 통신해서 데이터를 가져옴
```kotlin
package techtown.org.newtube

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import techtown.org.newtube.adapter.VideoAdapter
import techtown.org.newtube.dto.VideoDto
import techtown.org.newtube.service.VideoService

class MainActivity : AppCompatActivity() {

    private lateinit var videoAdapter: VideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // FrameLayout에 Fragment attach를 함
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, PlayerFragment())
            .commit()

        // 어댑터 인스턴스 생성
        videoAdapter = VideoAdapter(callback = { url, title ->
            // playerFragment를 가져와서 해당 url, title을 넘기기 위해서 처리함
            supportFragmentManager.fragments.find {it is PlayerFragment } ?.let {
                (it as PlayerFragment).play(url, title)
            }
        })

        // 리사이클러뷰 가져와서 어댑터 연결함(메인 리사이클러뷰)
        findViewById<RecyclerView>(R.id.mainRecyclerView).apply {
            adapter = videoAdapter
            layoutManager = LinearLayoutManager(context)
        }

        getVideoList()
    }

    private fun getVideoList() {
        // 인터페이스를 구현해서 videodto를 가져오는 함수
        // retrofit 인스턴스 생성
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // 인스턴스를 인터페이스 구현체로 바꿈
        retrofit.create(VideoService::class.java).also {
            // 서버에서 Video 관련 리스트 불러와서 enqueue에 넣어서 VideoDto를 활용함
            it.listVideos()
                .enqueue(object: Callback<VideoDto>{
                    override fun onResponse(call: Call<VideoDto>, response: Response<VideoDto>) {
                        if (response.isSuccessful.not()) {
                            Log.d("MainActivity","response fail")
                            return
                        }

                        response.body()?.let { videoDto ->
                            // videos를 가져와서 연결해줌
                            videoAdapter.submitList(videoDto.videos)
                        }

                    }

                    override fun onFailure(call: Call<VideoDto>, t: Throwable) {
                        // 통신 실패시 예외처리
                    }

                })
        }

    }
}
```

## 플레이어 화면
![one](/UpperIntermediate/NewTube/img/two.png)

- MotionLayout의 경우 터치에 대한 처리를 다 가져가서 리사이클러뷰 스크롤이 안되어 이를 직접 커스텀해서 사용하기 위해서 커스텀 MotionLayout을 만들어서 처리함

- 그리고 이 화면의 경우 Fragment이고 실제 유튜브에서 하단의 영상 목록과 같이 나타나 있음, ExoPlayer를 사용함

- 그래서 영상이 클릭시 혹은 직접 터치를 통해서 애니메이션을 활용할 수 있음

```xml
<?xml version="1.0" encoding="utf-8"?>
<!-- 기존의 모션 레이아웃상 스크롤 이슈로 인해서 커스텀해서 만든걸 사용함-->
<techtown.org.newtube.CustomMotionLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/playerMotionLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:layoutDescription="@xml/fragment_player_scene">

    <androidx.constraintlayout.widget.ConstraintLayout
        android:id="@+id/mainContainerLayout"
        android:layout_width="0dp"
        android:layout_height="250dp"
        android:background="#aaaaaa"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <com.google.android.exoplayer2.ui.PlayerView
        android:id="@+id/playerView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:resize_mode="fill"
        app:layout_constraintBottom_toBottomOf="@id/mainContainerLayout"
        app:layout_constraintStart_toStartOf="@id/mainContainerLayout"
        app:layout_constraintTop_toTopOf="@id/mainContainerLayout" />

    <ImageView
        android:id="@+id/bottomPlayerControlButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginEnd="24dp"
        tools:src="@drawable/ic_baseline_play_arrow_24"
        app:layout_constraintBottom_toBottomOf="@id/mainContainerLayout"
        app:layout_constraintEnd_toEndOf="@id/mainContainerLayout"
        app:layout_constraintTop_toTopOf="@id/mainContainerLayout" />

    <TextView
        android:id="@+id/bottomTitleTextView"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="8dp"
        android:layout_marginEnd="12dp"
        android:ellipsize="end"
        android:maxLines="1"
        android:singleLine="true"
        tools:text="제목입니다."
        android:textColor="@color/black"
        app:layout_constraintBottom_toBottomOf="@id/bottomPlayerControlButton"
        app:layout_constraintEnd_toStartOf="@id/bottomPlayerControlButton"
        app:layout_constraintStart_toEndOf="@id/playerView"
        app:layout_constraintTop_toTopOf="@id/bottomPlayerControlButton" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/fragmentRecyclerView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:background="@color/white"
        android:nestedScrollingEnabled="false"
        android:padding="16dp"
        android:clipToPadding="false"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/mainContainerLayout" />


</techtown.org.newtube.CustomMotionLayout>
```

```xml
<?xml version="1.0" encoding="utf-8"?>
<MotionScene 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:motion="http://schemas.android.com/apk/res-auto">

    <Transition
        motion:constraintSetEnd="@+id/end"
        motion:constraintSetStart="@id/start"
        motion:duration="300">
       <KeyFrameSet>
           <KeyAttribute
               motion:motionTarget="@+id/bottomTitleTextView"
               motion:framePosition="10"
               android:alpha="0" />
           <KeyAttribute
               motion:motionTarget="@+id/bottomPlayerControlButton"
               motion:framePosition="10"
               android:alpha="0" />
           <KeyPosition
               motion:motionTarget="@+id/playerView"
               motion:framePosition="10"
               motion:curveFit="linear"
               motion:keyPositionType="deltaRelative"
               motion:percentX="1"
               motion:percentWidth="1"/>
       </KeyFrameSet>
        <OnSwipe
            motion:touchAnchorId="@+id/mainContainerLayout"
            motion:touchAnchorSide="bottom" />
    </Transition>

    <ConstraintSet android:id="@+id/start">
        <Constraint
            android:id="@+id/fragmentRecyclerView"
            motion:layout_constraintEnd_toEndOf="parent"
            android:layout_width="0dp"
            android:layout_height="0.1dp"
            android:layout_marginBottom="66dp"
            motion:layout_constraintVertical_bias="1.0"
            motion:layout_constraintBottom_toBottomOf="parent"
            motion:layout_constraintTop_toBottomOf="@id/mainContainerLayout"
            motion:layout_constraintStart_toStartOf="parent" />

        <Constraint
            android:id="@+id/mainContainerLayout"
            motion:layout_constraintEnd_toEndOf="parent"
            android:layout_width="0dp"
            android:layout_height="56dp"
            motion:layout_constraintVertical_bias="1.0"
            motion:layout_constraintBottom_toBottomOf="parent"
            android:layout_marginBottom="66dp"
            motion:layout_constraintTop_toTopOf="parent"
            motion:layout_constraintStart_toStartOf="parent" />

        <Constraint
            android:id="@+id/playerView"
            android:layout_width="0dp"
            android:layout_height="0dp"
            motion:layout_constraintDimensionRatio="H,1:2.5"
            motion:layout_constraintBottom_toBottomOf="@id/mainContainerLayout"
            motion:layout_constraintTop_toTopOf="@id/mainContainerLayout"
            motion:layout_constraintStart_toStartOf="@id/mainContainerLayout" />
    </ConstraintSet>

    <ConstraintSet android:id="@+id/end">
        <Constraint
            android:id="@+id/playerView"
            android:layout_width="0dp"
            android:layout_height="0dp"
            motion:layout_constraintEnd_toEndOf="@id/mainContainerLayout"
            motion:layout_constraintBottom_toBottomOf="@id/mainContainerLayout"
            motion:layout_constraintTop_toTopOf="@id/mainContainerLayout"
            motion:layout_constraintStart_toStartOf="@id/mainContainerLayout" />

        <Constraint
            android:id="@+id/mainContainerLayout"
            motion:layout_constraintEnd_toEndOf="parent"
            android:layout_width="0dp"
            android:layout_height="250dp"
            motion:layout_constraintTop_toTopOf="parent"
            motion:layout_constraintStart_toStartOf="parent" />

        <Constraint
            android:id="@+id/fragmentRecyclerView"
            motion:layout_constraintEnd_toEndOf="parent"
            android:layout_width="0dp"
            android:layout_height="0dp"
            motion:layout_constraintBottom_toBottomOf="parent"
            motion:layout_constraintTop_toBottomOf="@id/mainContainerLayout"
            motion:layout_constraintStart_toStartOf="parent" />
        <Constraint
            android:id="@+id/bottomPlayerControlButton"
            motion:layout_constraintEnd_toEndOf="@id/mainContainerLayout"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:alpha="0"
            motion:layout_constraintBottom_toBottomOf="@id/mainContainerLayout"
            android:layout_marginEnd="24dp"
            motion:layout_constraintTop_toTopOf="@id/mainContainerLayout" />
    </ConstraintSet>
</MotionScene>
```

### CustomMotionLayout.kt
- 위에서 언급했듯이 스크롤 이슈를 해결하기 위해서 MotionLayout을 직접 상속받아서 만들어서 처리하고 활용함
```kotlin
package techtown.org.newtube

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout

/*
Custom을 만듬으로써 리사이클러뷰에서 스크롤 처리, 그 외에 이벤트 처리를 원활하게 하기 위해서 만든 커스텀 레이아웃
fragment_player에서 사용을 함
 */

class CustomMotionLayout(context: Context, attributeSet: AttributeSet ?= null): MotionLayout(context, attributeSet){

    // 터치가 눌렸을 때 true가 됨, 다른 곳을 눌렀을 때 false로 줌
    private var motionTouchStarted = false
    // 스크롤, 터치 처리를 위해서 해당 View를 가져옴
    private val mainContainerView by lazy {
        findViewById<View>(R.id.mainContainerLayout)
    }
    private val hitRect = Rect()

    init {
        // transition이 끝났을 때 motionTouchStarted를 false 처리하기 위해서 리스너 처리함
        setTransitionListener(object: TransitionListener{
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) { }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) { }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                motionTouchStarted = false
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) { }

        })
    }

    // 제스처 이벤트, 터치 이벤트 처리함, 조건에 맞게 재정의 하기 위해서
    // 터치 이벤트 눌렀을 때만을 처리를 함, 땠을 때는 굳이 고려하지 않아도 됨
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                motionTouchStarted = false
                return super.onTouchEvent(event) // 기존의 값을 리턴하게 함
            }
        }

        // motionTouchStarted를 확인하여서 처리를 함
        if(!motionTouchStarted) {
            // Rect에다가 값을 저장해서 반환하게함
            mainContainerView.getHitRect(hitRect)
            // 이벤트 x,y 좌표가 hitRect안에 일어난 값인지 확인함, 만약 맞다면 true가 됨
            motionTouchStarted = hitRect.contains(event.x.toInt(), event.y.toInt())
        }
        // 터치 이벤트에 대해서 재정의함
        return super.onTouchEvent(event) && motionTouchStarted
    }

    // 제스처 이벤트 처리, 스크롤 하는 이벤트를 재정의 할 것임
    private val gestureListener by lazy {
        object: GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                // 메인에서 일어난건지 확인
                mainContainerView.getHitRect(hitRect)
                // hitRect를 통해서 범주 안에서 일어났는지 체크
                return hitRect.contains(e1.x.toInt(),e1.y.toInt())
            }
        }
    }
    private val gestureDetector by lazy {
        GestureDetector(context, gestureListener)
    }
    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        // 터치 이벤트를 위에서 체크한 제스처 이벤트 처리를 가지고 적용을 함
        return gestureDetector.onTouchEvent(event)
    }

}
```

### PlayerFragment.kt
- 실질적인 영상 재생과 유튜브 영상 플레이어 화면 전환을 처리할 수 있도록 설정을 해 둠
```kotlin
package techtown.org.newtube

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import techtown.org.newtube.adapter.VideoAdapter
import techtown.org.newtube.databinding.FragmentPlayerBinding
import techtown.org.newtube.dto.VideoDto
import techtown.org.newtube.service.VideoService
import kotlin.math.abs


/*
PlayerFragment
Fragment와 BottomNavigationView는 같이 움직이게 처리함
 */
class PlayerFragment: Fragment(R.layout.fragment_player) {
    // 뷰바인딩
    private var binding : FragmentPlayerBinding? = null

    private lateinit var videoAdapter: VideoAdapter

    private var player: SimpleExoPlayer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 바인딩을 해줌
        val fragmentPlayerBinding = FragmentPlayerBinding.bind(view)
        binding = fragmentPlayerBinding

        initMotionLayoutEvent(fragmentPlayerBinding)
        initRecyclerView(fragmentPlayerBinding)
        initPlayer(fragmentPlayerBinding)
        initControlButton(fragmentPlayerBinding)

        getVideoList()
    }

    private fun initMotionLayoutEvent(fragmentPlayerBinding: FragmentPlayerBinding) {
        // Transition에 관한 이벤트 처리함
        fragmentPlayerBinding.playerMotionLayout.setTransitionListener(object: MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {

            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
                // change가 됐을 때 메인의 MotionLayout에다가 어디까지 이동이 됐는지 값을 줌
                // 이동된 값을 받음으로써 Drag 할 때 Fragment와 BottomNavigation이 같이 움직이게 됨
                binding?.let {
                    // 어느 액티비티인지 모르기 때문에 검사처리를 함, 그리고 View를 가져옴, 그리고 이동상태를 넘겨줌
                    (activity as MainActivity).also { mainActivity ->
                        mainActivity.findViewById<MotionLayout>(R.id.mainMotionLayout).progress = abs(progress)
                    }
                }
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {

            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {

            }

        })
    }

    private fun initRecyclerView(fragmentPlayerBinding: FragmentPlayerBinding) {

        videoAdapter = VideoAdapter(callback = { url, title ->
            play(url, title)
        })

        fragmentPlayerBinding.fragmentRecyclerView.apply {
            // 리사이클러뷰 초기화하고 붙임
            adapter = videoAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun initPlayer(fragmentPlayerBinding: FragmentPlayerBinding) {
        // ExoPlayer 초기화 작업
        context?.let {
            player = SimpleExoPlayer.Builder(it).build()
        }

        fragmentPlayerBinding.playerView.player = player

        binding?.let{
            player?.addListener(object: Player.EventListener {
                // playing 여부가 바뀔 때마다 들어오는 함수임
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    // play 여부에 따라 fragment내에 View를 설정함
                    if (isPlaying) {
                        it.bottomPlayerControlButton.setImageResource(R.drawable.ic_baseline_pause_24)
                    } else {
                        it.bottomPlayerControlButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    }
                }
            })
        }

    }

    private fun initControlButton(fragmentPlayerBinding: FragmentPlayerBinding) {
        fragmentPlayerBinding.bottomPlayerControlButton.setOnClickListener {
            // player에서 이 버튼을 눌렀을 때 player가 play 되고 있는지 여부를 확인함
            val player = this.player ?: return@setOnClickListener

            // 이미지를 바꾸는 것은 위에서 onIsPlayingChanged에서 알아서 처리됨
            if(player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
        }
    }

    private fun getVideoList() {
        // 인터페이스를 구현해서 videodto를 가져오는 함수
        // retrofit 인스턴스 생성
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // 인스턴스를 인터페이스 구현체로 바꿈
        retrofit.create(VideoService::class.java).also {
            // 서버에서 Video 관련 리스트 불러와서 enqueue에 넣어서 VideoDto를 활용함
            it.listVideos()
                .enqueue(object: Callback<VideoDto> {
                    override fun onResponse(call: Call<VideoDto>, response: Response<VideoDto>) {
                        if (response.isSuccessful.not()) {
                            Log.d("MainActivity","response fail")
                            return
                        }

                        response.body()?.let { videoDto ->
                            // videos를 가져와서 연결해줌
                            videoAdapter.submitList(videoDto.videos)
                        }

                    }

                    override fun onFailure(call: Call<VideoDto>, t: Throwable) {
                        // 통신 실패시 예외처리
                    }

                })
        }

    }

    fun play(url:String, title:String) {

        context?.let {
            // 데이터 소스에서 가져오고 미디어 소스로 만들고 플레이어로 전달해주기 위한 초기화 작업
            val dataSourceFactory = DefaultDataSourceFactory(it)
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(url))) // 받아온 url에 대해서 Uri로 변환해서 MediaSource로 만듬
            player?.setMediaSource(mediaSource)
            // 정상적으로 실행하기 위해서 아래와 같이 처리함
            player?.prepare()
            player?.play()
        }

        binding?.let {
            it.playerMotionLayout.transitionToEnd()
            it.bottomTitleTextView.text = title
        }
    }

    // 프래그먼트 생명주기에 맞게 player를 멈추고 정지하는 것을 설정함
    override fun onStop() {
        super.onStop()

        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
        player?.release()
    }

}
```