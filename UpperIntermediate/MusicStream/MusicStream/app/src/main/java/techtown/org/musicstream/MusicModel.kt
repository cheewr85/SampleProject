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