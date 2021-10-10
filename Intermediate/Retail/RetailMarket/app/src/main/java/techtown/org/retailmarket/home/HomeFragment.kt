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