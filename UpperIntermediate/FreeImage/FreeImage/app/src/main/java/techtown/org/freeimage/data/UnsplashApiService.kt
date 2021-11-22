package techtown.org.freeimage.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import techtown.org.freeimage.BuildConfig
import techtown.org.freeimage.data.models.PhotoResponse
/*
랜덤하게 사진을 불러올 수 있게 통신을 하게끔 처리한 쿼리
 */
interface UnsplashApiService {

    @GET(
        "photos/random?" +
                "client_id=${BuildConfig.UNSPLASH_ACCESS_KEY}" +
                "&count=30"
    )
    suspend fun getRandomPhotos(
        @Query("query") query: String?
    ): Response<List<PhotoResponse>>
}