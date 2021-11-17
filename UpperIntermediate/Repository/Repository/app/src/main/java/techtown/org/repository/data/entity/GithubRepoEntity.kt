package techtown.org.repository.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

/*
Room DB를 활용하여 저장할 Entity 객체
 */
@Entity(tableName= "GithubRepository")
data class GithubRepoEntity(
    val name: String,
    @PrimaryKey val fullName: String,
    @Embedded val owner: GithubOwner,
    val description: String?,
    val language: String?,
    val updatedAt: String,
    val stargazersCount: Int
)