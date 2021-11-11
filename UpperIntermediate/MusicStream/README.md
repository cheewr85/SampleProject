## 음악 스트리밍 앱
- 음악 스트리밍 앱과 같이 플레이리스트와 재생화면이 있음

- mocky를 통해 만든 API를 Retrofit을 통해 받아서 데이터를 받아옴

- ExoPlayer를 활용하여 미디어를 재생함

## 메인화면
- 프래그먼트를 활용하였기 때문에 FrameLayout으로만 구성하여 활용함
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <FrameLayout
        android:id="@+id/fragmentContainer"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```
- MainActivity.kt
```kotlin
package techtown.org.musicstream

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // FrameLayout에 프래그먼트를 보여줌
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, PlayerFragment().newInstance())
                .commit()
    }
}
```

## 재생화면 & 플레이리스트 화면
![one](/UpperIntermediate/MusicStream/img/one.png)
![one](/UpperIntermediate/MusicStream/img/two.png)

- xml 상에서 Group의 기능을 활용하여서 한 프래그먼트 안에서 visible 속성을 활용하여 playlist 버튼을 누름에 따라 재생화면과 플레이리스트 화면을 교차하게 나타날 수 있게끔 구현함

- 그렇기 때문에 같은 xml 상에서 존재할 수 있고 이 처리를 Group으로 묶어서 한 것임

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <androidx.constraintlayout.widget.Group
        android:id="@+id/playerViewGroup"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:visibility="gone"
        app:constraint_referenced_ids="trackTextView, artistTextView, coverImageCardView, bottomBackgroundView, playerSeekBar, playTimeTextView, totalTimeTextView"
        tools:visibility="visible" />

    <androidx.constraintlayout.widget.Group
        android:id="@+id/playListViewGroup"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:constraint_referenced_ids="titleTextView, playListRecyclerView, playListSeekBar" />

    <View
        android:id="@+id/topBackgroundView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:background="@color/background"
        app:layout_constraintBottom_toTopOf="@id/bottomBackgroundView"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_weight="3" />

    <View
        android:id="@+id/bottomBackgroundView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:background="@color/white_background"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/topBackgroundView"
        app:layout_constraintVertical_weight="2" />

    <TextView
        android:id="@+id/trackTextView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="30dp"
        android:textColor="@color/white"
        android:textSize="20sp"
        android:textStyle="bold"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        tools:text="애국가" />

    <TextView
        android:id="@+id/titleTextView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="30dp"
        android:text="재생목록"
        android:textColor="@color/white"
        android:textSize="20sp"
        android:textStyle="bold"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/artistTextView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="3dp"
        android:textColor="@color/gray_aa"
        android:textSize="15sp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/trackTextView"
        tools:text="대한민국" />

    <androidx.cardview.widget.CardView
        android:id="@+id/coverImageCardView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:layout_marginStart="36dp"
        android:layout_marginEnd="36dp"
        android:translationY="50dp"
        app:cardCornerRadius="5dp"
        app:cardElevation="10dp"
        app:layout_constraintBottom_toBottomOf="@id/topBackgroundView"
        app:layout_constraintDimensionRatio="H,1:1"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent">

        <ImageView
            android:id="@+id/coverImageView"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            tools:background="@color/purple_200" />

    </androidx.cardview.widget.CardView>

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/playListRecyclerView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:layout_marginTop="16dp"
        app:layout_constraintBottom_toTopOf="@id/playerView"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/titleTextView" />

    <com.google.android.exoplayer2.ui.PlayerView
        android:id="@+id/playerView"
        android:layout_width="0dp"
        android:layout_height="100dp"
        android:alpha="0"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:use_controller="false" />

    <SeekBar
        android:id="@+id/playerSeekBar"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="50dp"
        android:layout_marginEnd="50dp"
        android:layout_marginBottom="30dp"
        android:maxHeight="4dp"
        android:minHeight="4dp"
        android:paddingStart="0dp"
        android:paddingEnd="0dp"
        android:progressDrawable="@drawable/player_seek_background"
        android:thumb="@drawable/player_seek_thumb"
        app:layout_constraintBottom_toTopOf="@id/playerView"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        tools:progress="40" />

    <TextView
        android:id="@+id/playTimeTextView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="4dp"
        android:textColor="@color/purple_200"
        android:textStyle="bold"
        app:layout_constraintStart_toStartOf="@id/playerSeekBar"
        app:layout_constraintTop_toBottomOf="@id/playerSeekBar"
        tools:text="0:00" />

    <TextView
        android:id="@+id/totalTimeTextView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="4dp"
        android:textColor="@color/gray_97"
        android:textStyle="bold"
        app:layout_constraintEnd_toEndOf="@id/playerSeekBar"
        app:layout_constraintTop_toBottomOf="@id/playerSeekBar"
        tools:text="0:00" />

    <SeekBar
        android:id="@+id/playListSeekBar"
        android:layout_width="0dp"
        android:layout_height="2dp"
        android:clickable="false"
        android:paddingStart="0dp"
        android:paddingEnd="0dp"
        android:progressTint="@color/purple_200"
        android:thumbTint="@color/purple_200"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="@id/playerView"
        tools:progress="40" />

    <ImageView
        android:id="@+id/playControllImageView"
        android:layout_width="50dp"
        android:layout_height="50dp"
        android:src="@drawable/ic_baseline_play_arrow_24"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="@id/playerView"
        app:tint="@color/black" />

    <ImageView
        android:id="@+id/skipNextImageView"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:src="@drawable/ic_baseline_skip_next_24"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.2"
        app:layout_constraintStart_toEndOf="@id/playControllImageView"
        app:layout_constraintTop_toTopOf="@id/playerView"
        app:tint="@color/black" />

    <ImageView
        android:id="@+id/skipPrevImageView"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:src="@drawable/ic_baseline_skip_previous_24"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toStartOf="@id/playControllImageView"
        app:layout_constraintHorizontal_bias="0.8"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="@id/playerView"
        app:tint="@color/black" />

    <ImageView
        android:id="@+id/playlistImageView"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:layout_marginStart="24dp"
        android:src="@drawable/ic_baseline_playlist_play_24"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="@id/playerView"
        app:tint="@color/black" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

### service 패키지
- 서버에 있는 데이터를 Retrofit을 통해서 받아올 것인데 이 데이터를 처리하기 위해서 Entity, Dto, Service를 구현함

### MusicEntity.kt
- 서버에 JSON 형태로 저장되어 있는 데이터에 대해서 대응되는 값을 가져옴
```kotlin
package techtown.org.musicstream.service

import com.google.gson.annotations.SerializedName

/*
실제 서버에서 받아오는 데이터 클래스
 */
data class MusicEntity (
    @SerializedName("track") val track: String,
    @SerializedName("streamUrl") val streamUrl: String,
    @SerializedName("artist") val artist: String,
    @SerializedName("coverUrl") val coverUrl: String
)
```

### MusicDto.kt
- 서버에서 받아온 데이터에 대해서 모델 자체를 List로 가져와서 활용하기 위한 리스트 모델
```language
package techtown.org.musicstream.service
/*
리스트 형으로 데이터를 담기 위한 DTO 모델, 서버로 내려온 데이터 활용
 */
data class MusicDto (
    val musics: List<MusicEntity> // 서버 모델 그 자체를 가져옴, 뷰에서 사용하는 모델과 다름
)
```

### MusicService.kt
- Retrofit을 사용하는데 정의한 인터페이스
```kotlin
package techtown.org.musicstream.service

import retrofit2.Call
import retrofit2.http.GET

interface MusicService {

    @GET("/v3/a6949f13-8ca2-4234-b4ae-40bd1d704584")
    fun listMusics() : Call<MusicDto>
}
```

### Model, Mapper 클래스
- 리사이클러뷰에서 활용하기 위한 데이터 클래스와 위에서 데이터를 받아오는 것은 처리가 완료됐으나 이 값을 재생화면, 플레이리스트에 각각 활용할 것인데 이를 하나의 모델로 다 넣어두면 복잡하기 때문에 PlayerModel, MusicModel을 별도로 두고 서버에서 받아온 데이터에 대해서 처리를 하기 위한 Mapper 클래스를 만들어서 씀

### MusicModel.kt
```kotlin
package techtown.org.musicstream
/*
리사이클러뷰에서 쓸 데이터 클래스 모델(서버에 있는 데이터를 활용할 것임)
 */
data class MusicModel (
    val id: Long,
    val track: String,
    val streamUrl: String,
    val artist: String,
    val coverUrl: String,
    val isPlaying: Boolean = false // 초기값 설정, 서버에 없기 때문에
)
```

### PlayerModel.kt
```kotlin
package techtown.org.musicstream
/*
재생을 하기 위한 데이터 처리를 하기 위해서 만든 클래스
 */
data class PlayerModel (
    private val playMusicList: List<MusicModel> = emptyList(),
    var currentPosition: Int = -1,
    var isWatchingPlayListView: Boolean = true
) {
    // currentPosition을 보고 MusicModel의 재생중을 업데이트 하는 함수
    fun getAdapterModels(): List<MusicModel> {
        return playMusicList.mapIndexed { index, musicModel ->
            // 수정하는 값만 수정하고 클래스를 새로 만들어줌, 어댑터 안에 원래 있던 값 수정 값으로 바꾸면 어댑터에서는 갱신을 하지 않으므로 copy를 통해 새로운 값만 갱신하게함
            val newItem = musicModel.copy(
                isPlaying = index == currentPosition
            )
            newItem
        }
    }

    fun updateCurrentPosition(musicModel: MusicModel) {
        // 넘겨받은 musicmodel 기준으로 index를 통해서 현재 position을 업데이트함
        currentPosition = playMusicList.indexOf(musicModel)
    }

    // 다음 음악, 이전 음악을 재생할 수 있게 처리함
    fun nextMusic(): MusicModel? {
        if(playMusicList.isEmpty()) return null

        currentPosition = if ((currentPosition + 1) == playMusicList.size) 0 else currentPosition + 1
        return playMusicList[currentPosition]
    }

    fun prevMusic(): MusicModel? {
        if(playMusicList.isEmpty()) return null

        currentPosition = if((currentPosition - 1) < 0) playMusicList.lastIndex else currentPosition - 1
        return playMusicList[currentPosition]
    }

    fun currentMusicModel(): MusicModel? {
        if (playMusicList.isEmpty()) return null

        return playMusicList[currentPosition]
    }
}
```

### MusicModelMapper.kt
```kotlin
package techtown.org.musicstream

import techtown.org.musicstream.service.MusicDto
import techtown.org.musicstream.service.MusicEntity

/*
MusicEntity를 통해서 실제 데이터를 받아오고 그 데이터를 MusicModel 클래스에 활용해서 리사이클러뷰에 쓰기 위해서 만든 Mapper 클래스
MusicEntity를 확장한 것임
 */
fun MusicEntity.mapper(id:Long): MusicModel =
    MusicModel(
        id = id,
        streamUrl = streamUrl,
        coverUrl = coverUrl,
        track = track,
        artist = artist
    )

// Dto 자체를 맵핑하여서 PlayerModel에 넘겨서 사용하게 함
fun MusicDto.mapper(): PlayerModel =
    PlayerModel(
        playMusicList = musics.mapIndexed{ index, musicEntity ->
                // MusicEntity를 mapper 함수를 활용 가능 확장해서 만들어뒀기 때문에
                musicEntity.mapper(index.toLong()) // id는 mapIndexed에서의 값을 Long으로 보냄
            }
    )
```

### PlayListAdapter.kt
- 리사이클러뷰에서 사용할 어댑터 클래스
```kotlin
package techtown.org.musicstream

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PlayListAdapter(private val callback: (MusicModel) -> Unit): ListAdapter<MusicModel, PlayListAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val view: View): RecyclerView.ViewHolder(view) {

        fun bind(item: MusicModel) {
            val trackTextView = view.findViewById<TextView>(R.id.itemTrackTextView)
            val artistTextView = view.findViewById<TextView>(R.id.itemArtistTextView)
            val coverImageView = view.findViewById<ImageView>(R.id.itemCoverImageView)

            trackTextView.text = item.track
            artistTextView.text = item.artist

            Glide.with(coverImageView.context)
                .load(item.coverUrl)
                .into(coverImageView)

            // 현재 재생 상태에 따라서 배경색을 변경하고 callback으로 item에 대한 내용을 반환함
            if(item.isPlaying) {
                itemView.setBackgroundColor(Color.GRAY)
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT)
            }

            itemView.setOnClickListener {
                callback(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_music, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        currentList[position].also { musicModel ->
            holder.bind(musicModel)
        }
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<MusicModel>() {
            override fun areItemsTheSame(oldItem: MusicModel, newItem: MusicModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: MusicModel, newItem: MusicModel): Boolean {
                return oldItem == newItem
            }

        }
    }
}
```

### PlayerFragment.kt
- ExoPlayer 재생, 정지, 재생화면과 플레이리스트에 대한 UI 업데이트 및 데이터 처리를 함

- 재생화면에서 그리고 플레이리스트 화면에서의 각각 버튼들의 상호작용과 재생중인 음악에 대한 시간 처리도 Seekbar에 연계해서 함

```kotlin
package techtown.org.musicstream

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.core.util.TimeUtils
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import techtown.org.musicstream.databinding.FragmentPlayerBinding
import techtown.org.musicstream.service.MusicDto
import techtown.org.musicstream.service.MusicService
import java.util.concurrent.TimeUnit

class PlayerFragment : Fragment(R.layout.fragment_player) {

    private var model: PlayerModel = PlayerModel()
    private var binding: FragmentPlayerBinding? = null
    private var player: SimpleExoPlayer? = null
    private lateinit var playListAdapter: PlayListAdapter

    private val updateSeekRunnable = Runnable {
        updateSeek()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentPlayerBinding = FragmentPlayerBinding.bind(view)
        binding = fragmentPlayerBinding

        initPlayView(fragmentPlayerBinding)
        initPlayListButton(fragmentPlayerBinding)
        initPlayControlButtons(fragmentPlayerBinding)
        initSeekBar(fragmentPlayerBinding)
        initRecyclerView(fragmentPlayerBinding)

        getVideoListFromServer()
    }

    private fun initSeekBar(fragmentPlayerBinding: FragmentPlayerBinding) {
        fragmentPlayerBinding.playerSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // 손을 땠을 때 seekbar를 움직임
                player?.seekTo((seekBar.progress * 1000).toLong())

            }

        })

        fragmentPlayerBinding.playListSeekBar.setOnTouchListener{ v, event ->
            false
        }
    }

    private fun initPlayControlButtons(fragmentPlayerBinding: FragmentPlayerBinding) {
        // 단순히 initPlayView에서의 UI 변경말고 실제 Player의 동작을 상태에 따라 변경해주는 함수

        fragmentPlayerBinding.playControllImageView.setOnClickListener {
            val player = this.player ?: return@setOnClickListener

            if(player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
        }
        // 리스트가 한정적으로 있으므로 PlayerModel에서 함수를 통해 다음과 이전 음악을 처리함
        fragmentPlayerBinding.skipNextImageView.setOnClickListener {
            val nextMusic = model.nextMusic() ?: return@setOnClickListener
            playMusic(nextMusic)
        }

        fragmentPlayerBinding.skipPrevImageView.setOnClickListener {
            val prevMusic = model.prevMusic() ?: return@setOnClickListener
            playMusic(prevMusic)
        }
    }

    private fun initPlayView(fragmentPlayerBinding: FragmentPlayerBinding) {
        // 플레이어를 초기화 해주는 함수
        context?.let {
            player = SimpleExoPlayer.Builder(it).build()
        }

        fragmentPlayerBinding.playerView.player = player

        binding?.let { binding ->

            player?.addListener(object: Player.EventListener {

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    // 플레이 상태에 따라 이미지를 바꿔줌
                    if(isPlaying) {
                        binding.playControllImageView.setImageResource(R.drawable.ic_baseline_pause_24)
                    } else {
                        binding.playControllImageView.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    }
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    // 플레이어 상태가 바뀔 때 콜백으로 내려옴, 재생, 재생이 끝날때, 버퍼링등
                    // 이때 seekbar를 업데이트 할 것임, 플레이어의 상태 확인 반복문을 통해서 state 바뀌는거 보고
                    updateSeek()

                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)

                    // 플레이 한 것 기준으로 리사이클러뷰 아이템을 바꿔줌
                    val newIndex = mediaItem?.mediaId ?: return
                    model.currentPosition = newIndex.toInt()
                    updatePlayerView(model.currentMusicModel())
                    playListAdapter.submitList(model.getAdapterModels())
                }
            })
        }
    }

    private fun updateSeek() {

        val player = this.player ?: return
        val duration = if (player.duration >=0) player.duration else 0
        val position = player.currentPosition

        // 위에서 player 상태를 가져와서 ui를 업데이트 함
        updateSeekUi(duration, position)

        val state = player.playbackState

        // 또 들어올 수 있으므로 대기하는 Runnable을 제거해줌
        view?.removeCallbacks(updateSeekRunnable)
        if (state != Player.STATE_IDLE && state != Player.STATE_ENDED) {
            // STATE가 재생중이 아니거나 재생이 끝난게 아니라면, 딜레이를 줌
            view?.postDelayed(updateSeekRunnable, 1000)
        }
    }

    private fun updateSeekUi(duration: Long, position: Long) {
        // 넘겨받은 duration과 position을 기준으로 seekbar와 time에 대한 UI를 업데이트함
        binding?.let { binding ->

            binding.playListSeekBar.max = (duration / 1000).toInt()
            binding.playListSeekBar.progress = (position / 1000).toInt()

            binding.playerSeekBar.max = (duration / 1000).toInt()
            binding.playerSeekBar.progress = (position / 1000).toInt()

            binding.playTimeTextView.text = String.format("%02d:%02d",
                TimeUnit.MINUTES.convert(position, TimeUnit.MILLISECONDS),
                (position / 1000) % 60)
            binding.totalTimeTextView.text = String.format("%02d:%02d",
                TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS),
                (duration / 1000) % 60)
        }
    }

    private fun updatePlayerView(currentMusicModel: MusicModel?) {
        currentMusicModel ?: return

        // play하고 있는 View에 대해서 업데이트 함
        binding?.let { binding ->
            binding.trackTextView.text = currentMusicModel.track
            binding.artistTextView.text = currentMusicModel.artist
            Glide.with(binding.coverImageView.context)
                .load(currentMusicModel.coverUrl)
                .into(binding.coverImageView)
        }
    }

    private fun initRecyclerView(fragmentPlayerBinding: FragmentPlayerBinding) {
        playListAdapter = PlayListAdapter {
            // 음악을 재생, 해당 position을 선택하고 음악을 재생함
            playMusic(it)
        }
        // 리사이클러뷰 어댑터와 연결시켜줌
        fragmentPlayerBinding.playListRecyclerView.apply {
            adapter = playListAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun initPlayListButton(fragmentPlayerBinding: FragmentPlayerBinding) {
        fragmentPlayerBinding.playlistImageView.setOnClickListener {
            // 만약에 서버에서 데이터가 다 불려오지 않은 상태일 때, playList를 열지 않아야함, 이 경우는 -1일 때임
            if (model.currentPosition == -1) return@setOnClickListener

            // playlist 버튼을 누르면 ViewGroup으로 지정해준 것들에 대해서 Visibility 값을 설정함
            fragmentPlayerBinding.playerViewGroup.isVisible = model.isWatchingPlayListView
            fragmentPlayerBinding.playListViewGroup.isVisible = model.isWatchingPlayListView.not()

            model.isWatchingPlayListView  = !model.isWatchingPlayListView
        }
    }

    private fun getVideoListFromServer() {
        // Retrofit을 통해서 해당 Url의 JSON 데이터를 받아와서 처리함
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(MusicService::class.java)
            .also {
                it.listMusics()
                    .enqueue(object: Callback<MusicDto> {
                        override fun onResponse(
                            call: Call<MusicDto>,
                            response: Response<MusicDto>
                        ) {
                            response.body()?.let { musicDto ->

                                // 음악 리스트를 담아서 플레이를 할 수 있게 해당 모델에 넘김, mapper를 통해서 dto 자체를 넘겨서 처리해서 model을 활용함
                                model = musicDto.mapper()
                                setMusicList(model.getAdapterModels())
                                // modelList를 어댑터에 연결함
                                playListAdapter.submitList(model.getAdapterModels())
                            }
                        }

                        override fun onFailure(call: Call<MusicDto>, t: Throwable) {

                        }

                    })
            }
    }

    private fun setMusicList(modelList: List<MusicModel>) {
        // modelList에서 서버에서 받아온 재생목록을 아래와 같이 mediaItem으로 변환한 후 재생 처리를 함
        context?.let {
            player?.addMediaItems(modelList.map { musicModel ->
                MediaItem.Builder()
                    .setMediaId(musicModel.id.toString())
                    .setUri(musicModel.streamUrl)
                    .build()
            })

            player?.prepare()
        }
    }

    private fun playMusic(musicModel: MusicModel) {
        // player는 mediaItem, MusicModel의 형태 둘 다 존재함, 이를 모델의 함수를 활용함
        model.updateCurrentPosition(musicModel)
        player?.seekTo(model.currentPosition, 0)
        player?.play()

    }

    override fun onStop() {
        super.onStop()

        player?.pause()
        view?.removeCallbacks(updateSeekRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
        player?.release()
        view?.removeCallbacks(updateSeekRunnable)
    }

    // PlayerFragment를 만들어서 리턴해주는 함수, 메인에서 불러옴
    // 인자를 넘겨받아서 값을 가져오기 편하게 하기 위해서 함수를 만듬
    fun newInstance(): PlayerFragment {
        return PlayerFragment()
    }




}

```
