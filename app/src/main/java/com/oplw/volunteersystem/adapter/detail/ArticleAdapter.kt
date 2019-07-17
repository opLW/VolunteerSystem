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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, t: Article): View{
        if (holder !is ArticleVH) {
            throw Exception("The holder is not the ArticleVH")
        }
        holder.rebindData(t)
        return holder.itemView.also {
            it.setOnClickListener { clickListener(position, false) }
        }
    }

    private inner class ArticleVH(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val iv = itemView.findViewById<ImageView>(R.id.article_summary_iv)
        private val titleTv = itemView.findViewById<TextView>(R.id.article_title_tv)
        private val dateTv = itemView.findViewById<TextView>(R.id.article_date_tv)

        fun rebindData(data: Article) {
            iv.setImageDrawable(context.resources.getDrawable(R.drawable.ic_head_image, null))
            titleTv.text = data.title
            dateTv.text = FormatDateUtil.makeDateFormat(data.createdAt)
        }
    }
}