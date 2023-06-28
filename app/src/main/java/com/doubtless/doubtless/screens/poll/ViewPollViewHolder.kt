package com.doubtless.doubtless.screens.poll

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.doubtless.doubtless.R

class ViewPollViewHolder(val view: View, val interactionListener: InteractionListener):RecyclerView.ViewHolder(view) {

    interface InteractionListener{
        fun onPollOptionClicked()
    }

    private val userName: TextView
    private val heading: TextView
    private val description: TextView
    private val college: TextView
    private val ivDp: ImageView
    private val tvOption: TextView
    private val progressBar: ProgressBar
    private val llOptions: LinearLayout
    private val time: TextView
    private val tvYear: TextView

    init {
        userName = view.findViewById(R.id.tv_username_poll)
        heading = view.findViewById(R.id.tv_poll_heading)
        description = view.findViewById(R.id.tv_poll_description)
        college = view.findViewById(R.id.tv_poll_college)
        ivDp = view.findViewById(R.id.tv_user_dp_poll)
        progressBar = view.findViewById(R.id.progress_poll)
        tvOption = view.findViewById(R.id. tv_option)
        llOptions = view.findViewById(R.id.ll_options)
        time = view.findViewById(R.id.author_doubt_timestamp2)
        tvYear = view.findViewById(R.id.user_year2)
    }

}