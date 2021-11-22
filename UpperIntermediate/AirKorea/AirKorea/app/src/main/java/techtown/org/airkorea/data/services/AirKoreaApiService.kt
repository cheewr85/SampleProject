package techtown.org.airkorea.data.services

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import techtown.org.airkorea.BuildConfig
import techtown.org.airkorea.data.models.airquality.AirQualityResponse
import techtown.org.airkorea.data.models.monitoriongstation.MonitoringStationsResponse

/*
근접 측정소에 해당하는 좌표, 대기오염 정보를 불러오는데 통신하는 인터페이스
 */
interface AirKoreaApiService {

    @GET(
        "B552584/MsrstnInfoInqireSvc/getNearbyMsrstnList" +
                "?serviceKey=${BuildConfig.AIR_KOREA_SERVICE_KEY}" +
                "&returnType=json"
    )
    suspend fun getNearbyMonitoringStation(
        // 좌표를 불러옴
        @Query("tmX") tmX: Double,
        @Query("tmY") tmY: Double
    ): Response<MonitoringStationsResponse>

    @GET(
        "B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty" +
                "?serviceKey=${BuildConfig.AIR_KOREA_SERVICE_KEY}" +
                "&returnType=json" +
                "&dataTerm=DAILY" +
                "&ver=1.3"
    )
    suspend fun getRealtimeAirQualities(
        @Query("stationName") stationName: String
    ): Response<AirQualityResponse>
}