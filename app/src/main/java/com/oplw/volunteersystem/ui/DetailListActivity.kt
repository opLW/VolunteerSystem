package com.oplw.volunteersystem.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.oplw.common.customview.rv.CardRollingLayoutManager
import com.oplw.common.customview.rv.CardRollingRecyclerView
import com.oplw.volunteersystem.R
import com.oplw.volunteersystem.adapter.detail.DetailListAdapter
import com.oplw.volunteersystem.base.BaseAnimationListener
import com.oplw.volunteersystem.base.isNetConnected
import com.oplw.volunteersystem.base.showToastInBottom
import com.oplw.volunteersystem.base.showToastInCenter
import com.oplw.volunteersystem.net.bean.Article
import com.oplw.volunteersystem.net.bean.Recruitment
import com.oplw.volunteersystem.net.bean.SecondaryColumn
import com.oplw.volunteersystem.viewmodel.DetailViewModel
import com.oplw.volunteersystem.viewmodel.DetailViewModel.SignUpResult
import com.oplw.volunteersystem.viewmodel.DetailViewModel.Type
import kotlinx.android.synthetic.main.activity_detail_list.*

class DetailListActivity : BaseActivity() {
    companion object {
        const val TITLE_NAME = "titleName"
        const val HAS_SECONDARY_COLUMN = "hasSecondaryColumn"
        const val SECONDARY_COLUMN = "secondaryColumn"
        const val CHANNEL_ID = "channelId"
    }

    private lateinit var topColumnTitle: String
    /**
     * 用于标识当前的层数。
     * oneInterval代表可以直接退回MainActivity;
     * twoInterval代表当前还需要再退一层才可以回到MainActivity
     * eg: 当前的详细文章列表是通过二级栏目点击进来的，
     *     那么此时的numOfLayers为twoInterval代表需要先退回二级列表，
     *     然后才可以退回到MainActivity。
     */
    private var numOfIntervals = 0
    private val oneInterval = 1
    private val twoInterval = 2

    private val viewModel by lazy { ViewModelProviders.of(this).get(DetailViewModel::class.java) }
    private lateinit var swipeLayout: SwipeRefreshLayout
    private lateinit var recyclerView: CardRollingRecyclerView
    private var detailAdapter: DetailListAdapter? = null
    private var type = Type.None

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initToolbar()
        initMainContent()
    }

    private fun initToolbar() {
        topColumnTitle = intent.getStringExtra(TITLE_NAME)
        with(detail_list_title_toolbar) {
            title = topColumnTitle
            setTitleTextColor(Color.WHITE)
            setSupportActionBar(this)
            setNavigationIcon(R.drawable.ic_return)
            setNavigationOnClickListener {
                gotoUpperLayer()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            gotoUpperLayer()
            true
        } else {
            super.onKeyDown(keyCode, event)
        }
    }

    private fun gotoUpperLayer() {
        if (oneInterval == numOfIntervals) {
            finish()
        } else if (twoInterval == numOfIntervals) {
            numOfIntervals = oneInterval
            showTransitionAnim(true) {
                resetUI()
                resetData()
                showSecondaryColumn()
            }
        }
    }

    private fun resetUI() {
        detail_list_title_toolbar.title = topColumnTitle
        if (isNormalViewHiding()) {
            showNormalView()
        }
        if (type == Type.Recruitment) {
            updateCurrentNum(0)
            detail_list_bottom_container.visibility = View.GONE
        }
    }

    private  fun resetData() {
        type = Type.None
        viewModel.clearThirdColumns()
        if (type == Type.Recruitment) {
            (recyclerView.layoutManager as CardRollingLayoutManager).resetCurrentLeft()
        }
    }

    /**
     * 模拟界面切换时的动画。
     * @param isMoveToUpper true代表向上层移动，则动画向右移动。
     *                false代表向下层移动，则动画向左移动。
     */
    private fun showTransitionAnim(isMoveToUpper: Boolean, action: () -> Unit) {
        val width = if (isMoveToUpper) {
            getRootView().width.toFloat()
        } else {
            -getRootView().width.toFloat()
        }
        with(TranslateAnimation(0f, width, 0f, 0f)) {
            interpolator = AccelerateInterpolator()
            duration = 300
            setAnimationListener(object : BaseAnimationListener() {
                override fun onAnimationEnd(animation: Animation?) {
                    action()
                }
            })
            getRootView().startAnimation(this)
        }
    }

    private fun initMainContent() {
        val hasSecondaryColumn = intent.getBooleanExtra(HAS_SECONDARY_COLUMN, false)
        if (hasSecondaryColumn) {
            viewModel.secondaryColumns =
                intent.getSerializableExtra(SECONDARY_COLUMN) as List<SecondaryColumn>
            showSecondaryColumn()
        } else {
            viewModel.channelId = intent.getIntExtra(CHANNEL_ID, -1)
            showThirdColumn()
        }
        numOfIntervals = oneInterval
    }

    private fun showSecondaryColumn() {
        swipeLayout.isEnabled = false
        val adapter = viewModel.getAdapter(this, Type.Secondary) { position: Int, _: Boolean ->
            moveToThirdColumn(position)
        }
        val layoutManager = viewModel.getLayoutManager(this, Type.Secondary)
        detailAdapter = DetailListAdapter(adapter, viewModel.secondaryColumns)
        recyclerView.adapter = detailAdapter
        recyclerView.layoutManager = layoutManager
        recyclerView.enableSpecialFunction(false)
    }

    private fun moveToThirdColumn(position: Int) {
        val secondaryColumn = viewModel.secondaryColumns[position]
        viewModel.channelId = secondaryColumn.id

        if (checkNeedToShowVideo()) return

        numOfIntervals = twoInterval
        detail_list_title_toolbar.title = secondaryColumn.name
        showTransitionAnim(false) {
            swipeLayout.isEnabled = true
            showThirdColumn()
        }
    }

    private fun checkNeedToShowVideo(): Boolean{
        if (viewModel.videoId == viewModel.channelId) {
            startActivity(Intent(this, VideoActivity::class.java))
            return true
        }
        return false
    }

    private fun showThirdColumn() {
        showLoadingView()
        refreshThirdColumns()
    }

    private fun refreshThirdColumns() {
        viewModel.setStateToRefresh()
        loadThirdColumns()
    }

    private fun loadThirdColumns() {
        if (isNetConnected()) {
            viewModel.loadThirdColumns(loadThirdColumnsListener)
        } else  {
            if (viewModel.hasNoThirdColumns()) {
                showErrorView()
            }
        }
    }

    private val loadThirdColumnsListener = { countOfResult: Int, msg: String ->
        when {
            countOfResult > 0 -> {
                showLoadedResult()
                if (viewModel.isResultFromRefresh()) {
                    showToastInBottom(msg)
                }
            }
            countOfResult == 0 -> {
                if (viewModel.hasNoThirdColumns()) {
                    showNothingView()
                }
            }
            else -> {
                if (viewModel.hasNoThirdColumns()) {
                    showErrorView()
                } else {
                    showToastInCenter("刷新失败")
                }
            }
        }
    }

    private fun showLoadedResult() {
        showNormalView()
        if (viewModel.isResultFromRefresh()) {
            swipeLayout.isRefreshing = false
            makeNewRecyclerView()
        }
        if (viewModel.channelId == viewModel.recruitmentId) {
            updateBottomPrompt()
        }
        recyclerView.adapter!!.notifyDataSetChanged()
    }

    private fun makeNewRecyclerView() {
        when (viewModel.channelId) {
            viewModel.recruitmentId -> {
                if (type != Type.Recruitment) {
                    type = Type.Recruitment
                    makeRecruitmentRecyclerView()
                }
            }
            else -> {
                if (type != Type.Article) {
                    type = Type.Article
                    makeArticleRecyclerView()
                }
            }
        }
    }

    private val recruitmentListener = { position: Int, isSignUp: Boolean ->
        if (isSignUp) {
            when(viewModel.signUp(position) {
                    isSignUpSuccess: Boolean, msg: String ->
                if (isSignUpSuccess) {
                    recyclerView.adapter!!.notifyDataSetChanged()
                    updateBottomPrompt()
                }
                showToastInBottom(msg)
            }) {
                SignUpResult.NoSignIn -> showToastInCenter("请先登录")
                SignUpResult.TimeOut -> {
                    recyclerView.adapter!!.notifyDataSetChanged()
                    showToastInCenter("已经过了活动时间")
                }
                else -> { }
            }
        } else {
            showRecruitmentDetailInfo(position)
        }
    }

    private fun makeRecruitmentRecyclerView() {
        val adapter = viewModel.getAdapter(this, type, recruitmentListener)
        val layoutManager =
            viewModel.getLayoutManager(this, type) as CardRollingLayoutManager
        layoutManager.setMidItemChangeListener { position -> updateCurrentNum(position) }
        detailAdapter = DetailListAdapter(adapter, viewModel.thirdColumns)
        recyclerView.adapter = detailAdapter
        recyclerView.layoutManager = layoutManager
        recyclerView.enableSpecialFunction(true)
    }

    private fun updateBottomPrompt() {
        detail_list_bottom_container.visibility = View.VISIBLE
        detail_list_all_num_tv.text = "${viewModel.thirdColumns.size}"
    }

    private  fun updateCurrentNum(position: Int) {
        detail_list_current_num_tv.text = "${position + 1}"
    }

    private fun showRecruitmentDetailInfo(position: Int) {
        val recruitment = viewModel.thirdColumns[position] as Recruitment
        val intent = Intent(this, DetailInfoActivity::class.java)
        with(intent) {
            putExtra(DetailInfoActivity.TYPE, DetailInfoActivity.VOLUNTEER)
            putExtra(DetailInfoActivity.TITLE, recruitment.name)
            putExtra(DetailInfoActivity.ID, recruitment.id)
        }
        startActivity(intent)
    }

    private fun makeArticleRecyclerView() {
        val adapter = viewModel.getAdapter(this, type) {
                position: Int, _: Boolean ->
            showArticleDetailInfo(position)
        }
        val layoutManager = viewModel.getLayoutManager(this, type)
        detailAdapter = DetailListAdapter(adapter, viewModel.thirdColumns)
        recyclerView.adapter = detailAdapter
        recyclerView.layoutManager = layoutManager
        recyclerView.enableSpecialFunction(false)
    }

    private fun showArticleDetailInfo(position: Int) {
        val article = viewModel.thirdColumns[position] as Article
        val intent = Intent(this, DetailInfoActivity::class.java)
        with(intent) {
            putExtra(DetailInfoActivity.TYPE, DetailInfoActivity.ARTICLE)
            putExtra(DetailInfoActivity.TITLE, article.title)
            putExtra(DetailInfoActivity.ID, article.id)
        }
        startActivity(intent)
    }

    override fun getContentViewId() = R.layout.activity_detail_list

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE && detailAdapter!!.rollingToEnd) {
                detailAdapter?.let {
                    loadThirdColumns()
                }
            }
        }
    }

    override fun getNormalView(): View {
        recyclerView = CardRollingRecyclerView(this).also {
            val lp = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            it.layoutParams = lp
            it.addOnScrollListener(scrollListener)
        }
        swipeLayout = SwipeRefreshLayout(this).also {
            it.addView(recyclerView)
            it.setOnRefreshListener {
                refreshThirdColumns()
            }
        }
        return swipeLayout
    }

    override fun getRetryListener(): () -> Unit {
        return {
            showLoadingView()
            refreshThirdColumns()
        }
    }

    override fun getContainerForStatusLayout(): LinearLayout = detail_list_container
}
