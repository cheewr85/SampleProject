package techtown.org.locationmap.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/*
위도 경도에 대한 정보를 담는 데이터 클래스
 */
@Parcelize
data class LocationLatLngEntity (
    val latitude: Float,
    val longitude: Float
): Parcelable