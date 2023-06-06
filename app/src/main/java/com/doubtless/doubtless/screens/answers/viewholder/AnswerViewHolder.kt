package com.doubtless.doubtless.screens.answers.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.doubtless.doubtless.DoubtlessApp
import com.doubtless.doubtless.R
import com.doubtless.doubtless.screens.answers.AnswerData
import com.doubtless.doubtless.screens.doubt.usecases.VotingUseCase
import com.doubtless.doubtless.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.math.ceil
import kotlin.math.floor


class AnswerViewHolder(itemView: View, private val interactionListener: InteractionListener) :
    RecyclerView.ViewHolder(itemView) {

    interface InteractionListener {
        fun onAnswerClicked(answerData: AnswerData, position: Int)
    }

    private val authorName: TextView
    private val time: TextView
    private val description: TextView
    private val ivDp: ImageView
    private val tvYear: TextView
    private val tvVotes: TextView
    private val ivUpVote: ImageView
    private val ivDownVote: ImageView

    init {
        authorName = itemView.findViewById(R.id.tv_author_name)
        time = itemView.findViewById(R.id.author_doubt_timestamp_2)
        description = itemView.findViewById(R.id.author_answer_description_2)
        ivDp = itemView.findViewById(R.id.iv_dp_author)
        tvYear = itemView.findViewById(R.id.user_year)
        tvVotes = itemView.findViewById(R.id.tv_votes)
        ivUpVote = itemView.findViewById(R.id.iv_votes)
        ivDownVote = itemView.findViewById(R.id.iv_downvote)
    }

    fun setData(answerData: AnswerData) {

        itemView.setOnClickListener {
            interactionListener.onAnswerClicked(answerData, adapterPosition)
        }

        authorName.text = answerData.authorName
        time.text = Utils.getTimeAgo(Date(answerData.date.toString()))
        description.text = answerData.description
        tvYear.text = "| ${answerData.authorYear} Year |"

        val votingUseCase = DoubtlessApp.getInstance().getAppCompRoot().getAnswerVotingDoubtCase(answerData.copy())
        setVotesUi(answerData, votingUseCase)

        ivUpVote.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {

                val result = votingUseCase.upvoteDoubt()

                if (result is VotingUseCase.Result.UpVoted) {
                    answerData.netVotes += 1
                } else if (result is VotingUseCase.Result.UndoneUpVote) {
                    answerData.netVotes -= 1
                }

                setVotesUi(answerData, votingUseCase)
            }
        }

        ivDownVote.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {

                val result = votingUseCase.downVoteDoubt()

                if (result is VotingUseCase.Result.DownVoted) {
                    answerData.netVotes -= 1
                } else if (result is VotingUseCase.Result.UndoneDownVote) {
                    answerData.netVotes += 1
                }

                setVotesUi(answerData, votingUseCase)
            }
        }

        Glide.with(ivDp).load(answerData.authorPhotoUrl).circleCrop()
            .into(ivDp)

    }

    private fun setVotesUi(answerData: AnswerData, votingUseCase: VotingUseCase) {
        ivDownVote.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_baseline_thumb_up_off_alt_24))
        ivUpVote.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_baseline_thumb_up_off_alt_24))
        tvVotes.text = floor(answerData.netVotes).toInt().toString()

        CoroutineScope(Dispatchers.Main).launch {
            val currentState = votingUseCase.getUserCurrentState()

            if (currentState == VotingUseCase.UPVOTED)
                ivUpVote.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_baseline_thumb_up_filled))

            if (currentState == VotingUseCase.DOWNVOTED)
                ivDownVote.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_baseline_thumb_up_filled))
        }
    }

}
