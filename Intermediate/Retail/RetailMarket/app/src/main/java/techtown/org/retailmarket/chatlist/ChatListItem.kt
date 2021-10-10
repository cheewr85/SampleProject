package techtown.org.retailmarket.chatlist

/*
채팅 관련 내용에 대해서 저장한 데이터, 시간과 사는사람, 판매자 id와 itemTitle을 채팅방 이름으로 씀
 */
data class ChatListItem (
    val buyerId: String,
    val sellerId: String,
    val itemTitle: String,
    val key: Long
) {
    // Realtime Database에서 생성하므로 빈 생성자 필요함
    constructor(): this("","","",0)
}