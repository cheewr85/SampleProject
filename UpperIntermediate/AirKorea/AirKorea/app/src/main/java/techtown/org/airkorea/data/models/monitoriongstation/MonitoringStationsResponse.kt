package techtown.org.airkorea.data.models.monitoriongstation


import com.google.gson.annotations.SerializedName

data class MonitoringStationsResponse(
    @SerializedName("response")
    val response: Response?
)