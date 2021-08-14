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