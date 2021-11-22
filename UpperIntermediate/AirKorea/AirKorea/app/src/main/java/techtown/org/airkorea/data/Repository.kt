package techtown.org.airkorea.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import techtown.org.airkorea.BuildConfig
import techtown.org.airkorea.data.models.airquality.MeasuredValues
import techtown.org.airkorea.data.models.monitoriongstation.MonitoringStation
import techtown.org.airkorea.data.services.AirKoreaApiService
import techtown.org.airkorea.data.services.KakaoLocalApiService

/*
Retrofit을 통한 통신을 위해 싱글톤으로 만든 object
 */
object Repository {

    suspend fun getNearbyMonitoringStation(latitude: Double, longitude: Double): MonitoringStation? {
        // Tm 좌표를 받음
        val tmCoordinates=  kakaoLocalApiService
            .getTmCoordinates(longitude, latitude)
            .body()
            ?.documents // x,y좌표가 리스트 형태로 되어있음
            ?.firstOrNull() // 첫 좌표 확인

        val tmX = tmCoordinates?.x
        val tmY = tmCoordinates?.y

        // KAKAO를 통해 tm 좌표로 변환후 해당 좌표에서 측정소를 가져옴
        return airKoreaApiService
            .getNearbyMonitoringStation(tmX!!, tmY!!)
            .body()
            ?.response
            ?.body
            ?.monitoringStations
            ?.minByOrNull { it.tm ?: Double.MAX_VALUE } // API 접근해서 tm 값 중 가장 적은 즉 가까운 측정소를 가져옴
    }

    suspend fun getLatestAirQualityData(stationName: String): MeasuredValues? =
        airKoreaApiService
            .getRealtimeAirQualities(stationName)
            .body()
            ?.response
            ?.body
            ?.MeasuredValues // 측정된 데이터를 불러옴
            ?.firstOrNull() // 첫 값이 없으면 끝

    // Kakao API Service
    private val kakaoLocalApiService: KakaoLocalApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Url.KAKAO_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildHttpClient()) // logging을 위해 추가
            .build()
            .create()
    }

    // Air Korea Service
    private val airKoreaApiService: AirKoreaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Url.AIR_KOREA_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildHttpClient()) // logging을 위해 추가
            .build()
            .create()
    }

    // Header를 조작할 필요 없이 logging 목적의 클라이언트
    private fun buildHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply{
                    // logging level 수준에 따라 다르게 처리해줘야함(보이는 경고의 정도 결정)
                    level = if(BuildConfig.DEBUG) {
                        // Debug일 때만 Body까지 보여줌
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            )
            .build()
}