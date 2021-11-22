package techtown.org.airkorea.data.services

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import techtown.org.airkorea.BuildConfig
import techtown.org.airkorea.data.models.tmcoordinates.TmCoordinatesResponse

/*
카카오 API 사용을 통해서 좌표 변환을 위해서 통신하는 인터페이스
 */
interface KakaoLocalApiService {

    // KAKAO 좌표 변환을 위한 API 호출, tm 좌표로 변환
    @Headers("Authorization: KakaoAK ${BuildConfig.KAKAO_API_KEY}")
    @GET("v2/local/geo/transcoord.json?output_coord=TM")
    suspend fun getTmCoordinates(
        @Query("x") longitude: Double, // 경도
        @Query("y") latitude: Double // 위도
    ): Response<TmCoordinatesResponse>
}