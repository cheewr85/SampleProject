package techtown.org.alarm

/*
알람 데이터에 대한 것을 저장해두는 데이터 클래스
 */

data class AlarmDisplayModel(
    val hour: Int,
    val minute: Int,
    var onOff: Boolean // onOff 상황에 따라 다르므로 var로 만듬
) {
    // 받아온 데이터를 가공을 하고 UI에 업데이트 하게 할 수 있게 함, String 형태로 나갈 것임 09시 30분등의 형태로
    val timeText: String
    // val로 설정했으므로 getter로 설정 사용하기 위해서
        get() {
        // 9 -> 09, 23 -> 11로 들어오게 변환함 m도 3-> 03으로(String format을 통해)
            val h = "%02d".format(if(hour < 12) hour else hour - 12)
            val m = "%02d".format(minute)

            return "$h:$m"
        }

    val ampmText: String
    // ampm 설정을 위해서 getter 사용
        get() {
            return if(hour < 12) "AM" else "PM"
        }

    val onOffText: String
    // boolean 값에 따라 텍스트 바꿈
        get() {
            return if (onOff) "알람 끄기" else "알람 켜기"
        }

    // DB에 sharedPreferences에 저장을 위한 데이터를 만드는 함수(구분지어서 보내기 위해서)
    fun makeDataForDB(): String {
        return "$hour:$minute"
    }
}