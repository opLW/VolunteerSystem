package com.oplw.common.layoutstatus

import android.content.Context
import android.view.View
import android.view.ViewStub
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.oplw.common.R
import com.oplw.common.layoutstatus.LayoutStatusManager.Builder

/**
 *
 *   @author opLW
 *   @date  2019/7/12
 */
class StatusLayout(
    context: Context,
    private val builder: Builder,
    private val mView: View
) : FrameLayout(context) {
    /**
     * 由于可能有一些view没有设置ResourceId，导致加载不出来布局。
     * 所以使用数组来存放，保证顺序，方便判空
     */
    private var viewArray: Array<View?> = arrayOfNulls(Builder.MAX_STATUS_COUNT)
    private val normalViewIndex = 0
    private val loadingViewIndex = 1
    private val errorViewIndex = 2

    init {
        addAllView()
    }

    private fun addAllView() {
        with(mView) {
            viewArray[normalViewIndex] = this
            addView(this)
        }
        with(ViewStub(context)) {
            layoutResource = R.layout.layout_loading
            viewArray[loadingViewIndex] = this
            addView(this)
        }
        with(ViewStub(context)) {
            layoutResource = R.layout.layout_error
            viewArray[errorViewIndex] = this
            addView(this)
        }
    }

    fun showNormalView() {
        showView(normalViewIndex)
    }

    fun showLoadingView() {
        showView(loadingViewIndex)
        setLoadingContent()
    }

    fun showErrorView() {
        showView(errorViewIndex)
        setErrorContent()
    }

    fun showErrorWithNewContent(msg: String, resId: Int = -1) {
        showView(errorViewIndex)
        if (-1 == resId) {
            setErrorContent(msg)
        } else {
            setErrorContent(msg, resId)
        }
    }

    private fun showView(index: Int) {
        for (i in viewArray.indices) {
            if (i == index) {
                viewArray[i]!!.visibility = View.VISIBLE
            } else {
                if (null != viewArray[index]) {
                    viewArray[i]!!.visibility = View.GONE
                }
            }
        }
    }

    private fun setLoadingContent() {
        findViewById<TextView>(R.id.loading_tv).text = builder.loadingMsg
        findViewById<ImageView>(R.id.loading_iv).setImageResource(builder.loadingDrawableId)
    }

    private fun setErrorContent(
        msg: String = builder.errorMsg,
        resId: Int = builder.errorDrawableId,
        retryListener: () -> Unit = builder.retryListener
    ) {
        findViewById<TextView>(R.id.error_tv).text = msg
        findViewById<ImageView>(R.id.error_iv).setImageResource(resId)
        findViewById<Button>(R.id.error_btn).setOnClickListener { retryListener() }
    }

    fun getNormalView() = viewArray[normalViewIndex]

    fun getLoadingView() = viewArray[loadingViewIndex]

    fun getErrorView() = viewArray[errorViewIndex]
}