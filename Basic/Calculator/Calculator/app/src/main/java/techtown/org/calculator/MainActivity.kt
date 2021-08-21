package techtown.org.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.room.Room
import techtown.org.calculator.model.History
import kotlin.math.exp

class MainActivity : AppCompatActivity() {

    // 입력값을 나타내는 TextView
    private val expressionTextView : TextView by lazy {
        findViewById<TextView>(R.id.expressionTextView)
    }
    // 결과값을 나타내는 TextView
    private val resultTextView : TextView by lazy {
        findViewById<TextView>(R.id.resultTextView)
    }
    // history를 나타내는 부분, view에도 Visibility 기능이 있으므로 View로 설정
    private val historyLayout : View by lazy {
        findViewById<View>(R.id.historyLayout)
    }
    // ScrollView에 있는 LinearLayout
    private val historyLinearLayout : LinearLayout by lazy {
        findViewById<LinearLayout>(R.id.historyLinearLayout)
    }

    // 사전에 설정한 Room DB를 만든 걸 활용
    lateinit var db : AppDatabase

    // operator 입력 중인지를 확인, 그리고 operator가 이미 입력되었는지 확인
    private var isOperator = false
    private var hasOperator = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // AppDataBase를 만들기 위해서 할당함
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "historyDB"
        ).build() // AppDataBase를 반환을 하도록 설정을 함, 앞서 사전에 만든 것을 활용해서 만들어짐
    }

    fun buttonClicked(view: View) {
        // 숫자 등의 버튼을 구분하기 위해서 모든 버튼에 대해서 Id를 부여해서 적용함, 그러면 이미 onClick으로 View를 Binding 했으므로 Id만 확인하면 됨
        when(view.id) {
            // 만일 버튼이 눌렀다면 해당 버튼에 맞는 숫자 연결
            R.id.button0 -> numberButtonClicked("0")
            R.id.button1 -> numberButtonClicked("1")
            R.id.button2 -> numberButtonClicked("2")
            R.id.button3 -> numberButtonClicked("3")
            R.id.button4 -> numberButtonClicked("4")
            R.id.button5 -> numberButtonClicked("5")
            R.id.button6 -> numberButtonClicked("6")
            R.id.button7 -> numberButtonClicked("7")
            R.id.button8 -> numberButtonClicked("8")
            R.id.button9 -> numberButtonClicked("9")
            // 연산자의 경우
            R.id.buttonPlus -> operatorButtonClicked("+")
            R.id.buttonMinus -> operatorButtonClicked("-")
            R.id.buttonMulti -> operatorButtonClicked("*")
            R.id.buttonDivider -> operatorButtonClicked("/")
            R.id.buttonModulo -> operatorButtonClicked("%")
        }
    }

    // 숫자와 연산자에 대해서 버튼을 누르면 위에서 정의한 TextView에 연결할 것임
    // 여기서 제약조건으로 숫자는 15자리를 넘으면 안되고 연산자는 두 번 중복되게 누르면서 할 수 없음
    // 숫자버튼이 눌렸을 때를 처리하기 위한 함수
    private fun numberButtonClicked(number : String) {

        // operator를 입력하다 오면 띄어쓰기를 한 번 더 해야함, 입력을 숫자로 바꿈
        // 만일 operator를 입력하다가 왔다면
        if(isOperator) {
            expressionTextView.append(" ") // operator 입력하다가 숫자를 누른 것이므로 구분하기 위해서 빈값을 추가함, 숫자 입력을 위해서
        }
        // 이미 연산자 입력하다가 온 것이므로 숫자 입력으로 바뀐 상태니깐 false로 바꿈
        isOperator = false

        // TextView에 나타낼때 그리고 String으로 보여줄 때 숫자 연산자 숫자로 띄어쓰기로 구분할 것임(split 사용), 계산식을 저장
        val expressionText = expressionTextView.text.split(" ")
        // 숫자가 15자리를 넘어가지 않게 하기 위해서 처리함, last를 쓴 이유는 연산자가 입력되지 않는 경우에도 last가 숫자고 연산자가 입력되고 두번째 숫자여도 last가 숫자이므로
        if(expressionText.isNotEmpty() && expressionText.last().length >= 15){
            // 에러메시지를 통해 예외처리
            Toast.makeText(this,"15자리를 넘어서 입력할 수 없습니다.",Toast.LENGTH_SHORT).show()
            return // 더이상 로직이 실행되지 않게함
        } else if(expressionText.last().isEmpty() && number == "0"){
            // 맨 앞에 0을 입력하고 계속 입력하는 경우
            Toast.makeText(this,"0은 제일 앞에 올 수 없습니다.",Toast.LENGTH_SHORT).show()
            return // 더이상 로직이 실행되지 않게함
        }
        // 위에서 설정한 예외가 아닌 상황에는 정상적으로 숫자를 입력받는 상황임, 위에서 누른 number를 붙여줌 TextView에
        expressionTextView.append(number)

        // 결과값이 나오는 부분에 대해서 아래 구현한 계산을 가지고 바로 나오게끔 하면 됨
        resultTextView.text = calculateExpression()

    }

    // 연산자에 대해서 눌렀을 때 처리하기 위한 함수
    private fun operatorButtonClicked(operator : String) {
        if(expressionTextView.text.isEmpty()){
            // 맨 앞에 연산자가 올 수 없으므로 빈 상황에서 연산자를 입력하려고 하면 return을 해버림
            return
        }

        when {
            isOperator -> {
                // 연산자를 입력했는데 또 입력하려는 경우
                val text = expressionTextView.text.toString()
                // 이미 연산자를 입력해서 또 있는 상태이므로 연산자 부분을 떼고 새로 입력받은 연산자를 붙임
                expressionTextView.text = text.dropLast(1) + operator
            }

            hasOperator -> {
                // 이미 연산자를 입력되어 있는 경우
                Toast.makeText(this,"연산자는 한 번만 사용할 수 있습니다.",Toast.LENGTH_SHORT).show()
                return // 더이상 로직이 실행되지 않게함
            }

            else ->  {
                // is,has Operator가 false인 경우, 이 경우는 숫자만 입력한 경우임, 그러므로 이 경우엔 구분을 위해 앞에 띄어쓰기를 하고 연산자를 붙임
                expressionTextView.append(" $operator")
            }
        }
        // spannable 기능 연산자의 경우 초록색으로 따로 칠함, 해당 text를 받아서 처리함
        val ssb = SpannableStringBuilder(expressionTextView.text)
        // 이를 활용해서 연산자 자리에만 초록색으로 칠할 것임
        ssb.setSpan(
            // 연산자가 딱 한 번 밖에 올 수 밖에 없는 상황이라 마지막으로 확인을 함
            ForegroundColorSpan(getColor(R.color.green)),
            expressionTextView.text.length - 1,
            expressionTextView.text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        // expressionTextView를 다시 설정한 것이므로 ssb 값을 다시 넣어줌
        expressionTextView.text = ssb
        // 연산자가 입력된 상황이므로 눌렀다는 것은 true로 바꿔주면 됨
        isOperator = true
        hasOperator = true


    }

    fun clearButtonClicked(view: View) {
        // 단순하게 입력값 지우고 결과값 지우고 연산자를 입력했다는 state를 초기화하면 됨
        expressionTextView.text = ""
        resultTextView.text = ""
        isOperator = false
        hasOperator = false
    }
    private fun calculateExpression() : String {
        // expressionTextView에 있는 숫자 연산자 숫자를 가져와서 resultTextView에 넣기 위한 연산을 하고 그 값을 반환하는 함수
        val expressionTexts = expressionTextView.text.split(" ")

        if(hasOperator.not() || expressionTexts.size != 3) {
            // 만약 연산자가 입력되지 않고 숫자 연산자 숫자가 입력되지 않았다면
            return "" // 빈 문자열 반환
        } else if(expressionTexts[0].isNumber().not()||expressionTexts[2].isNumber().not()){
            // 만약 첫번째 부분, 세번째 부분 즉, 그 부분의 입력값이 숫자가 아니라면, 이때 숫자로 변환하려고 하면 에러가 발생하므로 예외처리 해야함, 아래의 구현한 확장함수 활용
            return "" // 빈 문자열 반환
        }
        // 위처럼 연산자가 입력되지 않거나 숫자가 입력되지 않거나 입력값이 숫자가 아니라면 정상적으로 처리 된 것이므로 숫자 연산자 숫자로 분류된 것
        val exp1 = expressionTexts[0].toBigInteger() // 첫번째로 입력받은 숫자
        val exp2 = expressionTexts[2].toBigInteger() // 두번째로 입력받은 숫자
        val op = expressionTexts[1] // 연산자

        return when(op) {
            // 연산에 맞춰서 계산하기
            "+" -> (exp1 + exp2).toString()
            "-" -> (exp1 - exp2).toString()
            "*" -> (exp1 * exp2).toString()
            "/" -> (exp1 / exp2).toString()
            "%" -> (exp1 % exp2).toString()
            else -> "" // 오류가 난 것이므로 빈값 반환
        }

    }
    fun resultButtonClicked(view: View) {
        //calcuateExpression과 유사한 연산을 하므로 표현이 같음
        val expressionTexts = expressionTextView.text.split(" ")

        if(expressionTextView.text.isEmpty() || expressionTexts.size == 1) {
            // 숫자만 들어온 경우 아무런 처리를 하지 않음
            return
        }
        if(expressionTexts.size != 3 && hasOperator) {
            // 입력을 다 받지 않은 경우 즉 연산자까지만 입력하고 두번째 숫자를 입력받지 않은 경우
            Toast.makeText(this,"아직 완성되지 않은 수식입니다.",Toast.LENGTH_SHORT).show()
            return // 더이상 로직이 실행되지 않게함
        }

        if(expressionTexts[0].isNumber().not()||expressionTexts[2].isNumber().not()){
            // 숫자가 정상적으로 치환이 되지 않은 경우, 발생해선 안되는 경우이지만 미리 만들어둠
            Toast.makeText(this,"오류가 발생했습니다.",Toast.LENGTH_SHORT).show()
            return // 더이상 로직이 실행되지 않게함
        }
        // 나중에 값을 DB에 저장하기 위해 입력받은 연산을 String으로 저장함
        val expressionText = expressionTextView.text.toString()
        // 결과값 저장
        val resultText = calculateExpression()

        // DB를 앞서 만든대로 History data에 담기 위해서 클래스를 만들었고 dao를 통해서 이 data를 담은 클래스를 DB로 활용하는 기능을 넣고 이를 실제 AppDatabase로 처리하여 활용하게끔 만듬
        // 여기서 DB작업은 Main쓰레드에서 하지 않고 새로운 쓰레드를 만들고 나서 해야함
        Thread(Runnable{
            // 메인쓰레드에는 무겁고 복잡한 작업을 하면 안됨 이런 DB 저장은 쓰레드를 활용해야함
            // 사전에 정의한 인터페이스 Dao에서 하나하나 저장하는 기능이 있었음, 이를 사용하면 됨
            // 이를 사용할 때 History 객체를 바로 인스턴스로 생성하고 해당 값들을 넣어둠, uid, expression, result를
            // 바로 위에 val 변수로 미리 저장한 것을 활용해서 넣어두면 됨, 왜냐하면 메인쓰레드가 먼저 실행될 지 현재 이 쓰레드가 먼저 실행될지 모르기 때문에
            // 아래와 같이 처리한다면 DB에 알아서 값이 insert가 됨
            db.historyDao().insertHistory(History(null, expressionText, resultText))
        }).start()

        // resultButton을 누르면 입력값에 해당 결과가 나오고 다시 연산을 하는 것이므로 아래와 같이 입력값에 결과값이 결과값은 초기화 됨
        // 계산결과값이 입력값하는곳에 올라가는 연산
        resultTextView.text = ""
        expressionTextView.text = resultText
        // 연산이 끝났으므로 초기화
        isOperator = false
        hasOperator = false
    }
    fun historyButtonClicked(view: View) {
        // 계산기에서 저장기록 버튼을 누르면 실행됨
        // DB에서 history값을 가져와서 historyLinearLayout에 보여주고 historyLayout을 보여주면 됨
        historyLayout.isVisible = true
        // 하위에 있는 View를 다 삭제함, 그리고 아래 forEach로 담아주기 위해서
        historyLinearLayout.removeAllViews()

        // DB의 저장된 값을 모두 가져오는 것, 이 역시 쓰레드를 열어서 사용해야함, 버튼 누르면 DB 값을 모두 가져옴
        Thread(Runnable {
            // getAll에 저장되어 있는 모든 DB를 가져오게 함
            // 저장시 최신저장이 나중에 저장되기 때문에 최신저장을 위에 보여주기 위해서 reversed를 함
            // 이 뒤집은 리스트를 하나씩 꺼내와서 HistoryLinearLayout에 넣어줄 것임
            db.historyDao().getAll().reversed().forEach {
                // 여기서 근데 LinearLayout에 담을 View가 없음, 이를 View로 붙여주기 위해서 LayoutInflater를 활용해서 붙여야함, 그러기 위해 layout 폴더에서 해당 View를 만들어줘야함
                // history_row를 통해서 Text를 담을 것임
                // 하지만 새로 생성한 Thread는 UiThread, MainThread가 아니므로 View를 그려주거나 수정하는 작업을 못하므로 UiThread를 불러와야함
                // UiThread를 열기 위한 방법
                runOnUiThread{
                    // history_row로 만든것을 불러오기 위해서 LayoutInflater를 활용함
                    val historyView = LayoutInflater.from(this).inflate(R.layout.history_row, null, false)
                    // historyView 즉, history_row에 만들어둔 expression, result 부분을 불러와서 해당 text를 설정함, 현재 db에서 History를 가르키므로 it을 통해 해당 값을 가져옴
                    historyView.findViewById<TextView>(R.id.expressionTextView).text = it.expression
                    historyView.findViewById<TextView>(R.id.resultTextView).text = "= ${it.result}"

                    // 앞서 정의한 history_row의 View를 LinearLayout에 vertical로 View를 보여주기 위해서 addView를 함, 그러면 이제 item처럼 쓸려고 만든 historyView가 착착 붙음
                    // ScrollView라서 많이해도 스크롤 가능함
                    historyLinearLayout.addView(historyView)

                }
            }

        }).start()

    }
    fun historyClearButtonClicked(view: View) {
        historyLinearLayout.removeAllViews() // historyLinearLayout에 있는 View를 다 삭제함

        // DB에 모든 기록 삭제, 여기서 계속 했지만 Thread를 열어주고 작업을 해야함
        Thread(Runnable{
            db.historyDao().deleteAll()
        }).start()

    }
    fun closeHistoryButtonClicked(view: View) {
        // historyLayout을 gone으로만 바꾸면 됨
        historyLayout.isVisible = false
    }


}
// 위에서 정의한 즉 String이 숫자인지 확인하기 위해서 String 클래스에서 확장함수로 직접 만듬
fun String.isNumber() : Boolean {
    // Number로 바꾸고 에러가 나면 Number가 아닌 것으로 둠, try-catch로 처리함
    return try {
        // 원래있던 String을 숫자로 바꿈, 범위를 넓게 잡기 위해 Big으로 씀
        this.toBigInteger()
        true // 성공적으로 변환했다는 것은 숫자인 것이므로 true 반환함
    } catch (e : NumberFormatException) { // 해당 toBigInteger를 타고 들어가면 Null, NumberFormat 에러가 뜸, 그 중 NumberFormat이 다뤄야할 에러임, 숫자로 제대로 치환되지 않은 것이므로
        false // 이때 false를 반환함
    }
}