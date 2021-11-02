package techtown.org.newtube.dto

import techtown.org.newtube.model.VideoModel

/*
VideoModel이라는 Array를 Dto로 받아서 사용하기 위한 클래스
 */
data class VideoDto (
    val videos: List<VideoModel>
    )