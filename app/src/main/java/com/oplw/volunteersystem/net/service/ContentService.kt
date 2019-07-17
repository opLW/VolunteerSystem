package com.oplw.volunteersystem.net.service

import com.oplw.volunteersystem.net.bean.Article
import com.oplw.volunteersystem.net.bean.GeneralResult
import com.oplw.volunteersystem.net.bean.Recruitment
import com.oplw.volunteersystem.net.bean.TopColumn
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
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

    /**
     * 获取栏目下的文章
     * @param channelId 代表当前请求的是哪一个栏目下的内容
     * @param page 代表当前请求的是第几个分页
     * @param size 代表每页请求的数量
     */
    @FormUrlEncoded
    @POST("article/list")
    fun getArticles(@Field("channelId") channelId: Int,
                    @Field("page") page: Int,
                    @Field("size") size: Int): Observable<GeneralResult<List<Article>>>

    /**
     * 获取志愿者招募信息
     */
    @FormUrlEncoded
    @POST("activiy/volunteers")
    fun getRecruitmentInfo(@Field("page") page: Int,
                           @Field("size") size: Int,
                           @Field("finished") finished: Boolean): Observable<GeneralResult<List<Recruitment>>>

    /**
     * 报名志愿者活动
     */
    @FormUrlEncoded
    @POST("activiy/doRegister")
    fun signUpVolunteerActivity(@Field("userId") userId: Int,
                                @Field("activityId") activityId: Int): Observable<GeneralResult<Any>>

    @FormUrlEncoded
    @POST("activiy/person")
    fun getRecruitmentForUser(@Field("userId") userId: Int): Observable<GeneralResult<List<Recruitment>>>
}