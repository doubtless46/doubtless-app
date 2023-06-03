package com.doubtless.doubtless.screens.doubt.view.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.doubtless.doubtless.R
import com.doubtless.doubtless.screens.doubt.DoubtData
import com.doubtless.doubtless.utils.Utils
import com.doubtless.doubtless.utils.Utils.flatten
import java.util.*

class DoubtPreviewViewHolder(view: View, private val interactionListener: InteractionListener) :
    RecyclerView.ViewHolder(view) {

    interface InteractionListener {
        fun onDoubtClicked(doubtData: DoubtData, position: Int)
    }

    private val userName: TextView
    private val time: TextView
    private val heading: TextView
    private val description: TextView
    private val ivDp: ImageView
    private val tvNetVotes: TextView
    private val tvAnswers: TextView
    private val tvTags: TextView
    private val tvCollege: TextView

    init {
        userName = view.findViewById(R.id.tv_username)
        time = view.findViewById(R.id.author_doubt_timestamp)
        heading = view.findViewById(R.id.user_doubt_heading)
        description = view.findViewById(R.id.user_doubt_description)
        ivDp = view.findViewById(R.id.iv_dp)
        tvNetVotes = view.findViewById(R.id.tv_votes)
        tvAnswers = view.findViewById(R.id.tv_answers)
        tvTags = view.findViewById(R.id.tv_tags)
        tvCollege = view.findViewById(R.id.user_college)
    }

    fun setData(doubtData: DoubtData) {

        itemView.setOnClickListener {
            interactionListener.onDoubtClicked(doubtData, adapterPosition)
        }

        userName.text = doubtData.userName!!.split(" ").first()

        tvCollege.text = doubtData.college

        time.text = Utils.getTimeAgo(Date(doubtData.date.toString()))

        heading.text = doubtData.heading

        tvNetVotes.text = (doubtData.netVotes.toInt()).toString()

        description.text = doubtData.description
        description.isVisible = !doubtData.description.isNullOrEmpty()

        tvAnswers.text = doubtData.no_answers.toString()

        if (!doubtData.tags.isNullOrEmpty())
            tvTags.text = "Related to : " + doubtData.tags!!.flatten()
        else
            tvTags.isVisible = false

        Glide.with(ivDp).load(doubtData.userPhotoUrl).circleCrop()
            .into(ivDp)
    }
}