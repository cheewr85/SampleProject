package techtown.org.repository.data.response

import techtown.org.repository.data.entity.GithubRepoEntity

data class GithubRepoSearchResponse (
    val totalCount: Int,
    val items: List<GithubRepoEntity>
)