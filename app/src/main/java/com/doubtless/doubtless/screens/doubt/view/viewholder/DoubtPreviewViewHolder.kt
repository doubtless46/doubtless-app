package com.doubtless.doubtless.screens.doubt.view.viewholder

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.doubtless.doubtless.R
import com.doubtless.doubtless.screens.doubt.DoubtData
import com.doubtless.doubtless.screens.doubt.usecases.VotingDoubtUseCase
import com.doubtless.doubtless.utils.Utils
import com.doubtless.doubtless.utils.Utils.flatten
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Math.ceil
import java.util.*

class DoubtPreviewViewHolder(
    view: View,
    private val showVotingLayout: Boolean,
    private val votingDoubtUseCase: VotingDoubtUseCase,
    private val interactionListener: InteractionListener
) :
    RecyclerView.ViewHolder(view) {

    interface InteractionListener {
        fun onDoubtClicked(doubtData: DoubtData, position: Int)
    }

    private val userName: TextView
    private val time: TextView
    private val heading: TextView
    private val description: TextView
    private val ivDp: ImageView
    private val tvNetVotes: TextView
    private val ivUpvotes: ImageView
    private val ivDownvotes: ImageView
    private val tvAnswers: TextView
    private val tvTags: TextView
    private val tvCollege: TextView
    private val tvYear: TextView = itemView.findViewById(R.id.user_year)

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
        ivUpvotes = view.findViewById(R.id.iv_upvotes)
        ivDownvotes = view.findViewById(R.id.iv_downvote)

        ivUpvotes.isVisible = showVotingLayout
        ivDownvotes.isVisible = showVotingLayout
    }

    fun setData(doubtData: DoubtData) {

        itemView.setOnClickListener {
            interactionListener.onDoubtClicked(doubtData, adapterPosition)
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

        tvYear.text = "| ${doubtData.year} Year |"

        description.text = doubtData.description
        description.isVisible = !doubtData.description.isNullOrEmpty()

        tvAnswers.text = doubtData.no_answers.toString()

        if (!doubtData.tags.isNullOrEmpty())
            tvTags.text = "Related to : " + doubtData.tags!!.flatten()
        else
            tvTags.isVisible = false

        Glide.with(ivDp).load(doubtData.userPhotoUrl).circleCrop()
            .into(ivDp)


        var lastClicked = System.currentTimeMillis()

        ivUpvotes.setOnClickListener {

            if (System.currentTimeMillis() - lastClicked < 3000L) {
                Toast.makeText(itemView.context, "Please read properly first!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lastClicked = System.currentTimeMillis()

            // If result comes after a while and this viewholder is used for another item, things can go wrong.
            CoroutineScope(Dispatchers.Main).launch {
                val result = votingDoubtUseCase.upvoteDoubt(doubtId = doubtData.id!!)

                if (result is VotingDoubtUseCase.Result.UpVoted) {
                    doubtData.netVotes += 1 // looks bad :/
                    Log.d("voting", "102 " + doubtData.netVotes + " " + doubtData.netVotes.toInt())
                    tvNetVotes.text = kotlin.math.floor(doubtData.netVotes).toInt().toString()
                }

                if (result is VotingDoubtUseCase.Result.UndoneUpVote) {
                    doubtData.netVotes -= 1 // looks bad :/
                    Log.d("voting", "107 " + doubtData.netVotes  + " " + doubtData.netVotes.toInt())
                    tvNetVotes.text = kotlin.math.floor(doubtData.netVotes).toInt().toString()
                }
            }
        }
    }
}