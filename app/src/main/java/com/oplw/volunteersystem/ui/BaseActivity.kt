package com.oplw.volunteersystem.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.oplw.common.layoutstatus.LayoutStatusManager
import com.oplw.volunteersystem.MyManager

/**
 *
 *   @author opLW
 *   @date  2019/7/11
 */
abstract class BaseActivity: AppCompatActivity() {
    private lateinit var manager: LayoutStatusManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getContentViewId())

        manager = LayoutStatusManager.builder
            .setRetryListener(getRetryListener())
            .build(this, getNormalView())
        getContainerForStatusLayout().addView(manager.getRootView())
    }

    override fun onStop() {
        MyManager.getInstance().shutdownAllConnector()
        super.onStop()
    }

    protected fun getRootView() = manager.getRootView()

    protected fun isNormalViewHiding() = !manager.isNormalViewShowing()

    protected fun showNormalView() = manager.showNormalView()

    protected fun showLoadingView() = manager.showLoadingView()

    protected fun showErrorView() = manager.showErrorView()

    protected fun showErrorWithNewContent(msg: String, resId: Int = -1) {
        manager.showErrorWithNewContent(msg, resId)
    }

    /**
     * 设置setContentView使用的布局ID
     */
    abstract fun getContentViewId(): Int

    /**
     * 设置存放manager三个子view的容器
     */
    abstract fun getContainerForStatusLayout(): ViewGroup

    /**
     * 设置网络正常时显示的画面
     */
    abstract fun getNormalView(): View

    /**
     * 设置没网络时点击重试的监听者
     */
    abstract fun getRetryListener(): () -> Unit
}