package com.doubtless.doubtless.screens.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.doubtless.doubtless.R
import com.doubtless.doubtless.screens.answers.AnswerDoubtEntity

class AnswerDoubtsAdapter(
    private val allAnswers: MutableList<AnswerDoubtEntity>,
    private val onLastItemReached: () -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            AnswerDoubtEntity.TYPE_DOUBT -> {
                val writeAnswerView = inflater.inflate(R.layout.enter_answer_layout, parent, false)
                WriteAnswerViewHolder(writeAnswerView)
            }

            AnswerDoubtEntity.TYPE_ANSWER_ENTER -> {
                val showAnswerView = inflater.inflate(R.layout.answer_layout, parent, false)
                ShowAnswerViewHolder(showAnswerView)
            }

            AnswerDoubtEntity.TYPE_ANSWER -> {
                val showQuestionView = inflater.inflate(R.layout.doubt_layout, parent, false)
                ViewDoubtsAdapter.ViewHolder(showQuestionView)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int = allAnswers.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is WriteAnswerViewHolder -> {


            }

            is ShowAnswerViewHolder -> {
//                holder.authorName.text = allAnswers[position].authorName

            }
            is ViewDoubtsAdapter.ViewHolder -> {
                //Todo()
            }
        }
        if (position == itemCount - 1) {
            onLastItemReached.invoke()
        }
    }

    override fun getItemViewType(position: Int): Int {
        val answer = allAnswers[position]
        return when (answer) {
            is AnswerDoubtEntity ->
            is AnswerDoubtEntity ->
            is AnswerDoubtEntity ->
            else -> throw IllegalArgumentException("Invalid item type")
        }
    }

    inner class WriteAnswerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val enterText: EditText

        init {
            enterText = itemView.findViewById(R.id.enter_answer_description)
        }
    }

    inner class ShowAnswerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val authorName: TextView
        val time: TextView
        val description: TextView
        val ivDp: ImageView

        init {
            authorName = itemView.findViewById(R.id.tv_author_name)
            time = itemView.findViewById(R.id.author_doubt_timestamp)
            description = itemView.findViewById(R.id.author_answer_description)
            ivDp = itemView.findViewById(R.id.iv_dp_author)
        }
    }
}

