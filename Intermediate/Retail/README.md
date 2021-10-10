## 중고거래 앱
- Firebase Authentication 기능을 활용하여 로그인 회원가입 구현

- 회원 기반으로 중고거래 아이템 이미지는 Firebase Storage를 통해서 저장함

- Firebase Realtime Database에서 중고거래 아이템에 관련된 내용을 저장해둠

- Fragment와 BottomNavigation을 활용하여 홈화면 채팅리스트 마이페이지 구현

- 홈화면의 경우 올린 아이템들이 RecyclerView 형태로 볼 수 있음, FloatingButton을 활용하여 아이템을 추가하는 액티비티 별도 구현

- 채팅 리스트의 경우 채팅하는 상대가 저정되어 있고 채팅방 액티비티를 별도로 만듬, 채팅 리스트 클릭시 넘어가서 Database에 있는 내용을 가져와서 의사소통을 함

## 메인화면
- FrameLayout을 활용하여 각 프래그먼트를 BottomNavigation에 따라서 각 화면에 해당하는 프래그먼트 나타나게 함

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <FrameLayout
        android:id="@+id/fragmentContainer"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintBottom_toTopOf="@id/bottomNavigationView"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottomNavigationView"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        app:itemIconTint="@drawable/selector_menu_color"
        app:itemRippleColor="@null"
        app:itemTextColor="@color/black"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:menu="@menu/bottom_navigation_menu" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

- BottomNavigation에 해당하는 menu 구현함, 총 3가지 메뉴에 대해서 구현함

- icno의 경우 drawable에 vector asset을 추가해서 설정함

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">

    <item android:id="@+id/home"
        android:icon="@drawable/ic_baseline_home_24"
        android:title="홈"
        />

    <item android:id="@+id/chatList"
        android:icon="@drawable/ic_baseline_chat_24"
        android:title="채팅"
        />

    <item android:id="@+id/myPage"
        android:icon="@drawable/ic_baseline_person_pin_24"
        android:title="나의 정보"
        />

</menu>
```

### MainActivity.kt
- MainActivity는 사실상 만든 프래그먼트를 보여주는 역할과 BottomNavigation에 대한 이벤트 처리만 함, 세부적인 각 화면별 내용은 해당 프래그먼트에서 구현함 

```kotlin
package techtown.org.retailmarket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import techtown.org.retailmarket.chatlist.ChatListFragment
import techtown.org.retailmarket.home.HomeFragment
import techtown.org.retailmarket.mypage.MyPageFragment

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 프래그먼트를 사용하기 위해서 생성을 함
        val homeFragment = HomeFragment()
        val chatListFragment = ChatListFragment()
        val myPageFragment = MyPageFragment()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // 초기에는 아무것도 없으므로 초기 프래그먼트 설정함
        replaceFragment(homeFragment)

        // 메뉴 아이템을 선택해서 프래그먼트에 붙일 수 있게 처리함
        bottomNavigationView.setOnNavigationItemSelectedListener {
            // menu에서 선택한 item들을 활용할 수 있음
            when (it.itemId) {
                // 각각 생성한 프래그먼트를 매개변수로 넘겨서 교체해줌
                R.id.home -> replaceFragment(homeFragment)
                R.id.chatList -> replaceFragment(chatListFragment)
                R.id.myPage -> replaceFragment(myPageFragment)
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        // 버튼 누른것에 따라서 프래그먼트를 바꾸기 위한 로직
        supportFragmentManager.beginTransaction()
            .apply {
                // fragment를 보여줄 FrameLayout에 각각의 fragment를 그리게끔 변경함
                replace(R.id.fragmentContainer, fragment)
                commit()
            }
    }
}
```

## 홈화면
- NoactionBar 처리를 하여서 바를 간단하게 레이아웃으로 만듬

- 리사이클러뷰를 통해서 Database에서 존재하는 중고거래 아이템 올린 부분에 대해서 가져옴

- FloatinActionButton의 경우에는 클릭시 물품을 등록하는 액티비티로 넘어감, 단 여기서 회원으로 등록이 된 경우에만 추가할 수 있도록 처리해둠

- 그리고 올린 제품에 대해서 클릭시 채팅방이 생성되게끔 함

![one](/Intermediate/Retail/img/one.png)

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">


    <!--Custom TabBar-->
    <LinearLayout
        android:id="@+id/toolbarLayout"
        android:layout_width="0dp"
        android:layout_height="?attr/actionBarSize"
        android:gravity="center_vertical"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginStart="16dp"
            android:text="중고거래"
            android:textColor="@color/black"
            android:textSize="20sp"
            android:textStyle="bold" />

    </LinearLayout>

    <View
        android:layout_width="0dp"
        android:layout_height="1dp"
        android:background="@color/gray_cc"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/toolbarLayout" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/articleRecyclerView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/toolbarLayout" />

    <com.google.android.material.floatingactionbutton.FloatingActionButton
        android:id="@+id/addFloatingButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_margin="16dp"
        android:backgroundTint="@color/orange"
        android:src="@drawable/ic_baseline_add_24"
        app:tint="@color/white"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"/>


</androidx.constraintlayout.widget.ConstraintLayout>
```

### ArticleModel.kt
- 먼저 아이템이 올라가 있다면 이 부분에 대해서 RecyclerView에 보여주기 전 해당 데이터를 처리할 모델을 아래와 같이 먼저 만듬
```kotlin
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
```

### ArticleAdapter
- 그 다음 ViewBinding을 활용하여서 Item 레이아웃을 연결함

- 아래와 같이 리사이클러뷰에 연결할 Item 레이아웃을 먼저 만듬 
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:paddingStart="16dp"
    android:paddingTop="16dp"
    android:paddingEnd="16dp">

    <ImageView
        android:id="@+id/thumbnailImageView"
        android:layout_width="110dp"
        android:layout_height="110dp"
        android:layout_marginBottom="16dp"
        android:scaleType="center"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/titleTextView"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:maxLines="2"
        android:textColor="@color/black"
        android:textSize="16sp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toEndOf="@id/thumbnailImageView"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/dateTextView"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginTop="6dp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="@id/titleTextView"
        app:layout_constraintTop_toBottomOf="@id/titleTextView" />

    <TextView
        android:id="@+id/priceTextView"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginTop="6dp"
        android:textColor="@color/black"
        android:textSize="15sp"
        android:textStyle="bold"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="@id/titleTextView"
        app:layout_constraintTop_toBottomOf="@id/dateTextView" />


    <View
        android:layout_width="0dp"
        android:layout_height="1dp"
        android:background="@color/gray_ec"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

### ArticleAdapter.kt
- 앞서 정의한 model 클래스에서 데이터를 받고 item에 연결을 하는 bind 함수와 기존에 작업에 대해서 어댑터 구현은 ListAdapter를 활용하여 처리함
```kotlin
package techtown.org.retailmarket.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import techtown.org.retailmarket.databinding.ItemArticleBinding
import java.text.SimpleDateFormat
import java.util.*

class ArticleAdapter(val onItemClicked: (ArticleModel) -> Unit): ListAdapter<ArticleModel, ArticleAdapter.ViewHolder>(diffUtil){

    inner class ViewHolder(private val binding: ItemArticleBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(articleModel: ArticleModel) {

            // 입력받은 값을 Long을 바꾸기 위해서, date를 String으로 바꿔줌
            val format = SimpleDateFormat("MM월 dd일")
            val date = Date(articleModel.createdAt)

            // ViewBinding에서 만든 View와 data class의 값을 연결해줌
            binding.titleTextView.text = articleModel.title
            binding.dateTextView.text = format.format(date).toString()
            binding.priceTextView.text = articleModel.price

            if(articleModel.imageUrl.isNotEmpty()) {
                // 이미지가 없는 경우도 있을 수 있으므로 그 경우 제외하고 넣어줌
                Glide.with(binding.thumbnailImageView)
                    .load(articleModel.imageUrl)
                    .into(binding.thumbnailImageView)
            }

            // 아이템을 클릭 했을 때 채팅을 열게 하기 위해서 onItemClick 리스너 달음
            binding.root.setOnClickListener {
                onItemClicked(articleModel)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<ArticleModel>() {
            override fun areItemsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
                // 새로운 아이템이 같은지 비교, 키값이 필요한데 현재 모델에서는 createdAt을 키값으로 둠
                return oldItem.createdAt == newItem.createdAt
            }

            override fun areContentsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
                // 현재 노출 아이템과 새로운 아이템이 같은지 비교
                return oldItem == newItem
            }

        }
    }
}
```

### 아이템 등록화면
- FloatingButton을 누르게 된다면 아이템 등록하는 화면에서 아이템 등록을 하게끔 함

![one](/Intermediate/Retail/img/two.png)

- 앞서 정의한 Model에 필요한 데이터를 그리고 DB에 저장할 데이터를 입력받고 처리함
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".home.AddArticleActivity">

    <!--Custom TabBar-->
    <LinearLayout
        android:id="@+id/toolbarLayout"
        android:layout_width="0dp"
        android:layout_height="?attr/actionBarSize"
        android:gravity="center_vertical"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginStart="16dp"
            android:text="아이템 등록"
            android:textColor="@color/black"
            android:textSize="20sp"
            android:textStyle="bold" />

    </LinearLayout>

    <View
        android:id="@+id/toolbarUnderLineView"
        android:layout_width="0dp"
        android:layout_height="1dp"
        android:background="@color/gray_cc"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/toolbarLayout" />

    <EditText
        android:id="@+id/titleEditText"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginTop="16dp"
        android:layout_marginEnd="16dp"
        android:hint="글 제목"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/toolbarUnderLineView" />

    <EditText
        android:id="@+id/priceEditText"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginEnd="16dp"
        android:hint="가격"
        android:inputType="numberDecimal"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/titleEditText" />

    <Button
        android:id="@+id/imageAddButton"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:backgroundTint="@color/orange"
        android:text="이미지 등록하기"
        app:layout_constraintEnd_toEndOf="@id/photoImageView"
        app:layout_constraintStart_toStartOf="@id/photoImageView"
        app:layout_constraintTop_toBottomOf="@id/photoImageView" />

    <ImageView
        android:id="@+id/photoImageView"
        android:layout_width="250dp"
        android:layout_height="250dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <Button
        android:id="@+id/submitButton"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginEnd="16dp"
        android:layout_marginBottom="16dp"
        android:backgroundTint="@color/orange"
        android:text="등록하기"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent" />

    <ProgressBar
        android:id="@+id/progressBar"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:visibility="gone"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        tools:visibility="visible" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

### AddArticleActivity.kt
- 이미지를 업로드하는 함수와 함께 ArticleModel을 통해서 데이터를 다 받은뒤 DB에 보내줌 
```kotlin
package techtown.org.retailmarket.home

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import techtown.org.retailmarket.DBKey.Companion.DB_ARTICLES
import techtown.org.retailmarket.R

class AddArticleActivity : AppCompatActivity() {

    private var selectedUri: Uri? = null

    // 사용할 Firebase 기능 추가함
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val storage: FirebaseStorage by lazy {
        Firebase.storage
    }
    private val articleDB: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_ARTICLES)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_article)

        findViewById<Button>(R.id.imageAddButton).setOnClickListener {
            // 이미지 권한을 가져오는 케이스 when문으로 확인
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // 권한이 PackageManager의 승인이 되었을 때
                    // 권한이 승이되었으므로 이미지 가져오기 위해서 ContentProvider로 감
                    startContentProvider()
                }
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    // 교육용 팝업이 필요한 경우(아직 승인이 안되서)
                    // 교육용 팝업을 띄움
                    showPermissionContextPopup()
                }

                else -> {
                    // 해당 권한에 대해서 요청하는 경우
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        1010
                    )
                }
            }
        }

        // 등록하기 버튼 누르면 아래와 같이 edittext와 auth를 통해서 값을 가져오고 DB에 업로드함
        findViewById<Button>(R.id.submitButton).setOnClickListener {
            val title = findViewById<EditText>(R.id.titleEditText).text.toString()
            val price = findViewById<EditText>(R.id.priceEditText).text.toString()
            val sellerId = auth.currentUser?.uid.orEmpty()

            // 이미지 업로드에 시간이 걸리므로 진행상태를 보여주기 위해서 띄움
            showProgress()

            // 이미지가 있을 때 이미지를 storage에 업로드함, Uri는 selectedUri를 통해서 받음
            if (selectedUri != null) {
                val photoUri = selectedUri ?: return@setOnClickListener // null 체크
                uploadPhoto(photoUri,
                    // 람다식을 통해 성공, 실패 경우를 나눔
                    successHandler = { uri -> // 성공한 경우 uri를 반환함
                        // 성공한 경우 이미지 Uri를 가져와서 첨부함
                        uploadArticle(sellerId, title, price, uri)
                    },
                    errorHandler = {
                        // 실패한 경우, Toast 메시지 띄우고 작업 취소함
                        Toast.makeText(this, "사진 업로드에 실패했습니다.",Toast.LENGTH_SHORT).show()
                        hideProgress() // 에러가 난 경우도 끝난 것이므로 progress 숨김
                    }
                )
            } else {
                // 이미지가 없는 경우 처리함(위의 과정이 비동기일 때 함수 실행하고 동기일 때도 함수 실행을 위해서 함수 선언)
                uploadArticle(sellerId, title, price, "") // 이미지가 없는 경우
            }


        }
    }

    private fun uploadPhoto(uri: Uri, successHandler: (String) -> Unit, errorHandler: () -> Unit) {
        // Firebase Storage 활용하여 업로드
        val fileName = "${System.currentTimeMillis()}.png" // 고유한 키값으로 쓰기 위해서 파일명 정함
        storage.reference.child("article/photo").child(fileName) // storage의 저장
            .putFile(uri) // uri를 넣음
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    // 업로드가 성공했다면 리스너 처리를 해 줌
                    storage.reference.child("article/photo").child(fileName)
                        .downloadUrl
                        .addOnSuccessListener { uri ->
                            successHandler(uri.toString())
                        }
                        .addOnFailureListener {
                            errorHandler()
                        }

                } else {
                    // 업로드 실패시
                    errorHandler()
                }
            }
    }

    private fun uploadArticle(sellerId: String, title: String, price: String, imageUrl: String) {
        // 모델을 만든 다음 하나의 아이템을 push하여 setValue를 통해서 DB에 업로드함
        val model = ArticleModel(sellerId, title, System.currentTimeMillis(), "$price 원", imageUrl)
        articleDB.push().setValue(model)

        hideProgress() // 완료되기 직전에 숨김(완료됨을 의미)
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // 권한 요청을 위한 requestCode를 불러옴
        when (requestCode) {
            1010 ->
                // 권한이 승인되었는지에 대해서 불러오고 승낙이 되었는지 확인함
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 만약 승낙이 되었다면 ContentProvider로 가서 사진을 가져옴
                    startContentProvider()
                } else {
                    // 승낙이 되어 있지 않으면 토스트 메시지 띄움
                    Toast.makeText(this, "권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun startContentProvider() {
        // ContentProvider 사용하기 위해 Intent에서 IntentFilter를 사용해서 처리함
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*" // 모든 이미지 가져옴
        startActivityForResult(intent, 2020) // 인텐트 처리하고 requestCode 받아옴, 이미지를 가져오니깐
    }

    private fun showProgress() {
        findViewById<ProgressBar>(R.id.progressBar).isVisible = true
    }

    private fun hideProgress() {
        findViewById<ProgressBar>(R.id.progressBar).isVisible = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 제대로 받았다면 resultOK가 된 것인데 아닌 경우 예외처리함
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        // 이미지를 가져오기 위해서 requestCode로 받아와서 처리
        when (requestCode) {
            2020 -> {
                // 정상적으로 받아왔을 때 데이터의 사진의 Uri가 넘어와서 이를 처리함
                val uri = data?.data
                if (uri != null) {
                    // 제대로 받았다면 이미지 표시를 함
                    findViewById<ImageView>(R.id.photoImageView).setImageURI(uri)
                    selectedUri = uri
                } else {
                    Toast.makeText(this, "사진을 가져오지 못했습니다", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this, "사진을 가져오지 못했습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPermissionContextPopup() {
        // 아직 승인이 안됐다면 승인을 하게끔 함
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("사진을 가져오기 위해 필요합니다.")
            .setPositiveButton("동의") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1010)
            }
            .create()
            .show()
    }
}
```

### HomeFragment.kt
- 그리고 프래그먼트로 만든 화면에 어댑터를 연결하고 DB에서 데이터를 받고 위에서 만든 기능들을 역할할 수 있도록 다 연결을 해 둠 
```kotlin
package techtown.org.retailmarket.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import techtown.org.retailmarket.DBKey.Companion.CHILD_CHAT
import techtown.org.retailmarket.DBKey.Companion.DB_ARTICLES
import techtown.org.retailmarket.DBKey.Companion.DB_USERS
import techtown.org.retailmarket.R
import techtown.org.retailmarket.chatlist.ChatListItem
import techtown.org.retailmarket.databinding.FragmentHomeBinding

// 인자로 레이아웃 파일을 넘기면 자동으로 attach됨
class HomeFragment: Fragment(R.layout.fragment_home) {

    // View Binding 사용
    private var binding: FragmentHomeBinding? = null

    // 리사이클러뷰에 사용하기 위한 어댑터
    private lateinit var articleAdapter: ArticleAdapter

    // 프래그먼트 이벤트 처리
    private val articleList = mutableListOf<ArticleModel>()
    private val listener = object: ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            // 추가될 때 datasnapshot에서 articleModel에서 어댑터의 submitList 해주면 됨

            // 모델 클래스 자체를 받을 것임, 데이터를 맵핑해서 해당 내용을 다 가져옴
            val articleModel = snapshot.getValue(ArticleModel::class.java)
            // 만약 null이면 종료
            articleModel ?: return

            // null이 아니면 추가하고 어댑터 갱신
            articleList.add(articleModel)
            articleAdapter.submitList(articleList)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onChildRemoved(snapshot: DataSnapshot) {}

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onCancelled(error: DatabaseError) {}

    }

    // auth 초기화
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    // DB 초기화
    private lateinit var articleDB: DatabaseReference
    private lateinit var userDB: DatabaseReference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // HomwFragment 만들어지면 바로 연결함
        val fragmentHomeBinding = FragmentHomeBinding.bind(view)
        binding = fragmentHomeBinding

        // 중복을 방지하기 위해서 다른 탭을 누를때 다시 돌아오면 데이터 담고 있는 List를 초기화
        // clear 처리하지 않으면 계속 중복되서 나타남
        articleList.clear()

        // articleDB 가져옴
        articleDB = Firebase.database.reference.child(DB_ARTICLES)

        // UserDB 즉 chat을 위한 DB도 활용함
        userDB = Firebase.database.reference.child(DB_USERS)

        // 어댑터 초기화, 아이템 클릭시 채팅 리스트로 넘어가게끔 구현함
        articleAdapter = ArticleAdapter(onItemClicked = { articleModel ->

            if(auth.currentUser != null) {
                // 로그인 한 상태
                if(auth.currentUser?.uid != articleModel.sellerId) {
                    // currentUser와 sellerId가 같지 않으면 채팅방을 연다
                    // chatListItem에 대해서 data 클래스를 활용해서 데이터를 가져다가 사용함
                    val chatRoom = ChatListItem(
                        buyerId = auth.currentUser!!.uid,
                        sellerId = articleModel.sellerId,
                        itemTitle = articleModel.title,
                        key = System.currentTimeMillis()
                    )

                    // userDB에 채팅 관련 내용을 DB에 저장함
                    userDB.child(auth.currentUser!!.uid)
                        .child(CHILD_CHAT)
                        .push()
                        .setValue(chatRoom)

                    userDB.child(articleModel.sellerId)
                        .child(CHILD_CHAT)
                        .push()
                        .setValue(chatRoom)

                    Snackbar.make(view, "채팅방이 생성되었습니다. 채팅탭에서 확인해주세요.",Snackbar.LENGTH_LONG).show()


                } else {
                    // 같을 경우 내가 올린 아이템이므로 스낵바 나타냄
                    Snackbar.make(view, "내가 올린 아이템입니다.",Snackbar.LENGTH_LONG).show()
                }
            } else {
                // 로그인을 안 한 상태, 스낵바로 안내내
                Snackbar.make(view, "로그인 후 사용해주세요",Snackbar.LENGTH_LONG).show()
            }



       })


        // 프래그먼트는 context가 아니므로 getContext를 통해서 가져와야함(생략 가능해서 context로 씀)
        fragmentHomeBinding.articleRecyclerView.layoutManager = LinearLayoutManager(context)
        fragmentHomeBinding.articleRecyclerView.adapter = articleAdapter

        // 플로팅 버튼을 통해서 물품등록 화면으로 넘어가게 하기 위해서 불러옴
        fragmentHomeBinding.addFloatingButton.setOnClickListener {
            // 프래그먼트 이므로 인텐트 처리시 context에 this를 사용할 수 없으므로, 아래와 같이 씀(context 값이 null일 수도 있기 때문에)
            if(auth.currentUser != null) {
                // 회원으로 등록을 했을 때만 등록처리를 하도록 함(sellerId가 있으므로)
                val intent = Intent(requireContext(), AddArticleActivity::class.java)
                startActivity(intent)
            } else {
                // 스낵바를 띄어줌
                Snackbar.make(view, "로그인 후 사용해주세요",Snackbar.LENGTH_LONG).show()
            }
            /*
            context?.let {
            // 하지만 null 체크를 하는게 좋긴 함
                val intent = Intent(it, ArticleAddActivity::class.java)로 써도 됨
                startActivity(intent)
            }
             */
        }

        // DB처리를 함
        articleDB.addChildEventListener(listener)
    }

    override fun onResume() {
        super.onResume()
        // 프래그먼트가 다시 보일 때마다 데이터 갱신
        articleAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 프래그먼트의 경우 탭으로 누를 때마다 생성되므로 프래그먼트가 사라질때 이벤트를 없애줘야함(그렇지 않으면 중복될 수도 있음)
        articleDB.removeEventListener(listener)
    }
}
```

## 마이페이지 화면
- 먼저 로그인 처리에 대해서 myPage 프래그먼트에서 처리를 해 줌

- 회원가입, 로그인 기능이 같이 있고 입력받은 이메일, 비밀번호 바탕으로 인식을 하고 로그인 처리 상태, EditText에 입력받은 상태에 따라 버튼 활성화를 다르게 하고 로그인이 됐다면 로그아웃 처리하게 함

![one](/Intermediate/Retail/img/three.png)

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="24dp">

    <EditText
        android:id="@+id/emailEditText"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginTop="50dp"
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
        android:id="@+id/signUpButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginEnd="10dp"
        android:backgroundTint="@color/orange"
        android:enabled="false"
        android:text="회원가입"
        app:layout_constraintEnd_toStartOf="@id/signInOutButton"
        app:layout_constraintTop_toBottomOf="@id/passwordEditText" />

    <Button
        android:id="@+id/signInOutButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:backgroundTint="@color/orange"
        android:enabled="false"
        android:text="로그인"
        app:layout_constraintEnd_toEndOf="@id/passwordEditText"
        app:layout_constraintTop_toBottomOf="@id/passwordEditText" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

### MyPageFragment.kt
- 위에서 설명한 로그인 처리에 대한 디테일한 설정을 다 해 둠 
```kotlin
package techtown.org.retailmarket.mypage

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import techtown.org.retailmarket.R
import techtown.org.retailmarket.databinding.FragmentMypageBinding

// 인자로 레이아웃 파일을 넘기면 자동으로 attach됨
class MyPageFragment: Fragment(R.layout.fragment_mypage) {

    private var binding: FragmentMypageBinding? = null
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // View Binding 활용함
        val fragmentMypageBinding = FragmentMypageBinding.bind(view)
        binding = fragmentMypageBinding

        // 각 버튼대로 클릭 리스너 처리를 함
        fragmentMypageBinding.signInOutButton.setOnClickListener {
            binding?.let { binding ->
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()

                // 현재 로그인이 되어 있다면 로그아웃, 로그인이 되어있지 않다면 로그인 시켜주면 됨
                if (auth.currentUser == null) {
                    // 로그인 해야함
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                successSignIn()
                            } else {
                                Toast.makeText(context, "로그인에 실패했습니다. 이메일 또는 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    // 로그아웃 해야함
                    auth.signOut()
                    // 로그아웃 하면서 EditText 초기화하고 버튼도 바꿈
                    binding.emailEditText.text.clear()
                    binding.emailEditText.isEnabled = true
                    binding.passwordEditText.text.clear()
                    binding.passwordEditText.isEnabled = true

                    binding.signInOutButton.text = "로그인"
                    binding.signInOutButton.isEnabled = false
                    binding.signUpButton.isEnabled = false
                }
            }
        }

        fragmentMypageBinding.signUpButton.setOnClickListener {
            // 회원가입 하는 경우
            binding?.let { binding ->
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) { task->
                        if(task.isSuccessful) {
                            Toast.makeText(context, "회원가입에 성공했습니다. 로그인 버튼을 눌러주세요.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "회원가입에 실패했습니다. 이미 가입한 이메일일 수 있습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        // enable을 조절하기 위해서 editText의 리스너처리함
        fragmentMypageBinding.emailEditText.addTextChangedListener {
            binding?.let { binding ->
                val enable = binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()
                binding.signInOutButton.isEnabled = enable
                binding.signUpButton.isEnabled = enable
            }
        }

        fragmentMypageBinding.passwordEditText. addTextChangedListener {
            binding?.let { binding ->
                val enable = binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()
                binding.signInOutButton.isEnabled = enable
                binding.signUpButton.isEnabled = enable
            }
        }


    }

    override fun onStart() {
        super.onStart()
        // 로그인 풀렸는지 확인
        if(auth.currentUser == null) {
            // 로그인이 안된 상태는 로그아웃 된 상태와 동일함
            binding?.let { binding ->
                binding.emailEditText.text.clear()
                binding.emailEditText.isEnabled = true
                binding.passwordEditText.text.clear()
                binding.passwordEditText.isEnabled = true

                binding.signInOutButton.text = "로그인"
                binding.signInOutButton.isEnabled = false
                binding.signUpButton.isEnabled = false
            }
        } else {
            // 현재 유저가 있다면
            binding?.let { binding ->
                binding.emailEditText.setText(auth.currentUser!!.email)
                binding.emailEditText.isEnabled = false
                binding.passwordEditText.setText("*******")
                binding.passwordEditText.isEnabled = false

                binding.signInOutButton.text = "로그아웃"
                binding.signInOutButton.isEnabled = true
                binding.signUpButton.isEnabled = false
            }
        }
    }

    private fun successSignIn() {
        // 로그인 성공시 editText, 버튼을 잠궈주고 로그인을 로그아웃 버튼으로 교체해줌
        if (auth.currentUser == null) {
            // 로그인 실패시 예외처리
            Toast.makeText(context, "로그인에 실패했습니다. 다시 시도해주세요.",Toast.LENGTH_SHORT).show()
            return
        }

        binding?.emailEditText?.isEnabled = false
        binding?.passwordEditText?.isEnabled = false
        binding?.signUpButton?.isEnabled = false
        binding?.signInOutButton?.text = "로그아웃"
    }
}
```

## 채팅리스트 화면
- 앞서 홈 화면에서 아이템 클릭시 채팅방이 생성되는데 생성된 채팅방 리스트를 보여주는 화면, 동일하게 리사이클러뷰를 활용함 

![one](/Intermediate/Retail/img/four.png)

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    xmlns:app="http://schemas.android.com/apk/res-auto">

    <!--Custom TabBar-->
    <LinearLayout
        android:id="@+id/toolbarLayout"
        android:layout_width="0dp"
        android:layout_height="?attr/actionBarSize"
        android:gravity="center_vertical"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginStart="16dp"
            android:text="채팅방 리스트"
            android:textColor="@color/black"
            android:textSize="20sp"
            android:textStyle="bold" />

    </LinearLayout>

    <View
        android:layout_width="0dp"
        android:layout_height="1dp"
        android:background="@color/gray_cc"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/toolbarLayout" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/chatListRecyclerView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/toolbarLayout" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

- 리사이클러뷰에 구현할 item 레이아웃은 단순함
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:padding="10dp">

    <TextView
        android:id="@+id/chatRoomTitleTextView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textColor="@color/black"
        android:textSize="20sp" />


</LinearLayout>
```

### ChatListItem.kt
- 아래의 데이터 모델은 채팅리스트와 별개로 DB에 채팅방에 대한 정보 저장을 위해서 아래와 같이 생성함
```kotlin
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
```

### ChatListAdapter.kt
- 어댑터 구현은 위와 유사하며, chatListItem 클릭시 채팅방으로 넘어가는 이벤트 처리만을 다룸
```kotlin
package techtown.org.retailmarket.chatlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import techtown.org.retailmarket.databinding.ItemArticleBinding
import techtown.org.retailmarket.databinding.ItemChatListBinding
import java.text.SimpleDateFormat
import java.util.*

// ArticleAdapter와 큰 로직은 유사함
class ChatListAdapter(val onItemClicked: (ChatListItem) -> Unit): ListAdapter<ChatListItem, ChatListAdapter.ViewHolder>(diffUtil){

    inner class ViewHolder(private val binding: ItemChatListBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(chatListItem: ChatListItem) {
            // 아이템을 클릭 했을 때 채팅을 열게 하기 위해서 onItemClick 리스너 달음
            binding.root.setOnClickListener {
                onItemClicked(chatListItem)
            }

            binding.chatRoomTitleTextView.text = chatListItem.itemTitle
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemChatListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<ChatListItem>() {
            override fun areItemsTheSame(oldItem: ChatListItem, newItem: ChatListItem): Boolean {
                // 새로운 아이템이 같은지 비교, 키값이 필요한데 현재 모델에서는 createdAt을 키값으로 둠
                return oldItem.key == newItem.key
            }

            override fun areContentsTheSame(oldItem: ChatListItem, newItem: ChatListItem): Boolean {
                // 현재 노출 아이템과 새로운 아이템이 같은지 비교
                return oldItem == newItem
            }

        }
    }
}
```

### ChatListFragment.kt
- 간단하게 로그인된 상태에서 DB에 접근해서 채팅 리스트에 대한 정보를 가지고 와서 채팅리스트를 보여줌
```kotlin
package techtown.org.retailmarket.chatlist

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import techtown.org.retailmarket.DBKey.Companion.CHILD_CHAT
import techtown.org.retailmarket.DBKey.Companion.DB_USERS
import techtown.org.retailmarket.R
import techtown.org.retailmarket.chatdetail.ChatRoomActivity
import techtown.org.retailmarket.databinding.FragmentChatlistBinding

// 인자로 레이아웃 파일을 넘기면 자동으로 attach됨
class ChatListFragment: Fragment(R.layout.fragment_chatlist) {

    // 바인딩 사용
    private var binding: FragmentChatlistBinding? = null

    // 어댑터 초기화
    private lateinit var chatListAdapter: ChatListAdapter

    // chatRoom 데이터 저장
    private val chatRoomList = mutableListOf<ChatListItem>()


    // auth 초기화
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentChatlistBinding = FragmentChatlistBinding.bind(view)
        binding = fragmentChatlistBinding

        chatListAdapter = ChatListAdapter(onItemClicked = { chatRoom ->
            // 채팅방으로 이동하는 코드
            context?.let{
                // 프래그먼트이므로 context를 넘겨줄 때 null 체크를 하고 넘겨줌
                val intent = Intent(it, ChatRoomActivity::class.java)
                intent.putExtra("chatKey", chatRoom.key)
                startActivity(intent)
            }

        })

        chatRoomList.clear()

        fragmentChatlistBinding.chatListRecyclerView.adapter = chatListAdapter
        fragmentChatlistBinding.chatListRecyclerView.layoutManager = LinearLayoutManager(context)

        if(auth.currentUser == null) {
            // 로그인이 안되어 있다면
            return
        }

        // chat내역을 가져오기 위해 DB 초기화
        // 로그인이 되면 DB에 접근해서 내용을 가져옴
        val chatDB = Firebase.database.reference.child(DB_USERS).child(auth.currentUser!!.uid).child(CHILD_CHAT)

        // chat 데이터 통째로 들고 오므로 for each로 잘라서 봐야함
        chatDB.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val model = it.getValue(ChatListItem::class.java)
                    model ?: return

                    chatRoomList.add(model)
                }

                chatListAdapter.submitList(chatRoomList)
                chatListAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onResume() {
        super.onResume()

        chatListAdapter.notifyDataSetChanged() // 뷰 갱신해줌
    }
}
```

### 채팅방
- 실제 채팅을 주고 받을 수 있게 구현과 함께 리사이클러뷰로 채팅내용 보여줌 

![one](/Intermediate/Retail/img/five.png)

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".chatdetail.ChatRoomActivity">

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/chatRecyclerView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintBottom_toTopOf="@id/messageEditText"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <EditText
        android:id="@+id/messageEditText"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toStartOf="@id/sendButton"
        app:layout_constraintStart_toStartOf="parent" />

    <Button
        android:id="@+id/sendButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:backgroundTint="@color/orange"
        android:text="전송"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

- item 레이아웃은 아래와 같이, id와 message만을 나타냄
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:padding="10dp">

    <TextView
        android:id="@+id/senderTextView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textSize="15sp"
        android:textStyle="bold"
        android:textColor="@color/black"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/messageTextView"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:textSize="15sp"
        android:textColor="@color/black"
        android:layout_marginStart="10dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toEndOf="@id/senderTextView"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

### ChatItem.kt
- DB에서 채팅 내용에 대해서 불러오기 위해서 만든 데이터 모델
```kotlin
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
```

### ChatItemAdapter.kt
- 내용에 대해서는 단순하게 Item을 통해서 받아서 그려주면 됨
```kotlin
package techtown.org.retailmarket.chatdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import techtown.org.retailmarket.databinding.ItemChatBinding
import techtown.org.retailmarket.databinding.ItemChatListBinding

class ChatItemAdapter: ListAdapter<ChatItem, ChatItemAdapter.ViewHolder>(diffUtil){

    inner class ViewHolder(private val binding: ItemChatBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(chatItem: ChatItem) {
            binding.senderTextView.text = chatItem.senderId
            binding.messageTextView.text = chatItem.message
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<ChatItem>() {
            override fun areItemsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
                // 새로운 아이템이 같은지 비교, 키값이 필요한데 현재 모델에서는 createdAt을 키값으로 둠
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
                // 현재 노출 아이템과 새로운 아이템이 같은지 비교
                return oldItem == newItem
            }

        }
    }
}
```

### ChatRoomActivity.kt
- 여기서 아이템 처리에 대해서 리사이클러뷰에 넣어주기 위해서 DB에 접근해서 직접 데이터를 가져와서 연결을 해서 보여줌
```kotlin
package techtown.org.retailmarket.chatdetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import techtown.org.retailmarket.DBKey.Companion.DB_CHATS
import techtown.org.retailmarket.R

class ChatRoomActivity : AppCompatActivity() {

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    // 채팅하는걸 저장하기 위한 리스트, 어댑터 연결, DB
    private val chatList = mutableListOf<ChatItem>()
    private val adapter = ChatItemAdapter()
    private var chatDB: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        // 채팅 리스트에서 누르면 채팅 화면으로 넘어오므로 거기서 key 값을 받아서 DB에 저장해둠
        val chatKey = intent.getLongExtra("chatKey", -1)

        chatDB = Firebase.database.reference.child(DB_CHATS).child("$chatKey")

        chatDB?.addChildEventListener(object: ChildEventListener{
            // 실제 이벤트를 받아서 리사이클러뷰에 넣음 위에서 DB에 넣었고 이 액티비티로 넘어오는데 DB에 작업은 다 해둠
            // snapshot 하나하나가 push를 한 chatItem임, 이를 어댑터에 연결해주고 리사이클러뷰에 보여주면 됨
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatItem = snapshot.getValue(ChatItem::class.java)
                chatItem ?: return

                chatList.add(chatItem)
                adapter.submitList(chatList)
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}

        })

        findViewById<RecyclerView>(R.id.chatRecyclerView).adapter = adapter
        findViewById<RecyclerView>(R.id.chatRecyclerView).layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.sendButton).setOnClickListener{
            val chatItem = ChatItem(
                // send 버튼 누르면 채팅하는거 보냄
                senderId = auth.currentUser!!.uid,
                message = findViewById<EditText>(R.id.messageEditText).text.toString()
            )

            chatDB?.push()?.setValue(chatItem)
        }
    }
}
```