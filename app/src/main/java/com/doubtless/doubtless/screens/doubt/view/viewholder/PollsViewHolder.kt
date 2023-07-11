package com.doubtless.doubtless.screens.doubt.view.viewholder

import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.doubtless.doubtless.R

class PollsViewHolder constructor(
    private val view: View
) : ViewHolder(view) {

    private val userName: TextView
    private val time: TextView
    private val heading: TextView
    private val ivDp: ImageView
    private val tvNetVotes: TextView
    private val upvotes: CheckBox
    private val downvotes: CheckBox
    private val tvAnswers: TextView
    private val tvCollege: TextView
    private val tvYear: TextView = itemView.findViewById(R.id.user_year)
    private val userBadge: ImageView = itemView.findViewById(R.id.user_badge)

    init {
        userName = view.findViewById(R.id.tv_username)
        time = view.findViewById(R.id.author_doubt_timestamp)
        heading = view.findViewById(R.id.user_doubt_heading)
        ivDp = view.findViewById(R.id.iv_dp)
        tvNetVotes = view.findViewById(R.id.tv_votes)
        tvAnswers = view.findViewById(R.id.tv_answers)
        tvCollege = view.findViewById(R.id.user_college)
        upvotes = view.findViewById(R.id.cb_upvotes)
        downvotes = view.findViewById(R.id.cb_downvote)
    }

}