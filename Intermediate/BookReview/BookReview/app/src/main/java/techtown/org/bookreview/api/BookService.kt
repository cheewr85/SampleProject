package techtown.org.bookreview.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import techtown.org.bookreview.model.BestSellerDto
import techtown.org.bookreview.model.SearchBookDto

/*
베스트셀러 API, 탐색하는 Search API 사용, 둘 다 GET 형식으로 가져옴(GET으로 데이터 요청시 서버에서 반환할 때 Http 형식으로 반환함, URL에 다 넣어서 반환함)
POST는 요청할 때, 새로운 데이터를 만들 때, CREATE 할 때(데이터가 커서 Http Body에 넣어서 전달함)
 */

interface BookService {
    // 데이터만 가져오면 되므로 GET만 사용, base URL 말고 GET을 API에 대해서만 value로 쓰면 됨, output은 JSON으로 고정할 것임
    // search API
    @GET("/api/search.api?output=json")
    // 그런 다음 가져오기 위한 함수 정의 가져올 params을 넣음, key, query 필요
    fun getBooksByName(
        @Query("key") apiKey: String,
        @Query("query") keyword: String
    ):Call<SearchBookDto>

    // 베스트셀러 API, 고정시킬 요소는 고정시켜둠
    @GET("/api/bestSeller.api?output=json&categoryId=100")
    fun getBestSellerBooks(
            @Query("key") apiKey: String
    ):Call<BestSellerDto>
}