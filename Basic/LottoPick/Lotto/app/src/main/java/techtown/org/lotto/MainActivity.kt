package techtown.org.lotto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible

class MainActivity : AppCompatActivity() {

    // 버튼등 사용할 뷰들에 대한 초기화 작업
    private val clearButton: Button by lazy {
        findViewById<Button>(R.id.clearButton)
    }

    private val addButton: Button by lazy {
        findViewById<Button>(R.id.addButton)
    }

    private val runButton: Button by lazy {
        findViewById<Button>(R.id.runButton)
    }

    private val numberPicker: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker)
    }

    // 순차적으로 쌓이는 것이므로 List에 담아서 활용함, 인자로 TextView로 받음
    private val numberTextViewList: List<TextView> by lazy {
        listOf<TextView>( // 6개의 TextView가 아래의 차례대로 들어가서 초기화 됨 리스트로
            findViewById<TextView>(R.id.firstNumberTextView),
            findViewById<TextView>(R.id.secondNumberTextView),
            findViewById<TextView>(R.id.thirdNumberTextView),
            findViewById<TextView>(R.id.fourthNumberTextView),
            findViewById<TextView>(R.id.fifthNumberTextView),
            findViewById<TextView>(R.id.sixthNumberTextView)
        )
    }

    private var didRun = false // 이미 자동생성시작을 눌러서 더이상 번호를 추가할 수 없는 경우를 확인하기 위한 변수

    private val pickNumberSet = hashSetOf<Int>() // 중복된 숫자가 추가되는 것을 방지하기 위해 Set으로 설정정

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // numberPicker의 범주 설정하기 최소~최대 범위 1~45로 설정함
        numberPicker.minValue = 1
        numberPicker.maxValue = 45

        initRunButton()
        initAddButton()
        initClearButton()
    }

    // 자동 생성 시작 버튼을 누르면 리스트를 생성해서 45개의 랜덤한 숫자를 가져오는 역할을 하는 함수
    private fun initRunButton() {
        runButton.setOnClickListener {
            val list = getRandomNumber() // 랜덤한 숫자 6개를 리스트로 가져오는 함수를 사용해서 6개짜리 리스트를 가져옴

            // 자동 생성시 번호 추가를 할 수가 없으므로 didRun을 true로 초기화
            didRun = true

            // 텍스트뷰에 적용해서 띄우게 함, index, number가 둘 다 리턴하게함
            list.forEachIndexed { index, number ->
                val textView = numberTextViewList[index]

                // 넘겨받은 숫자에 대해서 text로 받아서 나타낸 다음 보이게 함
                textView.text = number.toString()
                textView.isVisible = true

                // 텍스트 뷰 세팅될 때 숫자에 범위에 맞는 배경이 되도록 설정을 함
                setNumberBackground(number, textView)
                // 기존의 중복코드를 함수로 정리함
//                when(number) {
//                    in 1..10 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_yellow)
//                    in 11..20 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_blue)
//                    in 21..30 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_red)
//                    in 31..40 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_gray)
//                    else -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_green)
//                }
            }

            // 제대로 출력하는지 확인을 함, 로그를 통해서
//            Log.d("MainActivity", list.toString())
        }
    }

    // 수동으로 번호를 NumberPicker에서 선택해서 추가하는 버튼
    private fun initAddButton() {
        addButton.setOnClickListener {
            // addButton 눌렀을 때 이미 자동생성 시작을 해서 내가 버튼을 더 추가할 수 없을 때
            if (didRun) {
                Toast.makeText(this, "초기화 후에 시도해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // 이후 과정을 실행하지 않게 설정함, setOnClickListener를 리턴함(initAddButton인지 이건지 모르니깐)
            }

            // 5개까지만 선택이 가능하므로 그 이상을 입력 받을 경우 더 추가할 수 없게 처리함
            if (pickNumberSet.size >= 5) {
                Toast.makeText(this, "번호는 5개까지만 선택할 수 있습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 이미 선택한 번호를 또 선택한 경우 처리할 수 없게함
            if (pickNumberSet.contains(numberPicker.value)) { // numberPicker에서 선택한게 set에 있는지 확인함, 있으면 예외처리
                Toast.makeText(this, "이미 선택한 번호입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 이 이후는 우리가 선택한 번호를 추가한 것임
            // get을 써도 되지만 위에서 중복처리를 Set을 통해서 확인했으므로 Set이 하나도 없으면 처음부터인거고 3개가 있으면 그 다음부터 해야함
            // 그래서 Set 사이즈가 지금 초기화하는 데이터의 위치가 됨
            val textView = numberTextViewList[pickNumberSet.size]
            textView.isVisible = true // 텍스트 뷰를 보이게 함 추가 됐으므로
            textView.text = numberPicker.value.toString() // 해당 고른 숫자를 text로 나타냄

            // 텍스트 뷰 세팅될 때 숫자에 범위에 맞는 배경이 되도록 설정을 함
            setNumberBackground(numberPicker.value, textView)
            // 기존에는 아래와 같이 한 것을 중복코드 이므로 함수로 빼서 사용
//            when(numberPicker.value) {
//                in 1..10 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_yellow)
//                in 11..20 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_blue)
//                in 21..30 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_red)
//                in 31..40 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_gray)
//                else -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_green)
//            }

            pickNumberSet.add(numberPicker.value) // textView에 보이게 추가하고 그 값을 Set에 추가함
        }
    }

    // 텍스트 뷰 배경 설정이 when 문을 활용하여 숫자의 범위에 맞게 설정하는 것이 중복되게 사용됨, 중복된 코드이므로 이를 함수로 빼서 처리할 수 있음
    // 숫자와 텍스트 뷰를 인자로 받음
    private fun setNumberBackground(number : Int, textView: TextView) {
        // number의 값에 따라 textView를 설정하는 함수로 활용가능
        when(number) {
            in 1..10 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_yellow)
            in 11..20 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_blue)
            in 21..30 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_red)
            in 31..40 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_gray)
            else -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_green)
        }
    }

    // 숫자 생성과 추가된걸 아예 초기화하는 버튼
    private fun initClearButton() {
        clearButton.setOnClickListener {
            pickNumberSet.clear()
            numberTextViewList.forEach {
                // 앞에서부터 하나씩 꺼내서 순차적으로 확인할 수 있음
                it.isVisible = false
            }

            didRun = false
        }
    }

    // 자동생성 알고리즘은 1~45의 숫자를 리스트에 넣어서 셔플을 해서 그 중 0~6까지의 인덱스의 숫자를 보여주는 것, 셔플 통해 랜덤하게 함
    // 그러한 기능을 하는 함수
    private fun getRandomNumber(): List<Int> {

        // 1~45가 자동으로 생성되어 초기화 된 리스트
        val numberList = mutableListOf<Int>()
            .apply {
                for (i in 1..45) {
                    // 번호를 수동으로 추가하고 그 다음에 자동으로 추가할 때 해당 번호, 선택한 번호를 제외시킴 중복임을 피하기 위해서 continue로 넘어감
                    if(pickNumberSet.contains(i)) {
                        continue
                    }

                    this.add(i)
                }
            }
        // 순서를 섞어줌, 셔플함
        numberList.shuffle()

        // 그 섞은 숫자 중 6개의 랜덤한 숫자를 가져감, 그걸 리턴함, 6개 숫자를 추출함(0부터 6까지 자름 -> 6개(from ~ to))
        // 이미 선택한 번호를 toList로 먼저 추가를 한 다음에 위에서 수동으로 추가하는 경우가 있기 때문에 그 size만큼 제외함
        val newList = pickNumberSet.toList() + numberList.subList(0, 6 - pickNumberSet.size)

        return newList.sorted() // 랜덤해서 뽑은 숫자를 오름차순으로 정렬해서 리턴하게 함
    }
}