package techtown.org.airbab
/*
리스트형태로 JSON을 저장했으므로 List형태로 받기 위한 데이터 클래스
 */

data class HouseDto (
    val items: List<HouseModel>
)