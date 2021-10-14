package techtown.org.airbab

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

/*
BottomSheetDialog를 위한 리사이클러뷰 어댑터, ListAdapter를 사용해서 씀
 */

class HouseListAdapter : ListAdapter<HouseModel, HouseListAdapter.ItemViewHolder>(differ) {

    inner class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(houseModel: HouseModel) {
            // inflater를 통해서 가져온 itemView에 뷰들을 찾아서 정의함
            val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
            val priceTextView = view.findViewById<TextView>(R.id.priceTextView)
            val thumbnailImageView = view.findViewById<ImageView>(R.id.thumbnailImageView)

            // houseModel에 있는 내용을 각각 연결함
            titleTextView.text = houseModel.title
            priceTextView.text = houseModel.price

            Glide
                    .with(thumbnailImageView.context)
                    .load(houseModel.imgUrl)
                    .transform(CenterCrop(), RoundedCorners(dpToPx(thumbnailImageView.context, 12))) // 이미지 설정 변경함
                    .into(thumbnailImageView)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // View를 생성하기 위해서 Inflater를 설정함 ItemViewHolder로 감싸서 view가 내부 클래스로 들어감
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(inflater.inflate(R.layout.item_house, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(currentList[position]) // position에 있는 것을 bind 해줌
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        // 핸드폰마다 해상도가 달라서 글라이드에선 dp가 아닌 픽셀로 들어가기 때문에, 이를 변환해줘야함 dp를 그에 맞는 픽셀로
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics).toInt()
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