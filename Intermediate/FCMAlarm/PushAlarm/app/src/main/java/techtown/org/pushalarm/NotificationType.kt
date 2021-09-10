package techtown.org.pushalarm

/*
알림을 일반, 확장형, 커스텀을 구분해서 보여주기 위해 쓰는 enum 클래스
 */

enum class NotificationType(val title: String, val id: Int) {
    NORMAL("일반 알림", 0),
    EXPANDABLE("확장형 알림", 1),
    CUSTOM("커스텀 알림", 3)
}