package techtown.org.repository

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import kotlinx.coroutines.*
import techtown.org.repository.data.database.DataBaseProvider
import techtown.org.repository.data.entity.GithubRepoEntity
import techtown.org.repository.databinding.ActivityRepositoryBinding
import techtown.org.repository.extensions.loadCenterInside
import techtown.org.repository.utillity.RetrofitUtil
import kotlin.coroutines.CoroutineContext

class RepositoryActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + Job()

    private lateinit var binding: ActivityRepositoryBinding


    companion object {
        // owner, repository 이름이 필요하므로 object로 선언
        const val REPOSITORY_OWNER_KEY = "REPOSITORY_OWNER_KEY"
        const val REPOSITORY_NAME_KEY = "REPOSITORY_NAME_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepositoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 인텐트로 받은 데이터를 받아옴
        val repositoryOwner = intent.getStringExtra(REPOSITORY_OWNER_KEY) ?: kotlin.run {
            toast("Repository Owner 이름이 없습니다.")
            finish()
            return
        }

        val repositoryName = intent.getStringExtra(REPOSITORY_NAME_KEY) ?: kotlin.run {
            toast("Repository 이름이 없습니다.")
            finish()
            return
        }

        launch {
            // 인텐트로 받아온 데이터를 가지고 Repo를 불러옴
            loadRepository(repositoryOwner, repositoryName)?.let {
                setData(it)
            } ?: run {
                // 없을 경우 토스트 메시지 띄움
                toast("Repository 정보가 없습니다.")
                finish()
            }
        }

        showLoading(true)
    }

    // 코루틴을 이용, API 통신을 비동기적으로 처리함
    private suspend fun loadRepository(
        repositoryOwner: String,
        repositoryName: String
    ): GithubRepoEntity? =
        withContext(coroutineContext) {
            var repository: GithubRepoEntity? = null
            withContext(Dispatchers.IO) {
                val response = RetrofitUtil.githubApiService.getRepository(
                    ownerLogin = repositoryOwner,
                    repoName = repositoryName
                )
                if (response.isSuccessful) {
                    val body = response.body()
                    withContext(Dispatchers.Main) {
                        body?.let { repo ->
                            repository = repo
                        }
                    }
                }
            }
            repository
        }

    // UI로 뿌려줄 수 있게 처리를 함
    private fun setData(githubRepoEntity: GithubRepoEntity) = with(binding) {
        // repository xml에서 UI를 데이터에 맞게 갱신을 함
        showLoading(false)
        ownerProfileImageView.loadCenterInside(githubRepoEntity.owner.avatarUrl, 42f)
        ownerNameAndRepoNameTextView.text =
            "${githubRepoEntity.owner.login}/${githubRepoEntity.name}"
        stargazersCountText.text = githubRepoEntity.stargazersCount.toString()
        githubRepoEntity.language?.let { language ->
            languageText.isGone = false
            languageText.text = language
        } ?: kotlin.run {
            languageText.isGone = true
            languageText.text = ""
        }
        descriptionTextView.text = githubRepoEntity.description
        updateTimeTextView.text = githubRepoEntity.updatedAt

        setLikeState(githubRepoEntity)
    }

    // 찜을 한 것인지 확인하는 함수
    private fun setLikeState(githubRepoEntity: GithubRepoEntity) = launch {
        withContext(Dispatchers.IO) {
            // repository가 있는지 확인함
            val repository = DataBaseProvider.providerDB(this@RepositoryActivity).repositoryDao().getRepository(githubRepoEntity.fullName)
            val isLike = repository != null
            withContext(Dispatchers.Main) {
                setLikeImage(isLike)
                binding.likeButton.setOnClickListener {
                    // 찜 버튼 클릭 리스너, 누른 상태에 따라서 DB에 설정을 함
                    likeGithubRepo(githubRepoEntity, isLike)
                }
            }
        }
    }

    // 찜 이미지 갱신하는 함수, 상태에 맞게 갱신을 함
    private fun setLikeImage(isLike: Boolean) {
        binding.likeButton.setImageDrawable(
            ContextCompat.getDrawable(
                this@RepositoryActivity,
                if (isLike) {
                    R.drawable.ic_like
                } else {
                    R.drawable.ic_dislike
                }
            )
        )
    }

    // 찜 버튼에 따라서 Repo를 어떻게 할지 DB처리 하는 함수
    private fun likeGithubRepo(githubRepoEntity: GithubRepoEntity, isLike: Boolean) = launch {
        withContext(Dispatchers.IO) {
            // Like 상태에 따라 DB에 추가하거나 삭제를 함(눌렀을 때 기준으로)
            val dao = DataBaseProvider.providerDB(this@RepositoryActivity).repositoryDao()
            if(isLike) {
                dao.remove(githubRepoEntity.fullName)
            } else {
                dao.insert(githubRepoEntity)
            }
            withContext(Dispatchers.Main) {
                setLikeImage(isLike.not()) // 클릭시 바뀌어야 하니깐
            }
        }
    }

    // progressBar 설정하는 함수
    private fun showLoading(isShown: Boolean) = with(binding) {
        progressBar.isGone = isShown.not()
    }

    // Toast 메시지 출력을 확장함수 처리해서 간소화함
    private fun Context.toast(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}