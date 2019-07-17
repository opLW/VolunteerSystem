package com.oplw.volunteersystem.viewmodel

import android.content.Context
import androidx.collection.SparseArrayCompat
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oplw.common.customview.rv.CardRollingLayoutManager
import com.oplw.volunteersystem.MyManager
import com.oplw.volunteersystem.adapter.detail.ArticleAdapter
import com.oplw.volunteersystem.adapter.detail.IDelegateAdapter
import com.oplw.volunteersystem.adapter.detail.RecruitmentAdapter
import com.oplw.volunteersystem.adapter.detail.SecondaryAdapter
import com.oplw.volunteersystem.base.BaseObserver
import com.oplw.volunteersystem.net.bean.Recruitment
import com.oplw.volunteersystem.net.bean.SecondaryColumn
import com.oplw.volunteersystem.net.connector.ContentConnector
import io.reactivex.disposables.Disposable

/**
 *
 *   @author opLW
 *   @date  2019/7/15
 */
class DetailViewModel : ViewModel() {
    enum class Type {
        Secondary,
        Video,
        Article,
        Recruitment
    }

    private val adapters = SparseArrayCompat<IDelegateAdapter<Any>>()
    private val layoutManagers = SparseArrayCompat<RecyclerView.LayoutManager>()

    fun getAdapter(
        context: Context, type: Type, clickListener: (Int, Boolean) -> Unit
    ): IDelegateAdapter<Any> {
        val index = getAdapterIndex(type)
        var adapter = adapters.get(index)
        if (null == adapter) {
            adapter = (createAdapter(context, type, clickListener) as IDelegateAdapter<Any>?)!!
            adapters.put(index, adapter)
        }
        return adapter
    }

    private fun createAdapter(context: Context, type: Type, clickListener: (Int, Boolean) -> Unit) =
        when (type) {
            Type.Article -> ArticleAdapter(context, clickListener)
            Type.Secondary -> SecondaryAdapter(context, clickListener)
            else -> RecruitmentAdapter(context, clickListener)
            // else -> null // TODO 添加video的adapter
        }

    fun getLayoutManager(context: Context, type: Type): RecyclerView.LayoutManager {
        val index = getAdapterIndex(type)
        var layoutManager = layoutManagers.get(index)
        if (null == layoutManager) {
            layoutManager = createLayoutManager(context, type)
            layoutManagers.put(index, layoutManager)
        }
        return layoutManager
    }

    private fun createLayoutManager(context: Context, type: Type) = when (type) {
        Type.Secondary -> GridLayoutManager(context, 2)
        Type.Recruitment -> CardRollingLayoutManager()
        else -> LinearLayoutManager(context)
    }

    private fun getAdapterIndex(type: Type) = when (type) {
        Type.Secondary -> 0
        Type.Video -> 1
        Type.Article -> 2
        else -> 3
    }

    lateinit var secondaryColumns: List<SecondaryColumn>
    var thirdColumns = arrayListOf<Any>()
    var targetPage: Int = -1
    private val countOfPerPage = 15

    var channelId = -1
    val recruitmentId = 10
    val videoId = 16

    private lateinit var loadThirdColumnsListener: (Boolean, String) -> Unit
    private val thirdColumnObserver = object : BaseObserver<List<Any>>() {
        override fun onSubscribe(d: Disposable) {
            MyManager.getInstance().addDisposable(d)
        }

        override fun onNext(t: List<Any>) {
            thirdColumns.addAll(t)
            loadThirdColumnsListener(true, "刷新成功")
        }

        override fun onError(e: Throwable) {
            loadThirdColumnsListener(false, e.message ?: "")
        }
    }

    fun loadThirdColumns(listener: (Boolean, String) -> Unit) {
        targetPage++
        loadThirdColumnsListener = listener
        with(ContentConnector.getInstance()) {
            when (channelId) {
                recruitmentId -> getRecruitmentInfo(
                    targetPage,
                    countOfPerPage,
                    thirdColumnObserver
                )
                videoId -> { } //TODO 添加访问视频
                else -> getArticles(
                    channelId, targetPage,
                    countOfPerPage, thirdColumnObserver
                )
            }
        }
    }

    private var chosenRecruitmentIndex = -1
    private lateinit var signUpListener: (Boolean, String) -> Unit
    private val secondaryColumnObserver = object : BaseObserver<Any>() {
        override fun onSubscribe(d: Disposable) {
            MyManager.getInstance().addDisposable(d)
        }

        override fun onNext(t: Any) {
            thirdColumns.removeAt(chosenRecruitmentIndex)
            signUpListener(true, "报名成功")
        }

        override fun onError(e: Throwable) {
            signUpListener(false, e.message ?: "")
        }
    }

    fun signUp(position: Int, listener: (Boolean, String) -> Unit): Boolean {
        chosenRecruitmentIndex = position
        signUpListener = listener

        val recruitment = thirdColumns[position] as Recruitment
        return if (isTimeValid(recruitment.endAt)) {
            val userId = MyManager.getInstance().user.id
            val activityId = recruitment.id
            ContentConnector.getInstance()
                .signUpVolunteerActivity(userId, activityId, secondaryColumnObserver)
            true
        } else {
            thirdColumns.remove(chosenRecruitmentIndex)
            false
        }
    }

    private fun isTimeValid(endAt: Long) = System.currentTimeMillis() < endAt

    fun clearThirdColumns() {
        targetPage = 0
        thirdColumns.clear()
    }

    fun isRefreshResult() = targetPage == 1
}