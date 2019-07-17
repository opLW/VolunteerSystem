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
        const val TYPE = "type"
        const val VOLUNTEER = "volunteer"
        const val ARTICLE = "article"

        const val TITLE = "title"
        const val ID = "id"
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

        val title = intent.getStringExtra(TITLE)
        with(detail_info_title_toolbar) {
            this.title = title
            setSupportActionBar(this)
            this.navigationIcon = resources.getDrawable(R.drawable.ic_return, null)
            this.setNavigationOnClickListener {
                finish()
            }
        }

        with(detail_info_web_view) {
            webViewClient = this@DetailInfoActivity.webViewClient
            webChromeClient = this@DetailInfoActivity.webChromeClient
            settings.also {
                it.builtInZoomControls = true
                it.setSupportZoom(true)
                it.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
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
