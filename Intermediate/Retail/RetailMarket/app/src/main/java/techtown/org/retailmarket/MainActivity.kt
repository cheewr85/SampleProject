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