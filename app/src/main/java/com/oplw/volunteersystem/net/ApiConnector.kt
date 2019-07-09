package com.oplw.volunteersystem.net

import android.util.Log
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
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
class ApiConnector {
    private val BASE_URL = ""
    private var singleConnector: ApiConnector? = null
    private lateinit var apiService: ApiService
    var timeOutDuration = 4000L

    private constructor()

    fun getInstance(): ApiConnector{
        return singleConnector ?: synchronized(this) {
            singleConnector ?: ApiConnector()
        }
    }

    init {
        val interceptor = HttpLoggingInterceptor {
            Log.i("MessageInTheConnecting:", it.toString())
        }
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
        client.connectTimeout(timeOutDuration, TimeUnit.MILLISECONDS)
        client.addInterceptor(interceptor)

        val retrofit = Retrofit.Builder()
            .client(client.build())
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)
    }

    fun login(account: String, password: String) {

    }
}