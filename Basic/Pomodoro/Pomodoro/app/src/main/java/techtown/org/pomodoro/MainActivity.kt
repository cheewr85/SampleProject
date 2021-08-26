package techtown.org.pomodoro

import android.annotation.SuppressLint
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val remainMinutesTextView : TextView by lazy {
        findViewById<TextView>(R.id.remainMinutesTextView)
    }

    private val remainSecondsTextView : TextView by lazy {
        findViewById<TextView>(R.id.remainSecondsTextView)
    }

    private val seekBar : SeekBar by lazy {
        findViewById<SeekBar>(R.id.seekBar)
    }

    // 만일 SeekBar를 재조작해서 만질 경우 해당 부분에 대해서 타이머가 다시 초기화하고 돌아가야하기 때문에 이를 처리하기 위해서 변수로 선언함
    // SeekBar를 통해서 CountDown이 시작되는 것이므로 초기에는 null로 초기화함
    private var currentCountDownTimer: CountDownTimer? = null

    // SoundPool 선언과 함께 초기화, 기본속성 가지고 활용이 용이하므로 바로 빌드함
    private val soundPool = SoundPool.Builder().build()
    // 저장한 SoundId를 담기위한 변수 선언
    private var tickingSoundId : Int? = null
    private var bellSoundId : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        initSounds()
    }

    override fun onResume() {
        super.onResume()
        // 다시 실행시 다시 사운드 시작시킴, 백그라운드 진입하고 다시 실행시키면 소리가 다시 남
        soundPool.autoResume()
    }

    override fun onPause() {
        super.onPause()
        // 앱이 화면에서 보이지 않으면 사운드를 일시정지시킴
        soundPool.autoPause()
    }

    // 사운드 파일은 그 용량이 많이 나감, 그러므로 이를 안 쓸 경우 메모리에서 해제해주는 것이 중요함
    override fun onDestroy() {
        super.onDestroy()
        // 더 이상 앱을 사용하지 않을 때를 가정해서 사운드를 해제함
        soundPool.release()
    }

    // 각각의 View에 있는 리스너와 실제 로직을 연결함
    private fun bindViews() {

        seekBar.setOnSeekBarChangeListener(
                // SeekBar를 활용하기 위한 메소드를 사용함, 해당 인터페이스가 정의한 메소드를 가져와서 적용함
                object : SeekBar.OnSeekBarChangeListener{
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        // 유저가 컨트롤해서 바뀐건지, 코드상으로 건드려서 바뀐건지 알 수 있음
                        // 하지만 지금은 그 유무에 상관없이 progress를 알고 활용하기 위해 전달됨, 아래 메소드 활용함
                        if(fromUser) { // 초기화 과정에서 00으로 되고 이상하게 처리되는데 이는 유저가 건드렸을때만으로 둔다면 됨
                            // 이게 코드상인지 유저가 건드려서인지 알 수가 없기때문에 설정을 그렇게 함
                            updateRemainTime(progress * 60 / 1000L)
                        }
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        // 만약 CountDown 하고 있는 와중임에도 다시 시작하기 위해서 SeekBar를 건드린다면 이를 취소시킴
                        stopCountDown()
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        // SeekBar를 건드리고 있다가 뗐을 때 타이머가 실행함, 그런 상황이 바로 이 메소드에 해당함, 이때 SeekBar 갱신을 설정함
                        // 그냥 설정하면 SeekBar가 null인 경우때문에 예외가 나오는데 이를 방지하기 위해서 null일 경우 처리를 확실히 함
                        seekBar ?: return // null인 경우 카운트다운을 아예 진행을 안 하게 함

                        // progress가 0일땐 끝나고 새로 시작하면 안되니깐 소리가 나면 안됨
                        if(seekBar.progress == 0){
                            stopCountDown()
                        } else {
                            // 0이 아니면 새롭게 시작
                            startCountDown()
                        }
                    }

                }
        )
    }

    // 사운드 파일 실행전 로드하기 위한 메소드
    private fun initSounds() {
        // SoundPool을 통해서 로드할 것임, 로드된 SoundId를 저장함
        tickingSoundId = soundPool.load(this, R.raw.timer_ticking, 1)
        bellSoundId = soundPool.load(this, R.raw.timer_bell, 1)
    }

    // CountDownTimer를 만드는 메소드(리턴은 CountDownTimer), 인자로 몇 밀리세킨드 뒤에 받을지 받음, 아래와 같이 기존 함수형과 다르게 바로 받을 수 있음
    private fun createCountDownTimer(initialMillis : Long) =
        object : CountDownTimer(initialMillis, 1000L){// 1초마다 받으므로 범주는 1초로
            // 추상 클래스이므로 메소드를 구현해줘야함
            override fun onTick(millisUntilFinished: Long) {
            // onTick이 1초마다 달라지는 것이므로 메소드를 사용해서 View를 갱신하게 만듬
                updateRemainTime(millisUntilFinished)
                updateSeekBar(millisUntilFinished)
            }

            override fun onFinish() {
                completeCountDown()
            }
        }

    // SeekBar를 뗐을 때 CountDown을 시작하는데 이 로직이 너무 SeekBar에 많이 몰려서 추상화함
    private fun startCountDown() {
        // 다시 시작하는 경우를 생각해서 변수로 선언한 곳에 할당하고 시작함
        currentCountDownTimer = createCountDownTimer(seekBar.progress * 60 * 1000L)// Milliseconds로 만들어서 넘겨줘야함
        currentCountDownTimer?.start()
        // 카운트다운 시작과 동시에 Soundpool도 실행시킴, let을 통해 null이 아닐 경우에만 호출하게 함
        tickingSoundId?.let { soundId ->
            soundPool.play(soundId, 1F, 1F, 0, -1, 1F) // 사운드 실행 조건을 검
        }
    }

    // 카운트다운을 아예 종료하는 로직, 소리 역시 아예 종료시킴
    private fun stopCountDown() {
        currentCountDownTimer?.cancel()
        currentCountDownTimer = null
        soundPool.autoPause()
    }

    // onFinish에 너무 많은 로직이 있어서 한 번에 추상화시킴
    private fun completeCountDown() {
        // 볼 것도 없이 끝난것이므로 다 0으로 설정해줌
        updateRemainTime(0)
        updateSeekBar(0)
        // 모두 다 종료됐을 때 bell 소리가 나게함, 여기서 ticking 소리를 일시정지시키고 실행시켜야함
        soundPool.autoPause()
        bellSoundId?.let { soundId ->
            soundPool.play(soundId, 1F, 1F, 0, 0, 1F)
        }
    }
    // 1초마다 onTick에서 UI를 갱신해주기 위한 메소드
    @SuppressLint("SetTextI18n")
    private fun updateRemainTime(remainMillis: Long) {
        // Millis를 가공해서 TextView를 갱신해줘야함
        val remainSeconds = remainMillis / 1000 // 1000millis가 1초이기 때문에 1000으로 나눠주면 초가 됨

        remainMinutesTextView.text = "%02d'".format(remainSeconds / 60) // remainSeconds는 총 시간초이기 때문에 이를 60으로 나눈 값이 minutes임
        remainSecondsTextView.text = "%02d".format(remainSeconds % 60) // 남은 총 시간을 초로 표현한 것이므로 60으로 남은 나머지가 진짜 시간형태로 표시할 초가 됨
    }

    // SeekBar 역시 시간에 따라 줄어들기 때문에 그 UI도 바뀌게끔 해줘야함
    private fun updateSeekBar(remainMillis : Long) {
        // SeekBar는 고민할 필요없이 분이기 때문에 그에 맞게 나눠주면 됨
        seekBar.progress = (remainMillis / 1000 / 60).toInt() // 타입이 Long이므로 progress는 Int이므로 변환해줄 필요가 있음
    }
}