## 비밀번호가 있는 다이어리
- NumberPicker를 통해서 비밀번호 입력을 하듯이 구현

- 비밀번호가 맞으면 그 다음 페이지로 넘어가 일기처럼 글을 작성할 수 있음

- 비밀번호 변경도 가능

- 비밀번호, 다이어리에 쓴 글에 대해서 SharedPreference를 통해서 그 데이터 값을 저장해두고 활용함

## 비밀번호 입력 화면

### 실행화면

![one](/Basic/Diary/img/one.png)

- 폰트의 경우 res 폴더의 font 폴더를 만들어 무료 폰트를 저장한 후 추가함
- themes에서 ActionBar를 보여주지 않기 위해서 NoActionBar를 바로 설정할 수도 있지만, 이 부분은 style 태그를 추가해서 처리함
- Manifest에서 직접 activity에 대해서 theme 설정을 추가함
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="techtown.org.diary">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Diary">
        <activity android:name=".DiaryActivity"
            android:theme="@style/Theme.Diary.NoActionBar"></activity>
        <!-- 액션바 없애기 위해서 스타일 태그 추가하고 직접 매니페스트에서 직접 설정 -->
        <activity
            android:name=".MainActivity"
            android:theme="@style/Theme.Diary.NoActionBar">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

- 기존의 MaterialComponents의 영향을 받아서 Button 같은 것이 배경이 안 바뀌는 경우도 있음, 이런 상태를 방지하기 위해서 AppCompatButton을 사용함
- ConstraintLayout의 속성을 잘 활용해서 UI 설계함, 기존에 설계시 Relative, Linear를 중첩해서 사용할 수 있는 경우를 좀 더 간결하게 활용함
- 속성 잘 활용하기
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#3F51B5"
    tools:context=".MainActivity">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginBottom="50dp"
        android:fontFamily="@font/bm_font"
        android:text="@string/title_text"
        android:textSize="30sp"
        android:textStyle="bold"
        app:layout_constraintBottom_toTopOf="@id/passwordLayout"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent" />

    <androidx.constraintlayout.widget.ConstraintLayout
        android:id="@+id/passwordLayout"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="#CDCDCD"
        android:padding="15dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_bias="0.45">


        <!-- Material의 영향을 받지 않는 AppCompatButton-->
        <androidx.appcompat.widget.AppCompatButton
            android:id="@+id/openButton"
            android:layout_width="40dp"
            android:layout_height="60dp"
            android:layout_marginEnd="10dp"
            android:background="#A3A3A3"
            app:layout_constraintBottom_toBottomOf="@id/numberPicker1"
            app:layout_constraintEnd_toStartOf="@id/numberPicker1"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="@id/numberPicker1" />

        <androidx.appcompat.widget.AppCompatButton
            android:id="@+id/changePasswordButton"
            android:layout_width="10dp"
            android:layout_height="10dp"
            android:layout_marginTop="10dp"
            android:background="@color/black"
            app:layout_constraintEnd_toEndOf="@id/openButton"
            app:layout_constraintStart_toStartOf="@id/openButton"
            app:layout_constraintTop_toBottomOf="@id/openButton" />

        <NumberPicker
            android:id="@+id/numberPicker1"
            android:layout_width="30dp"
            android:layout_height="120dp"
            android:background="#A3A3A3"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toStartOf="@id/numberPicker2"
            app:layout_constraintHorizontal_chainStyle="packed"
            app:layout_constraintStart_toEndOf="@id/openButton"
            app:layout_constraintTop_toTopOf="parent" />

        <NumberPicker
            android:id="@+id/numberPicker2"
            android:layout_width="30dp"
            android:layout_height="120dp"
            android:background="#A3A3A3"
            app:layout_constraintEnd_toStartOf="@id/numberPicker3"
            app:layout_constraintStart_toEndOf="@id/numberPicker1"
            app:layout_constraintTop_toTopOf="@id/numberPicker1" />

        <NumberPicker
            android:id="@+id/numberPicker3"
            android:layout_width="30dp"
            android:layout_height="120dp"
            android:background="#A3A3A3"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toEndOf="@id/numberPicker2"
            app:layout_constraintTop_toTopOf="@id/numberPicker1" />

    </androidx.constraintlayout.widget.ConstraintLayout>


</androidx.constraintlayout.widget.ConstraintLayout>
```

### 구현 코드
- numberPicker에서 숫자를 고른 뒤 SharedPreference에 그 값을 저장해서 비밀번호 바꾸는 기능, 다이어리 여는 버튼에 활용함
- 예외 처리를 하여서 AlertDialog도 활용함
- MainActivity.kt

```kotlin
package techtown.org.diary

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.edit

class MainActivity : AppCompatActivity() {

    // 사용할 View 컴포넌트들 초기화, by lazy를 통해서 View가 다 그려지면 아래와 같이 적용함
    private val numberPicker1: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker1)
            .apply {
                // apply를 통해서 NUmberPicker의 값을 초기화함
                minValue = 0
                maxValue = 9
            }
    }

    private val numberPicker2: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker2)
            .apply {
                minValue = 0
                maxValue = 9
            }
    }

    private val numberPicker3: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker3)
            .apply {
                minValue = 0
                maxValue = 9
            }
    }

    private val openButton: AppCompatButton by lazy {
        findViewById<AppCompatButton>(R.id.openButton)
    }

    private val changePasswordButton: AppCompatButton by lazy {
        findViewById<AppCompatButton>(R.id.changePasswordButton)
    }

    // 비밀번호 변경동안에 다른 것을 할 수 없게 예외처리를 위한 변수(Open 버튼 누르면 반응하면 안됨)
    private var changePasswordMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 직접 호출해서 초기화를 진행함(직접 코드로 사용하지 않더라도)
        numberPicker1
        numberPicker2
        numberPicker3

        // openButton 동작 이벤트 리스너설정, 기기에 저장된 패스워드 값과 비교를 함
        // 로컬 DB, 파일에 직접 적음 SharedPreference를 통해서
        openButton.setOnClickListener {

            if(changePasswordMode) {
                // 만일 변경중인 상태면 openButton을 눌러도 open 이벤트가 실행이 되지 않게 하기 위해서 설정함
                Toast.makeText(this,"비밀번호 변경 중입니다",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // password라는 파일을 적음, MODE_PRIVATE 다른 앱과 공유하지 않기 위해서 씀, key-value 방식으로 저장됨
            val passwordPreference = getSharedPreferences("password", Context.MODE_PRIVATE)

            // numberPicker에서 선택한 패스워드 값을 저장해둠, String으로 3개 숫자 나열함, 이를 비교
            // 띄어쓰기 없이 아래와 같이 선택한 숫자를 연결한 걸 다 합친 값을 저장함
            val passwordFromUser = "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"

            // SharedPreference에 있는 값과 numberPicker에 있는 값을 비교함, 디폴트 초기값도 정의함
            // 여기서 equals를 통해서 NumberPicker의 입력값과 같으면 패스워드 성공임
            if(passwordPreference.getString("password","000").equals(passwordFromUser)) {
                // 패스워드 성공, 다이어리 페이지를 열어주면 됨, 인텐트 처리함
                startActivity(Intent(this,DiaryActivity::class.java))
            } else {
                // 패스워드 실패, AlertDialog로 띄어줌
                showErrorAlertDialog()
            }
        }

        // 비밀번호 바꾸는 기능
        changePasswordButton.setOnClickListener {
            val passwordPreference = getSharedPreferences("password", Context.MODE_PRIVATE)
            val passwordFromUser = "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"

            if(changePasswordMode) {
                // 현재 비밀번호 변경 버튼을 눌러서 활성화된 상태
                // 그 상태이고 거기서 한 번 더 누름 -> 비밀번호를 저장함, sharedPrefence에 저장할 것임

                // 저정하기 위해서 edit을 함, 별도로 edit()을 열어서 하지 않고 ktx를 통해서 아래와 같이 람다형태로 가능함
                // 선택한 숫자를 받고 putString으로 넣음 SharedPreference에, commit 값을 true로 둬서 설정을 함, UI 스레드를 잠시 블락함, 데이터가 저장될때까지 기다림
                // UI 스레드에서는 너무 무거운 작업을 하면 오래 멈춰있어서 앱이 죽어버릴 수 있음 조심해야함
                passwordPreference.edit(true){
                    putString("password",passwordFromUser)
                }

                // 저장이 끝났으므로 false로 바꾸고 배경을 바꿈
                changePasswordMode = false
                changePasswordButton.setBackgroundColor(Color.BLACK)


            } else {
                // 현재 비밀번호 변경 버튼을 누르지 않은 상태
                // 눌렀으므로 true로 활성화됨, 비밀번호가 맞는지도 체크해야함, 비밀번호가 같아야 바꿀 수 있으니깐

                // 비밀번호 맞는지 체크하기 위해서 SharedPreference를 확인함

                if(passwordPreference.getString("password","000").equals(passwordFromUser)) {
                    // 비밀번호가 같음을 확인함, 비밀번호 변경가능
                    changePasswordMode = true // 비밀번호 같음을 확인해서 변경이 가능한 상태로 바꿈
                    Toast.makeText(this,"변경할 패스워드를 입력하세요",Toast.LENGTH_SHORT).show()

                    // 활성화 됨을 알려주기 위해서 색깔을 바꿔줌
                    changePasswordButton.setBackgroundColor(Color.RED)
                } else {
                    showErrorAlertDialog()
                }

            }
        }
    }

    // 동일 코드가 중복되어 사용됐으므로 함수로 빼서 활용함
    private fun showErrorAlertDialog() {
        AlertDialog.Builder(this)
            .setTitle("실패!!")
            .setMessage("비밀번호가 잘못되었습니다.")
            .setPositiveButton("확인"){ _ , _ ->
                // 인자가 2개라 생략할 수 없음(dialog, which), 여기서 동작을 하는게 없어 _ 처리함
                // positive, negative 처리 가능 확인-취소와 같이, 그리고 텍스트 설정후 버튼처리
            }
            .create()
            .show()
    }
}
``` 

## 다이어리 화면

### 실행화면

![one](/Basic/Diary/img/two.png)

- 간단하게 EditText를 match_constraint로 하여서 다이어리처럼 만듬

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#3F51B5"
    android:padding="24dp"
    tools:context=".DiaryActivity">

    <EditText
        android:id="@+id/diaryEditText"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:background="@color/white"
        android:gravity="start|top"
        android:padding="10dp"
        android:textColor="@color/black"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

### 구현 코드
- 다이어리에 입력한 텍스트는 SharedPreference로 보존함
- 추가적으로 입력하는 텍스트에 대해서 바뀔때마다 저장을 해줘야 하는데 일일이 입력때마다 할 수 없기 때문에 그 부분을 스레드를 활용함
- 수정이 됐을 때 Runnable 인터페이스를 활용해서 Handler를 통해서 저장되는데 있어서 딜레이를 줌
- DiaryActivity.kt
```kotlin
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
```