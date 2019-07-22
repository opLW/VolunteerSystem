package com.oplw.volunteersystem.adapter.detail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.albumsmanager.utilities.FormatDateUtil
import com.oplw.volunteersystem.R
import com.oplw.volunteersystem.net.bean.Recruitment

/**
 *
 *   @author opLW
 *   @date  2019/7/16
 */
class RecruitmentAdapter(private val context: Context,
                         private val clickListener: (Int, isSignUp: Boolean) -> Unit
) : IDelegateAdapter<Recruitment> {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.item_recruitment_list, parent, false)
        return RecruitmentVH(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, t: Recruitment): View {
        if (holder !is RecruitmentVH) {
            throw Exception("The holder is not the RecruitmentVH")
        }
        holder.rebindData(t, position)
        return holder.itemView
    }

    private inner class RecruitmentVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val headerIv = itemView.findViewById<ImageView>(R.id.recruitment_iv)
        private val nameTv = itemView.findViewById<TextView>(R.id.recruitment_name_tv)
        private val maxNumTv = itemView.findViewById<TextView>(R.id.recruitment_maxNum_tv)
        private val currentNumTv = itemView.findViewById<TextView>(R.id.recruitment_currentNum_tv)
        private val locationTv = itemView.findViewById<TextView>(R.id.recruitment_location_tv)
        private val startTimeTv = itemView.findViewById<TextView>(R.id.recruitment_start_tv)
        private val endTimeTv = itemView.findViewById<TextView>(R.id.recruitment_end_tv)
        private val signUpBtn = itemView.findViewById<Button>(R.id.recruitment_sign_up_btn)
        private val checkDetailBtn = itemView.findViewById<Button>(R.id.recruitment_check_detail_btn)
        private var mPosition = -1

        init {
            checkDetailBtn.setOnClickListener { clickListener(mPosition, false) }
            signUpBtn.setOnClickListener { clickListener(mPosition, true) }
        }

        fun rebindData(data: Recruitment, position: Int) {
            mPosition = position
            headerIv.setImageResource(getDrawableId(position))
            with(data) {
                nameTv.text = name
                maxNumTv.text = "$maxApplicants"
                currentNumTv.text = "$applicantsNum"
                locationTv.text = location
                startTimeTv.text = FormatDateUtil.makeDateFormat(startAt)
                endTimeTv.text = FormatDateUtil.makeDateFormat(endAt)
            }
        }

        private fun getDrawableId(index: Int)
                = context.resources.getIdentifier("pic_${index % 5 + 1}", "drawable", context.packageName)
    }
}