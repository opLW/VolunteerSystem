package com.oplw.volunteersystem.viewmodel

import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.oplw.common.base.emailRegex
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
            Log.i("onNext", t.toString())
        }

        override fun onError(e: Throwable) {
            callback.loadingFinished(false, e.message)
            Log.i("onError", e.message)
        }

        override fun onSubscribe(d: Disposable) {
            callback.addNewConnector(d)
            Log.i("onSubscribe", d.toString())
        }

        override fun onComplete() {
            super.onComplete()
            Log.i("onComplete", "onComplete")
        }
    }

    fun changeState() {
        isLogin.set(!isLogin.get())
    }

    fun doAction() {
        val prompt = if (isLogin.get()) {
            login()
        } else {
            signIn()
        }
        callback.showMsg(prompt)
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
        return email.matches(Regex(emailRegex))
    }
}