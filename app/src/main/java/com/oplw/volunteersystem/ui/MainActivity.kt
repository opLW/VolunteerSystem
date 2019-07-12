package com.oplw.volunteersystem.ui

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProviders
import com.example.albumsmanager.utilities.FormatDateUtil
import com.google.android.material.navigation.NavigationView
import com.oplw.common.customview.RotateBanner
import com.oplw.common.layoutstatus.LayoutStatusManager
import com.oplw.volunteersystem.R
import com.oplw.volunteersystem.adapter.MainListViewAdapter
import com.oplw.volunteersystem.base.showToast
import com.oplw.volunteersystem.net.bean.Article
import com.oplw.volunteersystem.net.bean.SecondaryColumn
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
    private lateinit var manager: LayoutStatusManager
    private val viewModel
            by lazy { ViewModelProviders.of(this).get(MainViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_NoActionBar)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)

        initHeaderView()
        initStatusLayout()
    }

    private fun initHeaderView() {
        val headerView = nav_view.getHeaderView(0)
        emailTv = headerView.findViewById(R.id.nav_header_email_tv)
        nameTv = headerView.findViewById(R.id.nav_header_name_tv)
        switcherBtn = headerView.findViewById(R.id.nav_header_switch_user_btn)

        viewModel.initCurrentUser()
        setUserInfo()
        switcherBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivityForResult(intent, RESULT_CODE)
            overridePendingTransition(0, 0)
            drawer_layout.closeDrawer(GravityCompat.START)
        }
    }

    private fun initStatusLayout() {
        val mainView = LayoutInflater.from(this)
            .inflate(R.layout.content_main, main_container, false)
        banner = mainView.findViewById(R.id.content_banner)
        manager = LayoutStatusManager.builder
            .setRetryListener {
                loadData()
            }
            .build(this, mainView)
        main_container.addView(manager.getRootView())

        if (isNetConnecting()) {
            manager.showMainView()
            loadData()
        } else {
            // TODO 等后台可以了修改回来
            //manager.showErrorView()

            tmp()
        }
    }

    private fun tmp() {
        manager.showMainView()
        val list = arrayListOf<TopColumn>()
        var count = 0
        for (i in 0..4) {
            val l = arrayListOf<SecondaryColumn>()
            for (j in 0..i) {
                l.add(SecondaryColumn(count, "子栏目$count", i, System.currentTimeMillis()))
            }
            count ++
            list.add(TopColumn(i, "栏目$i", l))
        }
        list.add(TopColumn(10, "栏目10", null))
        updateColumns(list)
    }

    private fun isNetConnecting(): Boolean {
        // TODO 添加网络连接的判断
        return false
    }

    private fun loadData() {
        manager.showLoadingView()
        loadAllColumns()
        loadLatestArticles()
    }

    private fun loadAllColumns(){
        viewModel.getAllColumns { isSuccessful, list ->
            val prompt = if (isSuccessful) {
                "获取子栏目成功"
            } else {
                "获取子栏目失败"
            }
            showToast(prompt)

            val isMainShowing = manager.isMainViewShowing()
            if (isSuccessful) {
                if (!isMainShowing) {
                    manager.showMainView()
                    banner.startAutoRotate()
                }
                updateColumns(list!!)
            } else {
                if (!isMainShowing) {
                    manager.showErrorView()
                }
            }
        }
    }

    private fun updateColumns(list: List<TopColumn>) {
        viewModel.content = list // TODO 后台完成后,直接使用后台的值
        val adapter = MainListViewAdapter(this, list, R.layout.item_main_list_view) {
            startNewActivity(it)
        }
        main_list_view.adapter = adapter
    }

    private fun startNewActivity(position: Int) {
        val intent = Intent(this, DetailListActivity::class.java)
        val bundle = Bundle()
        val data = viewModel.content[position]
        with(bundle) {
            putString(DetailListActivity.TITLE_NAME, data.name)
            data.irChannels?.let {
                putBoolean(DetailListActivity.HAS_CHILD, true)
                putSerializable(DetailListActivity.SECONDARY_COLUMN, it)
            }
            Log.i("Main", data.irChannels.toString())
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
            showToast(prompt)

            val isMainShowing = manager.isMainViewShowing()
            if (isSuccessful) {
                if (!isMainShowing) {
                    manager.showMainView()
                    banner.startAutoRotate()
                }
                updateBanner(list!!)
            } else {
                if (!isMainShowing) {
                    manager.showErrorView()
                }
            }
        }
        content_banner.startAutoRotate()
    }

    private fun updateBanner(list: List<Article>) {
        val count = 0
        for (article in list) {
            with(content_banner.getChildAt(count)) {
                findViewById<TextView>(R.id.general_title_tv).text = article.title
                findViewById<TextView>(R.id.general_date_tv).text =
                    FormatDateUtil.makeSuitableDateFormat(article.createdAt)
                findViewById<ImageView>(R.id.general_summary_iv).setImageDrawable(loadSrc(article.poster))
            }
        }
    }

    private fun loadSrc(id: Int): Drawable {
        return resources.getDrawable(id, null)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onPause() {
        super.onPause()
        if (manager.isMainViewShowing()) {
            banner.stopAutoRotate()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_CODE) {
            viewModel.updatePersonInfo()
            setUserInfo()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun setUserInfo() {
        nameTv.text = viewModel.user.username
        emailTv.text = viewModel.user.email
    }
}
