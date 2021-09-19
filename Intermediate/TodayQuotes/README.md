## 오늘의 명언
- ViewPager2를 통해서 무한 스와이프가 가능하며 명언이 나오는 앱

- Firebase에서 Remote Config 기능을 활용하여 매개변수를 값을 넣어두고 그런 다음 앱 내에서도 Remote Config와 연결을 함

- 여기서 Remote Config의 설정값을 바꾸면 그에 맞게 UI도 변경되게함, 기본은 명언 + 이름인 형태에서 이름을 안 나오게끔 값을 Remote Config에서 변경하면 그에 맞게 변환됨

## 메인화면
![one](/Intermediate/TodayQuotes/img/one.png)
![one](/Intermediate/TodayQuotes/img/two.png)

- 기본적인 화면은 명언 + 이름으로 설계가 되었으나 Remote Config를 통해서 값을 변경하면 명언만 나오게 할 수 있음

- 그리고 ViewPager를 통한 스와이프도 하는 것이지만, 그 전에 ProgressBar를 통해서 현재 로딩중인 상태를 확인을 하여 나타냄

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <androidx.viewpager2.widget.ViewPager2
        android:id="@+id/viewPager"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

    <ProgressBar
        android:id="@+id/progressBar"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center" />

</FrameLayout>
```

- 추가적으로 Pager에 들어가는 item에 대한 xml은 다음과 같음, TextView 2개로 설정되어 있음
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <!--Pager에 들어가는 View-->

    <TextView
        android:id="@+id/quoteTextView"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginHorizontal="40dp"
        android:ellipsize="end"
        android:gravity="end|center_vertical"
        android:maxLines="6"
        android:textSize="30sp"
        app:layout_constraintBottom_toTopOf="@+id/nameTextView"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_bias="0.4"
        app:layout_constraintVertical_chainStyle="packed"
        tools:text="나는 생각한다 고로 존재한다" />

    <TextView
        android:id="@+id/nameTextView"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginTop="15dp"
        android:ellipsize="end"
        android:gravity="end|center_vertical"
        android:maxLines="1"
        android:textSize="20sp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="@id/quoteTextView"
        app:layout_constraintStart_toStartOf="@id/quoteTextView"
        app:layout_constraintTop_toBottomOf="@id/quoteTextView"
        tools:text="데카르트" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

### 구현코드
- MainActivity 코드에 앞서 명언에 대해서 명언의 내용과 이름을 담을 것이므로 이 부분에 대한 데이터 클래스를 만들고 ViewPager2 역시 adapter를 상속받아 연결함으로 이를 구현을 함
```kotlin
package techtown.org.todayquotes
/*
명언의 내용과 명언을 말한 사람에 대한 데이터를 저장하는 데이터 모델 클래스
 */
data class Quote(
    val quote: String,
    val name: String
)

```

```kotlin
package techtown.org.todayquotes

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/*
ViewPager에 추가를 하기 위한 Adapter 추가
 */
// ViewPager2는 RecyclerView 기반이므로 해당 adapter를 상속받음
class QuotesPagerAdapter(
    private val quotes: List<Quote>, // Quote 모델 받아옴
    private val isNameRevealed: Boolean // Remote할 요소로 Name의 reveal 여부이므로 이를 받아옴 Remote에서
): RecyclerView.Adapter<QuotesPagerAdapter.QuoteViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        QuoteViewHolder(
            // parent에 그대로 item_quote에 대해서 연결시켜줌
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_quote, parent, false)
        )

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        // quotes는 5개밖에 없으므로 값을 지정
        // quotes 사이즈는 5밖에 되지 않으므로 실제 포지션이 그 이상으로 갈 경우 사이즈로 나눠서 다시 맨 처음 값을 가르키게 만듬
        val actualPosition = position % quotes.size
        holder.bind(quotes[actualPosition], isNameRevealed)
    //원래 같으면 그냥 받으면 되지만 quotes는 5개 밖에 없으므로 무한 스와이프는 다르게 함
//        holder.bind(quotes[position], isNameRevealed) // Quote로 받은 모델의 내용을 bind를 함
    }

    // 원래라면 해당 모델에 저장된 quote 크기를 주면 됨
//    quotes.size // 해당 모델에 저장된 quote 크기
    // 하지만 무한 스와이프를 위해서 페이지 어댑터의 값을 Int의 맥스 값을 줘버림, 이렇게 하면 끝이 오긴 하지만 매우 큰 값이므로 충분히 무한 스와이프처럼 보이게 할 수 있음
    override fun getItemCount() = Int.MAX_VALUE

    class QuoteViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        // Pager에 연결하여 보여주기 위한 item_quote에 대해서 정의하고 사용함
        private val quoteTextView: TextView = itemView.findViewById(R.id.quoteTextView)
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)

        @SuppressLint("SetTextI18n")
        fun bind(quote:Quote, isNameRevealed: Boolean) {
           // 어떻게 랜더링 할 지 처리하는 함수, 불러온 View를 연결함, 큰 따옴표 붙임
            quoteTextView.text = "\"${quote.quote}\""

            if(isNameRevealed) {
                // isNameRevealed가 true일 경우만 name을 보여줌(isNameRevealed는 RemoteConfig에서 값을 바꾸면 그게 바로 반영됨)
                nameTextView.text = "-${quote.name}" // 대시 붙임
                nameTextView.visibility = View.VISIBLE
            } else {
                nameTextView.visibility = View.GONE
            }

        }
    }
}
```

- 위에서는 명언 + 이름을 담을 데이터 클래스를 만들고 이를 어댑터에 연결하는 작업을 한 것인데, 실질적인 이에 대한 데이터는 Firebase RemoteConfig에 이름을 보여줄지 여부와 함께 같이 아래와 같이 저장되어 있음

- 여기서 아래에 있는 name_revealed 부분에 의해서 true를 할 경우 위에서 보여진 화면 예시처럼 명언 + 이름이 나오지만 false로 값을 적용하고 설정하면 명언만 나오게 할 수 있음

- 이 부분에 대한 상세 코드는 아래 MainActivity.kt에서 Firebase와 연결하는 작업을 통해서 가능하게 함

![one](/Intermediate/TodayQuotes/img/three.png)


### MainActivity.kt
- 이 부분을 이제 메인에서 Firebase RemoteConfig에 연결을 하여 동기화하는 작업을 거치면 됨, 그러면서 View와 연결하는 작업을 함

```kotlin
package techtown.org.todayquotes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.absoluteValue

class MainActivity : AppCompatActivity() {

    private val viewPager: ViewPager2 by lazy {
        findViewById(R.id.viewPager)
    }
    
    private val progressBar: ProgressBar by lazy{
        findViewById(R.id.progressBar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        initData()
    }

    private fun initViews() {
        // ViewPager에 스와이프 전환시 효과를 주기 위해서 설정함
        viewPager.setPageTransformer { page, position ->
            // page -> 실제 변형을 나타낼 페이지 / position -> 현재 보여지는 위치에서 어디를 나타내는지 상대적으로 스크롤에 따라서 가지는 값(화면 보이는것을 0을 기준으로함)
            // alpha 값을 조정해서 스와이프의 효과를 줌, alpha는 0~1 사이의 값임 1이 다 보이는 것 0은 하나도 안 보이는 것임
            when {
                position.absoluteValue >= 1F -> {
                    // 신경쓰지 않을 위치일 경우
                    page.alpha = 0F
                }
                position == 0F -> {
                    page.alpha = 1F
                }
                else -> {
                    // 정중앙은 그대로 하면서 알파값은 줄어들고 커지는 함수식처럼 효과를 줌(투명도 넘어가는 등의 상황)
                    page.alpha = 1F - 2 * position.absoluteValue
                }
            }
        }
    }


    // 12시간 딜레이가 있음, 패치의 그 정도 시간이 걸림, 하지만 개발용으로 시간을 단축시킴
    private fun initData() {
        // firebase에서 설정한 remoteConfig에 접근함
        val remoteConfig = Firebase.remoteConfig
        remoteConfig.setConfigSettingsAsync(
            remoteConfigSettings {
                minimumFetchIntervalInSeconds = 0 // 앱을 곧바로 패치하게 설정함(비동기 설정)
            }
        )
        // 원래는 디폴트 config를 설정하지만 이 앱은 기본값이 없으므로 바로 액티브시킴
        remoteConfig.fetchAndActivate().addOnCompleteListener{
            // 패치가 완료된 시점에 progressbar를 안보이게 처리함
            progressBar.visibility = View.GONE
            // fetch 자체가 비동기로 일어나기 때문에 작어버을 확인하기 위해 리스너가 필요함
            if(it.isSuccessful) {
                // 패치가 성공했다면 그 값을 가져옴, remoteConfig에 저장한 값을 가져오기 위해 key 입력
                val quotes = parseQuotesJson(remoteConfig.getString("quotes"))
                val isNameRevealed = remoteConfig.getBoolean("is_name_revealed")
                displayQuotesPager(quotes, isNameRevealed) // 이렇게 RemoteConfig에서 받아서 Quote로 바꾼값에 대해서 Pager에 연결함

            }
        }
    }

    // 원래라면 Gson으로 변환하면 되지만 이 앱에서는 JSON 그대로 사용해볼 것임
    // remote에서 JSON을 받고 그걸 Quote 객체로 넘겨 Quote를 만듬
    private fun parseQuotesJson(json: String): List<Quote> {
        // JSON string을 가지고 JSON array를 받아 바꿈
        val jsonArray = JSONArray(json)
        // JSONArray는 JSONObject로 구성되어 있으므로 하나씩 가져와서 List에 저장하면 됨
        var jsonList = emptyList<JSONObject>()
        for(index in 0 until jsonArray.length()) {
            // 0부터 jsonArray에 있는 Object를 하나씩 가져옴
            val jsonObject = jsonArray.getJSONObject(index)
            jsonObject?.let{
                // 만약 jsonObject가 null이 아니라면 jsonList에 추가해줌(비어있는 리스트이므로 계속 뒤에다 덧붙임, 한개씩 jsonObject가 붙게됨)
                jsonList = jsonList + it
            }
        }

        return jsonList.map {
            // jsonList를 리턴하면 JSONObject를 리턴하므로 정의했던 데이터모델에 적용함
            // JSON에 저장된 quote와 name을 가져옴, 이를 Quote 리스트로 저장함
            Quote(
                // Quote 객체에 설정함
                quote = it.getString("quote"),
                name = it.getString("name"))
        }
    }

    // PagerAdapter에 연결함함
   private fun displayQuotesPager(quotes:List<Quote>, isNameRevealed: Boolean) {
        // QuotesPagerAdapter 만든 것을 연결함
        val adapter = QuotesPagerAdapter(
            quotes = quotes,
            isNameRevealed = isNameRevealed
        )

        // 2로 끊어서 중앙부터 위치하게함, 이렇게 된다면 왼쪽으로도 무한 스와이프가 가능하게 됨
        // 만약 첫번째가 중요하다면 그 값을 맞춰서 계산하여 넣어주면 됨
        viewPager.adapter = adapter
        viewPager.setCurrentItem(adapter.itemCount/2, false)
    }
}
```