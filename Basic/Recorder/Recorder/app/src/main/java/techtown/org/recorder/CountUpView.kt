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