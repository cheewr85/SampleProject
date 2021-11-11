package techtown.org.locationmap

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import techtown.org.locationmap.databinding.ViewholderSearchResultItemBinding
import techtown.org.locationmap.model.SearchResultEntity

class SearchRecyclerAdapter: RecyclerView.Adapter<SearchRecyclerAdapter.SearchResultItemViewHolder>(){

    private var searchResultList: List<SearchResultEntity> = listOf()
    private lateinit var searchResultClickListener: (SearchResultEntity) -> Unit


    class SearchResultItemViewHolder(private val binding: ViewholderSearchResultItemBinding, val searchResultClickListener: (SearchResultEntity) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        // ViewHolder 생성 및 검색 결과를 받기 때문에 리스너를 통해서 검색결과 받아옴
        fun bindData(data: SearchResultEntity) = with(binding) {
            textTextView.text = data.name
            subtextTextView.text = data.fullAddress
        }

        fun bindViews(data: SearchResultEntity) {
            binding.root.setOnClickListener {
                searchResultClickListener(data)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultItemViewHolder {
        // item으로 쓸 레이아웃을 itemViewHolder에 연결해서 binding 작업을 위해서 선언하고 처리함
        // view 객체를 만들어서 viewholder에 넣어줌
        val view = ViewholderSearchResultItemBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return SearchResultItemViewHolder(view, searchResultClickListener)

    }

    override fun onBindViewHolder(holder: SearchResultItemViewHolder, position: Int) {
        // 해당 위치에 있는 데이터를 넣게끔 설정
        holder.bindData(searchResultList[position])
        holder.bindViews(searchResultList[position])
    }

    override fun getItemCount(): Int = searchResultList.size

    fun setSearchResultList(searchResultList: List<SearchResultEntity>, searchResultClickListener:(SearchResultEntity) -> Unit) {
        this.searchResultList = searchResultList
        this.searchResultClickListener = searchResultClickListener
        notifyDataSetChanged() // 데이터 반영함
    }
}