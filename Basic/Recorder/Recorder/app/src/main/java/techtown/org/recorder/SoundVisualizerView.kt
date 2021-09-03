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