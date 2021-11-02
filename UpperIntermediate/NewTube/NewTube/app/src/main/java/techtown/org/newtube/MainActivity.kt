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