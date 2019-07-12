package com.oplw.volunteersystem.net.connector

import android.util.Log
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.oplw.volunteersystem.net.bean.GeneralResult
import io.reactivex.ObservableTransformer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/**
 *
 *   @author opLW
 *   @date  2019/7/7
 */
abstract class BaseConnector{
    private val baseUrl = "http://192.168.162.9:8080/"
    private val timeOutDuration = 4000L
    private var retrofit: Retrofit

    protected val successfulCode = 0
    protected val errorCode = 500

    init {
        val interceptor = HttpLoggingInterceptor {
            Log.i("Message", it.toString())
        }
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
        client.connectTimeout(timeOutDuration, TimeUnit.MILLISECONDS)
        client.addInterceptor(interceptor)

        retrofit = Retrofit.Builder()
            .client(client.build())
            .baseUrl(baseUrl)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> createConnector(service: Class<T>): T = retrofit.create(service)

    protected fun <T> getObservableTransformer(): ObservableTransformer<GeneralResult<T>, T> {
        return ObservableTransformer { upstream ->
            upstream.map(object : Function<GeneralResult<T>, T> {
                override fun apply(t: GeneralResult<T>): T {
                    if (t.code == errorCode) {
                        throw Exception(t.msg)
                    } else {
                        if (null == t.data) {
                            throw Exception("null data returned!")
                        } else {
                            return t.data as T
                        }
                    }
                }
            })
                .subscribeOn(Schedulers.newThread())
        }
    }
}