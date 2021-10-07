package techtown.org.tinder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    // 이메일 패스워드 받아와서 Firebase Auth에 전달해줄 것임
    private lateinit var auth: FirebaseAuth
    // facebook 로그인 버튼 누른 이후 facebook 앱 혹은 웹 로그인 후 완료한 뒤 activity callback으로 받기 때문에 callbackmanager를 선언함(facebook 라이브러리에 있음)
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Auth 인스턴스를 가져옴
        auth = Firebase.auth
        // callbackManger 초기화
        callbackManager = CallbackManager.Factory.create()

        initLoginButton()
        initSignUpButton()
        initEmailAndPasswordEditText()
        initFacebookLoginButton()
    }

    private fun initLoginButton() {
        // 로그인 버튼 눌렀을 때 동작 처리
        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            // 이메일과 패스워드를 먼저 가져옴
            val email = getInputEmail()
            val password = getInputPassword()

            // 이메일과 패스워드를 파라미터로 넘겨 받고 FirebaseAuth에 있는 SignIn 기능을 사용할 수 있음
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task -> // 반환값이 Task임<AuthResult>
                    if(task.isSuccessful) {
                        // 성공적으로 수행이 됐다면 LoginActivity를 종료시킴, 성공적으로 됐다면 FirebaseAuth에 저장될 것이므로
                        handleSuccessLogin()
                    } else { // 실패시 토스트 메시지 띄움
                        Toast.makeText(this, "로그인에 실패했습니다. 이메일 또는 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun initSignUpButton() {
        // 회원가입 버튼 눌렀을 때 동작 처리
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        signUpButton.setOnClickListener {
            // 이메일과 패스워드를 먼저 가져옴
            val email = getInputEmail()
            val password = getInputPassword()

            // 회원가입을 처리하는 FirebaseAuth에 있는 메소드 사용, 위의 로그인과 비슷한 로직으로 수행함
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        Toast.makeText(this, "회원가입에 성공했습니다. 로그인 버튼을 눌러 로그인 해주세요", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "이미 가입한 이메일이거나, 회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

        }
    }

    private fun initEmailAndPasswordEditText() {
        // 이메일과 패스워드가 만약 null 값일 경우에 대한 예외처리
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signUpButton = findViewById<Button>(R.id.signUpButton)

        // 텍스트 변화가 있을 때 즉 둘 다 비어있다면, 로그인, 회원가입 버튼 비활성화 시킬 것임
        emailEditText.addTextChangedListener { // 텍스트 입력될 때마다 리스너로 이벤트가 내려옴
            // 둘 다 비어있지 않을 때만 true 반환
            val enable = emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()
            // 그리고 그럴때만 활성화 시킴
            loginButton.isEnabled = enable
            signUpButton.isEnabled = enable
        }

        // 텍스트 변화가 있을 때 즉 둘 다 비어있다면, 로그인, 회원가입 버튼 비활성화 시킬 것임
        passwordEditText.addTextChangedListener { // 텍스트 입력될 때마다 리스너로 이벤트가 내려옴
            // 둘 다 비어있지 않을 때만 true 반환
            val enable = emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()
            // 그리고 그럴때만 활성화 시킴
            loginButton.isEnabled = enable
            signUpButton.isEnabled = enable
        }
    }

    private fun initFacebookLoginButton() {
        // 페이스북 로그인 버튼 초기화
        val facebookLoginButton = findViewById<LoginButton>(R.id.facebookLoginButton)

        facebookLoginButton.setPermissions("email", "public_profile") // 로그인 버튼 눌렀을 때 유저에게 받아올 정보를 설정함(이메일과 프로필 추가, 다른 정보도 가져올 수 있음)
        facebookLoginButton.registerCallback(callbackManager, object: FacebookCallback<LoginResult>{ // 로그인 버튼의 콜백도 추가함 앞서 선언한 콜백매니저
            // LoginResult를 통해서 로그인 결과를 받아옴
            override fun onSuccess(result: LoginResult) {
                // 로그인이 성공했을 때(?는 제외함, 성공한 케이스에서 null이 넘어올 수 없어서)
                // LoginResult에서 access Token을 가져온 뒤 Firebase에 알려줌
                val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                // firebase에서 facebook에 로그인한 access token을 아래와 같이 넘겨줌
                auth.signInWithCredential(credential)
                    .addOnCompleteListener(this@LoginActivity) { task ->
                        if(task.isSuccessful) {
                            handleSuccessLogin()
                        } else {
                            Toast.makeText(this@LoginActivity, "페이스북 로그인이 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

            override fun onCancel() {
                // 로그인 하다가 취소했을 때
            }

            override fun onError(error: FacebookException?) {
                // 로그인 에러가 났을 때
                Toast.makeText(this@LoginActivity, "페이스북 로그인이 실패했습니다.", Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun getInputEmail(): String {
        // 입력한 이메일을 가져와서 리턴함
        return findViewById<EditText>(R.id.emailEditText).text.toString()
    }

    private fun getInputPassword(): String {
        // 입력한 패스워드를 가져와서 리턴함
        return findViewById<EditText>(R.id.passwordEditText).text.toString()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // callback을 추가해서 결과를 넘김
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleSuccessLogin() {
        // 로그인이 성공했을 경우, 다시 한 번 확인, 그리고 DB에 저장함
        if(auth.currentUser == null) {
            Toast.makeText(this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        // 유저 아이디 받아옴, DB처리를 위해
        val userId = auth.currentUser?.uid.orEmpty()
        val currentUserDB = Firebase.database.reference.child("Users").child(userId) // Users의 데이터를 가져옴, 없으면 userId 추가하고 있으면 그 데이터 가져옴
        val user = mutableMapOf<String, Any>() // key-value 형태의 map으로 정보 저장
        user["userId"] = userId // userId 저장
        currentUserDB.updateChildren(user) // map으로 저장한 user를 DB에 저장함, 제일 상위의 리스트가 생겨 userId 추가됨

        finish()
    }
}