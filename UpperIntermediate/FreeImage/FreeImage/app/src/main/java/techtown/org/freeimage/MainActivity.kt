package techtown.org.freeimage

import android.Manifest
import android.app.WallpaperManager
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import techtown.org.freeimage.data.Repository
import techtown.org.freeimage.data.models.PhotoResponse
import techtown.org.freeimage.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        initViews()
        bindViews()

        // 권한 요청을 함(저장하는데 있어서 버전에 따라서)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            fetchRandomPhotos()
        } else {
            requestWriteStoragePermission()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val writeExternalStoragePermissionGranted =
            requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED

        // 권한을 확인하고 부여되면 사진을 fetch하게 함
        if(writeExternalStoragePermissionGranted) {
            fetchRandomPhotos()
        }
    }

    // 리사이클러뷰 초기화
    private fun initViews() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.recyclerView.adapter = PhotoAdapter()
    }

    private fun bindViews() {
        // 액션에 대해 리스너를 등록해 핸들링함
        binding.searchEditText
            .setOnEditorActionListener { editText, actionId, event ->
                // search하는 상황에서 키보드 사라지고 포커스를 바꾸는 이벤트 처리를 함
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    currentFocus?.let { view ->
                        val inputMethodManager =
                            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)

                        view.clearFocus()
                    }

                    fetchRandomPhotos(editText.text.toString()) // 데이터가 변경되서 갱신될 것임
                }

                true
            }

        // 리프레시 한 경우 해당 텍스트에 대해서 다시 탐색을 해야하므로
        binding.refreshLayout.setOnRefreshListener {
            fetchRandomPhotos(binding.searchEditText.text.toString())
        }

        (binding.recyclerView.adapter as? PhotoAdapter)?.onClickPhoto = { photo ->
            // Dialog를 띄우고 누르면 다운로드 할 수 있음
            showDownloadPhotoConfirmationDialog(photo)
        }
    }

    private fun requestWriteStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION
        )
    }

    // 데이터를 API와 통신해서 갱신하는 함수
    private fun fetchRandomPhotos(query: String? = null) = scope.launch {
        try {
            Repository.getRandomPhotos(query)?.let { photos ->
                binding.errorDescriptionTextView.visibility = View.GONE
                (binding.recyclerView.adapter as? PhotoAdapter)?.apply {
                    this.photos = photos
                    notifyDataSetChanged()
                }
            }
            binding.recyclerView.visibility = View.VISIBLE
        } catch (exception: Exception) {
            binding.recyclerView.visibility = View.INVISIBLE
            binding.errorDescriptionTextView.visibility = View.VISIBLE
        } finally {
            binding.shimmerLayout.visibility = View.GONE
            binding.refreshLayout.isRefreshing = false
        }
    }

    private fun showDownloadPhotoConfirmationDialog(photo: PhotoResponse) {
        AlertDialog.Builder(this)
            .setMessage("이 사진을 저장하시겠습니까?")
            .setPositiveButton("저장") { dialog, _ ->
                downloadPhoto(photo.urls?.full)
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->

                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun downloadPhoto(photoUrl: String?) {
        // Url 없으면 끝내고 있다면 다운로드를 함
        photoUrl ?: return

        Glide.with(this)
            .asBitmap()
            .load(photoUrl)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(
                object : CustomTarget<Bitmap>(SIZE_ORIGINAL, SIZE_ORIGINAL) {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        // 다운로드 끝나고 비트맵으로 바뀌면 이 때 저장을 함
                        saveBitmapToMediaStore(resource)

                        val wallpaperManager = WallpaperManager.getInstance(this@MainActivity)
                        val snackbar =  Snackbar.make(binding.root, "다운로드 완료", Snackbar.LENGTH_SHORT)

                        if(wallpaperManager.isWallpaperSupported
                            && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                                    && wallpaperManager.isSetWallpaperAllowed)) {
                            snackbar.setAction("배경 화면으로 저장") {
                                // 배경화면으로 저장하게끔 처리를 함
                                try {
                                    wallpaperManager.setBitmap(resource)
                                } catch(exception: Exception) {
                                    Snackbar.make(binding.root, "배경화면 저장 실패",Snackbar.LENGTH_SHORT)
                                }
                            }
                            snackbar.duration = Snackbar.LENGTH_INDEFINITE
                        }

                        snackbar.show()
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        Snackbar.make(binding.root, "다운로드 실패", Snackbar.LENGTH_SHORT).show()
                    }

                    override fun onLoadStarted(placeholder: Drawable?) {
                        super.onLoadStarted(placeholder)
                        Snackbar.make(binding.root, "다운로드 중...", Snackbar.LENGTH_INDEFINITE).show()
                    }

                }
            )
    }

    private fun saveBitmapToMediaStore(bitmap: Bitmap) {
        val fileName = "${System.currentTimeMillis()}.jpg" // 파일명
        val resolver = applicationContext.contentResolver
        val imageCollectionUri =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // 10버전 이상에서는 쓸 수 있는 Volume에 대해서 설정을 함
                MediaStore.Images.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL_PRIMARY
                )
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
        // 이미지 저장에 대한 세부적인 사항을 정함
        val imageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val imageUri = resolver.insert(imageCollectionUri, imageDetails)

        imageUri ?: return
        // 파일을 저장함
        resolver.openOutputStream(imageUri).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
        // 저장 이후 설정을 초기화시킴
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageDetails.clear()
            imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(imageUri, imageDetails, null, null)
        }
    }

    companion object {
        private const val REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 101
    }
}