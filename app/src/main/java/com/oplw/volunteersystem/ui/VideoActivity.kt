package com.oplw.volunteersystem.ui

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.jcodeing.kmedia.AndroidMediaPlayer
import com.jcodeing.kmedia.Player
import com.jcodeing.kmedia.video.ControlLayerView
import com.oplw.volunteersystem.MyManager
import com.oplw.volunteersystem.R
import com.oplw.volunteersystem.adapter.VideoRVAdapter
import com.oplw.volunteersystem.base.BaseObserver
import com.oplw.volunteersystem.base.showToastInBottom
import com.oplw.volunteersystem.base.showToastInCenter
import com.oplw.volunteersystem.net.bean.Video
import com.oplw.volunteersystem.net.connector.ContentConnector
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_video.*


class VideoActivity : BaseActivity() {
    private val videoKey = "videoKey"

    private var isFullScreen = false
    private var showingVideoId = -1

    private lateinit var player: Player
    private lateinit var minTitle: TextView
    private lateinit var controller: ControlLayerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeLayout: SwipeRefreshLayout

    private var videos = arrayListOf<Video>()
    private lateinit var videoAdapter: VideoRVAdapter
    private val observer = object : BaseObserver<List<Video>>() {
        override fun onSubscribe(d: Disposable) {
            MyManager.getInstance().addDisposable(d)
        }

        override fun onNext(t: List<Video>) {
            showLoadedResult(t)
            showToastInBottom("刷新成功")
        }

        override fun onError(e: Throwable) {
            if (videos.isEmpty()) {
                showErrorView()
            } else {
                showToastInBottom("刷新失败")
            }
        }
    }
    private val rvItemClickListener = {
        position: Int ->
        showVideoContent(position)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(video_toolbar) {
            title = "宣传视频"
            setSupportActionBar(this)
            setNavigationOnClickListener { exitActivityOrFullScreen() }
        }
        initVideoPlayer()

        showLoadingView()
        loadVideos()
    }

    private fun initVideoPlayer() {
        player = Player(this).init(AndroidMediaPlayer())
        video_player.setPlayer(player)
        video_player.setOrientationHelper(this, 1)

        controller = video_player.findViewById(R.id.k_ctrl_layer_port)
        with(controller.initPart(R.id.k_ctrl_layer_part_top)) {
            findViewById<ImageView>(R.id.video_min_return_iv).setOnClickListener {
                if (isFullScreen) exitActivityOrFullScreen()
            }
            findViewById<ImageView>(R.id.video_min_full_screen_iv).setOnClickListener {
                if (!isFullScreen) {
                    isFullScreen = true
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
            }
            minTitle = findViewById(R.id.video_min_title_tv)
        }
    }

    private fun loadVideos() {
        ContentConnector.getInstance().getVideos(1, 24, observer)
    }

    private fun showLoadedResult(list: List<Video>) {
        if (isNormalViewHiding()) {
            showNormalView()
        }
        swipeLayout.isRefreshing = false

        videos.clear()
        videos.addAll(list)
        videoAdapter.notifyDataSetChanged()
    }

    private fun showVideoContent(position: Int) {
        val video = videos[position]
        if (video.id != showingVideoId) {
            showingVideoId = video.id
            minTitle.text = clipTitle(video.title)
            player.play(Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"))
            player.start()

            showToastInCenter("加载中...")
        } else {
            showToastInBottom("请勿重复点击")
        }
    }

    private fun clipTitle(title: String) : String {
        val lastIndexOfPot = title.lastIndexOf(".")
        return title.substring(0 until lastIndexOfPot)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitActivityOrFullScreen()
            true
        } else {
            super.onKeyDown(keyCode, event)
        }
    }

    private fun exitActivityOrFullScreen() {
        if (isFullScreen) {
            isFullScreen = false
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            finish()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isFullScreen = true
            changeStateOfOtherView(View.GONE)
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            isFullScreen = false
            changeStateOfOtherView(View.VISIBLE)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(videoKey, videos)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        videos = savedInstanceState?.getSerializable(videoKey) as ArrayList<Video>
        videoAdapter.notifyDataSetChanged()
    }

    private fun changeStateOfOtherView(visibility: Int) {
        video_toolbar.visibility = visibility
        video_prompt_title.visibility = visibility
        video_container.visibility = visibility
    }

    override fun onPause() {
        super.onPause()
        player.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        video_player.finish()
        player.shutdown()
    }

    override fun getContentViewId() = R.layout.activity_video

    override fun getNormalView(): View {
        recyclerView = RecyclerView(this).also {
            val lp = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            it.layoutParams = lp
            it.layoutManager = GridLayoutManager(this, 3)
            videoAdapter = VideoRVAdapter(videos, rvItemClickListener)
            it.adapter = videoAdapter
        }
        swipeLayout = SwipeRefreshLayout(this).also {
            it.addView(recyclerView)
            it.isRefreshing = true
            it.setOnRefreshListener { loadVideos() }
        }
        return swipeLayout
    }

    override fun getRetryListener(): () -> Unit {
        return {
            showLoadingView()
            loadVideos()
        }
    }

    override fun getContainerForStatusLayout(): ViewGroup {
        return video_container
    }
}
