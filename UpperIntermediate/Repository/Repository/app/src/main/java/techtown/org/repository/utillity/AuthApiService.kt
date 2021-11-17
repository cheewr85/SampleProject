package techtown.org.repository.utillity

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST
import techtown.org.repository.data.response.GithubAccessTokenResponse

/*
OAuth를 통해서 깃허브에서 토큰을 가지고 받아올 수 있는 데이터를 URL을 통해서 활용하기 위해 만든 인터페이스
 */
interface AuthApiService {

    @FormUrlEncoded
    @POST("login/oauth/access_token")
    @Headers("Accept: application/json")
    suspend fun getAccessToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String
    ): GithubAccessTokenResponse
}