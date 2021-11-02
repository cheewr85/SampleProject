package techtown.org.newtube

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout

/*
Custom을 만듬으로써 리사이클러뷰에서 스크롤 처리, 그 외에 이벤트 처리를 원활하게 하기 위해서 만든 커스텀 레이아웃
fragment_player에서 사용을 함
 */

class CustomMotionLayout(context: Context, attributeSet: AttributeSet ?= null): MotionLayout(context, attributeSet){

    // 터치가 눌렸을 때 true가 됨, 다른 곳을 눌렀을 때 false로 줌
    private var motionTouchStarted = false
    // 스크롤, 터치 처리를 위해서 해당 View를 가져옴
    private val mainContainerView by lazy {
        findViewById<View>(R.id.mainContainerLayout)
    }
    private val hitRect = Rect()

    init {
        // transition이 끝났을 때 motionTouchStarted를 false 처리하기 위해서 리스너 처리함
        setTransitionListener(object: TransitionListener{
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) { }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) { }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                motionTouchStarted = false
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) { }

        })
    }

    // 제스처 이벤트, 터치 이벤트 처리함, 조건에 맞게 재정의 하기 위해서
    // 터치 이벤트 눌렀을 때만을 처리를 함, 땠을 때는 굳이 고려하지 않아도 됨
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                motionTouchStarted = false
                return super.onTouchEvent(event) // 기존의 값을 리턴하게 함
            }
        }

        // motionTouchStarted를 확인하여서 처리를 함
        if(!motionTouchStarted) {
            // Rect에다가 값을 저장해서 반환하게함
            mainContainerView.getHitRect(hitRect)
            // 이벤트 x,y 좌표가 hitRect안에 일어난 값인지 확인함, 만약 맞다면 true가 됨
            motionTouchStarted = hitRect.contains(event.x.toInt(), event.y.toInt())
        }
        // 터치 이벤트에 대해서 재정의함
        return super.onTouchEvent(event) && motionTouchStarted
    }

    // 제스처 이벤트 처리, 스크롤 하는 이벤트를 재정의 할 것임
    private val gestureListener by lazy {
        object: GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                // 메인에서 일어난건지 확인
                mainContainerView.getHitRect(hitRect)
                // hitRect를 통해서 범주 안에서 일어났는지 체크
                return hitRect.contains(e1.x.toInt(),e1.y.toInt())
            }
        }
    }
    private val gestureDetector by lazy {
        GestureDetector(context, gestureListener)
    }
    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        // 터치 이벤트를 위에서 체크한 제스처 이벤트 처리를 가지고 적용을 함
        return gestureDetector.onTouchEvent(event)
    }

}