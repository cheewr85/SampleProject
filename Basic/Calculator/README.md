## 계산기
- 흔히 사용하는 계산기 기능 활용함 

- 최대 15자리수까지 제한하고 몇 가지 기능은 제한되어 있음

- 계산 기록에 대해서 저장을 하고 삭제할 수 있음

- 계산 기록에 대해서 Room을 활용해서 저장하고 조회하고 삭제하는 기능을 씀

- 여기서 계산하는 값에 대해서 실시간으로 연산이 되면서 결과버튼을 누르기 전까지 실시간으로 연산하고 결과 버튼을 누르면 그 값이 계산이 진행되는 부분으로 올라오게 된다

## 메인화면
- 기본적인 계산하는 기능과 함께 계산기록 버튼을 누르면 나오는 계산기록 부분

### 실행화면 

![one](/Basic/Calculator/img/one.png)
![one](/Basic/Calculator/img/two.png)


- 계산하는 숫자와 연산자가 나오는 부분을 View로 구분함, 이후 TextView를 통해서 나타냄
- 숫자, 연산자 등의 버튼이 있는 부분에 대해서 TableLayout을 통해서 실제 계산기처럼 구현함
- 그리고 계산기록에 대해서는 바로 ConstraintLayout으로 만들어둠, 기본 visibility 값을 gone으로 설정해두어 계산기록 버튼을 눌렀을 때만 나오게끔 함
- 계산기록에 해당하는 부분에 대해서는 별도로 Layout파일을 만들어서 View를 만듬, LayoutInflater를 활용하여서 계산기록을 DB에서 조회하고 조회한 값을 바탕으로 TextView로 만들어 LinearLayout에 연결해줌
- 계산기록을 쭉 보기 위해서 ScrollView 활용함

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <!-- 숫자가 나오는 화면 부분-->
    <View
        android:id="@+id/topLayout"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintBottom_toTopOf="@id/keypadTableLayout"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_weight="1" />

    <!--계산식을 나타내는 TextView-->
    <TextView
        android:id="@+id/expressionTextView"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="15dp"
        android:layout_marginTop="44dp"
        android:layout_marginEnd="15dp"
        android:gravity="end"
        android:textColor="@color/black"
        android:textSize="30sp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/resultTextView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginStart="15dp"
        android:layout_marginEnd="15dp"
        android:layout_marginBottom="15dp"
        android:gravity="end"
        android:textColor="#AAAAAA"
        android:textSize="20sp"
        app:layout_constraintBottom_toTopOf="@id/keypadTableLayout"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent" />

    <!--숫자, 연산자 등의 버튼이 있는 부분-->
    <TableLayout
        android:id="@+id/keypadTableLayout"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:paddingStart="15dp"
        android:paddingTop="21dp"
        android:paddingEnd="15dp"
        android:paddingBottom="21dp"
        android:shrinkColumns="*"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/topLayout"
        app:layout_constraintVertical_weight="1.5">

        <!--격자무늬로 나누기 위해서 사용, 계산기 버튼이 5줄에 있으므로 5개 생성-->
        <TableRow android:layout_weight="1">

            <androidx.appcompat.widget.AppCompatButton
                android:id="@+id/clearButton"
                android:layout_width="wrap_content"
                android:layout_height="match_parent"
                android:layout_margin="7dp"
                android:background="@drawable/button_background"
                android:onClick="clearButtonClicked"
                android:stateListAnimator="@null"
                android:text="C"
                android:textSize="24sp" />

            <androidx.appcompat.widget.AppCompatButton
                android:layout_width="wrap_content"
                android:layout_height="match_parent"
                android:layout_margin="7dp"
                android:background="@drawable/button_background"
                android:clickable="false"
                android:enabled="false"
                android:stateListAnimator="@null"
                android:text="()"
                android:textColor="@color/green"
                android:textSize="24sp" />

            <androidx.appcompat.widget.AppCompatButton
                android:id="@+id/buttonModulo"
                android:layout_width="wrap_content"
                android:layout_height="match_parent"
                android:layout_margin="7dp"
                android:background="@drawable/button_background"
                android:onClick="buttonClicked"
                android:stateListAnimator="@null"
                android:text="%"
                android:textColor="@color/green"
                android:textSize="24sp" />

            <androidx.appcompat.widget.AppCompatButton
                android:id="@+id/buttonDivider"
                android:layout_width="wrap_content"
                android:layout_height="match_parent"
                android:layout_margin="7dp"
                android:background="@drawable/button_background"
                android:onClick="buttonClicked"
                android:stateListAnimator="@null"
                android:text="/"
                android:textColor="@color/green"
                android:textSize="24sp" />


        </TableRow>

        <TableRow android:layout_weight="1">

            <androidx.appcompat.widget.AppCompatButton
                android:id="@+id/button7"
                android:layout_width="wrap_content"
                android:layout_height="match_parent"
                android:layout_margin="7dp"
                android:background="@drawable/button_background"
                android:onClick="buttonClicked"
                android:stateListAnimator="@null"
                android:text="7"
                android:textColor="@color/textColor"
                android:textSize="24sp" />

            <androidx.appcompat.widget.AppCompatButton
                android:id="@+id/button8"
                android:layout_width="wrap_content"
                android:layout_height="match_parent"
                android:layout_margin="7dp"
                android:background="@drawable/button_background"
                android:onClick="buttonClicked"
                android:stateListAnimator="@null"
                android:text="8"
                android:textColor="@color/textColor"
                android:textSize="24sp" />

            <androidx.appcompat.widget.AppCompatButton
                android:id="@+id/button9"
                android:layout_width="wrap_content"
                android:layout_height="match_parent"
                android:layout_margin="7dp"
                android:background="@drawable/button_background"
                android:onClick="buttonClicked"
                android:stateListAnimator="@null"
                android:text="9"
                android:textColor="@color/textColor"
                android:textSize="24sp" />

            <androidx.appcompat.widget.AppCompatButton
                android:id="@+id/buttonMulti"
                android:layout_width="wrap_content"
                android:layout_height="match_parent"
                android:layout_margin="7dp"
                android:background="@drawable/button_background"
                android:onClick="buttonClicked"
                android:stateListAnimator="@null"
                android:text="X"
                android:textColor="@color/green"
                android:textSize="24sp" />

        </TableRow>

        <TableRow android:layout_weight="1">

            <androidx.appcompat.widget.AppCompatButton
                android:id="@+id/button4"
                android:layout_width="wrap_content"
                android:layout_height="match_parent"
                android:layout_margin="7dp"
                android:background="@drawable/button_background"
                android:onClick="buttonClicked"
                android:stateListAnimator="@null"
                android:text="4"
                android:textColor="@color/textColor"
                android:textSize="24sp" />

            <androidx.appcompat.widget.AppCompatButton
                android:id="@+id/button5"
                android:layout_width="wrap_content"
                android:layout_height="match_parent"
                android:layout_margin="7dp"
                android:background="@drawable/button_background"
                android:onClick="buttonClicked"
                android:stateListAnimator="@null"
                android:text="5"
                android:textColor="@color/textColor"
                android:textSize="24sp" />

            <androidx.appcompat.widget.AppCompatButton
                android:id="@+id/button6"
                android:layout_width="wrap_content"
                android:layout_height="match_parent"
                android:layout_margin="7dp"
                android:background="@drawable/button_background"
                android:onClick="buttonClicked"
                android:stateListAnimator="@null"
                android:text="6"
                android:textColor="@color/textColor"
                android:textSize="24sp" />

            <androidx.appcompat.widget.AppCompatButton
                android:id="@+id/buttonMinus"
                android:layout_width="wrap_content"
                android:layout_height="match_parent"
                android:layout_margin="7dp"
                android:background="@drawable/button_background"
                android:onClick="buttonClicked"
                android:stateListAnimator="@null"
                android:text="-"
                android:textColor="@color/green"
                android:textSize="24sp" />

        </TableRow>

        <TableRow android:layout_weight="1">

            <androidx.appcompat.widget.AppCompatButton
                android:id="@+id/button1"
                android:layout_width="wrap_content"
                android:layout_height="match_parent"
                android:layout_margin="7dp"
                android:background="@drawable/button_background"
                android:onClick="buttonClicked"
                android:stateListAnimator="@null"
                android:text="1"
                android:textColor="@color/textColor"
                android:textSize="24sp" />

            <androidx.appcompat.widget.AppCompatButton
                android:id="@+id/button2"
                android:layout_width="wrap_content"
                android:layout_height="match_parent"
                android:layout_margin="7dp"
                android:background="@drawable/button_background"
                android:onClick="buttonClicked"
                android:stateListAnimator="@null"
                android:text="2"
                android:textColor="@color/textColor"
                android:textSize="24sp" />

            <androidx.appcompat.widget.AppCompatButton
                android:id="@+id/button3"
                android:layout_width="wrap_content"
                android:layout_height="match_parent"
                android:layout_margin="7dp"
                android:background="@drawable/button_background"
                android:onClick="buttonClicked"
                android:stateListAnimator="@null"
                android:text="3"
                android:textColor="@color/textColor"
                android:textSize="24sp" />

            <androidx.appcompat.widget.AppCompatButton
                android:id="@+id/buttonPlus"
                android:layout_width="wrap_content"
                android:layout_height="match_parent"
                android:layout_margin="7dp"
                android:background="@drawable/button_background"
                android:onClick="buttonClicked"
                android:stateListAnimator="@null"
                android:text="+"
                android:textColor="@color/green"
                android:textSize="24sp" />

        </TableRow>

        <TableRow android:layout_weight="1">

            <ImageButton
                android:id="@+id/historyButton"
                android:layout_width="wrap_content"
                android:layout_height="match_parent"
                android:layout_margin="7dp"
                android:background="@drawable/button_background"
                android:onClick="historyButtonClicked"
                android:src="@drawable/ic_baseline_access_time_24"
                android:stateListAnimator="@null"
                android:textColor="@color/textColor"
                android:textSize="24sp" />

            <androidx.appcompat.widget.AppCompatButton
                android:id="@+id/button0"
                android:layout_width="wrap_content"
                android:layout_height="match_parent"
                android:layout_margin="7dp"
                android:background="@drawable/button_background"
                android:onClick="buttonClicked"
                android:stateListAnimator="@null"
                android:text="0"
                android:textColor="@color/textColor"
                android:textSize="24sp" />

            <androidx.appcompat.widget.AppCompatButton
                android:layout_width="wrap_content"
                android:layout_height="match_parent"
                android:layout_margin="7dp"
                android:background="@drawable/button_background"
                android:clickable="false"
                android:enabled="false"
                android:stateListAnimator="@null"
                android:text="."
                android:textColor="@color/textColor"
                android:textSize="24sp" />

            <androidx.appcompat.widget.AppCompatButton
                android:id="@+id/resultButton"
                android:layout_width="wrap_content"
                android:layout_height="match_parent"
                android:layout_margin="7dp"
                android:background="@drawable/button_background_green"
                android:onClick="resultButtonClicked"
                android:stateListAnimator="@null"
                android:text="="
                android:textColor="@color/white"
                android:textSize="24sp" />

        </TableRow>
    </TableLayout>
    <!-- 계산기록을 보여주기 위한 레이아웃, 계산기록 누르면 새로운 뷰가 뜸, keypad를 덮음-->
    <!-- 그래서 visibility gone으로 설정함-->
    <androidx.constraintlayout.widget.ConstraintLayout
        android:id="@+id/historyLayout"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:background="@color/white"
        android:visibility="gone"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="@id/keypadTableLayout"
        tools:visibility="visible">
        <!-- 계산기록이 나오고 계산기록을 삭제하는 등 스크롤로 확인 및 내부 뷰 채움-->
        <androidx.appcompat.widget.AppCompatButton
            android:id="@+id/closeButton"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:background="@null"
            android:onClick="closeHistoryButtonClicked"
            android:stateListAnimator="@null"
            android:text="닫기"
            android:textColor="@color/black"
            android:textSize="18sp"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent" />

        <ScrollView
            android:layout_width="0dp"
            android:layout_height="0dp"
            android:layout_margin="10dp"
            app:layout_constraintBottom_toTopOf="@id/historyClearButton"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toBottomOf="@id/closeButton">

            <LinearLayout
                android:id="@+id/historyLinearLayout"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"/>

        </ScrollView>

        <androidx.appcompat.widget.AppCompatButton
            android:id="@+id/historyClearButton"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_marginStart="47dp"
            android:layout_marginEnd="47dp"
            android:layout_marginBottom="38dp"
            android:background="@drawable/button_background_green"
            android:onClick="historyClearButtonClicked"
            android:stateListAnimator="@null"
            android:text="계산기록 삭제"
            android:textColor="@color/white"
            android:textSize="18sp"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent" />


    </androidx.constraintlayout.widget.ConstraintLayout>

</androidx.constraintlayout.widget.ConstraintLayout>
```

### LayoutInflater를 통해서 LinearLayout에 연결한 View
- 계산기록에 해당하는 부분에 대해서 보여주기 위한 View
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    xmlns:app="http://schemas.android.com/apk/res-auto">

    <TextView
        android:id="@+id/expressionTextView"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="15dp"
        android:layout_marginTop="44dp"
        android:layout_marginEnd="15dp"
        android:gravity="end"
        android:textColor="@color/black"
        android:textSize="30sp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/resultTextView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginStart="15dp"
        android:layout_marginEnd="15dp"
        android:layout_marginBottom="15dp"
        android:gravity="end"
        android:textColor="#AAAAAA"
        android:textSize="20sp"
        app:layout_constraintTop_toBottomOf="@id/expressionTextView"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```


### 사용한 Drawable
- 버튼에 대해서 직접 drawable 파일을 만들어서 활용함, 그 외에 VectorAsset을 활용하여 계산기록 이미지 버튼도 가지고 옴
- 숫자 이외의 버튼
```xml
<?xml version="1.0" encoding="utf-8"?>
<!-- 눌렀을 때 나타나는 색깔-->
<ripple xmlns:android="http://schemas.android.com/apk/res/android"
    android:color="@color/greenPress">

    <!--ripple은 눌렀을 때의 색깔이므로 background를 별도로 설정해줘야함-->
    <item android:id="@android:id/background">
        <shape android:shape="rectangle">
            <solid android:color="@color/green"/>
            <corners android:radius="100dp"/>
            <stroke
                android:width="1dp"
                android:color="@color/greenPress"/>
        </shape>

    </item>

</ripple>
```
- 숫자등의 버튼
```xml
<?xml version="1.0" encoding="utf-8"?>
<!-- 눌렀을 때 나타나는 색깔-->
<ripple xmlns:android="http://schemas.android.com/apk/res/android"
    android:color="@color/buttonPressGray">

    <!--ripple은 눌렀을 때의 색깔이므로 background를 별도로 설정해줘야함-->
    <item android:id="@android:id/background">
        <shape android:shape="rectangle">
            <solid android:color="@color/buttonGray"/>
            <corners android:radius="100dp"/>
            <stroke
                android:width="1dp"
                android:color="@color/buttonPressGray"/>
        </shape>

    </item>

</ripple>
```

### 구현코드
- 계산기에 대한 구현은 먼저 숫자, 연산자등의 버튼이 눌렸을 때 기능이 있음(buttonClicked 메소드)
- 그리고 누른 값에 대해서 초기화를 시키는 기능이 있음(clearButtonClicked 메소드)
- 그리고 결과값을 출력하게 하는 기능이 있음(resultButtonClicked 메소드)
- 계산기록을 보기 위해 계산기록을 보여주는 기능을 하는 버튼이 있음(historyButtonClicked 메소드)
- 계산기록을 초기화하는 버튼이 있음(historyClearButtonClicked 메소드)
- 계산기록은 처음에는 보이지 않다가 버튼을 누를때 보이는 것이므로 닫는 기능도 있음(closeHistoryButtonClicked 메소드)
- 각각 메소드와 버튼에 대한 기능들은 onClick을 통해서 이벤트 처리를 했음
- 하지만 이러한 메소드 설명에 앞서 위에서 설명한 것처럼 계산기록을 원활하게 보기 위해서 Room DB를 활용할 예정임
- 그래서 Room 라이브러리를 적용하고 이 부분에 대해서 미리 사전에 DB와 관련된 테이블을 model 패키지를 만들고 이 model을 처리하는 Dao 즉 DB에서 어떻게 저장, 삭제, 조회를 할 지에 대한 Dao를 만든뒤
- 이를 참조하는 데이터베이스를 사전에 만든 뒤 MainActivity에서 활용해야함

- 여기서 좀 더 알아둬야 할 부분은 아무래도 DB에 대해서 활용하는것이므로 어느정도 쿼리문과 DB에 대한 기본적인 개념이 애너테이션으로 쓰임

- History.kt
- 계산기록을 저장하기 위한 model, 데이터 클래스
- DB로 테이블을 사용하기 위해서 활용함
```kotlin
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

```

- HistoryDao.kt
- 이제 위에서 정의한 model 패키지에 대해서 어떻게 저장, 조회, 삭제를 할 것인지에 대해서 정의를 함
```kotlin
package techtown.org.calculator.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import techtown.org.calculator.model.History

// Room에 연결된 Dao
// 여기에는 이전에 만든 model 패키지에 있는 History Entity 저장을 어떻게 하고 조회는 어떻게 할 것이며 지우는 것은 어떻게 할 지에 대해서 정의를 함
// 데이터 저장, 삭제, 가져오는 기능
@Dao
interface HistoryDao {
    // History를 전부 가져오는 함수, Dao에서 Query문을 바로 작성하면됨
    @Query("SELECT * FROM history")
    fun getAll() : List<History>

    // 하나하나 저장, insert 하는 메소드, History를 받아와 저장
    @Insert
    fun insertHistory(history : History)

    // 전체삭제하는 기능, return 하는 것 없이 삭제만 하므로 직접 Query로 작성
    @Query("DELETE FROM history")
    fun deleteAll()

//    // 하나만 삭제하는 경우, History를 받아와 하나만 삭제함
//    @Delete
//    fun delete(history : History)
//
//    // SELECT 할 때 질의문을 줘서 조건에 부합한 것만 지울려고 할 때, Result 기준으로 받아오고 싶을 때
//    @Query("SELECT * FROM history WHERE result LIKE :result LIMIT 1") // 인자로 들어오는 result를 가지고 사용함, 하나만 반환하고 싶을 때 LIMIT을 검
//    fun findByResult(result : String) : History
}
```

- AppDatabase.kt
- 데이터 베이스 처리를 위해서 추상클래스 구현, 이제 이 데이터베이스를 구현한 클래스를 Main에서 활용함
```kotlin
package techtown.org.calculator

import androidx.room.Database
import androidx.room.RoomDatabase
import techtown.org.calculator.dao.HistoryDao
import techtown.org.calculator.model.History

// 데이터베이스를 만들어야함 그래서 추상클래스로 만들고 활용함, 데이터베이스 처리를 위해
// 데이터베이스임을 알려주기 위해, History를 DB로 만들것이므로 entity로 등록해서 사용함
// version도 작성해줘야함, 앱이 업데이트 하면 DB가 바뀔 수도 있음 1->2로 될 때 migration을 해주어서 데이터가 날라가지 않고 DB 버전과 구조가 바뀔 수 있으므로 이를 명시하고 버전을 체크를 해야함
// version이 바뀌면 migration 코드가 있어서 업데이트를 하므로 이를 위해서 version을 처리해줘야함
@Database(entities = [History::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    // historyDao를 가지고 있음, 이를 가져갈 수 있게 함
    // 이러면 AppDatabase를 사용할 때 historyDao를 활용할 수 있음
    abstract fun historyDao() : HistoryDao
}
```

- MainActivity.kt
- 자세한 설명은 주석을 확인하면 됨, 각각 기능에 맞게 TextView를 연결하고 연산을 하고 이런 부분을 보이게끔 처리를 했음
- 여기서 좀 더 설명이 필요한 부분은 숫자가 맞는지 확인하기 위해서 확장함수 활용, 기존에 String 클래스에서 숫자를 확인하기 위한 새로운 메소드를 적용하여서 활용함
- 그리고 DB에 대해서는 위에서 정의한 DB 클래스를 그대로 활용하면 됨, onCreate에서 Room을 활용, 로컬 DB를 생성하는 것임 
- 단 추가적으로 알아둬야할 것은, DB에 대한 작업은, Thread 처리를 한 것, 왜냐하면 메인 Thread에 대해서 작업이 많아지면 앱이 죽어버리거나 오류가 발생할 수 있으므로 메인 UI Thread에서 이러한 작업을 많이 주어서도 안되고 처리해서도 안됨
- UI Thread는 View를 주로 건드리기 때문에 그럼, 그래서 Thread를 생성하여서 DB에서 하는 저장, 조회, 삭제등의 작업을 처리를 하고 나중에 그 작업이 다 처리된것을 View에다가 보여줘야 할 때 UiThread에 접근해서 처리함, 왜냐하면 View에 대한 변경사항 적용은 UiThread만이 가능함, 즉 Main Thread에서만 처리할 수 있으므로
```kotlin
package techtown.org.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.room.Room
import techtown.org.calculator.model.History
import kotlin.math.exp

class MainActivity : AppCompatActivity() {

    // 입력값을 나타내는 TextView
    private val expressionTextView : TextView by lazy {
        findViewById<TextView>(R.id.expressionTextView)
    }
    // 결과값을 나타내는 TextView
    private val resultTextView : TextView by lazy {
        findViewById<TextView>(R.id.resultTextView)
    }
    // history를 나타내는 부분, view에도 Visibility 기능이 있으므로 View로 설정
    private val historyLayout : View by lazy {
        findViewById<View>(R.id.historyLayout)
    }
    // ScrollView에 있는 LinearLayout
    private val historyLinearLayout : LinearLayout by lazy {
        findViewById<LinearLayout>(R.id.historyLinearLayout)
    }

    // 사전에 설정한 Room DB를 만든 걸 활용
    lateinit var db : AppDatabase

    // operator 입력 중인지를 확인, 그리고 operator가 이미 입력되었는지 확인
    private var isOperator = false
    private var hasOperator = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // AppDataBase를 만들기 위해서 할당함
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "historyDB"
        ).build() // AppDataBase를 반환을 하도록 설정을 함, 앞서 사전에 만든 것을 활용해서 만들어짐
    }

    fun buttonClicked(view: View) {
        // 숫자 등의 버튼을 구분하기 위해서 모든 버튼에 대해서 Id를 부여해서 적용함, 그러면 이미 onClick으로 View를 Binding 했으므로 Id만 확인하면 됨
        when(view.id) {
            // 만일 버튼이 눌렀다면 해당 버튼에 맞는 숫자 연결
            R.id.button0 -> numberButtonClicked("0")
            R.id.button1 -> numberButtonClicked("1")
            R.id.button2 -> numberButtonClicked("2")
            R.id.button3 -> numberButtonClicked("3")
            R.id.button4 -> numberButtonClicked("4")
            R.id.button5 -> numberButtonClicked("5")
            R.id.button6 -> numberButtonClicked("6")
            R.id.button7 -> numberButtonClicked("7")
            R.id.button8 -> numberButtonClicked("8")
            R.id.button9 -> numberButtonClicked("9")
            // 연산자의 경우
            R.id.buttonPlus -> operatorButtonClicked("+")
            R.id.buttonMinus -> operatorButtonClicked("-")
            R.id.buttonMulti -> operatorButtonClicked("*")
            R.id.buttonDivider -> operatorButtonClicked("/")
            R.id.buttonModulo -> operatorButtonClicked("%")
        }
    }

    // 숫자와 연산자에 대해서 버튼을 누르면 위에서 정의한 TextView에 연결할 것임
    // 여기서 제약조건으로 숫자는 15자리를 넘으면 안되고 연산자는 두 번 중복되게 누르면서 할 수 없음
    // 숫자버튼이 눌렸을 때를 처리하기 위한 함수
    private fun numberButtonClicked(number : String) {

        // operator를 입력하다 오면 띄어쓰기를 한 번 더 해야함, 입력을 숫자로 바꿈
        // 만일 operator를 입력하다가 왔다면
        if(isOperator) {
            expressionTextView.append(" ") // operator 입력하다가 숫자를 누른 것이므로 구분하기 위해서 빈값을 추가함, 숫자 입력을 위해서
        }
        // 이미 연산자 입력하다가 온 것이므로 숫자 입력으로 바뀐 상태니깐 false로 바꿈
        isOperator = false

        // TextView에 나타낼때 그리고 String으로 보여줄 때 숫자 연산자 숫자로 띄어쓰기로 구분할 것임(split 사용), 계산식을 저장
        val expressionText = expressionTextView.text.split(" ")
        // 숫자가 15자리를 넘어가지 않게 하기 위해서 처리함, last를 쓴 이유는 연산자가 입력되지 않는 경우에도 last가 숫자고 연산자가 입력되고 두번째 숫자여도 last가 숫자이므로
        if(expressionText.isNotEmpty() && expressionText.last().length >= 15){
            // 에러메시지를 통해 예외처리
            Toast.makeText(this,"15자리를 넘어서 입력할 수 없습니다.",Toast.LENGTH_SHORT).show()
            return // 더이상 로직이 실행되지 않게함
        } else if(expressionText.last().isEmpty() && number == "0"){
            // 맨 앞에 0을 입력하고 계속 입력하는 경우
            Toast.makeText(this,"0은 제일 앞에 올 수 없습니다.",Toast.LENGTH_SHORT).show()
            return // 더이상 로직이 실행되지 않게함
        }
        // 위에서 설정한 예외가 아닌 상황에는 정상적으로 숫자를 입력받는 상황임, 위에서 누른 number를 붙여줌 TextView에
        expressionTextView.append(number)

        // 결과값이 나오는 부분에 대해서 아래 구현한 계산을 가지고 바로 나오게끔 하면 됨
        resultTextView.text = calculateExpression()

    }

    // 연산자에 대해서 눌렀을 때 처리하기 위한 함수
    private fun operatorButtonClicked(operator : String) {
        if(expressionTextView.text.isEmpty()){
            // 맨 앞에 연산자가 올 수 없으므로 빈 상황에서 연산자를 입력하려고 하면 return을 해버림
            return
        }

        when {
            isOperator -> {
                // 연산자를 입력했는데 또 입력하려는 경우
                val text = expressionTextView.text.toString()
                // 이미 연산자를 입력해서 또 있는 상태이므로 연산자 부분을 떼고 새로 입력받은 연산자를 붙임
                expressionTextView.text = text.dropLast(1) + operator
            }

            hasOperator -> {
                // 이미 연산자를 입력되어 있는 경우
                Toast.makeText(this,"연산자는 한 번만 사용할 수 있습니다.",Toast.LENGTH_SHORT).show()
                return // 더이상 로직이 실행되지 않게함
            }

            else ->  {
                // is,has Operator가 false인 경우, 이 경우는 숫자만 입력한 경우임, 그러므로 이 경우엔 구분을 위해 앞에 띄어쓰기를 하고 연산자를 붙임
                expressionTextView.append(" $operator")
            }
        }
        // spannable 기능 연산자의 경우 초록색으로 따로 칠함, 해당 text를 받아서 처리함
        val ssb = SpannableStringBuilder(expressionTextView.text)
        // 이를 활용해서 연산자 자리에만 초록색으로 칠할 것임
        ssb.setSpan(
            // 연산자가 딱 한 번 밖에 올 수 밖에 없는 상황이라 마지막으로 확인을 함
            ForegroundColorSpan(getColor(R.color.green)),
            expressionTextView.text.length - 1,
            expressionTextView.text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        // expressionTextView를 다시 설정한 것이므로 ssb 값을 다시 넣어줌
        expressionTextView.text = ssb
        // 연산자가 입력된 상황이므로 눌렀다는 것은 true로 바꿔주면 됨
        isOperator = true
        hasOperator = true


    }

    fun clearButtonClicked(view: View) {
        // 단순하게 입력값 지우고 결과값 지우고 연산자를 입력했다는 state를 초기화하면 됨
        expressionTextView.text = ""
        resultTextView.text = ""
        isOperator = false
        hasOperator = false
    }
    private fun calculateExpression() : String {
        // expressionTextView에 있는 숫자 연산자 숫자를 가져와서 resultTextView에 넣기 위한 연산을 하고 그 값을 반환하는 함수
        val expressionTexts = expressionTextView.text.split(" ")

        if(hasOperator.not() || expressionTexts.size != 3) {
            // 만약 연산자가 입력되지 않고 숫자 연산자 숫자가 입력되지 않았다면
            return "" // 빈 문자열 반환
        } else if(expressionTexts[0].isNumber().not()||expressionTexts[2].isNumber().not()){
            // 만약 첫번째 부분, 세번째 부분 즉, 그 부분의 입력값이 숫자가 아니라면, 이때 숫자로 변환하려고 하면 에러가 발생하므로 예외처리 해야함, 아래의 구현한 확장함수 활용
            return "" // 빈 문자열 반환
        }
        // 위처럼 연산자가 입력되지 않거나 숫자가 입력되지 않거나 입력값이 숫자가 아니라면 정상적으로 처리 된 것이므로 숫자 연산자 숫자로 분류된 것
        val exp1 = expressionTexts[0].toBigInteger() // 첫번째로 입력받은 숫자
        val exp2 = expressionTexts[2].toBigInteger() // 두번째로 입력받은 숫자
        val op = expressionTexts[1] // 연산자

        return when(op) {
            // 연산에 맞춰서 계산하기
            "+" -> (exp1 + exp2).toString()
            "-" -> (exp1 - exp2).toString()
            "*" -> (exp1 * exp2).toString()
            "/" -> (exp1 / exp2).toString()
            "%" -> (exp1 % exp2).toString()
            else -> "" // 오류가 난 것이므로 빈값 반환
        }

    }
    fun resultButtonClicked(view: View) {
        //calcuateExpression과 유사한 연산을 하므로 표현이 같음
        val expressionTexts = expressionTextView.text.split(" ")

        if(expressionTextView.text.isEmpty() || expressionTexts.size == 1) {
            // 숫자만 들어온 경우 아무런 처리를 하지 않음
            return
        }
        if(expressionTexts.size != 3 && hasOperator) {
            // 입력을 다 받지 않은 경우 즉 연산자까지만 입력하고 두번째 숫자를 입력받지 않은 경우
            Toast.makeText(this,"아직 완성되지 않은 수식입니다.",Toast.LENGTH_SHORT).show()
            return // 더이상 로직이 실행되지 않게함
        }

        if(expressionTexts[0].isNumber().not()||expressionTexts[2].isNumber().not()){
            // 숫자가 정상적으로 치환이 되지 않은 경우, 발생해선 안되는 경우이지만 미리 만들어둠
            Toast.makeText(this,"오류가 발생했습니다.",Toast.LENGTH_SHORT).show()
            return // 더이상 로직이 실행되지 않게함
        }
        // 나중에 값을 DB에 저장하기 위해 입력받은 연산을 String으로 저장함
        val expressionText = expressionTextView.text.toString()
        // 결과값 저장
        val resultText = calculateExpression()

        // DB를 앞서 만든대로 History data에 담기 위해서 클래스를 만들었고 dao를 통해서 이 data를 담은 클래스를 DB로 활용하는 기능을 넣고 이를 실제 AppDatabase로 처리하여 활용하게끔 만듬
        // 여기서 DB작업은 Main쓰레드에서 하지 않고 새로운 쓰레드를 만들고 나서 해야함
        Thread(Runnable{
            // 메인쓰레드에는 무겁고 복잡한 작업을 하면 안됨 이런 DB 저장은 쓰레드를 활용해야함
            // 사전에 정의한 인터페이스 Dao에서 하나하나 저장하는 기능이 있었음, 이를 사용하면 됨
            // 이를 사용할 때 History 객체를 바로 인스턴스로 생성하고 해당 값들을 넣어둠, uid, expression, result를
            // 바로 위에 val 변수로 미리 저장한 것을 활용해서 넣어두면 됨, 왜냐하면 메인쓰레드가 먼저 실행될 지 현재 이 쓰레드가 먼저 실행될지 모르기 때문에
            // 아래와 같이 처리한다면 DB에 알아서 값이 insert가 됨
            db.historyDao().insertHistory(History(null, expressionText, resultText))
        }).start()

        // resultButton을 누르면 입력값에 해당 결과가 나오고 다시 연산을 하는 것이므로 아래와 같이 입력값에 결과값이 결과값은 초기화 됨
        // 계산결과값이 입력값하는곳에 올라가는 연산
        resultTextView.text = ""
        expressionTextView.text = resultText
        // 연산이 끝났으므로 초기화
        isOperator = false
        hasOperator = false
    }
    fun historyButtonClicked(view: View) {
        // 계산기에서 저장기록 버튼을 누르면 실행됨
        // DB에서 history값을 가져와서 historyLinearLayout에 보여주고 historyLayout을 보여주면 됨
        historyLayout.isVisible = true
        // 하위에 있는 View를 다 삭제함, 그리고 아래 forEach로 담아주기 위해서
        historyLinearLayout.removeAllViews()

        // DB의 저장된 값을 모두 가져오는 것, 이 역시 쓰레드를 열어서 사용해야함, 버튼 누르면 DB 값을 모두 가져옴
        Thread(Runnable {
            // getAll에 저장되어 있는 모든 DB를 가져오게 함
            // 저장시 최신저장이 나중에 저장되기 때문에 최신저장을 위에 보여주기 위해서 reversed를 함
            // 이 뒤집은 리스트를 하나씩 꺼내와서 HistoryLinearLayout에 넣어줄 것임
            db.historyDao().getAll().reversed().forEach {
                // 여기서 근데 LinearLayout에 담을 View가 없음, 이를 View로 붙여주기 위해서 LayoutInflater를 활용해서 붙여야함, 그러기 위해 layout 폴더에서 해당 View를 만들어줘야함
                // history_row를 통해서 Text를 담을 것임
                // 하지만 새로 생성한 Thread는 UiThread, MainThread가 아니므로 View를 그려주거나 수정하는 작업을 못하므로 UiThread를 불러와야함
                // UiThread를 열기 위한 방법
                runOnUiThread{
                    // history_row로 만든것을 불러오기 위해서 LayoutInflater를 활용함
                    val historyView = LayoutInflater.from(this).inflate(R.layout.history_row, null, false)
                    // historyView 즉, history_row에 만들어둔 expression, result 부분을 불러와서 해당 text를 설정함, 현재 db에서 History를 가르키므로 it을 통해 해당 값을 가져옴
                    historyView.findViewById<TextView>(R.id.expressionTextView).text = it.expression
                    historyView.findViewById<TextView>(R.id.resultTextView).text = "= ${it.result}"

                    // 앞서 정의한 history_row의 View를 LinearLayout에 vertical로 View를 보여주기 위해서 addView를 함, 그러면 이제 item처럼 쓸려고 만든 historyView가 착착 붙음
                    // ScrollView라서 많이해도 스크롤 가능함
                    historyLinearLayout.addView(historyView)

                }
            }

        }).start()

    }
    fun historyClearButtonClicked(view: View) {
        historyLinearLayout.removeAllViews() // historyLinearLayout에 있는 View를 다 삭제함

        // DB에 모든 기록 삭제, 여기서 계속 했지만 Thread를 열어주고 작업을 해야함
        Thread(Runnable{
            db.historyDao().deleteAll()
        }).start()

    }
    fun closeHistoryButtonClicked(view: View) {
        // historyLayout을 gone으로만 바꾸면 됨
        historyLayout.isVisible = false
    }


}
// 위에서 정의한 즉 String이 숫자인지 확인하기 위해서 String 클래스에서 확장함수로 직접 만듬
fun String.isNumber() : Boolean {
    // Number로 바꾸고 에러가 나면 Number가 아닌 것으로 둠, try-catch로 처리함
    return try {
        // 원래있던 String을 숫자로 바꿈, 범위를 넓게 잡기 위해 Big으로 씀
        this.toBigInteger()
        true // 성공적으로 변환했다는 것은 숫자인 것이므로 true 반환함
    } catch (e : NumberFormatException) { // 해당 toBigInteger를 타고 들어가면 Null, NumberFormat 에러가 뜸, 그 중 NumberFormat이 다뤄야할 에러임, 숫자로 제대로 치환되지 않은 것이므로
        false // 이때 false를 반환함
    }
}
```