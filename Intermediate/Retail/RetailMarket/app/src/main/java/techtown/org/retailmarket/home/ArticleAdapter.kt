package techtown.org.retailmarket.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import techtown.org.retailmarket.databinding.ItemArticleBinding
import java.text.SimpleDateFormat
import java.util.*

class ArticleAdapter(val onItemClicked: (ArticleModel) -> Unit): ListAdapter<ArticleModel, ArticleAdapter.ViewHolder>(diffUtil){

    inner class ViewHolder(private val binding: ItemArticleBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(articleModel: ArticleModel) {

            // 입력받은 값을 Long을 바꾸기 위해서, date를 String으로 바꿔줌
            val format = SimpleDateFormat("MM월 dd일")
            val date = Date(articleModel.createdAt)

            // ViewBinding에서 만든 View와 data class의 값을 연결해줌
            binding.titleTextView.text = articleModel.title
            binding.dateTextView.text = format.format(date).toString()
            binding.priceTextView.text = articleModel.price

            if(articleModel.imageUrl.isNotEmpty()) {
                // 이미지가 없는 경우도 있을 수 있으므로 그 경우 제외하고 넣어줌
                Glide.with(binding.thumbnailImageView)
                    .load(articleModel.imageUrl)
                    .into(binding.thumbnailImageView)
            }

            // 아이템을 클릭 했을 때 채팅을 열게 하기 위해서 onItemClick 리스너 달음
            binding.root.setOnClickListener {
                onItemClicked(articleModel)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<ArticleModel>() {
            override fun areItemsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
                // 새로운 아이템이 같은지 비교, 키값이 필요한데 현재 모델에서는 createdAt을 키값으로 둠
                return oldItem.createdAt == newItem.createdAt
            }

            override fun areContentsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
                // 현재 노출 아이템과 새로운 아이템이 같은지 비교
                return oldItem == newItem
            }

        }
    }
}