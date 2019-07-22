package com.oplw.volunteersystem.adapter.detail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.albumsmanager.utilities.FormatDateUtil
import com.oplw.volunteersystem.R
import com.oplw.volunteersystem.net.bean.SecondaryColumn

/**
 *
 *   @author opLW
 *   @date  2019/7/15
 */
class SecondaryAdapter(private val context: Context,
                       private val clickListener: (Int, Boolean) -> Unit
): IDelegateAdapter<SecondaryColumn> {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_secondary_list, parent, false)
        return SecondaryVH(context, itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, t: SecondaryColumn): View {
        if (holder !is SecondaryVH) {
            throw Exception("The holder is not the SecondaryVH")
        }
        holder.rebindData(t, position)
        return holder.itemView
    }

    private inner class SecondaryVH(private val context: Context, itemView: View): RecyclerView.ViewHolder(itemView) {
        private val iv = itemView.findViewById<ImageView>(R.id.secondary_iv)
        private val titleTv = itemView.findViewById<TextView>(R.id.secondary_title_tv)
        private val dateTv = itemView.findViewById<TextView>(R.id.secondary_date_tv)
        private var mPosition = -1

        init {
            itemView.setOnClickListener { clickListener(mPosition, false) }
        }

        fun rebindData(data: SecondaryColumn, position: Int) {
            mPosition = position
            iv.setImageResource(getDrawableId(position))
            titleTv.text = data.name
            dateTv.text = FormatDateUtil.makeDateFormat(data.createdAt)
        }

        private fun getDrawableId(id: Int)
            = context.resources.getIdentifier("pic_secondary_${id % 5 + 1}", "drawable", context.packageName)
    }
}