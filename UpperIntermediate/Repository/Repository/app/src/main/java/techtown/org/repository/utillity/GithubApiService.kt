package techtown.org.repository.utillity

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import techtown.org.repository.data.entity.GithubRepoEntity
import techtown.org.repository.data.response.GithubRepoSearchResponse

/*
GitHub의 API와 통신하는 인터페이스
 */
interface GithubApiService {

    @GET("search/repositories")
    suspend fun searchRepositories(@Query("q") query: String): Response<GithubRepoSearchResponse>

    @GET("repos/{owner}/{name}")
    suspend fun getRepository(
        @Path("owner") ownerLogin: String,
        @Path("name") repoName: String
    ): Response<GithubRepoEntity>
}