package com.oplw.volunteersystem.net.service

import com.oplw.volunteersystem.net.bean.GeneralResult
import com.oplw.volunteersystem.net.bean.User
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 *
 *   @author opLW
 *   @date  2019/7/7
 */
interface LoginService {

    @FormUrlEncoded
    @POST("ir/volunteer/login")
    fun login(@Field("email") email: String,
              @Field("password") password: String): Observable<GeneralResult<User>>

    @FormUrlEncoded
    @POST("ir/volunteer/signUp")
    fun signUp(@Field("email") email: String,
               @Field("username") username: String,
               @Field("password") password: String): Observable<GeneralResult<Any>>
}