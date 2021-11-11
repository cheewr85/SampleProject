package techtown.org.locationmap.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/*
검색한 결과에 대한 데이터를 담는 데이터 클래스
인텐트에 담아서 데이트를 넘겨주기 위해서 Parcelize 사용함
 */
@Parcelize
data class SearchResultEntity (
    val fullAddress: String,
    val name: String,
    val locationLatLng: LocationLatLngEntity
): Parcelable