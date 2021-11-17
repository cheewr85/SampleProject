package techtown.org.repository.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import techtown.org.repository.data.dao.RepositoryDao
import techtown.org.repository.data.entity.GithubRepoEntity

@Database(entities = [GithubRepoEntity::class], version = 1)
abstract class SimpleGithubDatabase: RoomDatabase() {

    abstract fun repositoryDao(): RepositoryDao

}