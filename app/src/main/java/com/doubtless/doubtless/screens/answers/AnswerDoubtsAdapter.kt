package com.doubtless.doubtless.screens.answers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.doubtless.doubtless.R
import com.doubtless.doubtless.screens.answers.viewholder.AnswerViewHolder
import com.doubtless.doubtless.screens.answers.viewholder.EnterAnswerViewHolder
import com.doubtless.doubtless.screens.auth.User
import com.doubtless.doubtless.screens.doubt.DoubtData
import com.doubtless.doubtless.screens.doubt.view.viewholder.DoubtPreviewViewHolder

class AnswerDoubtsAdapter(
    private val doubtAnswerEntities: MutableList<AnswerDoubtEntity>,
    private val user: User,
    private val onLastItemReached: () -> Unit,
    private val interactionListener: InteractionListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface InteractionListener {
        fun onUserImageClicked(userId: String)
        fun onLayoutClicked()
        fun onDoubtClicked(doubtData: DoubtData, position: Int)
        fun onAnswerClicked(answerData: AnswerData, position: Int)
        fun onAnswerPublish(publishAnswerDTO: EnterAnswerViewHolder.PublishAnswerDTO)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            AnswerDoubtEntity.TYPE_DOUBT -> {
                val view = inflater.inflate(R.layout.doubt_layout, parent, false)
                return DoubtPreviewViewHolder(
                    view = view,
                    interactionListener = object : DoubtPreviewViewHolder.InteractionListener {
                        override fun onDoubtClicked(doubtData: DoubtData, position: Int) {
                            interactionListener.onDoubtClicked(doubtData, position)
                        }

                        override fun onUserImageClicked(userId: String) {
                            interactionListener.onUserImageClicked(userId)

                        }

                    }, showVotingLayout = true
                )
            }

            AnswerDoubtEntity.TYPE_ANSWER_ENTER -> {
                val view = inflater.inflate(R.layout.enter_answer_layout, parent, false)
                return EnterAnswerViewHolder(
                    view,
                    object : EnterAnswerViewHolder.InteractionListener {
                        override fun onAnswerPublish(publishAnswerDTO: EnterAnswerViewHolder.PublishAnswerDTO) {
                            interactionListener.onAnswerPublish(publishAnswerDTO)
                        }
                    })
            }

            AnswerDoubtEntity.TYPE_ANSWER -> {
                val view = inflater.inflate(R.layout.answer_layout, parent, false)
                return AnswerViewHolder(view, object : AnswerViewHolder.InteractionListener {
                    override fun onUserImageClicked(userId: String) {
                        interactionListener.onUserImageClicked(userId)
                    }

                    override fun onAnswerClicked(answerData: AnswerData, position: Int) {
                        interactionListener.onAnswerClicked(answerData, position)
                    }
                })
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int = doubtAnswerEntities.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DoubtPreviewViewHolder)
            holder.setData(doubtAnswerEntities[position].doubt!!)

        if (holder is AnswerViewHolder)
            holder.setData(
                doubtAnswerEntities[position].answer!!,
                doubtAnswerEntities[position].answerVotingUseCase!!
            )

        if (holder is EnterAnswerViewHolder)
            holder.setData(user)
    }

    override fun getItemViewType(position: Int): Int {
        return doubtAnswerEntities[position].type
    }

    fun appendAnswer(answers: List<AnswerDoubtEntity>) {
        val offset = doubtAnswerEntities.size
        doubtAnswerEntities.addAll(answers)
        notifyItemRangeChanged(offset, answers.size)
    }

    fun appendAnswerAtFirst(answerData: AnswerData) {
        val answer = AnswerData.toAnswerDoubtEntity(answerData)

        doubtAnswerEntities.add(
            index = 2,
            element = answer
        ) // first 2 are doubt and enter answer view

        notifyItemInserted(1)
    }

}

