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