package com.oplw.volunteersystem.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.albumsmanager.utilities.FormatDateUtil
import com.oplw.volunteersystem.MyManager
import com.oplw.volunteersystem.R
import com.oplw.volunteersystem.base.BaseObserver
import com.oplw.volunteersystem.base.showToastInBottom
import com.oplw.volunteersystem.net.bean.Recruitment
import com.oplw.volunteersystem.net.connector.ContentConnector
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_my_registration.*

/**
 *
 *   @author opLW
 *   @date  2019/7/17
 */
class MyRegistrationActivity: BaseActivity(){
    private lateinit var recyclerView: RecyclerView
    private lateinit var list: List<Recruitment>
    private var userId = -1
    private val listener = {
            position: Int ->
        val registration = list[position]
        val intent = Intent(this, DetailInfoActivity::class.java)
        intent.putExtra(DetailInfoActivity.TYPE, DetailInfoActivity.VOLUNTEER)
        intent.putExtra(DetailInfoActivity.TITLE, registration.name)
        intent.putExtra(DetailInfoActivity.ID, registration.id)
        startActivity(intent)
    }

    private val observer = object : BaseObserver<List<Recruitment>>() {
        override fun onSubscribe(d: Disposable) {
            MyManager.getInstance().addDisposable(d)
        }

        override fun onNext(t: List<Recruitment>) {
            list = t
            showResult()
        }

        override fun onError(e: Throwable) {
            showErrorView()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = MyManager.getInstance().user!!.id
        loadData()

        my_recruitment_tool_bar.setNavigationOnClickListener { finish() }
    }

    private fun showResult() {
        showNormalView()
        showToastInBottom("刷新成功")
        recyclerView.adapter!!.notifyDataSetChanged()
    }

    private inline fun loadData() {
        showLoadingView()
        ContentConnector.getInstance().getRecruitmentForUser(userId, observer)
    }

    override fun getContentViewId() = R.layout.activity_my_registration

    override fun getContainerForStatusLayout() = my_recruitment_container

    override fun getNormalView(): View {
        return RecyclerView(this).also {
            val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
            it.layoutParams = lp
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = MyAdapter()
            recyclerView = it
        }
    }

    override fun getRetryListener(): () -> Unit {
        return {
            loadData()
        }
    }

    inner class MyAdapter: RecyclerView.Adapter<MyAdapter.MyVH>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyVH {
            val itemView = LayoutInflater.from(this@MyRegistrationActivity)
                .inflate(R.layout.item_registration_list, parent, false)
            return MyVH(itemView)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: MyVH, position: Int) {
            holder.rebindData(position)
        }

        inner class MyVH(itemView: View): RecyclerView.ViewHolder(itemView) {
            private val name = itemView.findViewById<TextView>(R.id.registration_name_tv)
            private val location = itemView.findViewById<TextView>(R.id.registration_location_tv)
            private val ensTime = itemView.findViewById<TextView>(R.id.registration_end_time_tv)
            private val btn = itemView.findViewById<Button>(R.id.registration_check_detail_btn)
            private var chosenPosition: Int = -1

            init {
                btn.setOnClickListener { listener(chosenPosition) }
            }

            fun rebindData(position: Int) {
                chosenPosition = position
                val recruitment = list[chosenPosition]
                name.text = recruitment.name
                location.text = recruitment.location
                ensTime.text = FormatDateUtil.makeDateFormat(recruitment.endAt, true)
            }
        }
    }
}