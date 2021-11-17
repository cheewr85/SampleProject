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