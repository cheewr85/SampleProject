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