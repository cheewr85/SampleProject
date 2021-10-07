package techtown.org.tinder

/*
Swipe Animation으로 사용할 CardStackView에서의 Model Item
UserId와 name을 받음
 */

data class CardItem(
    val userId: String,
    var name: String
)