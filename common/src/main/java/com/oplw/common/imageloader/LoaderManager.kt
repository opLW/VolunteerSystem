/*
package com.oplw.common.imageloader

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

*/
/**
 *
 *   @author opLW
 *   @date  2019/7/11
 *//*

class LoaderManager private constructor() {

    private val loaders: ExecutorService by lazy { Executors.newCachedThreadPool() }

    fun addNewTask(task: LoaderTask) {
        loaders.execute(task)
    }

    companion object {
        val instance: LoaderManager
            get() = Holder.loaderManager
    }

    private object Holder {
        val loaderManager = LoaderManager()
    }
}*/
