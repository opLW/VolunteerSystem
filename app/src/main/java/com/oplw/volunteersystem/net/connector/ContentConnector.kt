package com.oplw.volunteersystem.net.connector

import com.oplw.volunteersystem.net.bean.Article
import com.oplw.volunteersystem.net.bean.TopColumn
import com.oplw.volunteersystem.net.service.ContentService
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 *
 *   @author opLW
 *   @date  2019/7/11
 */
class ContentConnector private constructor(): BaseConnector() {
    companion object {
        @Volatile private var connector: ContentConnector? = null

        fun getInstance(): ContentConnector {
            return connector ?: synchronized(this) {
                connector ?: ContentConnector().also { connector = it }
            }
        }
    }

    private val contentService by lazy { createConnector(ContentService::class.java) }

    fun getLatestArticles(observer: Observer<List<Article>>) {
        contentService.getLatestArticles()
            .compose(getObservableTransformer())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    fun getAllColumns(observer: Observer<List<TopColumn>>) {
        contentService.getAllColumns()
            .compose(getObservableTransformer())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }
}