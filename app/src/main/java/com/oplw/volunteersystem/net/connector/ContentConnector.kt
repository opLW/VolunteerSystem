package com.oplw.volunteersystem.net.connector

import com.oplw.volunteersystem.net.bean.Article
import com.oplw.volunteersystem.net.bean.Recruitment
import com.oplw.volunteersystem.net.bean.TopColumn
import com.oplw.volunteersystem.net.service.ContentService
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

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

    private val contentService = createConnector(ContentService::class.java)

    fun getAllColumns(observer: Observer<List<TopColumn>>) {
        contentService.getAllColumns()
            .compose(getObservableTransformer())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    fun getLatestArticles(observer: Observer<List<Article>>) {
        contentService.getLatestArticles()
            .compose(getObservableTransformer())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    fun getArticles(channelId: Int, page: Int, size: Int, observer: Observer<List<Any>>) {
        contentService.getArticles(channelId, page, size)
            .compose(getObservableTransformer())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    fun getRecruitmentInfo(page: Int, size: Int, observer: Observer<List<Any>>) {
        contentService.getRecruitmentInfo(page, size, false)
            .compose(getObservableTransformer())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    fun signUpVolunteerActivity(userId: Int, activityId: Int, observer: Observer<Any>) {
        contentService.signUpVolunteerActivity(userId, activityId)
            .map {
                if (successfulCode == it.code) {
                    return@map Any()
                } else {
                    throw Exception(it.msg)
                }
            }
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    fun getRecruitmentForUser(userId: Int, observer: Observer<List<Recruitment>>) {
        contentService.getRecruitmentForUser(userId)
            .compose(getObservableTransformer())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }
}