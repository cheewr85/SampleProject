package techtown.org.musicstream.service
/*
리스트 형으로 데이터를 담기 위한 DTO 모델, 서버로 내려온 데이터 활용
 */
data class MusicDto (
    val musics: List<MusicEntity> // 서버 모델 그 자체를 가져옴, 뷰에서 사용하는 모델과 다름
)