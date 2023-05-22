package com.doubtless.doubtless.screens.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.doubtless.doubtless.R
import com.doubtless.doubtless.screens.auth.User
import com.doubtless.doubtless.screens.doubt.DoubtData
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

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
        val ivDp: ImageView

        init {
            userName = view.findViewById(R.id.tv_username)
            time = view.findViewById(R.id.user_doubt_timestamp)
            heading = view.findViewById(R.id.user_doubt_heading)
            description = view.findViewById(R.id.user_doubt_description)
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

        val parser = SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy")
        val date = parser.parse(allDoubts[position].date)
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        val formattedDate = formatter.format(date)

        holder.time.text = formattedDate
        holder.heading.text = allDoubts[position].heading
        holder.description.text = allDoubts[position].description

        holder.description.isVisible = !allDoubts[position].description.isNullOrEmpty()

        Glide.with(holder.ivDp).load(allDoubts[position].userPhotoUrl).circleCrop().into(holder.ivDp)

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