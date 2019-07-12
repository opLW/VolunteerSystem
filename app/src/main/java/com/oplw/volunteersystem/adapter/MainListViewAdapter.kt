package com.oplw.volunteersystem.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.oplw.volunteersystem.R
import com.oplw.volunteersystem.net.bean.TopColumn

/**
 *
 *   @author opLW
 *   @date  2019/7/12
 */
class MainListViewAdapter(context: Context,
                          list: List<TopColumn>,
                          private val resId: Int,
                          private val listener: (Int) -> Unit): ArrayAdapter<TopColumn>(context, resId, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        fun bindData(holder: ViewHolder, position: Int) {
            holder.iv.setImageResource(R.drawable.ic_news)
            holder.tv.text = getItem(position)!!.name
        }
        val view: View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(resId, parent, false)
            view.setOnClickListener { listener(position) }
            val holder = ViewHolder()
            holder.iv = view.findViewById(R.id.main_list_view_iv)
            holder.tv = view.findViewById(R.id.main_list_view_tv)
            bindData(holder, position)
            view.tag = holder
        } else {
            view = convertView
            bindData(view.tag as ViewHolder, position)
        }
        return view
    }

    private inner class ViewHolder {
        lateinit var iv: ImageView
        lateinit var tv: TextView
    }
}