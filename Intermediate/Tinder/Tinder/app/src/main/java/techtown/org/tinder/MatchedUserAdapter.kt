package techtown.org.tinder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class MatchedUserAdapter: ListAdapter<CardItem, MatchedUserAdapter.ViewHolder>(diffUtil) {

    // ViewHolder 추가
    inner class ViewHolder(private val view: View): RecyclerView.ViewHolder(view) {

        fun bind(cardItem: CardItem) {
            // view를 불러와서 binding을 해 줌
            view.findViewById<TextView>(R.id.userNameTextView).text = cardItem.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // item xml을 연결하기 위해서 ViewHolder에서 inflater를 써서 만듬
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_matched_user, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position]) // 현재 item bind
    }

    companion object {
        // diffUtil 맞는지 컨텐츠 및 아이템 확인하기 위해서
        val diffUtil = object : DiffUtil.ItemCallback<CardItem>() {
            override fun areItemsTheSame(oldItem: CardItem, newItem: CardItem): Boolean {
                return oldItem.userId == newItem.userId
            }

            override fun areContentsTheSame(oldItem: CardItem, newItem: CardItem): Boolean {
                return oldItem == newItem
            }

        }
    }
}
