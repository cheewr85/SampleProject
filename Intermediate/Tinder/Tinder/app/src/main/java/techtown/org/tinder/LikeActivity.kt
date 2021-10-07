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