package techtown.org.bookreview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Room
import com.bumptech.glide.Glide
import techtown.org.bookreview.databinding.ActivityDetailBinding
import techtown.org.bookreview.model.Book
import techtown.org.bookreview.model.Review

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    // DB를 활용하기 위해서 선언함
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        // AppDatabase,DB를 빌드함
//        db = Room.databaseBuilder(
//            applicationContext,
//            AppDatabase::class.java,
//            "BookSearchDB"
//        ).build()

        // 하지만 DB를 추가하거나 바뀔 때 이 부분에 대해서 마이그레이션과 버전관리를 위해서 db 빌드의 코드를 전역으로 사용함
        db = getAppDatabase(this)

        // 메인에서 이미지, title, description을 인텐트를 통해서 넘겨받음
        // 이때 Parcelable한 Book 모델 클래스를 넘겼으므로 해당 클래스를 그대로 받아옴
        val model = intent.getParcelableExtra<Book>("bookModel")

        binding.titleTextView.text = model?.title.orEmpty()
        binding.descriptionTextView.text = model?.description.orEmpty()

        Glide.with(binding.coverImageView.context)
            .load(model?.coverSmallUrl.orEmpty())
            .into(binding.coverImageView)

        // Review를 가져오기 위한 처리
        Thread {
            val review = db.reviewDao().getOneReview(model?.id?.toInt() ?: 0)
            runOnUiThread{
                binding.reviewEditText.setText(review?.review.orEmpty())
            }
        }.start()

        binding.saveButton.setOnClickListener {
            Thread {
                // 저장하기 버튼을 누르면 리뷰에 대한 내용이 저장됨, 해당 DB에 맞게
                db.reviewDao().saveReview(
                    Review(model?.id?.toInt() ?: 0,
                    binding.reviewEditText.text.toString()
                    )
                )
            }.start()
        }
    }
}