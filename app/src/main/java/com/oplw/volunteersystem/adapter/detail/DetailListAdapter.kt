package com.oplw.volunteersystem.adapter.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.oplw.volunteersystem.R

/**
 *
 *   @author opLW
 *   @date  2019/7/4
 */
class DetailListAdapter(
    private val adapter: IDelegateAdapter<Any>,
    private val dataList: List<Any>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val normal = 0
    private val tail = 1
    var rollingToEnd = false
        private set
    private lateinit var tailHolder: TailVH

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == tail) {
            val itemView = LayoutInflater
                .from(parent.context).inflate(R.layout.item_paging_list_tail, parent, false)
            TailVH(itemView)
        } else {
            adapter.onCreateViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == dataList.size) {
            rollingToEnd = true
            tailHolder = (holder as TailVH)
            tailHolder.hideTail()
        } else {
            rollingToEnd = false
            adapter.onBindViewHolder(holder, position, dataList[position])
        }
    }

    override fun getItemCount(): Int {
        return dataList.size + 1
    }

    override fun getItemViewType(position: Int) = if (position == dataList.size) tail else normal

    fun showNoMoreState() {
        if (rollingToEnd) {
            tailHolder.showNoMoreState()
            tailHolder.itemView.postDelayed({
                tailHolder.hideTail()
            }, 1000)
        }
    }

    private inner class TailVH(itemView: View): RecyclerView.ViewHolder(itemView) {
        val progressBar
                = itemView.findViewById<ProgressBar>(R.id.detail_list_tail_progress_bar)
        val tv = itemView.findViewById<TextView>(R.id.detail_list_tail_tv)
        private val loading = "正在拼命加载..."
        private val noMore = "人家也是有底线的"

        fun showLoadingState() {
            progressBar.visibility = View.VISIBLE
            tv.visibility = View.VISIBLE
            tv.text = loading
        }

        fun showNoMoreState() {
            progressBar.visibility = View.GONE
            tv.text = noMore
        }

        fun hideTail() {
            progressBar.visibility = View.GONE
            tv.visibility = View.GONE
        }
    }
}
