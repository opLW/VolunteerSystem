package com.oplw.volunteersystem

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.oplw.volunteersystem.data.CommonItemBean

/**
 *
 *   @author opLW
 *   @date  2019/7/4
 */
class CommonRVAdapter(val context: Context,
                      val dataList: List<CommonItemBean>): RecyclerView.Adapter<CommonRVAdapter.GeneralViewHolder>(){

    inner class GeneralViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val iv = itemView.findViewById<ImageView>(R.id.general_summary_iv)
        private val titleTv = itemView.findViewById<TextView>(R.id.general_title_tv)
        private val dateTv = itemView.findViewById<TextView>(R.id.general_date_tv)

        fun rebindData(data: CommonItemBean) {
            iv.setImageDrawable(context.resources.getDrawable(R.drawable.ic_head_image, null))
            titleTv.text = data.title
            dateTv.text = data.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeneralViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_general, parent, false)
        return GeneralViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GeneralViewHolder, position: Int) {
        holder.rebindData(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}
