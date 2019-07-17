/*
package com.oplw.common.imageloader

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import java.util.concurrent.LinkedBlockingQueue

*/
/**
 *
 *   @author opLW
 *   @date  2019/7/11
 *//*

class RequestDispatcher(private val manager: RequestsManager): Thread() {

    private val handler = Handler(Looper.getMainLooper())

    override fun run() {
        while (!isInterrupted) {
            val request = queue.take()

            showPlaceHolder(request)
            val bitmap = findBitmap(request)
            showBitmap(bitmap)
        }
    }

    */
/**
     * 显示占位图
     *//*

    private fun showPlaceHolder(request: LoaderRequest) {
        if (request.resId > 0) {
            handler.post {
                val target = request.getTarget()
                target?.let {
                    it.setImageResource(request.resId)
                }
            }
        }
    }

    private fun findBitmap(request: LoaderRequest): Bitmap {
        if (request.strategy)
    }
}*/
