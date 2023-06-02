package com.doubtless.doubtless.screens.answers.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.doubtless.doubtless.R
import com.doubtless.doubtless.screens.answers.AnswerData
import com.doubtless.doubtless.utils.Utils
import java.util.Date


class AnswerViewHolder(itemView: View, private val interactionListener: InteractionListener) :
    RecyclerView.ViewHolder(itemView) {

    interface InteractionListener {
        fun onAnswerClicked(answerData: AnswerData, position:Int)
    }

    val authorName: TextView
    val time: TextView
    val description: TextView
    val ivDp: ImageView

    init {
        authorName = itemView.findViewById(R.id.tv_author_name)
        time = itemView.findViewById(R.id.author_doubt_timestamp_2)
        description = itemView.findViewById(R.id.author_answer_description_2)
        ivDp = itemView.findViewById(R.id.iv_dp_author)
    }

    fun setData(answerData: AnswerData) {

        itemView.setOnClickListener {
            interactionListener.onAnswerClicked(answerData, adapterPosition)
        }
        authorName.text = answerData.authorName
        time.text = Utils.getTimeAgo(Date(answerData.date.toString()))
        description.text = answerData.description

        Glide.with(ivDp).load(answerData.authorPhotoUrl).circleCrop()
            .into(ivDp)

    }

}
