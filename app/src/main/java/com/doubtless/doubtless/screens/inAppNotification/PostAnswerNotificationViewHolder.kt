package com.doubtless.doubtless.screens.inAppNotification

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.doubtless.doubtless.R
import com.doubtless.doubtless.screens.inAppNotification.model.InAppNotificationEntity
import com.doubtless.doubtless.utils.Utils

class PostAnswerNotificationViewHolder(
    itemView: View,
    private val interactionListener: InAppNotificationAdapter.InteractionListener
) : ViewHolder(itemView) {

    private val tvTopDescription: TextView = itemView.findViewById(R.id.tv_top_description)
    private val tvDoubtDescription: TextView = itemView.findViewById(R.id.tv_doubt_description)
    private val tvUsername: TextView = itemView.findViewById(R.id.tv_username)
    private val tvUserYear: TextView = itemView.findViewById(R.id.user_year)
    private val tvUserCollege: TextView = itemView.findViewById(R.id.user_college)
    private val tvTimestamp: TextView = itemView.findViewById(R.id.author_answer_timestamp)
    private val ivDp: ImageView = itemView.findViewById(R.id.iv_dp)
    private val tvAnswerDescription: TextView = itemView.findViewById(R.id.user_answer_description)

    fun bind(data: InAppNotificationEntity) {

        itemView.setOnClickListener {
            interactionListener.onPostAnswerNotifClicked(data.doubtId!!)
        }

        if (!data.isRead) {
            itemView.setBackgroundColor(itemView.resources.getColor(R.color.light_purple))
        } else {
            itemView.setBackgroundColor(itemView.resources.getColor(R.color.white))
        }

        tvDoubtDescription.text = data.doubtHeading
        Glide.with(itemView.context).load(data.authorPhotoUrl).circleCrop().into(ivDp)
        tvUsername.text = data.answerAuthorName
        tvUserYear.text = ""
        tvUserCollege.text = ""

        try {
            tvTimestamp.text = Utils.getTimeAgo(data.createdOn?.toDate()!!)
        } catch (e: Exception) {

        }

        tvAnswerDescription.text = data.description?.trim()
    }

}