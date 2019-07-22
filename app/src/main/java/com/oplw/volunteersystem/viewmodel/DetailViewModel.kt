package com.oplw.volunteersystem.viewmodel

import android.content.Context
import androidx.collection.SparseArrayCompat
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oplw.common.customview.rv.CardRollingLayoutManager
import com.oplw.volunteersystem.MyManager
import com.oplw.volunteersystem.adapter.detail.*
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
        Recruitment,
        None
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
            Type.Video -> VideoAdapter(clickListener)
            else -> RecruitmentAdapter(context, clickListener)
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
        Type.Recruitment -> CardRollingLayoutManager()
        Type.Secondary -> GridLayoutManager(context, 2)
        Type.Video -> GridLayoutManager(context, 3)
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

    private lateinit var loadThirdColumnsListener: (Int, String) -> Unit
    private val thirdColumnObserver = object : BaseObserver<List<Any>>() {
        override fun onSubscribe(d: Disposable) {
            MyManager.getInstance().addDisposable(d)
        }

        override fun onNext(t: List<Any>) {
            if (isResultFromRefresh()) {
                thirdColumns.clear()
            }
            thirdColumns.addAll(t)
            loadThirdColumnsListener(t.size, "刷新成功")
        }

        override fun onError(e: Throwable) {
            loadThirdColumnsListener(-1, e.message ?: "")
        }
    }

    fun loadThirdColumns(listener: (Int, String) -> Unit) {
        targetPage++
        loadThirdColumnsListener = listener
        with(ContentConnector.getInstance()) {
            when (channelId) {
                recruitmentId -> getRecruitmentInfo(
                    targetPage,
                    countOfPerPage,
                    thirdColumnObserver
                )
                videoId -> getVideos(
                    targetPage,
                    countOfPerPage,
                    thirdColumnObserver
                )
                else -> getArticles(
                    channelId, targetPage,
                    countOfPerPage, thirdColumnObserver
                )
            }
        }
    }

    enum class SignUpResult {
        NoSignIn,
        Success,
        TimeOut
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

    fun signUp(position: Int, listener: (Boolean, String) -> Unit): SignUpResult {
        val userId = MyManager.getInstance().user?.id ?: -1
        if (userId == -1) {
            return SignUpResult.NoSignIn
        }

        chosenRecruitmentIndex = position
        signUpListener = listener
        val recruitment = thirdColumns[position] as Recruitment
        return if (isTimeValid(recruitment.endAt)) {
            val activityId = recruitment.id
            ContentConnector.getInstance()
                .signUpVolunteerActivity(userId, activityId, secondaryColumnObserver)
            SignUpResult.Success
        } else {
            thirdColumns.remove(chosenRecruitmentIndex)
            SignUpResult.TimeOut
        }
    }

    private fun isTimeValid(endAt: Long) = System.currentTimeMillis() < endAt

    fun setStateToRefresh() {
        targetPage = 0
    }

    /**
     * 如果targetPage为1，表示请求的结果为第一页的内容，即为刷新
     */
    fun isResultFromRefresh() = targetPage == 1

    fun hasNoThirdColumns() = thirdColumns.isEmpty()

    fun clearThirdColumns() {
        thirdColumns.clear()
    }
}