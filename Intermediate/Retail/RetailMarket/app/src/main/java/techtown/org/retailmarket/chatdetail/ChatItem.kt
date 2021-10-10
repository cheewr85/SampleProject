package techtown.org.retailmarket.chatdetail
/*
실제 채팅 메시지 한 줄 한 줄 들어가는 모델
 */
data class ChatItem (
    val senderId: String,
    val message: String
) {
    constructor(): this("","")
}