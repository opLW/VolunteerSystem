package com.oplw.volunteersystem.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.oplw.volunteersystem.MyManager
import com.oplw.volunteersystem.R
import kotlinx.android.synthetic.main.activity_my_recruitment.*

/**
 *
 *   @author opLW
 *   @date  2019/7/17
 */
class MyRecruitmentActivity: BaseActivity(){
    private lateinit var recyclerView: RecyclerView
    private val userId =
        getSharedPreferences(MyManager.SP_NAME, Context.MODE_PRIVATE).getInt(MyManager.USER_ID, -1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun getContentViewId() = R.layout.activity_my_recruitment

    override fun getContainerForStatusLayout() = my_recruitment_container

    override fun getNormalView(): View {
        return RecyclerView(this).also {
            val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
            it.layoutParams = lp
            recyclerView = it
        }
    }

    override fun getRetryListener(): () -> Unit {
        return {
            //ContentConnector.getInstance().getRecruitmentForUser(userId,)
        }
    }
}