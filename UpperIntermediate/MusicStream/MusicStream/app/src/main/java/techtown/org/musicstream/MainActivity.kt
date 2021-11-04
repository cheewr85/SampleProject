package techtown.org.musicstream

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // FrameLayout에 프래그먼트를 보여줌
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, PlayerFragment().newInstance())
                .commit()
    }
}