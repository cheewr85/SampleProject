package techtown.org.ott

import android.app.Activity
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.AppBarLayout
import techtown.org.ott.databinding.ActivityMainBinding
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    // 뷰바인딩 처리
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater)}

    private var isGateringMotionAnimating: Boolean = false
    private var isCurationMotionAnimating: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        makeStatusBarTransparent() // status바까지 투명으로 만듬

        initAppBar() // toolbar, alpha 값을 수정함

        initInsetMargin() // insetmargin에 직접 접근하여서 수정을 함

        initScrollViewListener()

        initMotionLayoutListener()


    }

    private fun initScrollViewListener() {
        binding.scrollView.smoothScrollTo(0,0)

        // 스크롤 뷰 설정할 때 모션 레이아웃 처리를 함
        // 스크롤 한 만큼 value를 조절해줘야함, 더 많이
        binding.scrollView.viewTreeObserver.addOnScrollChangedListener {
            val scrolledValue = binding.scrollView.scrollY

            if(scrolledValue > 150f.dpToPx(this).toInt()) {
                if(isGateringMotionAnimating.not()) {
                    binding.gatheringDigitalThingsBackgroundMotionLayout.transitionToEnd()
                    binding.gatheringDigitalThingsLayout.transitionToEnd()
                    binding.buttonShownMotionLayout.transitionToEnd()
                }
            } else {
                if(isGateringMotionAnimating.not()) {
                    binding.gatheringDigitalThingsBackgroundMotionLayout.transitionToStart()
                    binding.gatheringDigitalThingsLayout.transitionToStart()
                    binding.buttonShownMotionLayout.transitionToStart()
                }
            }

            // 도형 애니메이션 준비하고 실행시킴, 스크롤뷰보다 더 스크롤이 되면
            if(scrolledValue > binding.scrollView.height) {
                if(isCurationMotionAnimating.not()) {
                    binding.curationAnimationMotionLayout.setTransition(R.id.curation_animation_start1, R.id.curation_animation_end1)
                    binding.curationAnimationMotionLayout.transitionToEnd()
                    isCurationMotionAnimating = true
                }
            }
        }

    }

    private fun initMotionLayoutListener() {
        // 애니메이션 동작 처리 true, false 처리함, 중복 방지를 위해서
        binding.gatheringDigitalThingsLayout.setTransitionListener(object: MotionLayout.TransitionListener {
            override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {
                isGateringMotionAnimating = true
            }

            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) = Unit

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                isGateringMotionAnimating = false
            }

            override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) = Unit

        })

        // 도형 애니메이션 처리
        binding.curationAnimationMotionLayout.setTransitionListener(object: MotionLayout.TransitionListener {
            override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) = Unit
            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) = Unit

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                // transition이 완료되면 현재 id에서 2번째 transition으로 전환되게 처리함
                when(currentId) {
                    R.id.curation_animation_end1 -> {
                        binding.curationAnimationMotionLayout.setTransition(R.id.curation_animation_start2, R.id.curation_animation_end2)
                        binding.curationAnimationMotionLayout.transitionToEnd()
                    }
                }
            }

            override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) = Unit

        })
    }

    // 윈도위에 있는 모든 시스템영역의 inset값을 조정하는 함수
    private fun initInsetMargin() = with(binding) {
        // 최상단 coordinator로부터 하단에 있는 것이 다 붙어서 그 WindowInsets값을 커스텀함
        ViewCompat.setOnApplyWindowInsetsListener(coordinator) {v: View, insets: WindowInsetsCompat ->
            val params = v.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin = insets.systemWindowInsetBottom
            // toolbar로 만든 레이아웃에 대해서 Insets area에서 줄 수 있는 margin을 줄 것임(collapsing도 포함, 단 다 0으로 설정함)
            toolbarContainer.layoutParams = (toolbarContainer.layoutParams as ViewGroup.MarginLayoutParams).apply {
                setMargins(0,insets.systemWindowInsetTop,0,0)
            }
            collapsingToolbarContainer.layoutParams = (collapsingToolbarContainer.layoutParams as ViewGroup.MarginLayoutParams).apply {
                setMargins(0,0,0,0)
            }

            insets.consumeSystemWindowInsets()
        }
    }

    private fun initAppBar() {
        // 스크롤 시 abstractOffset 즉, 특정 스크롤만큼 된다면 alpha 값을 조절함
        // 움직인 값만큼 빼서 alpha를 조정함, 그래서 애니메이션 처리를 좀 더 어색하지 않고 자연스럽게 처리하게함, offset을 직접 체킹하면서 원하는 애니메이션 처리를 함
        binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val topPadding = 300f.dpToPx(this)
            val realAlphaScrollHeight = appBarLayout.measuredHeight - appBarLayout.totalScrollRange
            val abstractOffset = abs(verticalOffset)

            val realAlphaVerticalOffset = if (abstractOffset - topPadding < 0) 0f else abstractOffset - topPadding

            if(abstractOffset < topPadding) {
                binding.toolbarBackgroundView.alpha = 0f
                return@OnOffsetChangedListener
            }
            // alpha 값을 스크롤 하는 시점에 바꿈
            val percentage = realAlphaVerticalOffset / realAlphaScrollHeight
            binding.toolbarBackgroundView.alpha = 1 - (if (1 - percentage * 2 < 0) 0f else 1 - percentage * 2)
        })
        initActionBar()
    }

    // 기존 앱테마의 툴바가 아닌 다른 툴바를 쓰기 위한 초기화 한 함수
    // 액션바를 활용해서 툴바를 붙일 것임, NoActionBar 설정으로 바탕으로 날림
    private fun initActionBar() = with(binding) {
        toolbar.navigationIcon = null
        toolbar.setContentInsetsAbsolute(0,0)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            it.setHomeButtonEnabled(false)
            it.setDisplayHomeAsUpEnabled(false)
            it.setDisplayShowHomeEnabled(false)
        }
    }

}
fun Float.dpToPx(context: Context): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics)

// 시스템 영역의 statusBar를 투명으로 만드는 함수
fun Activity.makeStatusBarTransparent() {
    window.apply {
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        statusBarColor = Color.TRANSPARENT
    }
}