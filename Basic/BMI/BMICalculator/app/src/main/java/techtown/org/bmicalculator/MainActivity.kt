package techtown.org.bmicalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // findViewById는 View를 반환함, 이게 무슨 View인지는 모름
        // heightEditText는 EditText 타입으로 선언이 되었으므로 알아서 EditText로 적용됨
        // 그렇지 않으면 weightEditText와 같이 <> 표시와 함께 어떤 타입인지 명시해줘야함
        val heightEditText : EditText = findViewById(R.id.heightEditText)
        val weightEditText = findViewById<EditText>(R.id.weightEditText)

        val resultButton = findViewById<Button>(R.id.resultButton)

        // resultButton 이벤트 처리
        // 인터페이스를 사용하는 방법
//        resultButton.setOnClickListener()
        // 람다형식 -> 람다가 정확하게 뭘까?(코틀린과 자바 비교)
        resultButton.setOnClickListener {
            // 테스트를 위해 로그를 찍음, d는 디버그를 의미함, 다양한 상태가 있음
//            Log.d("MainActivity", "ResultButton이 클릭되었습니다.")

            // 빈 값일 경우 빈 값이 입력되었다고 알림, 비어있을 경우 true 반환, 아니면 false 반환
            if (heightEditText.text.isEmpty() || weightEditText.text.isEmpty()){
                // 토스트 메시지로 Alert 메시지를 띄움
                Toast.makeText(this,"빈 값이 있습니다.",Toast.LENGTH_SHORT).show()

                // 빈 값이면 그냥 끝내야함, 즉 값을 받아올 필요가 없음
                // setOnClickListener를 나감
                return@setOnClickListener
            }

            // 빈값이 아닌 경우 위의 예외처리를 했으므로 절대 빈값이 올 수 없음
            // 입력받은 값을 받아옴 text를 Int로 변환해서 받아야함
            val height : Int = heightEditText.text.toString().toInt()
            val weight : Int = weightEditText.text.toString().toInt()

            // 입력값 로그로 확인함
//            Log.d("MainActivity","hegiht : $height weight : $weight")

            // 결과를 출력하는 화면으로 넘어가기 위해서 인텐트 사용
            // 이전은 즉 현재 Activity임(this), 넘어갈 화면 체크 ResultActivity
            val intent = Intent(this, ResultActivity::class.java)

            // 실질적으로 데이터는 intent에서 Android System으로 넘어가서 가고자 하는 Activity를 찾아서 넘어감(manifest에 있어야 넘어감)
            // onCreate에 Intent에 담아서 넘겨줌, 키와 체중을 인텐트에 담으면 됨
            // Extra로 height과 weight을 넘김, 이를 받을 액티비티에서 getIntent로 넘겨받음
            intent.putExtra("height", height)
            intent.putExtra("weight",weight)

            // intent로 넘겨줘서 Result를 실행하고 싶에 넘어가게함(버튼 리스너에 있으므로 버튼을 누르면 넘어감)
            startActivity(intent)
        }
    }
}