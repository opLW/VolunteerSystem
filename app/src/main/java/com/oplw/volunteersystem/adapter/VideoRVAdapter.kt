package com.oplw.volunteersystem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.oplw.volunteersystem.R
import com.oplw.volunteersystem.net.bean.Video

/**
 *
 *   @author opLW
 *   @date  2019/7/24
 */
class VideoRVAdapter(
    private val videoList: List<Video>,
    private val clickListener: (Int) -> Unit
) : RecyclerView.Adapter<VideoRVAdapter.VideoVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoVH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_video_list, parent, false)
        return VideoVH(itemView, clickListener)
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    override fun onBindViewHolder(holder: VideoVH, position: Int) {
        holder.rebindData(position)
    }

    inner class VideoVH(itemView: View, listener: (Int) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private var mPosition = -1
        private val tv = itemView.findViewById<TextView>(R.id.video_item_name_tv)

        init {
            itemView.setOnClickListener {
                listener(mPosition)
            }
        }

        fun rebindData(position: Int) {
            mPosition = position
            tv.text = "视频${position + 1}"
        }
    }
}