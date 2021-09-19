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