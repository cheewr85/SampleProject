## 녹음기
- 흔히 알고 있는 녹음기를 구현함

- 녹음을 하기 전과 녹음 중인 상태와 녹음 후 이를 재생할 수 있는 상태로 나뉨

- 이 녹음을 초기화 할 수 있음

- 그 외에 녹음이 잘 진행되고 있는지에 대해서 시각화를 함

## 메인화면
- 전형적인 녹음기 앱을 만듬 

- 추가적으로 시각화하는 부분이 있음

- RESET 버튼의 경우 RESET을 하는 경우가 아니라면 활성화되어 있지 않음

- 3가지의 CustomView를 구현함

- 녹음 상태, 재생 상태에 따라서 녹음 아이콘이 바뀌고 녹음하는 시간을 체크하는 부분 역시 시간 계산에 맞춰서 보여주기 위해서 만들었고, 녹음 시각화 부분도 녹음하는 음량에 따라 다르게 시각화 하기 위해서 구현함

### 실행화면
![one](/Basic/Recorder/img/one.png)
![one](/Basic/Recorder/img/two.png)

- 녹음을 시각화한 부분, 시간 체크하는 부분, 녹음 버튼 모두 CustomView로 구현함
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <techtown.org.recorder.SoundVisualizerView
        android:id="@+id/soundVisualizerView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:layout_marginBottom="10dp"
        app:layout_constraintBottom_toTopOf="@id/recordTimeTextView"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <Button
        android:id="@+id/resetButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="RESET"
        app:layout_constraintBottom_toBottomOf="@id/recordButton"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toLeftOf="@id/recordButton"
        app:layout_constraintTop_toTopOf="@id/recordButton"
        tools:ignore="HardcodedText" />

    <techtown.org.recorder.CountUpView
        android:id="@+id/recordTimeTextView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginBottom="20dp"
        android:text="00:00"
        android:textColor="@color/white"
        app:layout_constraintBottom_toTopOf="@id/recordButton"
        app:layout_constraintLeft_toLeftOf="@id/recordButton"
        app:layout_constraintRight_toRightOf="@id/recordButton"
        tools:ignore="HardcodedText" />

    <!--앞서 클래스로 정의한 RecordButton을 가져와서 사용할 수 있음-->
    <techtown.org.recorder.RecordButton
        android:id="@+id/recordButton"
        android:layout_width="100dp"
        android:layout_height="100dp"
        android:layout_marginBottom="50dp"
        android:padding="25dp"
        android:scaleType="fitCenter"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

### RecordButton CustomView
- 이전에 State 클래스를 알아야함 녹음 상태를 유기적으로 관리하기 위해서 열거형 클래스로 구현함, 이 상태에 따라 UI가 달라지므로 별도로 만듬
```kotlin
package techtown.org.recorder

// 녹음기의 상태를 나타내기 위해 만든 열거형 클래스
// 상태를 미리 지정해줌, 녹음 상태마다 보여지는 버튼이 달라지기 위해서 상태값에 따라서 UI가 달라지기 때문에 미리 정의함
enum class State {
    BEFORE_RECORDING,
    ON_RECORDING,
    AFTER_RECORDING,
    ON_PLAYING
}
```

- 앞서 설명했듯이, 상태에 따라 Icon을 바꿀 것임, 녹음 상태와 재생 상태를 구분하기 위해서

- 여기서 중요한 것은 클래스를 마들고 Context, AttributeSet을 파라미터로 기본적으로 받아줘야하고 커스텀을 할 베이스를 상속받고 구현해줘야함

- 버튼에 대해서는 기본 버튼 모양이 아닌 drawable에서 직접 shape을 그려서 초기화시킴
```kotlin
package techtown.org.recorder

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton

/*
CustomView로써 xml 파일에서 수정을 하게 하기 위한 클래스
그러면 이제 이 클래스에서 상태에 따라 UI를 수정하게끔 할 수 있음, 아래와 같이 ImageButton을 상속받고
미리 정의한 State 상태에 따라서 버튼을 바꾸면 됨
 */
class RecordButton( // CustomView로 활용하기 위해서 아래와 같이 파라미터를 기본적으로 받아줘야함
        context: Context,
        attrs: AttributeSet
): AppCompatImageButton(context,attrs) {

    init{
        // 동그란 버튼으로 하기 위해서 초기화를 함
        setBackgroundResource(R.drawable.shape_oval_button)
    }
    // State에 따라서 recordButton의 아이콘을 변경함
    // 앞서 정의한 State 클래스를 매개변수로 받아와서 when 분기문을 통해서 상태에 따라 Vector Asset으로 추가한 아이콘을 변경함
    fun updateIconWithState(state: State) {
        when(state) {
            State.BEFORE_RECORDING -> {
                setImageResource(R.drawable.ic_record)
            }
            State.ON_RECORDING -> {
                setImageResource(R.drawable.ic_stop)
            }
            State.AFTER_RECORDING -> {
                setImageResource(R.drawable.ic_play)
            }
            State.ON_PLAYING -> {
                setImageResource(R.drawable.ic_stop)
            }
        }
    }
}
```

### CountUpView CustomView
- 녹음하는 시간에 대해서 현재 시간 기준 몇 초가 흘렀는지 직접 체크하기 위해서 CustomView로 만듬

- 물론 Main에서 만들어서 이를 처리할 수 있지만, 그렇게 한다면 상당히 Main에서 하는 일도 많을 뿐더러 쓰레드 처리를 하기 때문에 불필요함

- 그리고 시간체크를 현재 시간 기준으로 하는 계산을 하기 때문에 어차피 TextView로 구현할 것이고 이 기능이 명확하기 때문에 이를 CustomView로 만듬

- 상속받고 CustomView로써 활용하느 것은 위에서 설명한 바와 동일함, 파라미터로 동일하게 받고 상속받음

- 이 CustomView에선 시간을 직접 쓰레드를 통해서 계산을 하고 View를 업데이트 함

```kotlin
package techtown.org.recorder

import android.annotation.SuppressLint
import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/*
녹음한 것을 시간을 체크해 이를 시각화하기 위한 CustomView
 */
class CountUpView(
        context: Context,
        attrs: AttributeSet? = null
): AppCompatTextView(context, attrs) {

    private var startTimeStamp: Long = 0L

    private val countUpAction: Runnable = object : Runnable {
        override fun run() {
            // 시작시 타임 스탬프를 찍고 1초마다 반복하게 함, 현재 시간을 가져와서 몇 초가 흘렀는지 보고 텍스트에 보이게 함
            val currentTimeStamp = SystemClock.elapsedRealtime()

            // 얼마의 시간 차이가 나는지 알 수 있음
            val countTimeSeconds = ((currentTimeStamp - startTimeStamp)/1000L).toInt()
            // 이를 가지고 시간을 계산해서 업데이트함
            updateCountTime(countTimeSeconds)
            // 1초씩 딜레이시켜서 실행함
            handler?.postDelayed(this, 1000L)
        }
    }

    fun startCountUp() {
        startTimeStamp = SystemClock.elapsedRealtime()
        handler?.post(countUpAction)
    }

    fun stopCountUp() {
        handler?.removeCallbacks(countUpAction)
    }

    // 초기화 하는 메서드
    fun clearCountTime() {
        updateCountTime(0)
    }

    @SuppressLint("SetTextI18n")
    private fun updateCountTime(countTimeSeconds: Int) {
        val minutes = countTimeSeconds / 60
        val seconds = countTimeSeconds % 60

        text = "%02d:%02d".format(minutes, seconds)
    }
}
```

### SoundVisualizerView CustomView
- 녹음이 잘 되고 있는지 녹음하는 부분에 대해서 시각화를 한 CustomView임

- 가장 중요한 부분은 시각화를 위해서 일단 View 클래스를 상속받고 직접 Canvas 클래스와 Paint를 통해서 그리고 onDraw를 오버라이딩해서 그려야함

- 여기서 하나 알아야 할 부분은 그릴 때 포인트임, 어느정도 그리는 부분에 대해서 합의가 있어야 함, 즉 기준을 가지고 직접 그려야 하는 것이지 그렇게 하지 못하면 예기치 못한 에러가 발생함

- 그러므로 그리는 기준은 일단 녹음을 하기 때문에 이 파장을 기준으로 직접 width, height 값을 계산하여서 그릴 것임, 이 그리는 기준은 직접 공백, 길이, 굵기 등을 사전에 정한 값을 기준으로 그림

- 그리고 메소드를 오버라이딩해서 사이즈에 맞게 그리도록 리스트에 담아서 쭉 시각화해서 보여지듯이 그림, 여기서 Main에서 Amplitude를 넘겨주게끔 처리를 함

- 그래서 MediaRecorder로 활용해서 얻을 수 있는 최대 진폭을 기준으로 퍼센티지로 따져서 현재 파장 역시 구해서 그릴 수 있음, 이를 리스트에 담는 것임
```kotlin
package techtown.org.recorder

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/*
녹음이 잘 되고 있는지 녹음하는 부분에 대해서 시각화를 하기 위한 CustomView
 */
class SoundVisualizerView(
        context: Context,
        attrs: AttributeSet? = null
) : View(context, attrs) {

    // 이를 통해 현재 Amplitude에 대해서 메인에서 받을 수 있음
    var onRequestCurrentAmplitude: (() -> Int)? = null

    // 음향 진폭을 시각화하고 Paint로 그릴 것임
    private val amplitudePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply{
        // 어떤 색으로 그릴지 지정
        color = context.getColor(R.color.purple_500)
        // 길이 지정
        strokeWidth = LINE_WIDTH
        // 라인의 양 끝을 Round 처리함
        strokeCap = Paint.Cap.ROUND
    } // 곡선이 좀 더 부드럽게 그려짐 ANTI_ALIAS
    // 그려야 할 사이즈 정해야함, 리소스를 많이 차지하므로 크기에 대해서는 미리 정하고 제대로 처리해야함
    private var drawingWidth: Int = 0
    private var drawingHeight: Int = 0
    // 리스트로 진폭을 저장하고 드로잉에 쓰기 위한 리스트
    private var drawingAmplitudes: List<Int> = emptyList()

    // 다시 플레이를 할 때도 시각화를 위해 선언, Replaying인지 확인하기 위해서 true/false로 구분
    private var isReplaying: Boolean = false
    private var replayingPosition: Int = 0



    // 시간이 지나가면서 흐르듯이 움직이는 듯한 효과를 줘야함, 실행시 그릴때 20프레임으로 움직이면서 스무스하게 녹음이 되듯이 오른쪽으로 녹음창이 흐름
    private val visualizeRepeatAction: Runnable = object : Runnable {
        override fun run() {
            if(!isReplaying) {
                val currentAmplitude = onRequestCurrentAmplitude?.invoke()
                        ?: 0 // 메인에 bindViews에 있는걸 가져옴, 그럼 오디오에 maxAmplitude가 들어옴(메인에서 설정했으므로)
                // Amplitude를 가져오고 Draw를 요청함
                drawingAmplitudes = listOf(currentAmplitude) + drawingAmplitudes // 순차적으로 넣기 위해서 설정함
            } else {
                // replaying시 멈춘 순간에서 다시 그 포지션 기준으로 추가하는 것이므로 Position을 더함
                replayingPosition++
            }
            invalidate() // onDraw를 다시 호출하기 위해, 갱신을 위해서
            handler?.postDelayed(this, ACTION_INTERVAL)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // 사이즈가 깨지면 안되고 제대로 처리해야하므로 들어온 값에 대해서 설정을 해줌, 이러면 깨질일이 없음
        drawingWidth = w
        drawingHeight = h
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // 진폭을 그리기 위해서 onDraw를 오버라이딩함, 진폭 표시는 캔버스에 선을 긋고 길이와 선 사이의 간격을 그리게끔 요청할 것임(무엇을 그릴건지)
        // 그다음 Paint에 어떤 굵기, 색깔이 있는지 처리해줘야함
        // Height는 실제 값에 기반해야함, 클 때는 길게 작을 때는 짧게 나오므로
        // Amplitude, 볼륨에 맞춰서 그 길이를 지정함

        canvas ?: return

        // 선에 중앙값을 그림(길이의 절반)
        val centerY = drawingHeight / 2f
        // 수많은 진폭값을 리스트로 저장하고 하나씩 오른쪽으로 채워나가면서 그릴 것임
        // 시작 포인트는 그리는 영역에 오른쪽 가로길이
        var offsetX = drawingWidth.toFloat()

        // 리스트에 담긴 것을 하나씩 그림
        drawingAmplitudes
                .let{
                    // 만약 Replaying 하는 상태라면 위에서 저장한 것 기준으로 그 다음 추가해서 그림
                    // 아닌 경우는 그냥 그리면 됨
                    amplitudes ->
                    if(isReplaying){
                        amplitudes.takeLast(replayingPosition)
                    } else {
                        amplitudes
                    }
                }
                .forEach { amplitude ->
            // 실제 그릴 length, 현재 진폭값에서 최대값을 나누고 그릴려는 높이를 전달하면 높이 대비 몇 % 그릴지 알 수 있음
            val lineLength = amplitude / MAX_AMPLITUDE * drawingHeight * 0.8F // 100일 경우 꽉차는 것을 대비하기 위해 0.8을 곱함

            // LINE_SPACE만큼 감소시키며 계속 우측으로 차례대로 그리므로
            offsetX -= LINE_SPACE

            // 다 못 그리는 왼쪽을 넘어가는 영역이 있을 수 있음, 뷰를 넘어서는 부분
            if(offsetX < 0) return@forEach

            // 시작과 끝을 전달받고 점을 찍고 종료하는 시점과 어떻게 그릴지 설정
            canvas.drawLine(
                    offsetX,
                    centerY - lineLength / 2F,
                    offsetX,
                    centerY + lineLength / 2F,
                    amplitudePaint
            )
        }

    }

    // 반복해서 호출하면서 그리게 됨
    fun startVisualizing(isReplaying: Boolean) {
        this.isReplaying = isReplaying
        handler?.post(visualizeRepeatAction)
    }

    // 반복 호출을 멈추는 메소드
    fun stopVisualizing() {
        // 여러번 다시 재생할 때 반복을 위해서
        replayingPosition = 0
        handler?.removeCallbacks(visualizeRepeatAction)
    }

    // 기존의 Drawing을 초기화하는 메서드
    fun clearVisualization() {
        drawingAmplitudes = emptyList()
        invalidate()
    }

    companion object {
        // 그리려고 하는 값은 미리 정하고 알아야 하기 때문에 상수로 저장
        private const val LINE_WIDTH = 10F
        private const val LINE_SPACE = 15F
        // 최대 볼륨에 대해서 지정함
        private const val MAX_AMPLITUDE = Short.MAX_VALUE.toFloat() // 0이 되는 현상을 방지하기 위해서 Float으로 바꿈
        private const val ACTION_INTERVAL = 20L
    }
}
```

### 구현 코드
- MediaRecorder, MediaPlayer를 활용해서 녹음을 하고 실행을 함

- 이때 녹음할 때의 실행할 때의 Path나 기본 세팅을 다 해주어야 함

- 그리고 앞서 정의하 State 클래스를 활용해서 녹음상태, 실행상태에 따라 분기를 나누고 메서드를 만들어 처리를 한 뒤 작업을 처리하고

- CustomView로 만든 부분에 대해서도 시각화를 하는 것, CountUp을 하는 것 모두 상태에 맞게 처리를 해주고 상태를 넘겨주면 됨

```kotlin
package techtown.org.recorder

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private val soundVisualizerView: SoundVisualizerView by lazy {
        findViewById(R.id.soundVisualizerView)
    }

    private val recordTimeTextView: CountUpView by lazy {
        findViewById(R.id.recordTimeTextView)
    }

    private val resetButton: Button by lazy {
        findViewById(R.id.resetButton)
    }

    private val recordButton: RecordButton by lazy {
        findViewById(R.id.recordButton)
    }

    // 초기 상태를 위해서 선언한 변수 상태는 언제든지 변할 수 있으니 var로 지정
    private var state = State.BEFORE_RECORDING
        set(value) {
            // 상태에 따라 UI를 변경해주기 위한 Setter, 새로운 state 들어올 때마다 Icon이 업데이트 됨
            field = value
            resetButton.isEnabled = (value == State.AFTER_RECORDING) ||
                    (value == State.ON_RECORDING) // reset 버튼 State 설정 녹음완료 재생되는 시점에 resetButton이 활성화됨
            recordButton.updateIconWithState(value)
        }

    // 앱에서 필요한 권한은 고정되어 있으므로 바로 정의함
    private val requiredPermissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE
    )

    // 캐시로 접근해서 저장을 하기 위해서 해당 디렉토리에 설정을 함
    private val recordingFilePath: String by lazy {
        "${externalCacheDir?.absolutePath}/recording.3gp"
    }

    // recorder 상태에 대해서 계속 바뀌고 확인해야기 때문에 프로퍼티로 미리 정의함, 사용할때와 하지 않을때가 정의되어 있으므로
    private var recorder: MediaRecorder? = null
    // 실행을 위한 Player 상태 정의
    private var player: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestAudioPermission() // 앱 시작하자마자 권한을 요청하게 함
        initViews()
        bindViews()
        initVariables()
    }

    // 요청한 권한에 대한 결과를 받고 처리하기 위해서 오버라이딩을 함
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // 오디오 권한이 부여가 됐는지 확인하기 위한 boolean 값
        // 아래처럼 requestCode가 Audio 관련된 것이고 넘긴 권한이 승인이 되었는지 확인함, 이 두 가지를 충족하면 권한이 허용된 것임
        val audioRecordPermissionGranted =
                requestCode == REQUEST_RECORD_AUDIO_PERMISSION &&
                    grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED

        if(!audioRecordPermissionGranted) {
            // 만일 권한이 넘어오지 않았다면 거부되었다면 앱을 종료시킴
            finish()
        }
    }

    // 오디오 권한에 대해서 요청하기 위한 메소드
    private fun requestAudioPermission() {
        // 앞서 정의한 권한과 requestCode를 할당해줌
        requestPermissions(requiredPermissions, REQUEST_RECORD_AUDIO_PERMISSION)
    }

    private fun initViews() {
        // 그런 다음 recordButton에 대해서 상태를 넣어서 초기화를 함
        recordButton.updateIconWithState(state)
    }

    // 실제 녹음버튼 기능 구현을 위한 메소드
    private fun bindViews() {
        soundVisualizerView.onRequestCurrentAmplitude = {
            // CustomView에 Amplitude를 넘겨줌 recorder를 통해서
            recorder?.maxAmplitude ?: 0
        }
        // 초기화 버튼 누르면 상태를 초기로 돌림
        resetButton.setOnClickListener {
            stopPlaying() // 재생중일 때도 누를 수 있으므로
            // 시각화와 시간도 초기화함
            soundVisualizerView.clearVisualization()
            recordTimeTextView.clearCountTime()
            state = State.BEFORE_RECORDING
        }
        recordButton.setOnClickListener {
            // 상태마다 다르게 행동함
            when(state) {
                State.BEFORE_RECORDING -> {
                    startRecording()
                }
                State.ON_RECORDING -> {
                    stopRecording()
                }
                State.AFTER_RECORDING -> {
                    startPlaying()
                }
                State.ON_PLAYING -> {
                    stopPlaying()
                }
            }
        }
    }

    // state를 다시 초기회해서 value에 넣을 수 있게끔 함
    private fun initVariables() {
        state = State.BEFORE_RECORDING
    }

    // 녹음을 시작할때 하므로 시작하는 메소드 만듬
    private fun startRecording() {
        // 시작하고 설정하기 위한 세팅을 쭉 해야함
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(recordingFilePath) // 외부 캐시 디렉토리에 접근해서 임시로 저장을 해서 캐시에 있는것을 쉽게 날리기 때문에 거기에 지정함
            prepare() // 녹음할 상태 완료됨
        }
        recorder?.start()
        // 시각화를 위해서 만든 클래스 활용, 처음 시작은 replaying이 아니라 false
        soundVisualizerView.startVisualizing(false)
        // 상태를 변화시켜주고 UI를 계속 바꿔줘야함
        recordTimeTextView.startCountUp()
        state = State.ON_RECORDING
    }

    // 녹음을 중단하는 메소드
    private fun stopRecording() {
        recorder?.run {
            stop()
            release() // 메모리 해제
        }
        recorder = null
        soundVisualizerView.stopVisualizing()
        recordTimeTextView.stopCountUp()
        state = State.AFTER_RECORDING
    }

    // 녹음을 재생하는 메소드
    private fun startPlaying() {
        player = MediaPlayer()
                .apply {
                    setDataSource(recordingFilePath)
                    prepare()
                }
        // 완료 타이밍을 받음, 재생이 다 끝났을 때 처리함
        player?.setOnCompletionListener {
            stopPlaying()
            state = State.AFTER_RECORDING
        }
        player?.start()
        soundVisualizerView.startVisualizing(true) // 녹음을 멈추고 재생하고 다시 하는 것이므로 리플레이가 맞음
        recordTimeTextView.startCountUp()
        state = State.ON_PLAYING
    }

    // 녹음을 멈추는 메소드
    private fun stopPlaying() {
        player?.release()
        player = null
        soundVisualizerView.stopVisualizing()
        recordTimeTextView.stopCountUp()
        state = State.AFTER_RECORDING
    }

    // RECORD_AUDIO에 대한 requestCode를 정의함
    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 201
    }
}
```