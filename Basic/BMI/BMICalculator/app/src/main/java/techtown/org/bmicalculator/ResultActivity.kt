package techtown.org.bmicalculator

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.pow

class ResultActivity : AppCompatActivity() {
    // 결과를 계산하는 액티비티, 액티비티를 직접 추가하지 않고 클래스 따로 만드는 방법, 그러기 위해서 직접 Manifest에 추가해야함

    // onCreate로 직접 오버라이드
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 레이아웃 만들고 직접 연결
        setContentView(R.layout.activity_result)

        // intent로 넘어온 값을 받음, name으로 받고 그 다음은 넘어온 값이 없을 경우를 설정함
        val height = intent.getIntExtra("height",0)
        val weight = intent.getIntExtra("weight",0)

        // 값 확인
//        Log.d("ResultActivity","height : $height , weight : $weight")

        // BMI 계산, 키를 m로 변환해서 계산함, pow로 제곱을 함, Double로 자동 치환해서 함 그냥 .만 붙이면 됨 자바는 Math.pow(height / 100.0, 2.0)으로 씀, 아래는 코틀린 방식
        val bmi = weight / (height / 100.0).pow(2.0)
        // when에서 나온 값을 바로 resultText로 보냄, BMI 기준에 따른 텍스트 연결함
        val resultText = when {
            bmi >= 35.0 -> "고도 비만"
            bmi >= 30.0 -> "중정도 비만"
            bmi >= 25.0 -> "경도 비만"
            bmi >= 23.0 -> "과체중"
            bmi >= 18.5 -> "정상체중"
            else -> "저체중"
        }

        // 결과를 나타내는 걸 연결하기 위해서 TextView를 가져옴
        val resultValueTextView = findViewById<TextView>(R.id.bmiResultTextView)
        val resultStringTextView = findViewById<TextView>(R.id.resultTextView)

        // 값을 넣어줌, String 형으로 넣어줌, 텍스트 표시를 위해
        resultValueTextView.text = bmi.toString()
        resultStringTextView.text = resultText
    }
}