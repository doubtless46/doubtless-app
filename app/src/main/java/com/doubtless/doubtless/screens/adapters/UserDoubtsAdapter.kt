package com.doubtless.doubtless.screens.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.doubtless.doubtless.R
import com.doubtless.doubtless.screens.doubt.DoubtData

class UserDoubtsAdapter(private val doubtData: ArrayList<DoubtData>):
    RecyclerView.Adapter<UserDoubtsAdapter.ViewHolder>(){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView
        val time: TextView
        val heading: TextView
        val description: TextView
        val voteCount: TextView
        val upvote: ImageButton
        val downvote: ImageButton


        init {
            userName = view.findViewById(R.id.user_name)
            time = view.findViewById(R.id.user_doubt_time)
            heading = view.findViewById(R.id.user_doubt_heading)
            description = view.findViewById(R.id.user_doubt_description)
            voteCount = view.findViewById(R.id.vote_count)
            upvote = view.findViewById(R.id.upvote_btn)
            downvote = view.findViewById(R.id.downvote_btn)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.doubt_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return doubtData.size
    }
}