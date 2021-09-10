package techtown.org.pushalarm

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private val resultTextView: TextView by lazy {
        findViewById(R.id.resultTextView)
    }
    
    private val firebaseToken: TextView by lazy {
        findViewById(R.id.firebaseTokenTextView)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFirebase()
        updateResult()
    }

    // SingleTop으로 FLAG 설정했으므로 중복으로 뜨지 않으므로 갱신이 됨
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        setIntent(intent) // 새로 들어온 것으로 교체해줘야함
        updateResult(true) // FLAG설정에 따라 하나만 있게 하므로 이 메소드가 호출되면 갱신된것임
    }

    // 알람메시지를 사용하는 간단한 메소드
    private fun initFirebase() {
        // 현재 등록한 토큰을 가져옴
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                // 정상적으로 토큰을 받았다면 앞서 선언한 TextView에 설정함
                if(task.isSuccessful) {
                    firebaseToken.text = task.result
                }
            }
    }

    // 그냥 들어왔을때와 notification 눌러서 들어왔을 때 구분하는 메소드
    @SuppressLint("SetTextI18n")
    private fun updateResult(isNewIntent: Boolean = false) {
        // isNewIntent가 true이면 갱신된 것, false면 onCreate 실행된 것임, 이때 Intent로 타입을 넘겼으므로 해당 타입을 체크하고 넘김
        resultTextView.text = (intent.getStringExtra("notificationType") ?: "앱 런처") +
                if(isNewIntent) {
                    // 인텐트에 값이 있고 넘어오면 널도 아니고 타입에 맞게 갱신한것이고
            "(으)로 갱신했습니다."
        } else {
            // 인텐트에 값이 없다면 그냥 실행한 것이라고 볼 수 있음
            "(으)로 실행했습니다."
        }
    }
}
