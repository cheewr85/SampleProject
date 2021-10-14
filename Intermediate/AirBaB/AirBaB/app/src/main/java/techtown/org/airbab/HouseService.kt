package techtown.org.airbab

import retrofit2.Call
import retrofit2.http.GET

interface HouseService {
    @GET("/v3/63233eee-52e1-41b4-aa7f-98b47809ca8f") // GET할 주소 입력
    fun getHouseList(): Call<HouseDto> // JSON에 items의 리스트에 데이터를 담았으므로 Callback으로 Dto로 List를 받고 거기에 있는 model을 통해서 데이터 접근
}