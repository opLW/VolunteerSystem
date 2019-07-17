package com.oplw.volunteersystem.adapter.detail

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 *   @author opLW
 *   @date  2019/7/15
 */
interface IDelegateAdapter<T> {

    /**
     * 返回新建的ViewHolder
     */
    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    /**
     * 重新绑定数据
     */
    fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, t: T): View
}
