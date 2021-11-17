package techtown.org.repository

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isGone
import kotlinx.coroutines.*
import techtown.org.repository.data.database.DataBaseProvider
import techtown.org.repository.data.entity.GithubRepoEntity
import techtown.org.repository.databinding.ActivityMainBinding
import techtown.org.repository.view.RepositoryRecyclerAdapter
import java.util.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + Job()

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: RepositoryRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAdapter()
        initViews()

    }

    private fun initAdapter() {
        adapter = RepositoryRecyclerAdapter()
    }

    private fun initViews() = with(binding) {
        emptyResultTextView.isGone = true
        recyclerView.adapter = adapter
        // search 화면으로 넘어가게끔 인텐트 처리함
        searchButton.setOnClickListener {
            startActivity(
                Intent(this@MainActivity, SearchActivity::class.java)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        launch(coroutineContext) {
            loadLikedRepositoryList()
        }
    }

    // 찜 기능을 위해서 찜을 누른 부분에 대해서 데이터를 가져옴
    private suspend fun loadLikedRepositoryList() = withContext(Dispatchers.IO) {
        val repoList = DataBaseProvider.providerDB(this@MainActivity).repositoryDao().getHistory()
        withContext(Dispatchers.Main) {
            setData(repoList)
        }
    }

    // 데이터가 있는지 체크함
    private fun setData(githubRepositoryList: List<GithubRepoEntity>) = with(binding) {
        if (githubRepositoryList.isEmpty()) {
            emptyResultTextView.isGone = false
            recyclerView.isGone = true
        } else {
            emptyResultTextView.isGone = true
            recyclerView.isGone = false
            adapter.setRepositoryList(githubRepositoryList) {
                // 어댑터에서 데이터 클릭시 상세화면으로 넘어가서 해당 owner와 name이 뜨면서 내용이 나옴
                startActivity(
                    Intent(this@MainActivity, RepositoryActivity::class.java).apply {
                        putExtra(RepositoryActivity.REPOSITORY_OWNER_KEY, it.owner.login)
                        putExtra(RepositoryActivity.REPOSITORY_NAME_KEY, it.name)
                    }
                )
            }
        }
    }


}