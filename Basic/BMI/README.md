## BMI 계산기
- 키, 체중을 입력해서 BMI를 계산해서 현재 상태를 알려주는 간단한 계산기

- 간단하게 레이아웃 설정하고 성질을 활용하며 인텐트 처리와 when 분기문 사용

## 키, 체중 입력 화면

### 실행화면
![one](/SampleProject/Basic/BMI/img/one.png)

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp"
    tools:context=".MainActivity">

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="@string/height"
        android:textColor="@color/custom_black"
        android:textSize="20sp"
        android:textStyle="bold" />

    <EditText
        android:id="@+id/heightEditText"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="10dp"
        android:inputType="number" />

    <TextView
        android:id="@+id/textView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="30dp"
        android:text="@string/weight"
        android:textColor="@color/custom_black"
        android:textSize="20sp"
        android:textStyle="bold" />

    <EditText
        android:id="@+id/weightEditText"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="10dp"
        android:ems="10"
        android:inputType="number" />

    <Button
        android:id="@+id/resultButton"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="50dp"
        android:text="@string/confirm" />


</LinearLayout>
```

### 구현 코드
- 데이터를 입력받고 버튼 이벤트 처리를 한 다음, 인텐트로 입력값을 넘겨줌

- MainActivity.kt
```kotlin
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
```

## 결과 출력 화면

### 실행화면
![two](/SampleProject/Basic/BMI/img/two.png)

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:gravity="center"
    android:orientation="vertical">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="center"
        android:padding="10dp"
        android:orientation="horizontal">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/bmiTitle"
            android:textSize="20sp"/>

        <TextView
            android:id="@+id/bmiResultTextView"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textSize="20sp"
            tools:text="23.11111" /><!--앱 실행시 나오지 않고 XML툴에서만 미리 볼 수 있음-->


    </LinearLayout>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="center"
        android:padding="10dp"
        android:orientation="horizontal">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/resultTitle"
            android:textSize="20sp"/>

        <TextView
            android:id="@+id/resultTextView"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            tools:text="과체중입니다."
            android:textSize="20sp"/>

    </LinearLayout>

</LinearLayout>
```

### 구현 코드
- 넘겨 받은 데이터를 받고 BMI를 계산하고 그 값에 따라서 BMI 계산값과 결과에 해당하는 텍스트를 출력함

- ResultActivity.kt
```kotlin
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
```