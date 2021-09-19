package techtown.org.todayquotes

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/*
ViewPager에 추가를 하기 위한 Adapter 추가
 */
// ViewPager2는 RecyclerView 기반이므로 해당 adapter를 상속받음
class QuotesPagerAdapter(
    private val quotes: List<Quote>, // Quote 모델 받아옴
    private val isNameRevealed: Boolean // Remote할 요소로 Name의 reveal 여부이므로 이를 받아옴 Remote에서
): RecyclerView.Adapter<QuotesPagerAdapter.QuoteViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        QuoteViewHolder(
            // parent에 그대로 item_quote에 대해서 연결시켜줌
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_quote, parent, false)
        )

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        // quotes는 5개밖에 없으므로 값을 지정
        // quotes 사이즈는 5밖에 되지 않으므로 실제 포지션이 그 이상으로 갈 경우 사이즈로 나눠서 다시 맨 처음 값을 가르키게 만듬
        val actualPosition = position % quotes.size
        holder.bind(quotes[actualPosition], isNameRevealed)
    //원래 같으면 그냥 받으면 되지만 quotes는 5개 밖에 없으므로 무한 스와이프는 다르게 함
//        holder.bind(quotes[position], isNameRevealed) // Quote로 받은 모델의 내용을 bind를 함
    }

    // 원래라면 해당 모델에 저장된 quote 크기를 주면 됨
//    quotes.size // 해당 모델에 저장된 quote 크기
    // 하지만 무한 스와이프를 위해서 페이지 어댑터의 값을 Int의 맥스 값을 줘버림, 이렇게 하면 끝이 오긴 하지만 매우 큰 값이므로 충분히 무한 스와이프처럼 보이게 할 수 있음
    override fun getItemCount() = Int.MAX_VALUE

    class QuoteViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        // Pager에 연결하여 보여주기 위한 item_quote에 대해서 정의하고 사용함
        private val quoteTextView: TextView = itemView.findViewById(R.id.quoteTextView)
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)

        @SuppressLint("SetTextI18n")
        fun bind(quote:Quote, isNameRevealed: Boolean) {
           // 어떻게 랜더링 할 지 처리하는 함수, 불러온 View를 연결함, 큰 따옴표 붙임
            quoteTextView.text = "\"${quote.quote}\""

            if(isNameRevealed) {
                // isNameRevealed가 true일 경우만 name을 보여줌(isNameRevealed는 RemoteConfig에서 값을 바꾸면 그게 바로 반영됨)
                nameTextView.text = "-${quote.name}" // 대시 붙임
                nameTextView.visibility = View.VISIBLE
            } else {
                nameTextView.visibility = View.GONE
            }

        }
    }
}