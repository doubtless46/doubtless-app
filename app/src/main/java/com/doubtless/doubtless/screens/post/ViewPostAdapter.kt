package com.doubtless.doubtless.screens.post

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.doubtless.doubtless.R

class ViewPostAdapter(private val allPosts: ArrayList<PostData>) :
    RecyclerView.Adapter<ViewPostAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView
        val postTime: TextView
        val doubt: TextView
        val heading: TextView
        val description: TextView

        init {
            userName = view.findViewById(R.id.user_name)
            postTime = view.findViewById(R.id.user_post_time)
            doubt = view.findViewById(R.id.user_post_doubt)
            heading = view.findViewById(R.id.user_post_heading)
            description = view.findViewById(R.id.user_post_description)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return allPosts.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.userName.text = allPosts[position].userName
        holder.postTime.text = allPosts[position].time
        holder.doubt.text = allPosts[position].doubt
        holder.heading.text = allPosts[position].heading
        holder.description.text = allPosts[position].description

    }
}