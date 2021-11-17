## 깃허브 레포지토리 앱
- GitHub OAuth 토큰을 활용하고 API를 통해서 깃허브 레포지토리를 검색하고 찜기능 구현

- RoomDB를 활용하여 검색한 저장소와 찜기능을 둔 저장소 검색시 들어가서 볼 수 있는 상세 내용을 저장해두고 갱신함

- 코루틴을 활용하여서 비동기적으로 데이터 불러오고 받아오고 서버 통신을 처리함

### 로그인 화면
![one](/UpperIntermediate/Repository/img/one.png)

- 깃허브 로그인 기능 구현, 로그인 버튼 누를시 깃허브 페이지와 연동되어 깃허브 OAuth 토큰이 발행된 상태라면 자동으로 로그인이 연동되게 처리함

- 홈페이지 들어갈 때 browser 라이브러리를 활용하여 login 하는 Url을 설정해서 처리하여 로그인함

- 로딩중을 나타내기 위해서 ProgressBar로 직접 구현을 함

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".SignInActivity">

    <ImageView
        android:id="@+id/logoImageView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@drawable/ic_github_logo"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintBottom_toTopOf="@+id/loginButton"
        app:layout_constraintVertical_chainStyle="packed"/>

    <Button
        android:id="@+id/loginButton"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginTop="24dp"
        android:layout_marginHorizontal="64dp"
        android:text="GitHub Login"
        android:textStyle="bold"
        app:layout_constraintTop_toBottomOf="@id/logoImageView"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"/>

    <ProgressBar
        android:id="@+id/progressBar"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:progressTint="@color/black"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toBottomOf="@id/logoImageView"
        android:layout_marginTop="24dp"
        android:visibility="gone"
        tools:visibility="visible"/>

    <TextView
        android:id="@+id/progressTextView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="로딩 중..."
        app:layout_constraintTop_toBottomOf="@id/progressBar"
        app:layout_constraintStart_toStartOf="@id/progressBar"
        app:layout_constraintEnd_toEndOf="@id/progressBar"
        android:textColor="@color/black"
        android:textSize="16sp"
        android:visibility="gone"
        tools:visibility="visible"/>

</androidx.constraintlayout.widget.ConstraintLayout>
```

### SignInActivity.kt
- Retrofit 통신, 해당 결과값, 그리고 API Interface를 각각 data 패키지 내부에 response, utility 패키지에 둠

- Url object를 통해서 접속하고 사용할 URL을 저장해둠

```kotlin
package techtown.org.repository

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.isGone
import kotlinx.coroutines.*
import techtown.org.repository.databinding.ActivitySignInBinding
import techtown.org.repository.utillity.AuthTokenProvider
import techtown.org.repository.utillity.RetrofitUtil
import kotlin.coroutines.CoroutineContext

class SignInActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var binding: ActivitySignInBinding

    private val authTokenProvider by lazy { AuthTokenProvider(this) }


    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (checkAuthCodeExist()) {
            // Token이 정상적으로 처리됐다면 Main으로 넘어감
            launchMainActivity()
        } else {
            initViews()
        }
    }

    private fun initViews() = with(binding) {
        loginButton.setOnClickListener {
            loginGithub()
        }
    }

    private fun launchMainActivity() {
        // 메인화면으로 넘어감
        startActivity(Intent(this,MainActivity::class.java).apply {
            // 플래그 처리함 SignIn은 종료를 시키고 메인을 실행함
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    // Preference의 접근해서 AuthCode가 있는지 확인하는 함수
    private fun checkAuthCodeExist(): Boolean = authTokenProvider.token.isNullOrEmpty().not()

    // todo https://github.com/login/oauth/authroize?client_id=GITHUB_CLIENT_ID
    // 위의 주소대로 처리한 것
    private fun loginGithub() {
        // Login을 하기 위해서 해당 주소로 넘어감
        val loginUri = Uri.Builder().scheme("https").authority("github.com")
            .appendPath("login")
            .appendPath("oauth")
            .appendPath("authorize")
            .appendQueryParameter("client_id",BuildConfig.GITHUB_CLIENT_ID)
            .build()

        // 브라우저 라이브러리 기반으로 선언하여 인텐트 필터를 정의하였으므로 해당 url 데이터 받아옴, 그리고 해당 URL을 넘어가서 커스텀 탭 인텐트로 넘어감
        CustomTabsIntent.Builder().build().also {
            it.launchUrl(this,loginUri)
        }


    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        // 위에서 인텐트 처리후 인텐트에서 받아온 값이 있을 경우 처리함
        // 로그인이 된 상태에서 해당 레포지토리를 접근한 토큰을 받아와 앱을 처리할 수 있음
        intent?.data?.getQueryParameter("code")?.let { code ->
            // todo getAccessToken
            GlobalScope.launch {
                showProgress() // 로딩을 나타냄
                val getAccessTokenJob = getAccessToken(code)
                getAccessTokenJob.join()
                dismissProgress()
                if (checkAuthCodeExist()) {
                    // Token이 정상적으로 처리됐다면 Main으로 넘어감
                    launchMainActivity()
                }
            }
        }
    }

    // progress 상태를 정의하는 함수들
    private suspend fun showProgress() = GlobalScope.launch {
        withContext(Dispatchers.Main) {
            with(binding) {
                loginButton.isGone = true
                progressBar.isGone = false
                progressTextView.isGone = false
            }
        }
    }

    private suspend fun dismissProgress() = GlobalScope.launch {
        withContext(Dispatchers.Main) {
            with(binding) {
                loginButton.isGone = false
                progressBar.isGone = true
                progressTextView.isGone = true
            }
        }
    }

    // 토큰을 받아오는데 있어서 비동기적으로 IO 쓰레드에서 받아서 처리함, 앞서 정의한 Retrofit 관련 클래스에서 콜을 하여서 결과를 받아옴
    private suspend fun getAccessToken(code: String) = launch(coroutineContext) {
        try {
            withContext(Dispatchers.IO) {
                val response = RetrofitUtil.authApiService.getAccessToken(
                    clientId = BuildConfig.GITHUB_CLIENT_ID,
                    clientSecret = BuildConfig.GITHUB_CLIENT_SECRET,
                    code = code
                )
                val accessToken = response.accessToken
                if(accessToken.isNotEmpty()) {
                    withContext(coroutineContext) {
                        authTokenProvider.updateToken(accessToken)
                    }
                }
            }
        } catch(e: Exception) {
            e.printStackTrace()
            Toast.makeText(this@SignInActivity,"로그인 과정에서 에러가 발생했습니다. : ${e.message}",Toast.LENGTH_SHORT).show()
        }
    }
}
```

### 메인화면
![one](/UpperIntermediate/Repository/img/two.png)
![one](/UpperIntermediate/Repository/img/three.png)

- 만약 찜기능을 통해서 찜한 경우 해당 레포지토리에 대한 아이템을 리사이클러뷰로 나타남 

- 플로팅 버튼을 누른다면 검색화면으로 넘어가서 레포지토리 검색을 할 수 있음

- 데이터 로드에 경우 앞서 말했듯이 RoomDB를 통해서 데이터를 가져옴

- 위의 첫번째 화면의 경우 아무것도 없어서 인텐트로 넘겨서 그리고 RoomDB에서 Entity로 받아와서 리사이클러뷰 어댑터에서 나타낼 것이 없는 상태임

- 만약 찜한 목록이 있다면 2번째 사진처럼 그 부분에 대해서 RetrofitUtil에 정의한대로 통신을 하고 인터페이스에서 GET을 통해서 데이터를 가져오고 Response로 Entity로 정의한 데이터를 가져온 뒤 저장한 후 리사이클러뷰에서 나타낼 수 있음

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager"/>

    <TextView
        android:id="@+id/emptyResultTextView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        android:visibility="gone"
        tools:visibility="visible"
        android:text="@string/empty_history_text"/>

    <com.google.android.material.floatingactionbutton.FloatingActionButton
        android:id="@+id/searchButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        android:layout_marginEnd="32dp"
        android:layout_marginBottom="32dp"
        android:backgroundTint="@color/black"
        app:tint="@color/white"
        android:src="@drawable/ic_search"/>

</androidx.constraintlayout.widget.ConstraintLayout>
```

### MainActivity.kt
- 데이터를 가져와서 찜한 목록만을 나타냄

```kotlin
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
```

### 검색화면
![one](/UpperIntermediate/Repository/img/four.png)

- 검색하기 위해서 EditText를 통해서 키워드를 입력받고 그 키워드를 바탕으로 검색한 내용을 가지고 해당 키워드를 기준으로 존재할 때 리사이클러뷰로 뿌려줌

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".SearchActivity">

    <ProgressBar
        android:id="@+id/progressBar"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        android:visibility="gone"/>

    <EditText
        android:id="@+id/searchBarInputView"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintEnd_toStartOf="@id/searchButton"
        android:hint="@string/please_input_search_keyword"/>

    <Button
        android:id="@+id/searchButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        android:text="@string/search"/>

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toBottomOf="@id/searchBarInputView"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager"/>

    <TextView
        android:id="@+id/emptyResultTextView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        android:text="@string/empty_result_text"
        android:visibility="gone"
        tools:visibility="visible"/>

</androidx.constraintlayout.widget.ConstraintLayout>
```

### SearchActivity.kt
- 이 역시 Retrofit을 바탕으로 인터페이스에 정의한대로 API와 통신을 하여서 키워드 기준으로 데이터를 찾아봄, 앞서 MainActivity에서 봤듯이 데이터를 가지고 Entity로 Response를 받음

- 그리고 해당 데이터를 누르면 레포지토리에 대한 기본적인 내용을 받아오는 화면으로 넘어감

```kotlin
package techtown.org.repository

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isGone
import kotlinx.coroutines.*
import okhttp3.Dispatcher
import techtown.org.repository.RepositoryActivity.Companion.REPOSITORY_NAME_KEY
import techtown.org.repository.RepositoryActivity.Companion.REPOSITORY_OWNER_KEY
import techtown.org.repository.data.entity.GithubRepoEntity
import techtown.org.repository.databinding.ActivitySearchBinding
import techtown.org.repository.utillity.RetrofitUtil
import techtown.org.repository.view.RepositoryRecyclerAdapter
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class SearchActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + Job()

    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: RepositoryRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAdapter()
        initViews()
        bindViews()
    }

    // 뷰와 어댑터를 초기화 해주는 함수
   private fun initAdapter() {
        adapter = RepositoryRecyclerAdapter()
    }

    private fun initViews() = with(binding) {
        emptyResultTextView.isGone = true
        recyclerView.adapter = adapter
    }

    private fun bindViews() = with(binding) {
        searchButton.setOnClickListener {
            // 검색버튼을 통해 검색을 할 수 있음
            searchKeyword(searchBarInputView.text.toString())
        }
    }

    // 검색을 해서 데이터 뿌려주는 함수
    private fun searchKeyword(keywordString: String) {
        showLoading(true)
        launch(coroutineContext) {
            try {
                withContext(Dispatchers.IO) {
                    val response = RetrofitUtil.githubApiService.searchRepositories(
                        query = keywordString
                    )
                    if (response.isSuccessful) {
                        val body = response.body()
                        withContext(Dispatchers.Main) {
                            body?.let { searchResponse ->
                                setData(searchResponse.items)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@SearchActivity,
                    "검색하는 과정에서 에러가 발생했습니다. : ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setData(githubRepoList: List<GithubRepoEntity>) = with(binding) {
        showLoading(false)
        if(githubRepoList.isEmpty()) {
            emptyResultTextView.isGone = false
            recyclerView.isGone = true
        } else {
            emptyResultTextView.isGone = true
            recyclerView.isGone = false
            adapter.setRepositoryList(githubRepoList) {
                startActivity(
                    Intent(this@SearchActivity, RepositoryActivity::class.java).apply {
                        putExtra(REPOSITORY_OWNER_KEY, it.owner.login)
                        putExtra(REPOSITORY_NAME_KEY, it.name)
                    }
                )
            }
        }
    }


    private fun showLoading(isShown: Boolean) = with(binding) {
        progressBar.isGone = isShown.not()
    }
}
```

### 레포지토리 화면
![one](/UpperIntermediate/Repository/img/five.png)

- 깃허브 API에서 가져온 데이터를 바탕으로 통신을 받아온 Response를 해당 View에 정보를 나타냄

- 그리고 찜 기능을 구현하여 찜 버튼을 누르면 RoomDB에 저장을 하여서 찜 상태를 가지고 저장을 해 둠

- 해당 화면은 찜한 레포지토리 혹은 검색시 세부 사항을 보는 것이기 때문에 검색화면과 메인화면에서 아이템 클릭시 인텐트로 데이터를 받아오고 세부사항을 표현하여 구현함

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".RepositoryActivity">

    <ProgressBar
        android:id="@+id/progressBar"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"/>

    <ImageView
        android:id="@+id/ownerProfileImageView"
        android:layout_width="84dp"
        android:layout_height="84dp"
        android:layout_margin="8dp"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toStartOf="@id/ownerNameAndRepoNameTextView"
        tools:src="@drawable/ic_github_logo" />

    <TextView
        android:id="@+id/ownerNameAndRepoNameTextView"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="8dp"
        android:textColor="@color/black"
        android:textStyle="bold"
        android:textSize="24sp"
        app:layout_constraintTop_toTopOf="@id/ownerProfileImageView"
        app:layout_constraintStart_toEndOf="@id/ownerProfileImageView"
        app:layout_constraintBottom_toTopOf="@id/stargazersCountText"
        app:layout_constraintEnd_toStartOf="@id/likeButton"
        android:maxLines="3"
        android:ellipsize="end"
        tools:text="soda1127/blahblah"
        app:layout_constraintVertical_chainStyle="packed"/>

    <ImageView
        android:id="@+id/likeButton"
        android:layout_width="42dp"
        android:layout_height="42dp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="@id/dividerView"
        android:layout_marginEnd="8dp"
        tools:src="@drawable/ic_dislike"
        android:scaleType="center"
        app:tint="@color/red" />

    <TextView
        android:id="@+id/stargazersCountText"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginVertical="8dp"
        android:gravity="center_vertical"
        android:textSize="16sp"
        app:layout_constraintTop_toBottomOf="@id/ownerNameAndRepoNameTextView"
        app:layout_constraintStart_toStartOf="@id/ownerNameAndRepoNameTextView"
        app:layout_constraintBottom_toBottomOf="@id/ownerProfileImageView"
        app:drawableStartCompat="@drawable/ic_star"
        app:drawableTint="@color/orange_yellow"
        tools:text="1.2k" />

    <TextView
        android:id="@+id/languageText"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginVertical="8dp"
        android:gravity="center_vertical"
        android:textSize="16sp"
        android:textStyle="bold"
        app:layout_constraintTop_toTopOf="@id/stargazersCountText"
        app:layout_constraintBottom_toBottomOf="@id/stargazersCountText"
        app:layout_constraintStart_toEndOf="@id/stargazersCountText"
        app:drawableStartCompat="@drawable/ic_circle"
        android:drawablePadding="4dp"
        android:layout_marginStart="6dp"
        tools:text="Java" />

    <View
        android:id="@+id/dividerView"
        android:layout_width="0dp"
        android:layout_height="0.5dp"
        android:background="@color/black"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toBottomOf="@id/ownerProfileImageView"
        android:layout_marginTop="16dp"/>

    <TextView
        android:id="@+id/descriptionTitleTextView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:layout_constraintStart_toStartOf="@id/ownerProfileImageView"
        app:layout_constraintTop_toBottomOf="@id/dividerView"
        android:layout_marginTop="16dp"
        android:text="설명"
        android:textStyle="bold"
        android:textSize="16sp"
        android:textColor="@color/black"/>

    <TextView
        android:id="@+id/descriptionTextView"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp"
        app:layout_constraintTop_toBottomOf="@id/descriptionTitleTextView"
        app:layout_constraintStart_toStartOf="@id/ownerProfileImageView"
        app:layout_constraintEnd_toEndOf="parent"
        android:layout_marginEnd="8dp"
        tools:text="부제목" />

    <TextView
        android:id="@+id/updateTimeTitleTextView"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp"
        app:layout_constraintTop_toBottomOf="@id/descriptionTextView"
        app:layout_constraintStart_toStartOf="@id/ownerProfileImageView"
        app:layout_constraintEnd_toEndOf="parent"
        android:layout_marginEnd="8dp"
        android:text="업데이트 시간"
        android:textStyle="bold"
        android:textSize="16sp"
        android:textColor="@color/black"/>

    <TextView
        android:id="@+id/updateTimeTextView"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp"
        app:layout_constraintTop_toBottomOf="@id/updateTimeTitleTextView"
        app:layout_constraintStart_toStartOf="@id/ownerProfileImageView"
        app:layout_constraintEnd_toEndOf="parent"
        android:layout_marginEnd="8dp"
        tools:text="부제목" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

### RepositoryActivity.kt
- 인텐트로 데이터를 받아와서 세부사항을 보여줌

```kotlin
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
```

### 패키지
- View패키지의 경우 리사이클러뷰에 어댑터를 구현함
 
- Utility패키지는 Retrofit으로 서버와 통신을 구현하고 해당 데이터를 불러오는데 있어서 인터페이스로 구현하여 Response로 받음, 그리고 AuthToken 관련해서 역시 받아옴

- Response패키지는 인터페이스에 구현한 Response의 값을 데이터로 받아서 처리함

- Entity, Database, Dao 패키지의 경우 RoomDB를 통한 저장을 위해서 구현한 패키지임

- Extension 패키지는 dp에서 px로 바꿔주는 확장함수와 Glide에 접근해서 scaletype 변경을 위한 함수를 추가함

