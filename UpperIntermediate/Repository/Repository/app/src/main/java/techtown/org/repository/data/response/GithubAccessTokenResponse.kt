package techtown.org.repository.data.response
/*
토큰을 통한 접근시 받아온 데이터를 담을 클래스
 */
class GithubAccessTokenResponse (
    val accessToken: String,
    val scope: String,
    val tokenType: String
)