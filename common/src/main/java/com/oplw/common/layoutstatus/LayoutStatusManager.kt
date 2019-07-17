package com.oplw.common.layoutstatus

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.oplw.common.R

/**
 *
 *   @author opLW
 *   @date  2019/7/12
 */
class LayoutStatusManager(context: Context, builder: Builder, mainView: View) {

    private var rootView: StatusLayout = StatusLayout(context, builder, mainView)

    init {
        val lp = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        rootView.layoutParams = lp
    }

    fun getRootView() = rootView

    fun isNormalViewShowing(): Boolean{
        val normalView = rootView.getNormalView()
        return null != normalView && normalView.visibility != View.GONE
    }

    fun showNormalView() {
        rootView.showNormalView()
    }

    fun showLoadingView() {
        rootView.showLoadingView()
    }

    fun showErrorView() {
        rootView.showErrorView()
    }

    fun showErrorWithNewContent(msg: String, resId: Int = -1) {
        rootView.showErrorWithNewContent(msg, resId)
    }

    class Builder {
        companion object {
            const val MAX_STATUS_COUNT = 3
        }

        var errorDrawableId: Int = R.drawable.ic_failed
        var errorMsg = "网络出现错误"
        lateinit var retryListener: () -> Unit
        var loadingDrawableId: Int = R.drawable.ic_loading
        var loadingMsg = "正在加载..."

        fun setErrorContent(drawableId: Int, msg: String): Builder {
            errorDrawableId = drawableId
            errorMsg = msg
            return this
        }

        fun setLoadingContent(drawableId: Int, msg: String): Builder {
            loadingDrawableId = drawableId
            loadingMsg = msg
            return this
        }

        fun setRetryListener(listener: () -> Unit): Builder {
            retryListener = listener
            return this
        }

        fun build(context: Context, mainView: View) = LayoutStatusManager(context, this, mainView)
    }

    companion object {
        val builder = Builder()
    }
}