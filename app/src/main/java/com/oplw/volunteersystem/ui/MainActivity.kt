package com.oplw.volunteersystem.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.oplw.volunteersystem.CustomBanner
import com.oplw.volunteersystem.R
import com.oplw.volunteersystem.data.CommonItemBean
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

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
        addListenerForSwitchUserBtn()

        initBanner()
        initChildItem()
    }

    private fun initBanner() {
        val list = arrayListOf<CommonItemBean>()
        for (i in 0..3) {
            list.add(CommonItemBean("2019-7-7", "公益活动", ""))
            list.add(CommonItemBean("2019-7-8", "新闻专栏", ""))
            list.add(CommonItemBean("2019-7-9", "精彩瞬间", ""))
            list.add(CommonItemBean("2019-7-10", "志愿者招募", ""))
        }
        for (i in 0..3) {
            content_banner.getChildAt(i).findViewById<TextView>(R.id.general_date_tv).text = list[i].date
            content_banner.getChildAt(i).findViewById<TextView>(R.id.general_title_tv).text = list[i].title
        }

        content_banner.setChildClickListener(object: CustomBanner.OnChildClickListener {
            override fun onClick(index: Int) {

            }
        })
    }

    private fun initChildItem() {
        content_child_item_1.setOnClickListener {
            startNewActivity(DetailListActivity.ACTIVITY)
        }
        content_child_item_2.setOnClickListener {
             startNewActivity(DetailListActivity.NEWS)
        }
        content_child_item_3.setOnClickListener {
            startNewActivity(DetailListActivity.VIDEO)
        }
        content_child_item_1.setOnClickListener {
            startNewActivity(DetailListActivity.VOLUNTEER_RECRUIT)
        }
    }

    private fun startNewActivity(type: String) {
        val intent = Intent(this, DetailListActivity::class.java)
        intent.putExtra(DetailListActivity.KEY, type)
        startActivity(intent)
    }

    private fun addListenerForSwitchUserBtn() {
        val btn = nav_view.getHeaderView(0).findViewById<Button>(R.id.nav_header_switch_user_btn)
        btn.setOnClickListener {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            overridePendingTransition(0,0)
            drawer_layout.closeDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onResume() {
        super.onResume()
        content_banner.startAutoRotate()
    }

    override fun onPause() {
        super.onPause()
        content_banner.stopAutoRotate()
    }
}
