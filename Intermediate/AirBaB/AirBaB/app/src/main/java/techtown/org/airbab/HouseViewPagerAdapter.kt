package techtown.org.airbab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

/*
ViewPager2 어댑터 클래스 리사이클러뷰 어댑터와 유사함, ListAdapter를 사용해서 씀
HouseModel을 공유하기 위해서 클릭 리스너를 달아둠
 */

class HouseViewPagerAdapter(val itemClicked: (HouseModel) -> Unit) : ListAdapter<HouseModel, HouseViewPagerAdapter.ItemViewHolder>(differ) {

    inner class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(houseModel: HouseModel) {
            // inflater를 통해서 가져온 itemView에 뷰들을 찾아서 정의함
            val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
            val priceTextView = view.findViewById<TextView>(R.id.priceTextView)
            val thumbnailImageView = view.findViewById<ImageView>(R.id.thumbnailImageView)

            // houseModel에 있는 내용을 각각 연결함
            titleTextView.text = houseModel.title
            priceTextView.text = houseModel.price

            view.setOnClickListener {
                // 클릭 처리를 위해서 Model을 그대로 넘김
                itemClicked(houseModel)
            }

            Glide
                    .with(thumbnailImageView.context)
                    .load(houseModel.imgUrl)
                    .into(thumbnailImageView)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // View를 생성하기 위해서 Inflater를 설정함 ItemViewHolder로 감싸서 view가 내부 클래스로 들어감
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(inflater.inflate(R.layout.item_house_detail_for_viewpager, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(currentList[position]) // position에 있는 것을 bind 해줌
    }

    companion object {
        val differ = object: DiffUtil.ItemCallback<HouseModel>() {
            override fun areItemsTheSame(oldItem: HouseModel, newItem: HouseModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: HouseModel, newItem: HouseModel): Boolean {
                return oldItem == newItem
            }

        }
    }
}