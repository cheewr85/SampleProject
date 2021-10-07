package techtown.org.tinder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    // 바로 auth 초기화
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        if (auth.currentUser == null) {
            // 만약 현재 유저가 없다면 로그인 정보가 없다면, 로그인이 되지 않았을 때 로그인 액티비티 실행하게함
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            // 만약 로그인이 된 상태면 Like Activity 실행함
            startActivity(Intent(this, LikeActivity::class.java))
            finish() // 메인은 끔
        }
    }
}