## 전자액자
- 저장소의 접근 권한을 얻어 로컬 사진을 로드함

- 로드한 로컬 사진을 보여주고 그 로드한 사진을 이미지 뷰에 보여줌

- 그 중 사진을 선택하여서 일정한 간격으로 전환하여 사진을 보여줌

## 사진 추가 화면
- 사진 추가하기를 통해서 저장소에 있는 사진을 최대 6개 나타나게 함

- 여기서 6개의 추가하는 사진을 각 비율이 정해져 있고 일정하게 나오게 함 

- 그러기 위해서 해당 Layout의 Ratio를 ConstraintLayout의 성질을 활용해서 가로 세로 사이즈의 비율을 정해서 나오게 함

- 여기서 이미지 같은 경우 centerCrop, 가로 세로 길이 중 짧은 쪽을 ImageView 레이아웃에 꽉 차게 크기를 맞춰서 출력함, 이러면 가로/세로 비율은 유지되고 레이아웃 영역에서 벗어난 이미지는 출력되지 않음

### 실행화면 
![one](/Basic/Album/img/one.png)
![one](/Basic/Album/img/two.png)

- 사진 추가하기를 하면 정해진 비율에 맞게 사진이 추가됨, 최대 6개까지 추가가능함
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <!--Ratio를 사용해 가로,세로 사이즈의 비율에 따라 나오게 함, W는 세로 사이즈 H는 가로 사이즈-->
    <LinearLayout
        android:id="@+id/firstRowLinearLayout"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintDimensionRatio="H, 3:1"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent">

        <ImageView
            android:id="@+id/imageView11"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="1"
            android:scaleType="centerCrop" />

        <ImageView
            android:id="@+id/imageView12"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="1"
            android:scaleType="centerCrop" />

        <ImageView
            android:id="@+id/imageView13"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="1"
            android:scaleType="centerCrop" />
    </LinearLayout>

    <LinearLayout
        android:id="@+id/secondRowLinearLayout"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintDimensionRatio="H, 3:1"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/firstRowLinearLayout">

        <ImageView
            android:id="@+id/imageView21"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="1"
            android:scaleType="centerCrop" />

        <ImageView
            android:id="@+id/imageView22"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="1"
            android:scaleType="centerCrop" />

        <ImageView
            android:id="@+id/imageView23"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="1"
            android:scaleType="centerCrop" />
    </LinearLayout>

    <Button
        android:id="@+id/startPhotoFrameModeButton"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:text="전자액자 실행하기"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent" />

    <Button
        android:id="@+id/addPhotoButton"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:text="사진 추가하기"
        app:layout_constraintBottom_toTopOf="@id/startPhotoFrameModeButton"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent" />


</androidx.constraintlayout.widget.ConstraintLayout>
```

### 구현 코드
- 6개의 imageView에 대해서 리스트로 담아서 연결함, 그리고 이미지에 대한 Uri 데이터에 대한 정보도 리스트에 담아둠

- 먼저 권한이 있는지를 확인하고 권한 처리를 우선적으로 함

- 그리고 권한이 있다면 인텐트를 통해서 저장소에 접근한 뒤 선택한 이미지에 대해서 리스트에 담게끔 연결을 함

- 그리고 그 이미지를 실행화면과 같이 보이게끔 연결함

```kotlin
package techtown.org.album

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val addPhotoButton: Button by lazy {
        findViewById<Button>(R.id.addPhotoButton)
    }

    private val startPhotoFrameModeButton: Button by lazy {
        findViewById<Button>(R.id.startPhotoFrameModeButton)
    }

    // 사진을 6개를 보여주게 설계함, 이를 리스트로 담아서 연결
    private val imageViewList: List<ImageView> by lazy {
        mutableListOf<ImageView>().apply {
            add(findViewById(R.id.imageView11))
            add(findViewById(R.id.imageView12))
            add(findViewById(R.id.imageView13))
            add(findViewById(R.id.imageView21))
            add(findViewById(R.id.imageView22))
            add(findViewById(R.id.imageView23))
        }
    }

    // 인텐트로 받아온 이미지 Uri에 대해서 리스트로 담아서 저장해둠, 추후 연결하기 위해서
    private val imageUriList: MutableList<Uri> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // AddPhotoButton을 초기화해주는 메소드
        initAddPhotoButton()
        initStartPhotoFrameModeButton()
    }



    private fun initAddPhotoButton() {
        addPhotoButton.setOnClickListener{
            // 권한이 있는지 확인
            when{
                // 권한이 있는지 체크하기 위해서 쓰는 메소드
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                // checkSelfPermission이 리턴을 하는대로 만약 GRANTED라면
                ) == PackageManager.PERMISSION_GRANTED -> {
                    navigatePhotos()
                }
                // 권한이 부여되지 않는 경우
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    // 권한이 없는 것이므로 교육용 팝업을 띄워야 함
                    // 교육용 팝업 확인 후 권한 팝업을 띄워야 함
                    showPermissionContextPopup()

                }
                else -> {
                    // 아무런 경우도 아니라면 바로 권한을 요청하는 팝업을 띄움, Array의 String으로 받는데 저장소에 대한 것만 있으면 되므로 하나만 넣음
                    // requestCode도 넣음
                    requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1000)
                }
            }
        }
    }

    private fun initStartPhotoFrameModeButton() {
        // start 버튼을 통해서 전자액자로 그려서 나오게 함, 인텐트로 사진 데이터를 넘겨서 해당 액티비티로 넘어감
        startPhotoFrameModeButton.setOnClickListener {
            val intent = Intent(this, PhotoFrameActivity::class.java) // 해당 인텐트로 넘어감
            // Uri 자체를 넘길 수 없으므로 String으로 바꿔서 넘김
            imageUriList.forEachIndexed { index, uri ->
                // Uri를 하나하나 꺼내와서 PutExtra를 함
                intent.putExtra("photo$index", uri.toString()) // Uri를 String으로 바꿔서 보냄
            }
            // List에 몇 번째 index까지 get을 해야하는지 알려주기 위해서 사이즈도 넘김
            intent.putExtra("photoListSize", imageUriList.size)
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        // 권한의 승인여부를 확인한 후 사진을 선택하기 위해서 해당 메소드를 오버라이드해서 상태 확인
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            1000 -> {
                // 저장소에 대한 권한을 1000으로 했음, 그 코드에 대한 처리하기
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한이 부여됐다면 사진을 가져옴
                    navigatePhotos()
                } else {
                    // 권한이 없다면
                    Toast.makeText(this, "권한을 거부하셨습니다", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                // 1000이 아니면 처리를 안 했으므로 크게 처리는 안해도 됨
            }
        }
    }

    private fun navigatePhotos() {
        // 권한이 정상적으로 잘 부여되었을 때 사진을 가져오게 함
        // SAF 기능을 사용함, 인텐트 활용해서
        val intent = Intent(Intent.ACTION_GET_CONTENT) // SAF 기능 사용 컨텐츠를 가져오게 함
        intent.type = "image/*" // 이미지만 가져올 것임
        startActivityForResult(intent, 2000) // 사진을 선택하고 돌아올 것이기 때문에 콜백으로 2000의 requestCode를 부여함
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode != Activity.RESULT_OK) {
            return // 만일 정상적으로 결과를 받은게 아닌 경우가 있을 수 있으므로 return을 처리함
        }

        // 사진을 선택하고 받아올 때 2000의 requestCode로 받아옴
        when(requestCode) {
            2000 -> {
                // 이미지는 data(Intent)로 받음, Intent가 데이터를 null, 안 내려줬을 때 오류가 발생할 수 있으므로 nullable로 선언함, Uri도 똑같이함
                val selectedImageUri: Uri? = data?.data // 인텐트에 있는 데이터 가져옴

                if(selectedImageUri != null) {

                    // 6개 이상일 경우 토스트 메시지 띄움, 예외처리
                    if(imageUriList.size == 6) {
                        Toast.makeText(this, "이미 사진이 꽉 찼습니다.",Toast.LENGTH_SHORT).show()
                        return
                    }

                    imageUriList.add(selectedImageUri) /// 이미 널체크를 했으므로 정상적으로 추가가 됨, 위에서 선언한 리스트에서
                    imageViewList[imageUriList.size - 1].setImageURI(selectedImageUri) // 위에서 크기를 설정했고 그 다음 그 이미지를 그대로 넣어주면 됨
                } else {
                    // null을 받은 경우
                    Toast.makeText(this, "사진을 가져오지 못했습니다.",Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                // 다른 requestCode는 없으므로 예외처리만 함
                Toast.makeText(this, "사진을 가져오지 못했습니다.",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPermissionContextPopup() {
        // 권한 팝업을 띄우는 함수, AlertDialog로 띄움
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다")
            .setMessage("전자액자 앱에서 사진을 불러오기 위해서 권한이 필요합니다.")
            .setPositiveButton("동의하기") { _, _ ->
                // 동의를 한 것이므로 실제 권한에 대한 동의를 할 수 있게끔 하는 팝업을 띄움
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
            }
            .setNegativeButton("취소하기") { _, _ ->
                // 취소하기에선 팝업을 닫는 것이므로 아무것도 추가할 필요가 없음
            }
            .create()
            .show()

    }


}
```

## 전자액자 화면

### 실행화면
![one](/Basic/Album/img/three.png)

- Fade-In, Fade-Out 효과를 보이기 위해서 배경색을 검은색으로 설정함

- 여기서 2가지의 imageView에 대해서는 애니메이션 효과를 통해서 바꿔줌

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".PhotoFrameActivity">

    <!--FadeIn, FadeOut처럼 효과를 내기 위해서 ImageView 2개를 사용해서 함-->
    <ImageView
        android:id="@+id/backgroundPhotoImageView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:background="@color/black"
        android:scaleType="center"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"/>

    <ImageView
        android:id="@+id/photoImageView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:background="@color/black"
        android:scaleType="center"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"/>

</androidx.constraintlayout.widget.ConstraintLayout>
```

### 구현코드
- 여기서 애니메이션 효과를 위해서 타이머를 실행시켜서 시간을 설정함

- 5초에 한 번씩 전환해주는 것을 위해서 타이머를 설정하고 이 타이머에서 UI를 바꾸는 작업을 함

- 여기에 그치지 않고 안드로이드 생명주기를 고려해서 타이머가 한 없이 onCreate에서 키고 계속 돌아가지 않게 하기 위해서, 적절한 상황에 맞게 타이머 설정을 추가함

```kotlin
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
```