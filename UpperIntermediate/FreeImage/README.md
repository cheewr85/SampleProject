## 저작권 무료 이미지앱
- Unsplash API로부터 사진들을 랜덤하게 가져올 수 있음

- 검색한 사진을 다운받고 배경화면으로 설정할 수 있음

- 로딩할 때 Loading Shimmer를 볼 수 있음

### data 패키지
- API 사용을 할 것이므로 해당 API에서의 통신을 통해 불러올 data class를 미리 만듬, 이 부분 역시 해당 JSON format을 그대로 가지고 온 뒤 만듬

### API 통신
- 기본 Url 정보, Repository 부분을 싱글톤으로 구현함

### Url.kt
```kotlin
package techtown.org.freeimage.data
// BaseUrl 불러오는 객체
object Url {
    const val UNSPLASH_BASE_URL = "https://api.unsplash.com/"
}
```

### Repository.kt
```kotlin
package techtown.org.freeimage.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import techtown.org.freeimage.BuildConfig
import techtown.org.freeimage.data.models.PhotoResponse

object Repository {

    // Retrofit 생성하여 처리하는 함수
    private val unsplashApiService: UnsplashApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Url.UNSPLASH_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildOkHttpClient())
            .build()
            .create()
    }

    // Api를 통해서 호출해서 활용하는 부분
    suspend fun getRandomPhotos(query: String?): List<PhotoResponse>? =
        unsplashApiService.getRandomPhotos(query).body()

    // 로깅을 위해서 만든 클라이언트
    private fun buildOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if(BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            )
            .build()

}
```

### UnsplashApiService.kt
- 랜덤하게 데이터를 가져올 수 있게끔 해당 API 사용법에 맞게 쿼리처리를 해서 데이터를 가져옴
```kotlin
package techtown.org.freeimage.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import techtown.org.freeimage.BuildConfig
import techtown.org.freeimage.data.models.PhotoResponse
/*
랜덤하게 사진을 불러올 수 있게 통신을 하게끔 처리한 쿼리
 */
interface UnsplashApiService {

    @GET(
        "photos/random?" +
                "client_id=${BuildConfig.UNSPLASH_ACCESS_KEY}" +
                "&count=30"
    )
    suspend fun getRandomPhotos(
        @Query("query") query: String?
    ): Response<List<PhotoResponse>>
}
```

## 메인화면
![one](/UpperIntermediate/FreeImage/img/one.png)
![one](/UpperIntermediate/FreeImage/img/two.png)
![one](/UpperIntermediate/FreeImage/img/three.png)

- 메인화면에서 랜덤이미지의 키워드를 입력하는 부분과 해당 부분을 리사이클러뷰를 통해서 나타나게함

- 여기서 Loading Shimmer를 활용하여서 로딩중인 상황에서 페이스북에서 Blur 처리한 것 같이 나타나게끔 처리함

- 리사이클러뷰 item의 경우 위의 사진과 같이 구현함

- 리사이클러뷰에 들어갈 view
```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:paddingHorizontal="12dp"
    android:paddingVertical="6dp">

    <androidx.cardview.widget.CardView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:cardCornerRadius="8dp">

        <androidx.constraintlayout.widget.ConstraintLayout
            android:id="@+id/contentsContainer"
            android:layout_width="match_parent"
            android:layout_height="0dp"
            tools:layout_height="500dp">

            <ImageView
                android:id="@+id/photoImageView"
                android:layout_width="0dp"
                android:layout_height="match_parent"
                app:layout_constraintBottom_toBottomOf="parent"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toTopOf="parent"
                tools:background="@color/teal_200"
                tools:ignore="ContentDescription"
                 />

            <ImageView
                android:id="@+id/profileImageView"
                android:layout_width="24dp"
                android:layout_height="24dp"
                android:layout_marginStart="12dp"
                android:layout_marginBottom="12dp"
                app:layout_constraintBottom_toBottomOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                tools:background="@color/teal_700"
                tools:ignore="ContentDescription" />

            <TextView
                android:id="@+id/authorTextView"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_marginStart="6dp"
                android:layout_marginEnd="12dp"
                android:ellipsize="end"
                android:maxLines="1"
                android:shadowColor="#60000000"
                android:shadowDx="1"
                android:shadowDy="1"
                android:shadowRadius="5"
                android:textColor="@color/white"
                app:layout_constraintBottom_toTopOf="@id/descriptionTextView"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toEndOf="@id/profileImageView"
                app:layout_constraintTop_toTopOf="@id/profileImageView"
                app:layout_constraintVertical_chainStyle="packed"
                tools:text="Author" />

            <TextView
                android:id="@+id/descriptionTextView"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:alpha="0.8"
                android:ellipsize="end"
                android:maxLines="1"
                android:shadowColor="#60000000"
                android:shadowDx="1"
                android:shadowDy="1"
                android:shadowRadius="5"
                android:textColor="@color/white"
                android:textSize="12sp"
                app:layout_constraintBottom_toBottomOf="@id/profileImageView"
                app:layout_constraintEnd_toEndOf="@id/authorTextView"
                app:layout_constraintStart_toStartOf="@id/authorTextView"
                app:layout_constraintTop_toBottomOf="@id/authorTextView"
                tools:text="description" />


        </androidx.constraintlayout.widget.ConstraintLayout>

    </androidx.cardview.widget.CardView>

</FrameLayout>
```

- Loading Shimmer 부분
```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:paddingHorizontal="12dp"
    android:paddingVertical="6dp">


    <androidx.constraintlayout.widget.ConstraintLayout
        android:id="@+id/contentsContainer"
        android:layout_width="match_parent"
        android:layout_height="400dp">

        <View
            android:id="@+id/photoImageView"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:background="#FFEEEEEE"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent" />

        <View
            android:id="@+id/profileImageView"
            android:layout_width="24dp"
            android:layout_height="24dp"
            android:layout_marginStart="12dp"
            android:layout_marginBottom="12dp"
            android:background="@drawable/shape_profile_placeholder"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintStart_toStartOf="parent" />

        <View
            android:id="@+id/authorTextView"
            android:layout_width="0dp"
            android:layout_height="10dp"
            android:layout_marginStart="6dp"
            android:layout_marginEnd="12dp"
            android:background="#FFDDDDDD"
            app:layout_constraintBottom_toTopOf="@id/descriptionTextView"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toEndOf="@id/profileImageView"
            app:layout_constraintTop_toTopOf="@id/profileImageView"
            app:layout_constraintVertical_chainStyle="packed" />

        <View
            android:id="@+id/descriptionTextView"
            android:layout_width="0dp"
            android:layout_height="10dp"
            android:layout_marginTop="5dp"
            android:background="#FFDDDDDD"
            app:layout_constraintBottom_toBottomOf="@id/profileImageView"
            app:layout_constraintEnd_toEndOf="@id/authorTextView"
            app:layout_constraintStart_toStartOf="@id/authorTextView"
            app:layout_constraintTop_toBottomOf="@id/authorTextView" />


    </androidx.constraintlayout.widget.ConstraintLayout>


</FrameLayout>
```

- 메인화면
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    tools:context=".MainActivity">

    <EditText
        android:id="@+id/searchEditText"
        android:layout_width="match_parent"
        android:layout_height="48dp"
        android:background="@color/white"
        android:drawableStart="@drawable/ic_baseline_search_24"
        android:drawablePadding="6dp"
        android:elevation="8dp"
        android:hint="이미지를 검색해보세요."
        android:imeOptions="actionSearch"
        android:importantForAutofill="no"
        android:inputType="text"
        android:paddingHorizontal="12dp"
        android:textSize="14sp"
        tools:ignore="HardcodedText" />

    <androidx.swiperefreshlayout.widget.SwipeRefreshLayout
        android:id="@+id/refreshLayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <FrameLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent">

            <androidx.recyclerview.widget.RecyclerView
                android:id="@+id/recyclerView"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:visibility="invisible"
                android:clipToPadding="false"
                android:paddingVertical="6dp"
                tools:listitem="@layout/item_photo" />

            <TextView
                android:id="@+id/errorDescriptionTextView"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginTop="80dp"
                android:drawablePadding="6dp"
                android:gravity="center"
                android:text="문제가 생겼어요!\n 아래로 당겨 새로고침 해주세요."
                android:visibility="gone"
                app:drawableTopCompat="@drawable/ic_baseline_refresh_24"
                tools:ignore="HardcodedText" />

            <com.facebook.shimmer.ShimmerFrameLayout
                android:id="@+id/shimmerLayout"
                android:layout_width="match_parent"
                android:layout_height="match_parent">

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical">

                    <include layout="@layout/view_shimmer_item_photo" />

                    <include layout="@layout/view_shimmer_item_photo" />

                </LinearLayout>

            </com.facebook.shimmer.ShimmerFrameLayout>

        </FrameLayout>

    </androidx.swiperefreshlayout.widget.SwipeRefreshLayout>


</LinearLayout>
```

### PhotoAdapter.kt
- 리사이클러뷰 어댑터 처리, 이미지 불러오는 부분에 있어서 Glide에 다양한 속성을 활용하여서 처리함, 직접 비율에 맞게 나타날 수 있도록 커스텀함

- 통신을 원활하게 성공했을 경우 받은 response 값을 기준으로 함

- 리스너를 달아서 클릭시 해당 포지션에 대해서 다운로드 처리를 할 수 있게 설정함

```kotlin
package techtown.org.freeimage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import techtown.org.freeimage.data.models.PhotoResponse
import techtown.org.freeimage.databinding.ItemPhotoBinding

/*
어댑터 클래스
 */
class PhotoAdapter : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {

    var photos: List<PhotoResponse> = emptyList()

    var onClickPhoto: (PhotoResponse) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ItemPhotoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(photos[position])
    }

    override fun getItemCount(): Int = photos.size

    inner class ViewHolder(
        private val binding: ItemPhotoBinding
    ): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                // 클릭 당시 어댑터 포지션을 이벤트로 전달함
                onClickPhoto(photos[adapterPosition])
            }
        }

        // 받은 Response 데이터와 연결함
        fun bind(photo: PhotoResponse) {
            // 서버를 통해서 받은 데이터의 길이를 계산하여서 그 비율에 맞게 수정을 함
            val dimensionRatio = photo.height / photo.width.toFloat()
            val targetWidth = binding.root.resources.displayMetrics.widthPixels -
                    (binding.root.paddingStart + binding.root.paddingEnd)
            val targetHeight = (targetWidth * dimensionRatio).toInt()

            binding.contentsContainer.layoutParams =
                binding.contentsContainer.layoutParams.apply {
                    height = targetHeight
                }


            // Glide를 통해서 이미지를 불러와서 처리하는 방식을 추가함(로딩과 로딩이후 방식 추가)
            Glide.with(binding.root)
                .load(photo.urls?.regular)
                .thumbnail(
                    Glide.with(binding.root)
                        .load(photo.urls?.thumb)
                        .transition(DrawableTransitionOptions.withCrossFade())
                )
                .override(targetWidth,targetHeight)
                .into(binding.photoImageView)

            Glide.with(binding.root)
                .load(photo.user?.profileImageUrls?.small)
                .placeholder(R.drawable.shape_profile_placeholder)
                .circleCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.profileImageView)

            if(photo.user?.name.isNullOrBlank()) {
                binding.authorTextView.visibility = View.GONE
            } else {
                binding.authorTextView.visibility = View.VISIBLE
                binding.authorTextView.text = photo.user?.name
            }
            if(photo.description.isNullOrBlank()) {
                binding.descriptionTextView.visibility = View.GONE
            } else {
                binding.descriptionTextView.visibility = View.VISIBLE
                binding.descriptionTextView.text = photo.description
            }

        }
    }


}
```

### MainActivity.kt
- 앞서 정의한 함수를 기반으로 데이터 통신하는 것과 추가적으로 리스너를 달아서 넘겨줘서 다운로드 처리할 수 있도록 다운로드 하는 함수도 추가하면서 다운로드 위해 Dialog 처리도 함

- 그리고 사진을 직접 저장하는 부분도 추가해둠

```kotlin
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
```