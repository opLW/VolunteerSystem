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
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.oplw.common.customview.rv.CardRollingLayoutManager
import com.oplw.common.customview.rv.CardRollingRecyclerView
import com.oplw.volunteersystem.R
import com.oplw.volunteersystem.adapter.detail.DetailListAdapter
import com.oplw.volunteersystem.base.BaseAnimationListener
import com.oplw.volunteersystem.base.showToastInBottom
import com.oplw.volunteersystem.net.bean.Article
import com.oplw.volunteersystem.net.bean.Recruitment
import com.oplw.volunteersystem.net.bean.SecondaryColumn
import com.oplw.volunteersystem.viewmodel.DetailViewModel
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
                showSecondaryColumn()
            }
        }
    }

    private fun resetUI() {
        detail_list_title_toolbar.title = topColumnTitle
        detail_list_bottom_container.visibility = View.GONE
        if (isNormalViewHiding()) {
            showNormalView()
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
        val translationX = TranslateAnimation(0f, width, 0f, 0f)
        translationX.interpolator = AccelerateInterpolator()
        translationX.duration = 300
        translationX.setAnimationListener(object : BaseAnimationListener() {
            override fun onAnimationEnd(animation: Animation?) {
                action()
            }
        })
        getRootView().startAnimation(translationX)
    }

    private fun initMainContent() {
        val hasSecondaryColumn = intent.getBooleanExtra(HAS_SECONDARY_COLUMN, false)
        if (hasSecondaryColumn) {
            viewModel.secondaryColumns =
                intent.getSerializableExtra(SECONDARY_COLUMN) as List<SecondaryColumn>
            showSecondaryColumn()
        } else {
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
        recyclerView.adapter = DetailListAdapter(adapter, viewModel.secondaryColumns) { }
        recyclerView.layoutManager = layoutManager
        recyclerView.enableSpecialFunction(false)
    }

    private fun moveToThirdColumn(position: Int) {
        numOfIntervals = twoInterval
        val secondaryColumn = viewModel.secondaryColumns[position]
        detail_list_title_toolbar.title = secondaryColumn.name
        showTransitionAnim(false) {
            viewModel.channelId = secondaryColumn.id
            refreshThirdColumns()
        }
    }

    private inline fun refreshThirdColumns() {
        viewModel.clearThirdColumns()
        showLoadingView()
        loadThirdColumns()
    }

    private val loadThirdColumnsListener = {
            isLoadingSuccess: Boolean, msg: String ->
        if (isLoadingSuccess) {
            showLoadedResult()
        } else {
            showErrorView()
        }
        showToastInBottom(msg)
    }

    private fun loadThirdColumns() {
        if (!isNetConnecting()) {
            showErrorView()
        } else {
            viewModel.loadThirdColumns(loadThirdColumnsListener)
        }
    }

    private fun isNetConnecting(): Boolean {
        // TODO 添加网络判断
        return true
    }

    private fun showThirdColumn() {
        viewModel.channelId = intent.getIntExtra(CHANNEL_ID, -1)
        refreshThirdColumns()
    }

    private fun showLoadedResult() {
        swipeLayout.isEnabled = true
        if (viewModel.isRefreshResult()) {
            showNormalView()
            when (viewModel.channelId) {
                viewModel.recruitmentId -> makeRecruitmentRecyclerView()
                viewModel.videoId -> {
                } //TODO 添加访问视频
                else -> makeArticleRecyclerView()
            }
        } else {
            recyclerView.adapter!!.notifyDataSetChanged()
        }
        if (detail_list_bottom_container.isVisible) {
            showBottomPrompt(viewModel.thirdColumns.size)
        }
    }

    private val recruitmentListener = { position: Int, isSignUp: Boolean ->
        if (isSignUp) {
            val isTimeValid =
                viewModel.signUp(position) { isSignUpSuccessfully: Boolean, msg: String ->
                    if (isSignUpSuccessfully) {
                        recyclerView.adapter!!.notifyDataSetChanged()
                    }
                    showToastInBottom(msg)
                }
            if (!isTimeValid) {
                recyclerView.adapter!!.notifyDataSetChanged()
                showToastInBottom("志愿活动已经超时，请及时刷新")
            }
        } else {
            showRecruitmentDetailInfo(position)
        }
    }

    private fun makeRecruitmentRecyclerView() {
        val type = Type.Recruitment
        val adapter = viewModel.getAdapter(this, type, recruitmentListener)
        val layoutManager =
            viewModel.getLayoutManager(this, type) as CardRollingLayoutManager
        layoutManager.setMidItemChangeListener { position -> updateBottomPrompt(position) }
        recyclerView.adapter = DetailListAdapter(adapter, viewModel.thirdColumns) { loadThirdColumns() }
        recyclerView.layoutManager = layoutManager
        recyclerView.enableSpecialFunction(true)
        detail_list_bottom_container.visibility = View.VISIBLE
    }

    private fun showBottomPrompt(size: Int) {
        detail_list_bottom_container.visibility = View.VISIBLE
        detail_list_all_num_tv.text = "$size"
    }

    private inline fun updateBottomPrompt(position: Int) {
        detail_list_current_num_tv.text = "${position + 1}"
    }

    private fun makeArticleRecyclerView() {
        val type = Type.Article
        val adapter = viewModel.getAdapter(this, type) { position: Int, isSignUp: Boolean ->
            showArticleDetailInfo(position)
        }
        val layoutManager = viewModel.getLayoutManager(this, type)
        recyclerView.adapter = DetailListAdapter(adapter, viewModel.thirdColumns)  { loadThirdColumns() }
        recyclerView.layoutManager = layoutManager
        recyclerView.enableSpecialFunction(false)
    }

    private fun showRecruitmentDetailInfo(position: Int) {
        val recruitment = viewModel.thirdColumns[position] as Recruitment
        val intent = Intent(this, DetailInfoActivity::class.java)
        intent.putExtra(DetailInfoActivity.TYPE, DetailInfoActivity.VOLUNTEER)
        intent.putExtra(DetailInfoActivity.TITLE, recruitment.name)
        intent.putExtra(DetailInfoActivity.ID, recruitment.id)
        startActivity(intent)
    }

    private fun showArticleDetailInfo(position: Int) {
        val article = viewModel.thirdColumns[position] as Article
        val intent = Intent(this, DetailInfoActivity::class.java)
        intent.putExtra(DetailInfoActivity.TYPE, DetailInfoActivity.ARTICLE)
        intent.putExtra(DetailInfoActivity.TITLE, article.title)
        intent.putExtra(DetailInfoActivity.ID, article.id)
        startActivity(intent)
    }

    override fun getContentViewId() = R.layout.activity_detail_list

    override fun getNormalView(): View {
        recyclerView = CardRollingRecyclerView(this).also {
            val lp = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            it.layoutParams = lp
        }
        swipeLayout = SwipeRefreshLayout(this).also {
            it.addView(recyclerView)
            it.setOnRefreshListener {
                loadThirdColumns()
            }
        }
        return swipeLayout
    }

    override fun getRetryListener(): () -> Unit {
        return {
            refreshThirdColumns()
        }
    }

    override fun getContainerForStatusLayout(): LinearLayout = detail_list_container
}
