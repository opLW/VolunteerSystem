package com.oplw.volunteersystem.net.service

import com.oplw.volunteersystem.net.bean.Article
import com.oplw.volunteersystem.net.bean.GeneralResult
import com.oplw.volunteersystem.net.bean.TopColumn
import io.reactivex.Observable
import retrofit2.http.POST

/**
 *
 *   @author opLW
 *   @date  2019/7/11
 */
interface ContentService {

    /**
     * 获取最新的四篇文章，供首屏的轮播图使用
     */
    @POST("content/rotation")
    fun getLatestArticles(): Observable<GeneralResult<List<Article>>>

    /**
     * 获取所有的栏目，包括两级
     */
    @POST("article/tree")
    fun getAllColumns(): Observable<GeneralResult<List<TopColumn>>>
}