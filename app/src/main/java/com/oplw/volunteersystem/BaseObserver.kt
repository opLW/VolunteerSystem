package com.oplw.volunteersystem.base

import io.reactivex.Observer

/**
 *
 *   @author opLW
 *   @date  2019/7/10
 */
abstract class BaseObserver<T>: Observer<T> {
    override fun onComplete() {}
}