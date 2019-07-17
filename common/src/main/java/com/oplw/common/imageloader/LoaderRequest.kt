package com.oplw.common.imageloader

import android.content.Context
import android.widget.ImageView
import com.oplw.common.base.MD5Utils
import java.lang.ref.SoftReference

/**
 *   封装一个图片请求
 *   @author opLW
 *   @date  2019/7/11
 */
class LoaderRequest(val context: Context) {
    enum class RequestType {
        URL,
        RES_ID
    }

    enum class CacheStrategy {
        ONLY_RAM,
        BOTH
    }

    /**
     * 判断是请求url资源还是drawable文件夹下的内容
     */
    var type: RequestType? = null
        private set

    /**
     * 缓存的策略
     */
    var strategy = CacheStrategy.ONLY_RAM

    var url: String? = null

    var resId: Int = -1

    var placeHolder: Int = -1

    var listener: RequestListener? = null

    lateinit var urlMD5: String

    private lateinit var softReference: SoftReference<ImageView>

    fun setCacheStrategy(strategy: CacheStrategy): LoaderRequest {
        this.strategy = strategy
        return this
    }

    fun setUrl(url: String): LoaderRequest {
        type = RequestType.URL
        this.url = url
        this.urlMD5 = MD5Utils.MD5Encode(url)!!
        return this
    }

    fun setResId(resId: Int): LoaderRequest {
        type = RequestType.RES_ID
        this.resId = resId
        return this
    }

    fun setPlaceHolder(resID: Int): LoaderRequest {
        this.placeHolder = resID
        return this
    }

    fun setListener(listener: RequestListener): LoaderRequest {
        this.listener = listener
        return this
    }

    fun into(imageView: ImageView) {
        imageView.tag = urlMD5
        softReference = SoftReference(imageView)
    }

    fun getTarget(): ImageView? {
        return softReference.get()
    }
}

