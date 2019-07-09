package com.oplw.volunteersystem.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.oplw.volunteersystem.CommonRVAdapter
import com.oplw.volunteersystem.R
import com.oplw.volunteersystem.data.CommonItemBean
import kotlinx.android.synthetic.main.activity_detail_info.*

class DetailListActivity : AppCompatActivity() {
    companion object {
        val KEY = "key"
        val ACTIVITY = "activity"
        val NEWS = "news"
        val VIDEO = "video"
        val VOLUNTEER_RECRUIT = "volunteerRecruit"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_info)

        initToolbar()

        detail_info_RV.layoutManager = LinearLayoutManager(this)
        val list = arrayListOf<CommonItemBean>()
            list.add(CommonItemBean("2019-7-7", "公益活动", ""))
            list.add(CommonItemBean("2019-7-8", "新闻专栏", ""))
            list.add(CommonItemBean("2019-7-9", "精彩瞬间", ""))
            list.add(CommonItemBean("2019-7-10", "志愿者招募", ""))
        detail_info_RV.adapter = CommonRVAdapter(this, list)
        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        detail_info_RV.addItemDecoration(divider)
    }

    private fun initToolbar() {
        with(detail_info_title_toolbar) {
            this.title = getActivityTitle()
            setSupportActionBar(this)
            this.setNavigationIcon(R.drawable.ic_return)
            this.setNavigationOnClickListener {
                finish()
            }
        }
    }

    private fun getActivityTitle() = when(intent.getStringExtra(KEY)) {
        ACTIVITY -> "公益活动"
        NEWS -> "新闻专栏"
        VIDEO -> "精彩瞬间"
        else -> "志愿者招募"
    }
}
