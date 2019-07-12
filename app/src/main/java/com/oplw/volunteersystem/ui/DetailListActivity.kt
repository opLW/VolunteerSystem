package com.oplw.volunteersystem.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.oplw.volunteersystem.R
import com.oplw.volunteersystem.net.bean.SecondaryColumn
import kotlinx.android.synthetic.main.activity_detail_info.*
import java.util.*

class DetailListActivity : AppCompatActivity() {
    companion object {
        const val TITLE_NAME = "titleName"
        const val HAS_CHILD = "hasChild"
        const val SECONDARY_COLUMN = "secondaryColumn"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_info)

        val title = intent.getStringExtra(TITLE_NAME)
        initToolbar(title)

        val hasChild = intent.getBooleanExtra(HAS_CHILD, false)
        val manager = if (hasChild) {
            val irChannel = intent.getSerializableExtra(SECONDARY_COLUMN) as ArrayList<SecondaryColumn>
            Log.i("Detail", irChannel.toString())
            GridLayoutManager(this, 2)
        } else {
            LinearLayoutManager(this)
        }
    }

    private fun initToolbar(title: String) {
        with(detail_info_title_toolbar) {
            setTitle(title)
            setSupportActionBar(this)
            setNavigationIcon(R.drawable.ic_return)
            setNavigationOnClickListener {
                finish()
            }
        }
    }
}
