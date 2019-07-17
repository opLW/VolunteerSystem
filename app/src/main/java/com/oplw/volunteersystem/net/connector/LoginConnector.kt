package com.oplw.volunteersystem.net.connector

import com.oplw.volunteersystem.net.bean.User
import com.oplw.volunteersystem.net.service.LoginService
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 *
 *   @author opLW
 *   @date  2019/7/11
 */
class LoginConnector private constructor() : BaseConnector() {

    companion object {
        @Volatile private var connector: LoginConnector? = null

        fun getInstance(): LoginConnector {
            return connector ?: synchronized(this) {
                connector ?: LoginConnector().also { connector = it }
            }
        }
    }

    private val loginService = createConnector(LoginService::class.java)

    fun login(email: String, password: String, observer: Observer<User>) {
        loginService.login(email, password)
            .compose(getObservableTransformer())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    fun signUp(email: String, userName: String, password: String, observer: Observer<User>) {
        loginService.signUp(email, userName, password)
            .map {
                if (successfulCode == it.code) {
                    return@map Any()
                } else {
                    throw Exception(it.msg)
                }
            }
            .subscribeOn(Schedulers.newThread())
            .observeOn(Schedulers.newThread())
            .flatMap {
                loginService.login(email, password)
                    .compose(getObservableTransformer())
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }
}