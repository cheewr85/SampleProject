package techtown.org.musicstream.service

import retrofit2.Call
import retrofit2.http.GET

interface MusicService {

    @GET("/v3/a6949f13-8ca2-4234-b4ae-40bd1d704584")
    fun listMusics() : Call<MusicDto>
}