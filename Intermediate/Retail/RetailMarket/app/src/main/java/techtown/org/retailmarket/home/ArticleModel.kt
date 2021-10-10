package techtown.org.retailmarket.home
/*
data class로 넘겨받을 데이터들
 */
data class ArticleModel (
    val sellerId: String,
    val title: String,
    val createdAt: Long,
    val price: String,
    val imageUrl: String
) {
    // Firebase Realtime Database의 모델 클래스를 그대로 쓰기 위해선 constructor 필요함
    constructor(): this("","",0,"","")
}