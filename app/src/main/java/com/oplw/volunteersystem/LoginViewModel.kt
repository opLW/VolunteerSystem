package com.oplw.volunteersystem

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.oplw.volunteersystem.ui.LoginActivity

/**
 *
 *   @author opLW
 *   @date  2019/7/3
 */
class LoginViewModel: ViewModel() {
    val isLogin = ObservableBoolean(true)
    val isLoading = ObservableBoolean()
    val name = ObservableField<String>("")
    val password = ObservableField<String>("")
    val confrimPassword = ObservableField<String>("")
    lateinit var callback: LoginActivity.CallBack

    fun changeState() {
        isLogin.set(!isLogin.get())
    }

    fun doAction() {
        val prompt = if (isLogin.get()) {
            login()
            "登录中..."
        } else {
            signIn()
            "注册中..."
        }
        callback.loading(prompt)
    }

    private fun signIn() {
        callback.loadingFinished(true)
        //TODO 添加注册
    }

    private fun login() {
        //TODO 添加登录
    }
}