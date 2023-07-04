package com.doubtless.doubtless.screens.common

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.doubtless.doubtless.DoubtlessApp
import com.doubtless.doubtless.R
import com.doubtless.doubtless.screens.dashboard.viewholder.UserProfileViewHolder
import com.doubtless.doubtless.screens.doubt.DoubtData
import com.doubtless.doubtless.screens.doubt.view.viewholder.DoubtPreviewViewHolder
import com.doubtless.doubtless.screens.home.entities.FeedEntity

class GenericFeedAdapter(
    private val genericFeedEntities: MutableList<FeedEntity>,
    private val onLastItemReached: () -> Unit,
    private val interactionListener: InteractionListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface InteractionListener {
        fun onDoubtClicked(doubtData: DoubtData, position: Int)
        fun onSignOutClicked()
        fun onSubmitFeedbackClicked()
        fun onDeleteAccountClicked()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when (viewType) {
            FeedEntity.TYPE_USER_PROFILE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.user_profile_layout, parent, false)
                return UserProfileViewHolder(
                    view,
                    object : UserProfileViewHolder.InteractionListener {
                        override fun onDeleteAccountClicked() =
                            interactionListener.onDeleteAccountClicked()

                        override fun onSignOutClicked() = interactionListener.onSignOutClicked()
                        override fun onSubmitFeedbackClicked() =
                            interactionListener.onSubmitFeedbackClicked()
                    })
            }

            FeedEntity.TYPE_DOUBT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.doubt_layout, parent, false)
                return DoubtPreviewViewHolder(view = view,
                    showVotingLayout = true,
                    interactionListener = object : DoubtPreviewViewHolder.InteractionListener {
                        override fun onDoubtClicked(doubtData: DoubtData, position: Int) {
                            interactionListener.onDoubtClicked(doubtData, position)
                        }
                    })
            }

            FeedEntity.TYPE_SEARCH_RESULT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.doubt_layout, parent, false)
                return DoubtPreviewViewHolder(view = view,
                    showVotingLayout = false,
                    interactionListener = object : DoubtPreviewViewHolder.InteractionListener {
                        override fun onDoubtClicked(doubtData: DoubtData, position: Int) {
                            interactionListener.onDoubtClicked(doubtData, position)
                        }
                    })
            }
        }

        throw Exception("type is not defined")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is UserProfileViewHolder) holder.setData(
            userManager = DoubtlessApp.getInstance().getAppCompRoot().getUserManager()
        )

        if (holder is DoubtPreviewViewHolder && getItemViewType(position) == FeedEntity.TYPE_DOUBT) holder.setData(
            genericFeedEntities[position].doubt!!
        )

        if (holder is DoubtPreviewViewHolder && getItemViewType(position) == FeedEntity.TYPE_SEARCH_RESULT) holder.setData(
            genericFeedEntities[position].search_doubt!!.toDoubtData()
        )

        if (position == itemCount - 1) {
            onLastItemReached.invoke()
        }
    }

    fun clearCurrentList() {
        genericFeedEntities.clear()
        notifyDataSetChanged()
    }

    fun appendDoubts(doubts: List<FeedEntity>) {
        val offset = genericFeedEntities.size
        genericFeedEntities.addAll(doubts)
        notifyItemRangeInserted(offset, doubts.size)
    }

    override fun getItemCount(): Int {
        Log.i("AdapterItemCount", genericFeedEntities.size.toString())
        return genericFeedEntities.size
    }

    override fun getItemViewType(position: Int): Int {
        return genericFeedEntities[position].type
    }
}