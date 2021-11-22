package techtown.org.freeimage.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import techtown.org.freeimage.BuildConfig
import techtown.org.freeimage.data.models.PhotoResponse

object Repository {

    // Retrofit 생성하여 처리하는 함수
    private val unsplashApiService: UnsplashApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Url.UNSPLASH_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildOkHttpClient())
            .build()
            .create()
    }

    // Api를 통해서 호출해서 활용하는 부분
    suspend fun getRandomPhotos(query: String?): List<PhotoResponse>? =
        unsplashApiService.getRandomPhotos(query).body()

    // 로깅을 위해서 만든 클라이언트
    private fun buildOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if(BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            )
            .build()

}