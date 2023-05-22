package com.doubtless.doubtless.screens.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.doubtless.doubtless.R
import com.doubtless.doubtless.screens.auth.User
import com.doubtless.doubtless.screens.doubt.DoubtData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Date

class ViewDoubtsAdapter(
    private val allDoubts: MutableList<DoubtData>,
    private val user: User,
    private val onLastItemReached: () -> Unit
) : RecyclerView.Adapter<ViewDoubtsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView
        val time: TextView
        val heading: TextView
        val description: TextView
        val voteCount: TextView
        val upvote: ImageButton
        val downvote: ImageButton
        val ivDp: ImageView

        init {
            userName = view.findViewById(R.id.user_name)
            time = view.findViewById(R.id.user_doubt_time)
            heading = view.findViewById(R.id.user_doubt_heading)
            description = view.findViewById(R.id.user_doubt_description)
            voteCount = view.findViewById(R.id.vote_count)
            upvote = view.findViewById(R.id.upvote_btn)
            downvote = view.findViewById(R.id.downvote_btn)
            ivDp = view.findViewById(R.id.iv_dp)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.doubt_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return allDoubts.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.userName.text = allDoubts[position].userName
        holder.time.text = Date(allDoubts[position].date!!).day.toString()
        holder.heading.text = allDoubts[position].heading
        holder.description.text = allDoubts[position].description
        holder.voteCount.text =
            (allDoubts[position].upVotes - allDoubts[position].downVotes).toString()

        Glide.with(holder.ivDp).load(allDoubts[position].userPhotoUrl).circleCrop().into(holder.ivDp)

        val doubt = allDoubts[position]

        holder.upvote.setOnClickListener {

        }

        holder.downvote.setOnClickListener {

        }

        if (position == itemCount - 1) {
            onLastItemReached.invoke()
        }

    }

    fun clearCurrentList() {
        allDoubts.clear()
        notifyDataSetChanged()
    }

    fun appendDoubts(doubts: List<DoubtData>) {
        val offset = allDoubts.size
        allDoubts.addAll(doubts)
        notifyItemRangeChanged(offset, doubts.size)
    }
}