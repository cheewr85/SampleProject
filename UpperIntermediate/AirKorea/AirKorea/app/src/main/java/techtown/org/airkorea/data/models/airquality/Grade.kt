package techtown.org.airkorea.data.models.airquality

import androidx.annotation.ColorRes
import com.google.gson.annotations.SerializedName
import techtown.org.airkorea.R

// 대기 오염 등급을 나타내는 클래스
enum class Grade(
    val label: String,
    val emoji: String,
    @ColorRes val colorResId: Int
    ) {
    // 기본 생성자 넣어줌
    @SerializedName("1")
    GOOD("좋음","😀", R.color.blue),

    @SerializedName("2")
    NORMAL("보통","😊", R.color.green),

    @SerializedName("3")
    BAD("나쁨","😥", R.color.yellow),

    @SerializedName("4")
    AWFUL("매우 나쁨","😣", R.color.red),

    UNKNOWN("미측정","🤔", R.color.gray);

    override fun toString(): String {
        return "$label $emoji"
    }
}