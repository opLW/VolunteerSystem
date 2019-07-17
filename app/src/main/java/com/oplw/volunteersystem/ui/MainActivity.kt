package com.oplw.volunteersystem.ui

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.albumsmanager.utilities.FormatDateUtil
import com.google.android.material.navigation.NavigationView
import com.oplw.common.customview.RotateBanner
import com.oplw.volunteersystem.R
import com.oplw.volunteersystem.adapter.MainRVAdapter
import com.oplw.volunteersystem.base.showToastInBottom
import com.oplw.volunteersystem.net.bean.Article
import com.oplw.volunteersystem.net.bean.TopColumn
import com.oplw.volunteersystem.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        /**
         * 登录成功时，返回的结果码
         */
        const val RESULT_CODE = 200
    }

    private lateinit var nameTv: TextView
    private lateinit var emailTv: TextView
    private lateinit var switcherBtn: Button
    private lateinit var banner: RotateBanner
    private val viewModel
            by lazy { ViewModelProviders.of(this).get(MainViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)

        initHeaderView()
        initMainContent()
    }

    private fun initHeaderView() {
        val headerView = nav_view.getHeaderView(0)
        emailTv = headerView.findViewById(R.id.nav_header_email_tv)
        nameTv = headerView.findViewById(R.id.nav_header_name_tv)
        switcherBtn = headerView.findViewById(R.id.nav_header_switch_user_btn)

        switcherBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivityForResult(intent, RESULT_CODE)
            overridePendingTransition(0, 0)
            drawer_layout.closeDrawer(GravityCompat.START)
        }
        viewModel.initCurrentUser()
        showUserInfo()
    }

    private fun initMainContent() {
        if (isNetConnecting()) {
            loadData()
        } else {
            showErrorView()
        }
    }

    private fun isNetConnecting(): Boolean {
        // TODO 添加网络连接的判断
        return true
    }

    private fun loadData() {
        showLoadingView()
        loadAllColumns()
        loadLatestArticles()
    }

    private fun loadAllColumns() {
        viewModel.getAllColumns { isSuccessful, list ->
            val prompt = if (isSuccessful) {
                "获取子栏目成功"
            } else {
                "获取子栏目失败"
            }
            showToastInBottom(prompt)

            if (isSuccessful) {
                if (isNormalViewHiding()) {
                    showNormalView()
                    banner.startAutoRotate()
                }
                showTopColumns(list!!)
            } else {
                if (isNormalViewHiding()) {
                    showErrorView()
                }
            }
        }
    }

    private fun showTopColumns(list: List<TopColumn>) {
        viewModel.content = list
        val adapter = MainRVAdapter(this, list) {
            startNewActivity(it)
        }
        main_recycler_view.layoutManager = LinearLayoutManager(this)
            .also { it.orientation = LinearLayoutManager.HORIZONTAL }
        main_recycler_view.adapter = adapter
    }

    private fun startNewActivity(position: Int) {
        val intent = Intent(this, DetailListActivity::class.java)
        val bundle = Bundle()
        val data = viewModel.content[position]
        with(bundle) {
            putInt(DetailListActivity.CHANNEL_ID, data.id)
            putString(DetailListActivity.TITLE_NAME, data.name)
            data.irChannels?.let {
                putBoolean(DetailListActivity.HAS_SECONDARY_COLUMN, true)
                putSerializable(DetailListActivity.SECONDARY_COLUMN, it)
            }
        }
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun loadLatestArticles() {
        viewModel.getLatestArticles { isSuccessful, list ->
            val prompt = if (isSuccessful) {
                "每日看点更新成功"
            } else {
                "更新失败"
            }
            showToastInBottom(prompt)

            if (isSuccessful) {
                if (isNormalViewHiding()) {
                    showNormalView()
                    banner.startAutoRotate()
                }
                showArticlesOnBanner(list!!)
            } else {
                if (isNormalViewHiding()) {
                    showErrorView()
                }
            }
        }
    }

    private fun showArticlesOnBanner(list: List<Article>) {
        viewModel.latestArticles = list
        val maxCount = Math.min(list.size, RotateBanner.MAX_COUNT)
        for (i in 0 until maxCount) {
            val article = list[i]
            with(content_banner.getChildAt(i)) {
                findViewById<TextView>(R.id.article_title_tv).text = article.title
                findViewById<TextView>(R.id.article_date_tv).text =
                    FormatDateUtil.makeDateFormat(article.createdAt)
                //findViewById<ImageView>(R.id.article_summary_iv).setImageDrawable(loadSrc(article.poster))
            }
        }
        content_banner.setChildClickListener { showDetailInfo(it) }
    }

    private fun loadSrc(id: Int): Drawable {
        // TODO 加载背景图片
        return resources.getDrawable(id, null)
    }

    private fun showDetailInfo(index: Int) {
        val article = viewModel.latestArticles[index]
        val intent = Intent(this, DetailInfoActivity::class.java)
        intent.putExtra(DetailInfoActivity.TYPE, DetailInfoActivity.ARTICLE)
        intent.putExtra(DetailInfoActivity.TITLE, article.title)
        intent.putExtra(DetailInfoActivity.ID, article.id)
        startActivity(intent)
    }

    override fun getContentViewId(): Int {
        setTheme(R.style.AppTheme_NoActionBar)
        return R.layout.activity_main
    }

    override fun getNormalView(): View {
        val mainView = LayoutInflater.from(this)
            .inflate(R.layout.content_main, main_container, false)
        banner = mainView.findViewById(R.id.content_banner)
        return mainView
    }

    override fun getRetryListener(): () -> Unit {
        return { loadData() }
    }

    override fun getContainerForStatusLayout() = main_container

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.my_recruitment -> {
                startActivity(Intent(this, MyRecruitmentActivity::class.java))
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onResume() {
        super.onResume()
        if (!isNormalViewHiding()) {
            banner.startAutoRotate()
        }
    }

    override fun onPause() {
        super.onPause()
        if (!isNormalViewHiding()) {
            banner.stopAutoRotate()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_CODE) {
            viewModel.updatePersonInfo()
            showUserInfo()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun showUserInfo() {
        nameTv.text = viewModel.user.username
        emailTv.text = viewModel.user.email
    }
}
