package techtown.org.bookreview.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/*
Retrofit을 통해서 받은 데이터에 대한 Call 리턴 값으로 활용한 Book 데이터
 */
@Parcelize // Book 클래스 자체를 직렬화로 넘기기 위해서 선언
data class Book(
    // API를 통해서 GET한 데이터 중 활용할 item만을 뽑음
    // GET을 통해 받은 JSON value의 값 중 itemId 값을 받과 매칭하기 위해서 어노테이션 사용, 서버에선 itemId로 현재 Book data class에선 id의 값으로 맵핑이 되서 데이터 가져옴
    @SerializedName("itemId") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("coverSmallUrl") val coverSmallUrl: String
): Parcelable // 직렬화 가능하게 붙임