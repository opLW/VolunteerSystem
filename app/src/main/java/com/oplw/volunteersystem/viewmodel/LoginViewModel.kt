package com.oplw.volunteersystem.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.oplw.volunteersystem.base.BaseObserver
import com.oplw.volunteersystem.net.bean.User
import com.oplw.volunteersystem.net.connector.LoginConnector
import com.oplw.volunteersystem.ui.LoginActivity
import io.reactivex.disposables.Disposable

/**
 *
 *   @author opLW
 *   @date  2019/7/3
 */
class LoginViewModel : ViewModel() {
    val isLogin = ObservableBoolean(true)
    val name = ObservableField<String>("")
    val email = ObservableField<String>("")
    val password = ObservableField<String>("")
    val confirmPassword = ObservableField<String>("")
    lateinit var callback: LoginActivity.CallBack

    private var observer = object : BaseObserver<User>() {
        override fun onNext(t: User) {
            callback.loadingFinished(true, user = t)
        }

        override fun onError(e: Throwable) {
            callback.loadingFinished(false, e.message)
        }

        override fun onSubscribe(d: Disposable) {
            callback.addNewConnector(d)
        }
    }

    fun changeState() {
        isLogin.set(!isLogin.get())
    }

    fun doAction() {
        val prompt = if (!isNetConnecting()) {
            "网络开小差！"
        } else if (isLogin.get()) {
            login()
        } else {
            signIn()
        }
        callback.showMsg(prompt)
    }

    private fun isNetConnecting(): Boolean {
        // TODO 添加网络连接的判断
        return true
    }

    private fun signIn(): String {
        val email = email.get()?.apply { if (this.isEmpty()) return "请输入邮箱" }
        val name = name.get()?.apply { if (this.isEmpty()) return "请输入用户名" }
        val password = password.get()?.apply { if (this.isEmpty()) return "请输入密码" }
        val confirmPassword = confirmPassword.get()?.apply { if (this.isEmpty()) return "请输入确认密码" }

        return when {
            !isEmailValid(email) -> "邮箱无效"
            password != confirmPassword -> "两次密码不一样"
            else -> {
                LoginConnector.getInstance().signUp(email!!, name!!, password!!, observer)
                "注册中..."
            }
        }
    }

    private fun login(): String {
        val email = email.get()?.apply { if (this.isEmpty()) return "请输入邮箱" }
        val password = password.get()?.apply { if (this.isEmpty()) return "请输入密码" }

        return when {
            !isEmailValid(email) -> "邮箱无效"
            else -> {
                LoginConnector.getInstance().login(email!!, password!!, observer)
                "登录中..."
            }
        }
    }

    private fun isEmailValid(email: String?): Boolean {
        if (null == email) {
            return false
        }
        // TODO 加入判断邮箱是否正确的正则
        return true
    }
}