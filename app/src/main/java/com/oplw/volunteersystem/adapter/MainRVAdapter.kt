package com.oplw.volunteersystem.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.oplw.volunteersystem.R
import com.oplw.volunteersystem.net.bean.TopColumn

/**
 *
 *   @author opLW
 *   @date  2019/7/12
 */
class MainRVAdapter(private val context: Context,
                    private val list: List<TopColumn>,
                    private val listener: (Int) -> Unit): RecyclerView.Adapter<MainRVAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context)
            .inflate(R.layout.item_top_list_main, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.rebindData(list[position])
        holder.itemView.setOnClickListener { listener(position) }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private lateinit var iv: ImageView
        private var tv = itemView.findViewById<TextView>(R.id.main_rv_view_tv)

        fun rebindData(column: TopColumn) {
            // TODO 根据情况变换icon
            tv.text = column.name
        }
    }
}