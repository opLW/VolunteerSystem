package com.oplw.volunteersystem

import com.oplw.volunteersystem.net.bean.User
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 *
 *   @author opLW
 *   @date  2019/7/10
 */
class MyManager private constructor() {
    companion object {
        const val SP_NAME = "volunteerSystem"
        const val USER_ID ="userId"
        const val USER_NAME = "userName"
        const val USER_EMAIL = "userEmail"
        const val LAST_VISIT_TIME = "lastVisitTime"

        @Volatile
        private var myManager: MyManager? = null

        fun getInstance(): MyManager {
            return myManager ?: synchronized(this) {
                myManager ?: MyManager().also { myManager = it }
            }
        }
    }

    var user: User? = null
    private var compositeDisposable = CompositeDisposable()

    fun addDisposable(disposable: Disposable) {
        //compositeDisposable.add(disposable)
    }

    /**
     * 在Activity关闭时，切断所有的网络连接
     */
    fun shutdownAllConnector() {
        //compositeDisposable.dispose()
    }
}
