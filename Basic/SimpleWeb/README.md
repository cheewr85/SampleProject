## 심플 웹 브라우저
- 웹 브라우저 앱과 유사하게 구현을 함

- 아주 기본적인 기능 위주로 구현 웹사이트를 불러오고 뒤로가기, 앞으로 가기, 홈버튼 등 

- 웹사이트의 로딩 정도 역시 확인할 수 있음

## 메인화면
- 실제 스마트폰 웹 브라우저와 유사하게 화면을 구성함

- 여기서 추가적으로 체크할 부분은 버튼에 대해서 클릭 및 터치시에 파동처럼 퍼져나가는 이펙트인 Ripple Effect를 xml상에서 background로 직접 설정해주었다

- 그리고 최대한 깔끔하게 처리하기 위해서 ConstraintLayout으로 설정했으므로 이에 대한 Ratio 역시 활용해서 정리했다

- 흔히 웹페이지 로딩 상태에 대해서 보여주는데 이를 ContentLoadingProgressBar를 활용하여 로딩 상태를 보여주기 위해서 구현을 했고 웹페이지가 로딩중일 때 보여주고 로딩이 끝나면 숨기고 로딩 상태에 대해 보여주기 위한 코드처리를 추가적으로 하였다

### 실행화면
![one](/Basic/SimpleWeb/img/one.png)
![one](/Basic/SimpleWeb/img/two.png)
![one](/Basic/SimpleWeb/img/three.png)

- 위와 같은 UI를 바탕으로 주소창을 입력하고 뒤로가기 앞으로가기 버튼은 처음엔 비활성화이고 나중에 활성화가 됨

- 그리고 추가적으로 스와이프해서 새로고침도 가능함, 이는 SwipeRefreshLayout 라이브러리를 추가하고 활용함

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <!-- 주소창 영역-->
    <androidx.constraintlayout.widget.ConstraintLayout
        android:id="@+id/toolbar"
        android:layout_width="0dp"
        android:layout_height="?attr/actionBarSize"
        android:background="@color/white"
        android:elevation="4dp"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent">

        <!--홈버튼, 주소창, 뒤로가기버튼, 앞으로가기버튼-->
        <ImageButton
            android:id="@+id/goHomeButton"
            android:layout_width="0dp"
            android:layout_height="0dp"
            android:background="?attr/selectableItemBackground"
            android:src="@drawable/ic_home"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintDimensionRatio="1:1"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            tools:ignore="ContentDescription" />

        <EditText
            android:id="@+id/addressBar"
            android:layout_width="0dp"
            android:layout_height="32dp"
            android:background="@drawable/shape_address_bar"
            android:imeOptions="actionDone"
            android:importantForAutofill="no"
            android:inputType="textUri"
            android:paddingHorizontal="16dp"
            android:selectAllOnFocus="true"
            android:textSize="14sp"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintLeft_toRightOf="@id/goHomeButton"
            app:layout_constraintRight_toLeftOf="@id/goBackButton"
            app:layout_constraintTop_toTopOf="parent"
            tools:ignore="LabelFor" />

        <ImageButton
            android:id="@+id/goBackButton"
            android:layout_width="0dp"
            android:layout_height="0dp"
            android:background="?attr/selectableItemBackground"
            android:src="@drawable/ic_back"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintDimensionRatio="1:1"
            app:layout_constraintRight_toLeftOf="@id/goForwardButton"
            app:layout_constraintTop_toTopOf="parent"
            tools:ignore="ContentDescription" />

        <ImageButton
            android:id="@+id/goForwardButton"
            android:layout_width="0dp"
            android:layout_height="0dp"
            android:background="?attr/selectableItemBackground"
            android:src="@drawable/ic_forward"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintDimensionRatio="1:1"
            app:layout_constraintRight_toRightOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            tools:ignore="ContentDescription" />

    </androidx.constraintlayout.widget.ConstraintLayout>

    <androidx.swiperefreshlayout.widget.SwipeRefreshLayout
        android:id="@+id/refreshLayout"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toBottomOf="@id/toolbar">

        <WebView
            android:id="@+id/webView"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />

    </androidx.swiperefreshlayout.widget.SwipeRefreshLayout>

    <!--현재 웹페이지의 로딩 상태를 보여주기 위해서 구현-->
    <androidx.core.widget.ContentLoadingProgressBar
        android:id="@+id/progressBar"
        style="@style/Widget.AppCompat.ProgressBar.Horizontal"
        android:layout_width="0dp"
        android:layout_height="2dp"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toBottomOf="@id/toolbar" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

### 구현 코드
- 주로 WebView를 가져와서 Url을 로드하게끔 하면 됨, 그리고 페이지 로딩 상태에 대해서 확인하고 이 상태를 보여주고 체크를 함

- 그 외에 뒤로가기, 앞으로가기, 홈버튼 기능은 WebView에 있는 메소드를 그대로 활용해서 쓰면 됨

```kotlin
package techtown.org.simpleweb

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.webkit.URLUtil
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.widget.ContentLoadingProgressBar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {

    private val goHomeButton: ImageButton by lazy {
        findViewById(R.id.goHomeButton)
    }

    private val addressBar: EditText by lazy {
        findViewById(R.id.addressBar)
    }

    private val goBackButton: ImageButton by lazy {
        findViewById(R.id.goBackButton)
    }

    private val goForwardButton: ImageButton by lazy {
        findViewById(R.id.goForwardButton)
    }

    private val refreshLayout: SwipeRefreshLayout by lazy {
        findViewById(R.id.refreshLayout)
    }

    private val webView: WebView by lazy {
        findViewById(R.id.webView)
    }

    private val progressBar: ContentLoadingProgressBar by lazy {
        findViewById(R.id.progressBar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        bindViews()
    }

    override fun onBackPressed() {
        // 앱 내의 뒤로가기 버튼 말고 실제 뒤로가기 누를 시 종료가 아닌 웹을 뒤로가게 처리하기 위해 오버라이딩 함

        // canGoBack 즉 히스토리에 쌓여 있다면 goBack을 함
        if(webView.canGoBack()){
            webView.goBack()
        } else { // 그게 아니라면 그냥 종료
            super.onBackPressed()
        }

    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initViews() {
        // 직접 만든 WebView를 구현하기 위해서 아래와 같이 WebViewClient를 해줘야 앱 내에서 실행됨
        // Web자체의 동작은 JS로 하는데 이를 안드로이드에선 허용하지 않음(보안상의 이유로) 그렇기 때문에 웹을 정상적으로 사용하기 위해서 이 부분을 허용해야함
        // 처음에 초기 Url을 설정함, 하지만 기본 동작은 Url을 다루는 디폴트 웹 브라우저로 연동됨
        // WebChromeClient로 로딩 상태를 파악함, 주로 WebView로 다양하게 쓴다면 webViewClient, webChromeClient 둘 다 씀
        webView.apply {
            // 깔끔하게 처리하기 위해 apply 적용
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            settings.javaScriptEnabled = true
            loadUrl(DEFAULT_URL)
        }
    }

    private fun bindViews() {

        // 홈버튼 누르면 초기로 돌아감, 이는 즉 원래 초기 화면으로 가게함
        goHomeButton.setOnClickListener {
            webView.loadUrl(DEFAULT_URL)
        }

        // 액션이 수행됐을 때 즉 입력이 끝나고 완료의 기능을 하는 액션 버튼을 눌렀을 때의 이벤트 처리함, 아이디는 xml 상에서 처리한 imeOption의 아이디가 넘어옴
        // 그러면 지금 기준으로 imeOption을 actionDone으로 했으므로 그 값이 넘어옴, return 설정을 true를 하면 이 부분은 끝내고 false를 하면 다른 곳에서 이벤트 처리 가능함
        addressBar.setOnEditorActionListener { v, actionId, event ->
            // 주소입력후 Done 버튼을 눌렀다면
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                // 주소창에 입력한 텍스트를 가져와서 load함, 여기서 http가 붙지 않고 치는 경우도 자동으로 할 수 있게끔 처리함
                val loadingUrl = v.text.toString()
                // http, https가 있는지 확인 없으면 false를 반환함
                if(URLUtil.isNetworkUrl(loadingUrl)) {
                    // true인 경우 그냥 로드하면 됨
                    webView.loadUrl(loadingUrl)
                } else {
                    webView.loadUrl("http://$loadingUrl")
                }

            }

            return@setOnEditorActionListener false // 키보드를 ACTION_DONE으로 내려야 하므로 아직 Action을 사용처리 안한 false를 반환함
        }

        // 네비게이션 기능을 구현하기 위해서 각 버튼의 이벤트 처리를 함
        goBackButton.setOnClickListener {
            // 뒤로가기 기능 구현, 이전의 히스토리로 감
            webView.goBack()
        }

        goForwardButton.setOnClickListener {
            webView.goForward()
        }

        // 스와이프해서 새로고침하는 레이아웃을 추가함, 새로고침 기능을 활용하기 위해서 이벤트를 추가함
        refreshLayout.setOnRefreshListener {
            // 실제 WebView를 다시 불러오게 함
            webView.reload()
        }

    }

    // inner로 하는 이유는 상위, 즉 현재 MainActivity의 프로퍼티에 접근해서 처리하기 위해서 사용함
    // 여기서 WebViewClient를 직접 정의하여서 스와이프하고 새로고침 이후 스와이프하고 로딩하는 아이콘을 없애기 위해서 직접 정의해서 사용함
    inner class WebViewClient: android.webkit.WebViewClient() {

        // 시작하면 로딩을 보여주므로 progrssbar를 보여주면 됨
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)

            progressBar.show()
        }

        // 페이지가 다 로딩되었을 때 refreshLayout에 새로고침이 완료되면 자연스럽게 사라지도록 하게끔 false를 줌
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            // false 처리를 하지 않으면 스와이프 새로고침 할 때 뜨는 view가 계속해서 남아있음
            refreshLayout.isRefreshing = false

            // 로딩이 다 된 것이니 굳이 상태 확인이 필요 없으므로 progressBar를 숨김
            progressBar.hide()
            // 히스토리가 있는 경우 뒤로가기, 앞으로가기를 누르고 그렇지 않으면 못가게 함
            goBackButton.isEnabled = webView.canGoBack()
            goForwardButton.isEnabled = webView.canGoForward()
            // 최종적으로 로딩되는 url을 보이게 함
            addressBar.setText(url)

        }
    }

    // 로딩이 어느정도 진행되었는지 알기 위해서 선언, 브라우저 관점 이벤트를 알기 위해서
    inner class WebChromeClient: android.webkit.WebChromeClient() {

        // 현재 페이지의 로딩 상태를 보여줌, 그대로 progressBar에 보여주면 됨 0~100이므로
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)

            progressBar.progress = newProgress
        }
    }

    companion object {
        // 디폴트 URL이 바뀔 수 있으므로 상수로 정의해서 사용함, 하드코딩을 지양함
        private const val DEFAULT_URL = "http://www.google.com"
    }
}
``` 