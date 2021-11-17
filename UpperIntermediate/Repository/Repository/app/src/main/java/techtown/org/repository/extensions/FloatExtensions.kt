package techtown.org.repository.extensions

import android.content.res.Resources
// dp에서 px로 바꿔주는 확장함수
internal fun Float.fromDpToPx(): Int {
    return (this * Resources.getSystem().displayMetrics.density).toInt()
}