package com.oplw.volunteersystem.adapter.detail

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 *
 *   @author opLW
 *   @date  2019/7/4
 */
class DetailListAdapter(
    private val adapter: IDelegateAdapter<Any>,
    private val dataList: List<Any>,
    private val loadMoreListener: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return adapter.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        adapter.onBindViewHolder(holder, position, dataList[position])
        /*if (position == dataList.lastIndex) {
            loadMoreListener()
        }*/
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}
