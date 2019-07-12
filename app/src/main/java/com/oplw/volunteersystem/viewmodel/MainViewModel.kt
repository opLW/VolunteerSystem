package com.oplw.volunteersystem.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.oplw.volunteersystem.MyManager
import com.oplw.volunteersystem.base.BaseObserver
import com.oplw.volunteersystem.net.bean.Article
import com.oplw.volunteersystem.net.bean.TopColumn
import com.oplw.volunteersystem.net.bean.User
import com.oplw.volunteersystem.net.connector.ContentConnector
import io.reactivex.disposables.Disposable

/**
 *
 *   @author opLW
 *   @date  2019/7/11
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val sp = getApplication<Application>().getSharedPreferences(MyManager.SP_NAME, Context.MODE_PRIVATE)
    private lateinit var listener1: (isSuccessful: Boolean, list: List<Article>?) -> Unit
    private val observer1 = object : BaseObserver<List<Article>>() {
        override fun onSubscribe(d: Disposable) {
            MyManager.getInstance().addDisposable(d)
        }

        override fun onNext(t: List<Article>) {
            listener1(true, t)
        }

        override fun onError(e: Throwable) {
            listener1(false, null)
        }
    }
    private lateinit var listener2: (isSuccessful: Boolean, list: List<TopColumn>?) -> Unit
    private val observer2 = object : BaseObserver<List<TopColumn>>() {
        override fun onSubscribe(d: Disposable) {
            MyManager.getInstance().addDisposable(d)
        }

        override fun onNext(t: List<TopColumn>) {
            content = t
            listener2(true, t)
        }

        override fun onError(e: Throwable) {
            listener2(false, null)
        }
    }
    lateinit var user: User
    lateinit var content: List<TopColumn>

    fun initCurrentUser(): Boolean {
        with(sp) {
            val isLoginValid = isLoginValid(getLong(MyManager.LAST_VISIT_TIME, System.currentTimeMillis()))
            return if (isLoginValid) {
                val id = getInt(MyManager.USER_ID, 0)
                val name = getString(MyManager.USER_NAME, "")
                val email = getString(MyManager.USER_EMAIL, "")
                user = User(id, email!!, name!!)
                MyManager.getInstance().user = user
                true
            } else {
                // TODO 登录超时的提醒
                false
            }
        }
    }

    private fun isLoginValid(lastLoginTime: Long): Boolean {
        // TODO 添加超时的判断
        return true
    }

    /**
     * 在切换用户成功时，更新当前用户的信息
     */
    fun updatePersonInfo() {
        val user = MyManager.getInstance().user
        this.user = user
        sp.edit()
            .also {
                it.putInt(MyManager.USER_ID, user.id)
                it.putString(MyManager.USER_NAME, user.username)
                it.putString(MyManager.USER_EMAIL, user.email)
                it.putLong(MyManager.LAST_VISIT_TIME, System.currentTimeMillis())
                it.apply()
            }
    }

    fun getLatestArticles(listener: (Boolean, List<Article>?) -> Unit) {
        this.listener1 = listener
        ContentConnector.getInstance().getLatestArticles(observer1)
    }

    fun getAllColumns(listener: (Boolean, List<TopColumn>?) -> Unit) {
        this.listener2 = listener
        ContentConnector.getInstance().getAllColumns(observer2)
    }
}