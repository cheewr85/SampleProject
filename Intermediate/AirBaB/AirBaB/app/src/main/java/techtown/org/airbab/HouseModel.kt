package techtown.org.airbab
/*
JSON으로 저장한 내용을 받아오기 위한 데이터 클래스
 */

data class HouseModel (
    val id: Int,
    val title: String,
    val price: String,
    val imgUrl: String,
    val lat: Double,
    val lng: Double
)