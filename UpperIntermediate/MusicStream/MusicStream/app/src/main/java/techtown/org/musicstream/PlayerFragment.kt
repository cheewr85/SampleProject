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
                // ?????? ?????? ??? seekbar??? ?????????
                player?.seekTo((seekBar.progress * 1000).toLong())

            }

        })

        fragmentPlayerBinding.playListSeekBar.setOnTouchListener{ v, event ->
            false
        }
    }

    private fun initPlayControlButtons(fragmentPlayerBinding: FragmentPlayerBinding) {
        // ????????? initPlayView????????? UI ???????????? ?????? Player??? ????????? ????????? ?????? ??????????????? ??????

        fragmentPlayerBinding.playControllImageView.setOnClickListener {
            val player = this.player ?: return@setOnClickListener

            if(player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
        }
        // ???????????? ??????????????? ???????????? PlayerModel?????? ????????? ?????? ????????? ?????? ????????? ?????????
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
        // ??????????????? ????????? ????????? ??????
        context?.let {
            player = SimpleExoPlayer.Builder(it).build()
        }

        fragmentPlayerBinding.playerView.player = player

        binding?.let { binding ->

            player?.addListener(object: Player.EventListener {

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    // ????????? ????????? ?????? ???????????? ?????????
                    if(isPlaying) {
                        binding.playControllImageView.setImageResource(R.drawable.ic_baseline_pause_24)
                    } else {
                        binding.playControllImageView.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    }
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    // ???????????? ????????? ?????? ??? ???????????? ?????????, ??????, ????????? ?????????, ????????????
                    // ?????? seekbar??? ???????????? ??? ??????, ??????????????? ?????? ?????? ???????????? ????????? state ???????????? ??????
                    updateSeek()

                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)

                    // ????????? ??? ??? ???????????? ?????????????????? ???????????? ?????????
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

        // ????????? player ????????? ???????????? ui??? ???????????? ???
        updateSeekUi(duration, position)

        val state = player.playbackState

        // ??? ????????? ??? ???????????? ???????????? Runnable??? ????????????
        view?.removeCallbacks(updateSeekRunnable)
        if (state != Player.STATE_IDLE && state != Player.STATE_ENDED) {
            // STATE??? ???????????? ???????????? ????????? ????????? ????????????, ???????????? ???
            view?.postDelayed(updateSeekRunnable, 1000)
        }
    }

    private fun updateSeekUi(duration: Long, position: Long) {
        // ???????????? duration??? position??? ???????????? seekbar??? time??? ?????? UI??? ???????????????
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

        // play?????? ?????? View??? ????????? ???????????? ???
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
            // ????????? ??????, ?????? position??? ???????????? ????????? ?????????
            playMusic(it)
        }
        // ?????????????????? ???????????? ???????????????
        fragmentPlayerBinding.playListRecyclerView.apply {
            adapter = playListAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun initPlayListButton(fragmentPlayerBinding: FragmentPlayerBinding) {
        fragmentPlayerBinding.playlistImageView.setOnClickListener {
            // ????????? ???????????? ???????????? ??? ???????????? ?????? ????????? ???, playList??? ?????? ????????????, ??? ????????? -1??? ??????
            if (model.currentPosition == -1) return@setOnClickListener

            // playlist ????????? ????????? ViewGroup?????? ???????????? ????????? ????????? Visibility ?????? ?????????
            fragmentPlayerBinding.playerViewGroup.isVisible = model.isWatchingPlayListView
            fragmentPlayerBinding.playListViewGroup.isVisible = model.isWatchingPlayListView.not()

            model.isWatchingPlayListView  = !model.isWatchingPlayListView
        }
    }

    private fun getVideoListFromServer() {
        // Retrofit??? ????????? ?????? Url??? JSON ???????????? ???????????? ?????????
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

                                // ?????? ???????????? ????????? ???????????? ??? ??? ?????? ?????? ????????? ??????, mapper??? ????????? dto ????????? ????????? ???????????? model??? ?????????
                                model = musicDto.mapper()
                                setMusicList(model.getAdapterModels())
                                // modelList??? ???????????? ?????????
                                playListAdapter.submitList(model.getAdapterModels())
                            }
                        }

                        override fun onFailure(call: Call<MusicDto>, t: Throwable) {

                        }

                    })
            }
    }

    private fun setMusicList(modelList: List<MusicModel>) {
        // modelList?????? ???????????? ????????? ??????????????? ????????? ?????? mediaItem?????? ????????? ??? ?????? ????????? ???
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
        // player??? mediaItem, MusicModel??? ?????? ??? ??? ?????????, ?????? ????????? ????????? ?????????
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

    // PlayerFragment??? ???????????? ??????????????? ??????, ???????????? ?????????
    // ????????? ??????????????? ?????? ???????????? ????????? ?????? ????????? ????????? ??????
    fun newInstance(): PlayerFragment {
        return PlayerFragment()
    }




}
