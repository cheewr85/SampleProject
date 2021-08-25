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