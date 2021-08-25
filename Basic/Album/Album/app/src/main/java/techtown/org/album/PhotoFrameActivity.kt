package techtown.org.album

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import java.util.*
import kotlin.concurrent.timer

class PhotoFrameActivity : AppCompatActivity() {

    private val photoList = mutableListOf<Uri>() // 인텐트로 넘겨받은 Uri에 대해서 저장할 List

    private var currentPosition = 0 // 현재 어디까지 돌았는지 확인하기 위해서

    // 타이머 실행시, 앱 종료시에도 실행된다면 껐다가 다시 실행을 해야하는 조정이 필요함 그래서 변수로 우선 정의함
    private var timer: Timer?= null


    private val photoImageView: ImageView by lazy {
        findViewById<ImageView>(R.id.photoImageView)
    }

    private val backgroundPhotoImageView: ImageView by lazy {
        findViewById<ImageView>(R.id.backgroundPhotoImageView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_frame)

        getPhotoUriFromIntent()

//        startTimer(), onCreate는 일시적인 것이므로 onStart에서 함수를 실행시킴
    }

    // onCreate가 난잡해지는 것을 막기 위해서 함수로 빼서 사용함
    private fun getPhotoUriFromIntent() {
        val size = intent.getIntExtra("photoListSize",0) // List에 Size를 받아옴
        for(i in 0.. size) {
            // size에 맞게 사진 Uri 데이터를 가져옴
            intent.getStringExtra("photo$i")?.let{
                // null일 수 있으므로 let 함수로 null이 아닐때만 실행하게 함
                photoList.add(Uri.parse(it)) // String으로 받았으므로 다시 Uri 객체로 변환해서 보냄
            }
        }
    }

    // 5초에 한 번씩 전환해주기 위해서 타이머 사용함
    private fun startTimer() {
        timer = timer(period = 5 * 1000) {
            // 타이머는 메인 쓰레드가 아니므로 메인 쓰레드로 바꿔줘야함, 메인 쓰레드에서 UI 작업을 하니깐 배경을 바꾸기 위해서
            runOnUiThread {
                // List의 이미지를 가져오기 위해서 position을 확인함, 여기서 next에 대해서는 리스트가 한정되었으므로 맨 마지막인 경우엔 다시 앞으로 가게끔 설정함
                // 현재 + 다음 같이 인덱스로 활용해서 background와 photo에 대해서 주기를 두면서 돌아갈 수 있게끔 아래와 같이 설정함
                val current = currentPosition
                val next = if (photoList.size <= currentPosition + 1) 0 else currentPosition + 1

                backgroundPhotoImageView.setImageURI(photoList[current])

                photoImageView.alpha = 0f // 투명도를 0으로 줌, 안 보인다는 뜻
                photoImageView.setImageURI(photoList[next])
                photoImageView.animate()
                    .alpha(1.0f)
                    .setDuration(1000)
                    .start()

                currentPosition = next
            }
        }
    }

    // Log를 찍어서 확인가능
    // 액티비티가 백그라운드로 들어가 더 이상 사용되지 않을 때
    override fun onStop() {
        super.onStop()

        timer?.cancel() // 타이머 종료시킴
    }

    override fun onStart() {
        super.onStart()
        // 다시 시작될 때는 타이머를 새롭게 시작하면 됨
        startTimer()
    }

    // 앱이 아예 완전히 꺼진것이므로 타이머를 꺼버려야함
    override fun onDestroy() {
        super.onDestroy()

        timer?.cancel()
    }
}