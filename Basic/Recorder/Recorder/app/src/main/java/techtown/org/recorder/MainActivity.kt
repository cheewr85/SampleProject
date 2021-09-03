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