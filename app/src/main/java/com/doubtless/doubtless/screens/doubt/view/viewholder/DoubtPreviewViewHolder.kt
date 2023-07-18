package com.doubtless.doubtless.screens.doubt.view.viewholder

import android.text.util.Linkify
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.doubtless.doubtless.DoubtlessApp
import com.doubtless.doubtless.R
import com.doubtless.doubtless.constants.GamificationConstants
import com.doubtless.doubtless.screens.doubt.DoubtData
import com.doubtless.doubtless.screens.doubt.usecases.VotingUseCase
import com.doubtless.doubtless.utils.Utils
import com.doubtless.doubtless.utils.Utils.toPx
import com.doubtless.doubtless.utils.addStateListAnimation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.math.floor

class DoubtPreviewViewHolder(
    view: View,
    private val showVotingLayout: Boolean,
    private val interactionListener: InteractionListener
) :
    RecyclerView.ViewHolder(view) {

    interface InteractionListener {
        fun onDoubtClicked(doubtData: DoubtData, position: Int)
        fun onUserImageClicked(userId: String)
    }

    private val userName: TextView
    private val time: TextView
    private val heading: TextView
    private val description: TextView
    private val ivDp: ImageView
    private val tvNetVotes: TextView
    private val upvotes: CheckBox
    private val downvotes: CheckBox
    private val tvAnswers: TextView
    private val tvTags: TextView
    private val tvCollege: TextView
    private val tvYear: TextView = itemView.findViewById(R.id.user_year)
    private val userBadge: ImageView = itemView.findViewById(R.id.user_badge)
    private val llMentorsDp: LinearLayout = itemView.findViewById(R.id.ll_answered_mentor)
    private val ivContent: ImageView = itemView.findViewById(R.id.iv_content)

    private val analyticsTracker = DoubtlessApp.getInstance().getAppCompRoot().getAnalyticsTracker()

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
        upvotes = view.findViewById(R.id.cb_upvotes)
        downvotes = view.findViewById(R.id.cb_downvote)

        upvotes.isVisible = showVotingLayout
        downvotes.isVisible = showVotingLayout
    }

    fun setData(doubtData: DoubtData) {

        itemView.setOnClickListener {
            interactionListener.onDoubtClicked(doubtData, adapterPosition)
        }

        ivDp.setOnClickListener {
            interactionListener.onUserImageClicked(doubtData.userId!!)
        }

        if (showVotingLayout) {
            // this is detailed doubt screen
            description.autoLinkMask = Linkify.WEB_URLS
        }

        userName.text = doubtData.userName!!.split(" ").first()

        tvCollege.text = doubtData.college

        try {
            time.text = Utils.getTimeAgo(Date(doubtData.date.toString()))
        } catch (e: Exception) {
            time.isVisible = false
        }

        heading.text = doubtData.heading

        tvNetVotes.text = kotlin.math.floor(doubtData.netVotes).toInt().toString()

        if (doubtData.year.equals("passout", ignoreCase = true)) {
            tvYear.text = "| ${doubtData.year} |"
        } else {
            tvYear.text = "| ${doubtData.year} Year |"
        }

        description.text = doubtData.description?.trim()
        description.isVisible = !doubtData.description.isNullOrEmpty()

        tvAnswers.text = doubtData.no_answers.toString()

        if (!doubtData.tags.isNullOrEmpty()) {
            var tags = ""

            doubtData.tags?.forEach {
                tags += "#$it "
            }

            tvTags.text = tags

        } else
            tvTags.isVisible = false

        Glide.with(ivDp).load(doubtData.userPhotoUrl).circleCrop()
            .into(ivDp)

        // image content
        if (!doubtData.imageContentUrl.isNullOrEmpty()) {

            ivContent.isVisible = true

            Glide.with(itemView.context)
                .load(doubtData.imageContentUrl)
                .into(ivContent)

        } else {
            ivContent.isVisible = false
        }

        userBadge.isVisible = doubtData.xpCount > GamificationConstants.MENTOR_XP_THRESHOLD


        setupMentorsWhoInteractedDpUi(doubtData)

        // voting ui

        val votingUseCase = DoubtlessApp.getInstance().getAppCompRoot()
            .getDoubtVotingDoubtCase(doubtData.copy())

        setVotesUi(doubtData, votingUseCase)


        upvotes.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                if (it.stateListAnimator == null)
                    it.addStateListAnimation(R.animator.scale_votes_icon)

                val result = votingUseCase.upvoteDoubt()

                if (result is VotingUseCase.Result.UpVoted) {
                    analyticsTracker.trackDoubtUpVoted(doubtData.copy())
                    doubtData.netVotes += 1
                } else if (result is VotingUseCase.Result.UndoneUpVote) {
                    doubtData.netVotes -= 1
                }

                setVotesUi(doubtData, votingUseCase)
            }
        }

        downvotes.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                if (it.stateListAnimator == null)
                    it.addStateListAnimation(R.animator.scale_votes_icon)

                val result = votingUseCase.downVoteDoubt()

                if (result is VotingUseCase.Result.DownVoted) {
                    analyticsTracker.trackDoubtDownVoted(doubtData.copy())
                    doubtData.netVotes -= 1
                } else if (result is VotingUseCase.Result.UndoneDownVote) {
                    doubtData.netVotes += 1
                }

                setVotesUi(doubtData, votingUseCase)
            }
        }
    }

    private fun setupMentorsWhoInteractedDpUi(doubtData: DoubtData) {

        // reset view state first!
        val dpsCount = llMentorsDp.childCount - 3

        if (dpsCount > 0) {
            repeat(dpsCount) {
                llMentorsDp.removeView(llMentorsDp.getChildAt(it + 3)) // 3,4,5..
            }
        }

        // then setup ui
        doubtData.mentorsDpWhoInteracted.forEach {
            if (it.isEmpty()) return@forEach

            val view = ImageView(itemView.context)
            view.layoutParams = LinearLayout.LayoutParams(22.toPx().toInt(), 22.toPx().toInt())
                .apply {
                    this.marginStart = 4.toPx().toInt()
                }

            Glide.with(itemView.context).load(it).circleCrop().into(view)

            llMentorsDp.addView(view)
        }

        llMentorsDp.isVisible = !doubtData.mentorsDpWhoInteracted.isEmpty()

    }

    private fun setVotesUi(doubtData: DoubtData, votingUseCase: VotingUseCase) {
        tvNetVotes.text = floor(doubtData.netVotes).toInt().toString()
        CoroutineScope(Dispatchers.Main).launch {
            when (votingUseCase.getUserCurrentState()) {
                VotingUseCase.UPVOTED -> {
                    downvotes.isClickable = false
                    upvotes.isChecked = true
                }

                VotingUseCase.DOWNVOTED -> {
                    upvotes.isClickable = false
                    downvotes.isChecked = true
                }

                else -> {
                    downvotes.isClickable = true
                    upvotes.isClickable = true
                }
            }
        }
    }
}
