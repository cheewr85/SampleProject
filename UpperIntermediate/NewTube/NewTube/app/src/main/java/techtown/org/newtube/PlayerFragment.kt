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