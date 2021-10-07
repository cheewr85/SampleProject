## 틴더앱
- Firebase Authentication을 통해 이메일 로그인과 페이스북 로그인 가능

- Firebase Realtime Database를 활용하여 유저 정보를 저장하고 매칭 시스템처럼 저장하기 위해서 Database의 기록을 남김

- 틴더에서 사용하는 스와이프같은 기능을 활용하기 위해서 오픈소스에 있는 CardStackView를 활용함

## 로그인 화면
- 이메일 & 비밀번호를 입력받을 수 있고, 회원가입 버튼을 누르면 Firebase에 해당 정보가 회원가입됨이 저장됨

- 로그인을 하게 되면 입력한 이메일 & 비밀번호로 로그인을 함

- 페이스북 로그인 역시 페이스북과 연동하여 내 페이스북 계정을 연결하여서 로그인 처리를 할 수 있음

- 페이스북 로그인 버튼은 페이스북 라이브러리 활용해서 만듬

![one](/Intermediate/Tinder/img/one.png)

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="24dp"
    tools:context=".LoginActivity">

    <EditText
        android:id="@+id/emailEditText"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <EditText
        android:id="@+id/passwordEditText"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:inputType="textPassword"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/emailEditText" />

    <Button
        android:id="@+id/loginButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="로그인"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toBottomOf="@id/passwordEditText" />

    <Button
        android:id="@+id/signUpButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginEnd="4dp"
        android:text="회원가입"
        app:layout_constraintEnd_toStartOf="@id/loginButton"
        app:layout_constraintTop_toBottomOf="@id/passwordEditText" />

    <com.facebook.login.widget.LoginButton
        android:id="@+id/facebookLoginButton"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginTop="30dp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/loginButton" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

### LoginActivity.kt
- 일반적인 이메일 & 비밀번호 로그인의 경우 EditText로 입력받아 그 값에 대해서 FirebaseAuth를 활용해서 로그인 처리와 회원가입 처리를 하고, 이 정보에 대해서 DB에 처리하기 위해서 DB에 추가도 함

- 그리고 페이스북 로그인의 경우 페이스북에 로그인을 할 때 로그인시 페이스북 앱 혹은 웹 상에서 로그인을 한 후 그 토큰값이 넘어 오는데 그 값에 대해서 콜백처리를 하여서 로그인 처리를 함

```koltin
package techtown.org.tinder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    // 이메일 패스워드 받아와서 Firebase Auth에 전달해줄 것임
    private lateinit var auth: FirebaseAuth
    // facebook 로그인 버튼 누른 이후 facebook 앱 혹은 웹 로그인 후 완료한 뒤 activity callback으로 받기 때문에 callbackmanager를 선언함(facebook 라이브러리에 있음)
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Auth 인스턴스를 가져옴
        auth = Firebase.auth
        // callbackManger 초기화
        callbackManager = CallbackManager.Factory.create()

        initLoginButton()
        initSignUpButton()
        initEmailAndPasswordEditText()
        initFacebookLoginButton()
    }

    private fun initLoginButton() {
        // 로그인 버튼 눌렀을 때 동작 처리
        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            // 이메일과 패스워드를 먼저 가져옴
            val email = getInputEmail()
            val password = getInputPassword()

            // 이메일과 패스워드를 파라미터로 넘겨 받고 FirebaseAuth에 있는 SignIn 기능을 사용할 수 있음
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task -> // 반환값이 Task임<AuthResult>
                    if(task.isSuccessful) {
                        // 성공적으로 수행이 됐다면 LoginActivity를 종료시킴, 성공적으로 됐다면 FirebaseAuth에 저장될 것이므로
                        handleSuccessLogin()
                    } else { // 실패시 토스트 메시지 띄움
                        Toast.makeText(this, "로그인에 실패했습니다. 이메일 또는 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun initSignUpButton() {
        // 회원가입 버튼 눌렀을 때 동작 처리
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        signUpButton.setOnClickListener {
            // 이메일과 패스워드를 먼저 가져옴
            val email = getInputEmail()
            val password = getInputPassword()

            // 회원가입을 처리하는 FirebaseAuth에 있는 메소드 사용, 위의 로그인과 비슷한 로직으로 수행함
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        Toast.makeText(this, "회원가입에 성공했습니다. 로그인 버튼을 눌러 로그인 해주세요", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "이미 가입한 이메일이거나, 회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

        }
    }

    private fun initEmailAndPasswordEditText() {
        // 이메일과 패스워드가 만약 null 값일 경우에 대한 예외처리
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signUpButton = findViewById<Button>(R.id.signUpButton)

        // 텍스트 변화가 있을 때 즉 둘 다 비어있다면, 로그인, 회원가입 버튼 비활성화 시킬 것임
        emailEditText.addTextChangedListener { // 텍스트 입력될 때마다 리스너로 이벤트가 내려옴
            // 둘 다 비어있지 않을 때만 true 반환
            val enable = emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()
            // 그리고 그럴때만 활성화 시킴
            loginButton.isEnabled = enable
            signUpButton.isEnabled = enable
        }

        // 텍스트 변화가 있을 때 즉 둘 다 비어있다면, 로그인, 회원가입 버튼 비활성화 시킬 것임
        passwordEditText.addTextChangedListener { // 텍스트 입력될 때마다 리스너로 이벤트가 내려옴
            // 둘 다 비어있지 않을 때만 true 반환
            val enable = emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()
            // 그리고 그럴때만 활성화 시킴
            loginButton.isEnabled = enable
            signUpButton.isEnabled = enable
        }
    }

    private fun initFacebookLoginButton() {
        // 페이스북 로그인 버튼 초기화
        val facebookLoginButton = findViewById<LoginButton>(R.id.facebookLoginButton)

        facebookLoginButton.setPermissions("email", "public_profile") // 로그인 버튼 눌렀을 때 유저에게 받아올 정보를 설정함(이메일과 프로필 추가, 다른 정보도 가져올 수 있음)
        facebookLoginButton.registerCallback(callbackManager, object: FacebookCallback<LoginResult>{ // 로그인 버튼의 콜백도 추가함 앞서 선언한 콜백매니저
            // LoginResult를 통해서 로그인 결과를 받아옴
            override fun onSuccess(result: LoginResult) {
                // 로그인이 성공했을 때(?는 제외함, 성공한 케이스에서 null이 넘어올 수 없어서)
                // LoginResult에서 access Token을 가져온 뒤 Firebase에 알려줌
                val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                // firebase에서 facebook에 로그인한 access token을 아래와 같이 넘겨줌
                auth.signInWithCredential(credential)
                    .addOnCompleteListener(this@LoginActivity) { task ->
                        if(task.isSuccessful) {
                            handleSuccessLogin()
                        } else {
                            Toast.makeText(this@LoginActivity, "페이스북 로그인이 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

            override fun onCancel() {
                // 로그인 하다가 취소했을 때
            }

            override fun onError(error: FacebookException?) {
                // 로그인 에러가 났을 때
                Toast.makeText(this@LoginActivity, "페이스북 로그인이 실패했습니다.", Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun getInputEmail(): String {
        // 입력한 이메일을 가져와서 리턴함
        return findViewById<EditText>(R.id.emailEditText).text.toString()
    }

    private fun getInputPassword(): String {
        // 입력한 패스워드를 가져와서 리턴함
        return findViewById<EditText>(R.id.passwordEditText).text.toString()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // callback을 추가해서 결과를 넘김
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleSuccessLogin() {
        // 로그인이 성공했을 경우, 다시 한 번 확인, 그리고 DB에 저장함
        if(auth.currentUser == null) {
            Toast.makeText(this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        // 유저 아이디 받아옴, DB처리를 위해
        val userId = auth.currentUser?.uid.orEmpty()
        val currentUserDB = Firebase.database.reference.child("Users").child(userId) // Users의 데이터를 가져옴, 없으면 userId 추가하고 있으면 그 데이터 가져옴
        val user = mutableMapOf<String, Any>() // key-value 형태의 map으로 정보 저장
        user["userId"] = userId // userId 저장
        currentUserDB.updateChildren(user) // map으로 저장한 user를 DB에 저장함, 제일 상위의 리스트가 생겨 userId 추가됨

        finish()
    }
}
```

### MainActivity.kt
- MainActivity의 경우 로그인 상태를 확인하여서 인텐트를 통해서 Activity 처리를 하는 기능만을 함
```kotlin
package techtown.org.tinder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    // 바로 auth 초기화
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        if (auth.currentUser == null) {
            // 만약 현재 유저가 없다면 로그인 정보가 없다면, 로그인이 되지 않았을 때 로그인 액티비티 실행하게함
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            // 만약 로그인이 된 상태면 Like Activity 실행함
            startActivity(Intent(this, LikeActivity::class.java))
            finish() // 메인은 끔
        }
    }
}
```

## 매칭 화면
- Tinder에서 쓰는 Swipe Animation을 사용하기 위해서 오픈소스 중에서 CardStackView를 활용함

- 이와 관련되서 앱 단위 gradle에서 직접 해당 라이브러리를 추가하고 사용해줘야함

![one](/Intermediate/Tinder/img/two.png)

- 여기서 체크해야할 것은 CardStackView 역시 RecyclerView와 같은 어댑터를 활용하는 점이 특징임

- 아래와 같이 매칭의 경우 화면을 구성함
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".LikeActivity">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="매칭할 카드가 없습니다."
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <com.yuyakaido.android.cardstackview.CardStackView
        android:id="@+id/cardStackView"
        android:layout_width="0dp"
        android:layout_height="300dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="LIKE"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="DISLIKE"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <Button
        android:id="@+id/matchListButton"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:text="매치 리스트 보기"
        app:layout_constraintBottom_toTopOf="@id/signOutButton"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent" />

    <Button
        android:id="@+id/signOutButton"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:text="로그아웃 하기"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

- cardItem으로 사용할 xml도 정의함
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.cardview.widget.CardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_margin="24dp"
    app:cardCornerRadius="16dp">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="#FFC107">

        <TextView
            android:id="@+id/nameTextView"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:gravity="center"
            android:text="name"
            android:textColor="@color/black"
            android:textSize="40sp" />

    </LinearLayout>

</androidx.cardview.widget.CardView>

```

- CardItem으로 DB에서 받아서 데이터를 처리할 것이므로 모델 클래스를 아래와 같이 먼저 만듬

### CardItem.kt
```kotlin
package techtown.org.tinder

/*
Swipe Animation으로 사용할 CardStackView에서의 Model Item
UserId와 name을 받음
 */

data class CardItem(
    val userId: String,
    var name: String
)
```

- 그리고 앞서 말했듯이 RecyclerView에서 사용하는 Adapter와 사용법이 유사하기 때문에 CardStackView에 대한 adapter역시 유사하게 작성해서 활용함
### CardItemAdapter.kt
```language
package techtown.org.tinder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/*
CardStackView의 경우에는 RecyclerView adapter를 활용한 것이므로 해당 adapter를 활용함
 */
class CardItemAdapter: ListAdapter<CardItem, CardItemAdapter.ViewHolder>(diffUtil) {

    // ViewHolder 추가
    inner class ViewHolder(private val view: View): RecyclerView.ViewHolder(view) {

        fun bind(cardItem: CardItem) {
            // view를 불러와서 binding을 해 줌
            view.findViewById<TextView>(R.id.nameTextView).text = cardItem.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // item xml을 연결하기 위해서 ViewHolder에서 inflater를 써서 만듬
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_card, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position]) // 현재 item bind
    }

    companion object {
        // diffUtil 맞는지 컨텐츠 및 아이템 확인하기 위해서
        val diffUtil = object : DiffUtil.ItemCallback<CardItem>() {
            override fun areItemsTheSame(oldItem: CardItem, newItem: CardItem): Boolean {
                return oldItem.userId == newItem.userId
            }

            override fun areContentsTheSame(oldItem: CardItem, newItem: CardItem): Boolean {
                return oldItem == newItem
            }

        }
    }
}
```

### LikeActivity.kt
- 위에서 정의한 모델 클래스, 어댑터를 가지고 이제 LikeActivity에 대한 로직을 마듬

- 알아둘 부분은 CardStackView를 사용하기 위해 해당 Interface를 구현해서 메소드를 사용한 것과 이 모든 Like, 매칭 처리, 유저 체크 확인하는 작업은 Firebase Realtime Database를 참조하여서 데이터를 불러온 다음에 CardStackView와 매칭한 유저에 대한 데이터 모두 받아온 데이터를 기반으로 모델 클래스의 리스트로 만든 뒤 이를 어댑터에 연결을 하는 작업을 거침

- 그리고 Firebase Realtime Database를 사용하는데 있어서 DB에서의 이벤트 처리를 리스너를 통해서 처리했음

```kotlin
 package techtown.org.tinder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction
import techtown.org.tinder.DBKey.Companion.DIS_LIKE
import techtown.org.tinder.DBKey.Companion.LIKE
import techtown.org.tinder.DBKey.Companion.LIKED_BY
import techtown.org.tinder.DBKey.Companion.NAME
import techtown.org.tinder.DBKey.Companion.USERS
import techtown.org.tinder.DBKey.Companion.USER_ID

 class LikeActivity : AppCompatActivity(), CardStackListener { // CardStack 사용시 쓰이는 상호작용을 쓰기 위해서 인터페이스 구현함

    // auth를 가져옴
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var userDB: DatabaseReference

    // 어댑터 선언
    private val adapter = CardItemAdapter()
    private val cardItems = mutableListOf<CardItem>() // data 객체 list로 만듬
    private val manager by lazy {
        CardStackLayoutManager(this, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_like)

        // currentUserDB에 이름이 있는지 체크함
        userDB = Firebase.database.reference.child(USERS) // 다른 유저 정보를 알아오기 위해서

        val currentUserDB = userDB.child(getCurrentUserID()) // userId 내용을 가져옴
        currentUserDB.addListenerForSingleValueEvent(object: ValueEventListener{ // 하나의 값만 가져올 것이므로

            override fun onDataChange(snapshot: DataSnapshot) {
                // 데이터 수정, 데이터가 추가될 때 현재 이벤트로 들어옴
                // 이름이 변경 혹은 누군가 좋아요를 눌렀을 때
                if(snapshot.child(NAME).value == null) {
                    // 만약 이름이 없다면 이름을 추가함
                    showNameInputPopup()
                    return
                }

                getUnSelectedUsers()
                // 유저 정보 갱신을 위한 함수
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        initCardStackView()
        // 버튼을 초기화함
        initSignOutButton()
        initMatchedListButton()
    }

    private fun initCardStackView() {
        // CardStackView를 초기화 하는 함수
        val stackView = findViewById<CardStackView>(R.id.cardStackView)
        // 레이아웃 매니저, 어댑터 연결하는게 초기화(리사이클러뷰 처럼)
        stackView.layoutManager = manager
        stackView.adapter = adapter
    }

    private fun initSignOutButton() {
        val signOutButton = findViewById<Button>(R.id.signOutButton)
        signOutButton.setOnClickListener {
            auth.signOut()
            // 로그아웃 하고 메인으로 넘어감, 예외처리
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun initMatchedListButton() {
        val matchedListButton = findViewById<Button>(R.id.matchListButton)
        matchedListButton.setOnClickListener {
            // 매치 화면으로 넘어감
            startActivity(Intent(this, MatchedUserActivity::class.java))
        }
    }

    private fun getUnSelectedUsers() {
        userDB.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                // 초기에 user를 불러오거나 새로운 유저가 등록되는 경우
                if(snapshot.child(USER_ID).value != getCurrentUserID()
                    && snapshot.child(LIKED_BY).child(LIKE).hasChild(getCurrentUserID()).not()
                    && snapshot.child(LIKED_BY).child(DIS_LIKE).hasChild(getCurrentUserID()).not()) {
                    // 선택되지 않는 유저만 불러옴, userId가 같은 경우 제외하고 좋아요, 싫어요 한 유저에서 내가 있는 경우도 제외함
                    // 즉 내가 한 번도 선택하지 않은 유저임
                    val userId = snapshot.child(USER_ID).value.toString()
                    var name = "undecided" // 초기값은 없는 값으로
                    if (snapshot.child(NAME).value != null) {
                        name = snapshot.child(NAME).value.toString() // name이 비어있다면 name을 가져옴
                    }

                    cardItems.add(CardItem(userId, name)) // 리스트에 추가함
                    // 어댑터에 해당 리스트를 추가하고 데이터 갱신을 함
                    adapter.submitList(cardItems)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                // 이름이 바뀌었을 때 혹은 Like를 눌렀을 때
                // 변경된 유저를 아래와 같이 찾음
                cardItems.find { it.userId == snapshot.key }?.let {
                    it.name = snapshot.child(NAME).value.toString()
                }
                // 어댑터 갱신을 함
                adapter.submitList(cardItems)
                adapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) { }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) { }

            override fun onCancelled(error: DatabaseError) { }

        })
    }

    private fun showNameInputPopup() {
        // 이름 입력을 위해서 팝업으로 띄어서 입력 받음(AlertDialog를 통해서 EditText로 입력받음, View 추가)
        val editText = EditText(this) // 추가하고자 하는 View 선언

        AlertDialog.Builder(this)
            .setTitle(R.string.write_name)
            .setView(editText) // editText 추가
            .setPositiveButton("저장") { _, _ ->
                if(editText.text.isEmpty()) {
                    showNameInputPopup() // 만약 입력이 없고 누르면 다시 팝업 띄움
                } else {
                    // 유저이름 저장
                    saveUserName(editText.text.toString())
                }
            }
            .setCancelable(false) // 취소를 하지 못하게 설정
            .show()
    }

    private fun saveUserName(name: String) {
        // 유저 아이디 받아옴, DB처리를 위해
        val userId = getCurrentUserID()
        val currentUserDB = userDB.child(userId) // Users의 데이터를 가져옴, 없으면 userId 추가하고 있으면 그 데이터 가져옴
        val user = mutableMapOf<String, Any>() // key-value 형태의 map으로 정보 저장
        user["userId"] = userId // userId 저장
        user["name"] = name // name을 저장함
        currentUserDB.updateChildren(user) // map으로 저장한 user를 DB에 저장함, 제일 상위의 리스트가 생겨 userId, name 추가됨

        getUnSelectedUsers()
        // 유저 정보를 가져오는 함수
    }

    private fun getCurrentUserID(): String {
        // 현재 UserID를 가져옴
        if(auth.currentUser == null) {
            // 예외처리함 만약 없다면
            Toast.makeText(this,"로그인이 되어있지 않습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }

        return auth.currentUser?.uid.orEmpty()
   }

    private fun like() {
        // userId를 알아서 like 처리를 해 줌
        val card = cardItems[manager.topPosition - 1] // cardItems의 item을 가져옴 cardStack에서의 위치에서
        cardItems.removeFirst() // 데이터도 실제로 지워버림

        // 나의 currentUserId를 상대방의 like dislike에 저장함
        userDB.child(card.userId)
            .child("likedBy")
            .child("like")
            .child(getCurrentUserID())
            .setValue(true)

        // 매칭이 되었다는 걸 알려줘야함, 그 시점을 봐야함
        saveMatchIfOtherUserLikedMe(card.userId)
        Toast.makeText(this, "${card.name}님을 Like 하셨습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun disLike() {
        // userId를 알아서 dislike 처리를 해 줌
        val card = cardItems[manager.topPosition - 1] // cardItems의 item을 가져옴 cardStack에서의 위치에서
        cardItems.removeFirst() // 데이터도 실제로 지워버림

        // 나의 currentUserId를 상대방의 like dislike에 저장함
        userDB.child(card.userId)
            .child("likedBy")
            .child("disLike")
            .child(getCurrentUserID())
            .setValue(true)

        Toast.makeText(this, "${card.name}님을 disLike 하셨습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun saveMatchIfOtherUserLikedMe(otherUserId: String) {
        // 상대방의 유저의 id를 가져옴, like을 누른 유저에 대해서
        val otherUserDB = userDB.child(getCurrentUserID()).child("likedBy").child("like").child(otherUserId)
        // 만약 like을 누른 유저의 값이 true라면 나를 like 누른 것임
        otherUserDB.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value == true) {
                    // 만약 상대방이 like를 눌렀다면 나도 like을 누른거라서 매칭이 된 것임
                    userDB.child(getCurrentUserID())
                        .child("likedBy")
                        .child("match")
                        .child(otherUserId)
                        .setValue(true)

                    // 상대방의 DB에도 매칭을 저장함
                    userDB.child(otherUserId)
                        .child("likedBy")
                        .child("match")
                        .child(getCurrentUserID())
                        .setValue(true)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    // swipe 말고 쓸 일이 없을 것임, 현재 앱에선
    override fun onCardDragging(direction: Direction?, ratio: Float) { }

    override fun onCardSwiped(direction: Direction?) {
        // 스와이프를 해서 이벤트 처리를 함, 왼쪽 오른쪽 처리만 해주면 됨
        when(direction) {
            Direction.Right -> like() // 오른쪽 스와이프시 like
            Direction.Left -> disLike() // 왼쪽 스와이프시 dislike
            else -> {

            }
        }
    }

    override fun onCardRewound() { }

    override fun onCardCanceled() { }

    override fun onCardAppeared(view: View?, position: Int) { }

    override fun onCardDisappeared(view: View?, position: Int) { }
}
```

## 매치 리스트 화면
- DB 상으로 그리고 상호간의 Like를 했다면 아래와 같이 매치된 유저가 나옴

![one](/Intermediate/Tinder/img/three.png)

- 리사이클러뷰를 활용하여 매치된 유저를 나열함

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MatchedUserActivity">

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/matchedUserRecyclerView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"/>

</androidx.constraintlayout.widget.ConstraintLayout>
```

- 리사이클러뷰를 활용했으므로 해당 ItemView 역시 구현함
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="10dp">

    <TextView
        android:id="@+id/userNameTextView"
        android:textSize="30sp"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>


</LinearLayout>
```

- 그리고 리사이클러뷰로 구현했기 때문에 이에 맞게 어댑터 역시 구현을 함

### MatchedUserAdapter.kt
```kotlin
package techtown.org.tinder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class MatchedUserAdapter: ListAdapter<CardItem, MatchedUserAdapter.ViewHolder>(diffUtil) {

    // ViewHolder 추가
    inner class ViewHolder(private val view: View): RecyclerView.ViewHolder(view) {

        fun bind(cardItem: CardItem) {
            // view를 불러와서 binding을 해 줌
            view.findViewById<TextView>(R.id.userNameTextView).text = cardItem.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // item xml을 연결하기 위해서 ViewHolder에서 inflater를 써서 만듬
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_matched_user, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position]) // 현재 item bind
    }

    companion object {
        // diffUtil 맞는지 컨텐츠 및 아이템 확인하기 위해서
        val diffUtil = object : DiffUtil.ItemCallback<CardItem>() {
            override fun areItemsTheSame(oldItem: CardItem, newItem: CardItem): Boolean {
                return oldItem.userId == newItem.userId
            }

            override fun areContentsTheSame(oldItem: CardItem, newItem: CardItem): Boolean {
                return oldItem == newItem
            }

        }
    }
}

```

### MatchedUserActivity.kt
- 그리고 해당 액티비티에서는 리사이클러뷰를 불러와서 어댑터를 연결 시켜주는 작업을 할 것인데, 여기서 매치된 리스트를 가져올 때 RealtimeDatabase에 접근하여서 해당 부분에 대한 이벤트 처리를 한 뒤 어댑터에 연결하는 작업을 해줘야함

```kotlin
package techtown.org.tinder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MatchedUserActivity : AppCompatActivity() {

    // auth를 가져옴
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var userDB: DatabaseReference

    private val adapter = MatchedUserAdapter()
    private val cardItems = mutableListOf<CardItem>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matched_user)

        userDB = Firebase.database.reference.child("Users")

        initMatchedUserRecyclerView()
        getMatchUsers()
    }

    private fun initMatchedUserRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.matchedUserRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun getMatchUsers() {
        // like에 매치된 리스트를 가져옴
        val matchedDB = userDB.child(getCurrentUserID()).child("likedBy").child("match")

        matchedDB.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if(snapshot.key?.isNotEmpty() == true) {
                    // matched 키가 존재한다면 데이터를 UserDB에서 다시 가져와야함
                    getUserByKey(snapshot.key.orEmpty())
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    private fun getUserByKey(userId: String) {
        userDB.child(userId).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cardItems.add(CardItem(userId, snapshot.child("name").value.toString()))
                // 매치된 userId의 name을 가져옴
                adapter.submitList(cardItems) // 그리고 그 유저의 아이디를 보이게 갱신함
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun getCurrentUserID(): String {
        // 현재 UserID를 가져옴
        if(auth.currentUser == null) {
            // 예외처리함 만약 없다면
            Toast.makeText(this,"로그인이 되어있지 않습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }

        return auth.currentUser?.uid.orEmpty()
    }
}
```