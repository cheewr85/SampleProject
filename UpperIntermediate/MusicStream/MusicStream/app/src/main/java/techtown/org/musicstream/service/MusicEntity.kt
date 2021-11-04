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