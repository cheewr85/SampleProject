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