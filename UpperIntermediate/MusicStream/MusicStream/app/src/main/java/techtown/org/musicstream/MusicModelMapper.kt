package techtown.org.musicstream

import techtown.org.musicstream.service.MusicDto
import techtown.org.musicstream.service.MusicEntity

/*
MusicEntity를 통해서 실제 데이터를 받아오고 그 데이터를 MusicModel 클래스에 활용해서 리사이클러뷰에 쓰기 위해서 만든 Mapper 클래스
MusicEntity를 확장한 것임
 */
fun MusicEntity.mapper(id:Long): MusicModel =
    MusicModel(
        id = id,
        streamUrl = streamUrl,
        coverUrl = coverUrl,
        track = track,
        artist = artist
    )

// Dto 자체를 맵핑하여서 PlayerModel에 넘겨서 사용하게 함
fun MusicDto.mapper(): PlayerModel =
    PlayerModel(
        playMusicList = musics.mapIndexed{ index, musicEntity ->
                // MusicEntity를 mapper 함수를 활용 가능 확장해서 만들어뒀기 때문에
                musicEntity.mapper(index.toLong()) // id는 mapIndexed에서의 값을 Long으로 보냄
            }
    )