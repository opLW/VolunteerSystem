/*
package com.oplw.common.imageloader

import android.app.Application
import android.graphics.Bitmap
import android.util.LruCache
import java.lang.ref.SoftReference
import java.util.concurrent.LinkedBlockingQueue


*/
/**
 *
 *   @author opLW
 *   @date  2019/7/11
 *//*


class RequestsManager private constructor() {

    val queue = LinkedBlockingQueue<LoaderRequest>()

    */
/**
     * 设置一个软引用
     *//*

    val hashMap = HashMap<String, SoftReference<Bitmap>>()

    private val maxCache =

    val lruCache = object : LruCache<String, Bitmap>(maxCache) {
        override fun sizeOf(key: String?, value: Bitmap?): Int {
            return value?.byteCount ?: 0
        }
    }
}
*/
