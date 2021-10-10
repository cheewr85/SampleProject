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