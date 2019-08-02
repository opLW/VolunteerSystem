package com.oplw.volunteersystem.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.oplw.volunteersystem.R
import kotlinx.android.synthetic.main.activity_detail_info.*
import kotlinx.android.synthetic.main.content_detail_info.*

class DetailInfoActivity : BaseActivity() {
    companion object {
        const val ID = "id"
        const val TITLE = "title"

        const val TYPE = "type"
        const val VOLUNTEER = "volunteer"
        const val ARTICLE = "article"
    }

    private lateinit var webView: WebView
    private val webViewClient = object : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            showLoadingView()
            detail_info_progress_bar.visibility = View.VISIBLE
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            showNormalView()
            detail_info_progress_bar.visibility = View.GONE
        }

        override fun onReceivedError(
            view: WebView?,
            errorCode: Int,
            description: String?,
            failingUrl: String?
        ) {
            val msg = when(errorCode) {
                403 -> "服务器禁止访问"
                404 -> "内容找不到"
                500 -> "服务器出现错误"
                else -> "未知错误"
            }
            showErrorWithNewContent(msg)
        }
    }
    private val webChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            detail_info_progress_bar.progress = newProgress
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(detail_info_title_toolbar) {
            this.title = ""
            setSupportActionBar(this)
            this.navigationIcon = resources.getDrawable(R.drawable.ic_return, null)
            this.setNavigationOnClickListener {
                finish()
            }
        }
        detail_info_title.text = intent.getStringExtra(TITLE)

        with(detail_info_web_view) {
            webViewClient = this@DetailInfoActivity.webViewClient
            webChromeClient = this@DetailInfoActivity.webChromeClient
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
            settings.also {
                it.setSupportZoom(true)
                //设置缓存模式
                it.cacheMode = WebSettings.LOAD_NO_CACHE

                // 让WebView的所有内容限制在一页
                it.useWideViewPort = true
                it.loadWithOverviewMode = true

                // 让控制放大缩小的按钮消失
                it.displayZoomControls = false

            }
        }

        loadDetailHtml()
    }

    private fun loadDetailHtml() {
        when(intent.getStringExtra(TYPE)) {
            VOLUNTEER -> loadVolunteerHtml()
            ARTICLE -> loadArticleHtml()
        }
    }

    private fun loadVolunteerHtml() {
        val id = intent.getIntExtra(ID, -1)
        val url = "http://192.168.162.9:8080/ir/activiy/volunteerText/$id"
        webView.loadUrl(url)
    }

    private fun loadArticleHtml() {
        val id = intent.getIntExtra(ID, -1)
        val url = "http://192.168.162.9:8080/ir/content/richtext/$id"
        webView.loadUrl(url)
    }

    override fun getContentViewId() = R.layout.activity_detail_info

    override fun getContainerForStatusLayout() = detail_info_container

    override fun getNormalView(): View {
        val view = layoutInflater.inflate(R.layout.content_detail_info, detail_info_container, false)
        webView = view.findViewById(R.id.detail_info_web_view)
        return view
    }

    override fun getRetryListener(): () -> Unit {
        return {
            loadDetailHtml()
        }
    }
}
