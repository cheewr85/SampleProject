package techtown.org.bookreview.model

import com.google.gson.annotations.SerializedName

/*
Book data class는 단순히 JSON에서 받은 결과 중 item에 있는 특정 값만 받은 것임
실제 GET한 전체 JSON데이터에서 쓰기 위해선 전체 모델이 필요함
실제 전체 JSON 데이터 중 여러가지를 사용함
 */
class BestSellerDto(
    // BestSellerDto로 전체 API JSON을 받아오고 해당 JSON에서 title, item이 매칭이 되고 그 매칭된 값에서 item에 있는 내용 title, description등을 쓸 것이므로 Book객체의 List 형태로 받음
    // 그러면 알아서 Book 데이터에 item을 불러온걸 바탕으로 맵핑이 됨
    @SerializedName("title") val title: String,
    @SerializedName("item") val books: List<Book>
)