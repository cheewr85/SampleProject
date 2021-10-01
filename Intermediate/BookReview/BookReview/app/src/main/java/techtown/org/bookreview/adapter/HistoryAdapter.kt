package techtown.org.bookreview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import techtown.org.bookreview.databinding.ItemHistoryBinding
import techtown.org.bookreview.model.History

class HistoryAdapter(val historyDeleteClickedListener: (String) -> Unit): ListAdapter<History, HistoryAdapter.HistoryItemViewHolder>(diffUtil) { // 삭제버튼 이벤트 정의를 위해 메인에서 함수를 던져서 가져와 처리하기 위해 매개변수 추가
    // View Binding을 활용하여 RecyclerView를 사용함
    // View Binding을 위해서 RecyclerView에 들어갈 아이템에 대해서 item_history.xml로 추가함, 그래서 자동으로 트랙킹해서 ItemBookBinding을 import 할 수 있음
    // 여기서 root, ItemBookBinding은 item_history.xml이 해당됨
    inner class HistoryItemViewHolder(private val binding: ItemHistoryBinding): RecyclerView.ViewHolder(binding.root){ // 미리 몇 개 만들어진 ViewHolder를 재활용 하는것이 RecyclerView임, 미리 View를 Hold해서 ViewHolder임

        // 위에서 ListAdapter에서 History 클래스를 가져오게 함
        fun bind(historyModel: History) {
            binding.historyKeywordTextView.text = historyModel.keyword

            // 삭제에 대해서 keyword를 넘기고 이벤트 처리를 함
            binding.historyKeywordDeleteButton.setOnClickListener {
                historyDeleteClickedListener(historyModel.keyword.orEmpty())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryItemViewHolder {
        // 미리 만들어진 ViewHolder가 없을 경우에 새로 생성을 하는 함수
        // inner class로 HistoryItemViewHolder에 매개변수로 ItemHistoryBinding을 받아야함
        // 그리고 inflate를 해줘야함, context는 adapter여서 this를 줄 수 없기 때문에 View에 해당하는 것을 주기 위해서 parent 불러옴
        // 이러면 LayoutInflater로 불러온 뒤 ViewHolder를 생성
        return HistoryItemViewHolder(ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: HistoryItemViewHolder, position: Int) {
        // 실제 ViewHolder가 그려지게 됐을 때 데이터를 그려주게 되는 데이터를 바인드하게 되는 함수
        holder.bind(currentList[position]) // ListAdapter에 Data에 리스트는 미리 저장되어 있어서 currentList로 접근 가능
    }

    // 이미 View에 올라와 있을 때 같은 값을 할당할 필요가 없으므로 이를 판단해주는 object
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<History>(){
            // 아래의 두 기준으로 리사이클러뷰가 데이터를 업데이트 할 건지 지울건지 새로 import할 건지 알려줌
            override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
                // oldItem과 newItem이 같은지 다른지 확인
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
                // 안에 있는 content가 같은지 다른지 확인
                return oldItem.keyword == newItem.keyword
            }

        }
    }

}