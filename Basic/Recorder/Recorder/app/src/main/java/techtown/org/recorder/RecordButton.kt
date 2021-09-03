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