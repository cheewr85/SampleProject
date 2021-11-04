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
