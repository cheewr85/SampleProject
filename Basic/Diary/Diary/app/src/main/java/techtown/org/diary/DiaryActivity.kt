package techtown.org.diary

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener

class DiaryActivity : AppCompatActivity() {

    // 핸들러 사용하기 위해서 미리 정의함
    // Looper에서 MainLooper를 만들면 Main스레드와 연결된 핸들러 하나가 생성됨
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        // onCreate 밖에서 쓸 일이 없기 때문에 onCreate에 내부에서 사용
        val diaryEditText = findViewById<EditText>(R.id.diaryEditText)

        // 다이어리 내용을 저장할 SharedPreference 저장함
        val detailPreference = getSharedPreferences("diary", Context.MODE_PRIVATE)

        // detail이라고 저장한 preference에서 가져와서 보여줌, 저장된 것이 없다면 빈 스트링으로
        diaryEditText.setText(detailPreference.getString("detail",""))

        // 하지만 addText리스너 사용시 한 자 한 자 변경될 때마다 저장이 되기 때문에, 이를 스레드를 사용해서 멈칫했을 때도 저장하게끔 함
        // 스레드 기능 사용을 위해서 스레드에 넣는 Runnable 인터페이스를 사용함
        val runnable = Runnable {
            // commit을 true로 주면 UI를 기다렸지만, 수시로 백그라운드에서 글을 저장을 하는 것임
            // commit을 하지 않고 스레드에서 저장을 계속 하게끔 비동기로 넘겨버림, Main에서는 commit으로 블락을 했지만 너무 오래걸리면 죽어버리기때문에
            // 스레드를 만들고 runnable과 handler를 통해서 처리한 것임 혹은 apply를 쓰면 됨
            getSharedPreferences("diary", Context.MODE_PRIVATE).edit{
                // 수정이 됐다면 detail에 저장이 됨
                putString("detail", diaryEditText.text.toString())
            }
        }

        // 내용이 수정이 될 때마다 값을 저장함, 텍스트 변경될 때마다 아래의 리스너가 호출이 됨
        // UI 관리는 UI,Main 스레드에서 처리함
        // 핸들러 사용함 -> 스레드 열었을 때 새로운 스레드가 나옴 이건 UI, main 스레드가 아님, 이때 연결해줘야할 때가 있음, UI를 못 바꿔주므로
        // 핸들러를 통해서 새로 만든 스레드와 UI 스레드를 연결해줌
        // Activity, View 등에도 Handler가 존재함, 여기서 postDelay, 몇 초후에 스레드를 실행시키는 기능을 구현함
        diaryEditText.addTextChangedListener{
            // runnable에서 스레드에서 일어나는 일은 구현을 해둠
            // postDelay로 몇 초후에 runnable을 실행하게끔 함, 500밀리초에 한 번씩 runnable이 실행됨
            // 0.5초 이전에 아직 실행되지 않고 있는 runnable이 있으면 지워주기 위해서 removeCallback을 함
            // 이렇게 처리하면 한글자 한글자 바로 저장되지 않고 사용자의 입력하는 잠시 멈칫하거나 그런 타이밍에 저장을 할 수 있음
            handler.removeCallbacks(runnable)
            handler.postDelayed(runnable, 500)
        }
    }
}