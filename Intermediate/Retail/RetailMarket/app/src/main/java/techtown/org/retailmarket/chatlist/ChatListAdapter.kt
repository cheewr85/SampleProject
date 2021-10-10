package techtown.org.retailmarket.chatlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import techtown.org.retailmarket.databinding.ItemArticleBinding
import techtown.org.retailmarket.databinding.ItemChatListBinding
import java.text.SimpleDateFormat
import java.util.*

// ArticleAdapter와 큰 로직은 유사함
class ChatListAdapter(val onItemClicked: (ChatListItem) -> Unit): ListAdapter<ChatListItem, ChatListAdapter.ViewHolder>(diffUtil){

    inner class ViewHolder(private val binding: ItemChatListBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(chatListItem: ChatListItem) {
            // 아이템을 클릭 했을 때 채팅을 열게 하기 위해서 onItemClick 리스너 달음
            binding.root.setOnClickListener {
                onItemClicked(chatListItem)
            }

            binding.chatRoomTitleTextView.text = chatListItem.itemTitle
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemChatListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<ChatListItem>() {
            override fun areItemsTheSame(oldItem: ChatListItem, newItem: ChatListItem): Boolean {
                // 새로운 아이템이 같은지 비교, 키값이 필요한데 현재 모델에서는 createdAt을 키값으로 둠
                return oldItem.key == newItem.key
            }

            override fun areContentsTheSame(oldItem: ChatListItem, newItem: ChatListItem): Boolean {
                // 현재 노출 아이템과 새로운 아이템이 같은지 비교
                return oldItem == newItem
            }

        }
    }
}