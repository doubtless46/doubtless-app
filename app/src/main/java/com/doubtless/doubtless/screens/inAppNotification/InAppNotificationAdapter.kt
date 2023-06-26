package com.doubtless.doubtless.screens.inAppNotification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.doubtless.doubtless.R
import com.doubtless.doubtless.screens.inAppNotification.model.InAppNotificationEntity

class InAppNotificationAdapter constructor(
    private val notifications: List<InAppNotificationEntity>,
    private val interactionListener: InteractionListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface InteractionListener {
        fun onPostAnswerNotifClicked(doubtId: String)
    }

    // rn there is only post answer type.
    private val TYPE_POST_ANSWER = 1

    private val data: MutableList<InAppNotificationEntity> = notifications.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // fyi: passing null in the parent, renders spacings incorrectly.
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_post_answer_notif, parent, false)
        return PostAnswerNotificationViewHolder(itemView, interactionListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PostAnswerNotificationViewHolder) {
            holder.bind(data[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return TYPE_POST_ANSWER
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setNewNotifications(newNotifications: MutableList<InAppNotificationEntity>) {
        data.clear()
        data.addAll(newNotifications)
        notifyDataSetChanged()
    }

}