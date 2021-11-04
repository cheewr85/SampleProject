package techtown.org.musicstream
/*
재생을 하기 위한 데이터 처리를 하기 위해서 만든 클래스
 */
data class PlayerModel (
    private val playMusicList: List<MusicModel> = emptyList(),
    var currentPosition: Int = -1,
    var isWatchingPlayListView: Boolean = true
) {
    // currentPosition을 보고 MusicModel의 재생중을 업데이트 하는 함수
    fun getAdapterModels(): List<MusicModel> {
        return playMusicList.mapIndexed { index, musicModel ->
            // 수정하는 값만 수정하고 클래스를 새로 만들어줌, 어댑터 안에 원래 있던 값 수정 값으로 바꾸면 어댑터에서는 갱신을 하지 않으므로 copy를 통해 새로운 값만 갱신하게함
            val newItem = musicModel.copy(
                isPlaying = index == currentPosition
            )
            newItem
        }
    }

    fun updateCurrentPosition(musicModel: MusicModel) {
        // 넘겨받은 musicmodel 기준으로 index를 통해서 현재 position을 업데이트함
        currentPosition = playMusicList.indexOf(musicModel)
    }

    // 다음 음악, 이전 음악을 재생할 수 있게 처리함
    fun nextMusic(): MusicModel? {
        if(playMusicList.isEmpty()) return null

        currentPosition = if ((currentPosition + 1) == playMusicList.size) 0 else currentPosition + 1
        return playMusicList[currentPosition]
    }

    fun prevMusic(): MusicModel? {
        if(playMusicList.isEmpty()) return null

        currentPosition = if((currentPosition - 1) < 0) playMusicList.lastIndex else currentPosition - 1
        return playMusicList[currentPosition]
    }

    fun currentMusicModel(): MusicModel? {
        if (playMusicList.isEmpty()) return null

        return playMusicList[currentPosition]
    }
}