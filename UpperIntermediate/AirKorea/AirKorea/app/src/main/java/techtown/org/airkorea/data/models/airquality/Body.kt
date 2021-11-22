package techtown.org.airkorea.data.models.airquality


import com.google.gson.annotations.SerializedName

data class Body(
    @SerializedName("items")
    val MeasuredValues: List<MeasuredValues>? = null,
    @SerializedName("numOfRows")
    val numOfRows: Int? = null,
    @SerializedName("pageNo")
    val pageNo: Int? = null,
    @SerializedName("totalCount")
    val totalCount: Int? = null
)