package com.oplw.volunteersystem.adapter.detail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.albumsmanager.utilities.FormatDateUtil
import com.oplw.volunteersystem.R
import com.oplw.volunteersystem.net.bean.Article

/**
 *
 *   @author opLW
 *   @date  2019/7/15
 */
class ArticleAdapter(private val context: Context,
                     private val clickListener: (Int, Boolean) -> Unit
) : IDelegateAdapter<Article> {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_article_list, parent, false)
        return ArticleVH(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, t: Article): View {
        if (holder !is ArticleVH) {
            throw Exception("The holder is not the ArticleVH")
        }
        holder.rebindData(t, position)
        return holder.itemView
    }

    private inner class ArticleVH(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val titleTv = itemView.findViewById<TextView>(R.id.article_title_tv)
        private val dateTv = itemView.findViewById<TextView>(R.id.article_date_tv)
        private var mPosition = -1

        init {
            itemView.setOnClickListener {
                clickListener(mPosition, false)
            }
        }

        fun rebindData(data: Article, position: Int) {
            mPosition = position
            itemView.background = context.resources.getDrawable(getDrawableId(position), null)
            titleTv.text = data.title
            dateTv.text = FormatDateUtil.makeDateFormat(data.createdAt)
        }

        private fun getDrawableId(index: Int)
                = context.resources.getIdentifier("pic_${index % 5 + 1}", "drawable", context.packageName)
    }
}