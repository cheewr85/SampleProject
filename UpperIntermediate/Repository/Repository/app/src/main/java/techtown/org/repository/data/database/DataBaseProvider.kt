package techtown.org.repository.data.database

import android.content.Context
import androidx.room.Room
/*
데이터베이스에 접근하게 할 수 있게 하는 Provider
 */

object DataBaseProvider {

    private const val DB_NAME = "github_repository_app.db"

    fun providerDB(applicationContext: Context) = Room.databaseBuilder(
        applicationContext,
        SimpleGithubDatabase::class.java, DB_NAME
    ).build()
}