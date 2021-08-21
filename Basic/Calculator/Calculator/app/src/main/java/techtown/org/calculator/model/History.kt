package techtown.org.calculator.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// result 버튼 누를 때마다 DB에 저장하기 위해서 그 저장한 데이터 형식
// expressionText, resultText 별도로 저장, 결과, Expression 식 두개로 나오게 됨
// 모델, 데이터 클래스
// Room 데이터 베이스 사용할 것임, uid는 primary key로 사용함, History 데이터 클래스 자체를 DB에 테이블로 사용을 하기 위해서 @Entity 추가함, 사용하기 위해서 Gradle에 implements해야함 Room 라이브러리를

@Entity
data class History(
    // 아래 DB에서 테이블처럼 사용할 것이므로 이부분에 대해서 애너테이션으로 알려줘야함
    @PrimaryKey val uid: Int?, // 구분을 위한 id
    // 아래와 같이 하면 expression과 result로 나뉘어서 DB 테이블이 생성됨
    @ColumnInfo(name = "expression") val expression: String?, // 계산식 저장
    @ColumnInfo(name = "result") val result: String? // 결과값도 저장
)
