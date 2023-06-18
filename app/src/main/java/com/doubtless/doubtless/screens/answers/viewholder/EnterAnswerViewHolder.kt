package com.doubtless.doubtless.screens.answers.viewholder

import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.doubtless.doubtless.R
import com.doubtless.doubtless.screens.answers.PublishAnswerRequest
import com.doubtless.doubtless.screens.auth.User
import com.doubtless.doubtless.utils.Utils
import java.util.*

class EnterAnswerViewHolder(itemView: View, private val interactionListener: InteractionListener) :
    RecyclerView.ViewHolder(itemView) {

    data class PublishAnswerDTO(
        val description: String
    )

    interface InteractionListener {
        fun onAnswerPublish(publishAnswerDTO: PublishAnswerDTO)
    }

    private val etAnswer: EditText = itemView.findViewById(R.id.enter_answer_description)
    private val btnPublish: View = itemView.findViewById(R.id.btn_publish)
    private val authorName: TextView
    private val ivDp: ImageView
    private val tvCollege: TextView = itemView.findViewById(R.id.tv_college)

    init {
        authorName = itemView.findViewById(R.id.tv_author_name)
        ivDp = itemView.findViewById(R.id.iv_dp_author)

        etAnswer.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)

        etAnswer.addTextChangedListener {
            if (it.toString().isEmpty()) {
                btnPublish.isVisible = false
            } else {
                btnPublish.isVisible = true
                btnPublish.setOnClickListener {
                    interactionListener.onAnswerPublish(PublishAnswerDTO(etAnswer.text.toString()))
                    etAnswer.setText("")
                }
            }
        }
    }

    fun setData(user: User) {
        authorName.text = user.name
        tvCollege.text = user.local_user_attr!!.college

        if (etAnswer.toString().isEmpty()) {
            btnPublish.isVisible = false
        }

        Glide.with(ivDp).load(user.photoUrl).circleCrop()
            .into(ivDp)
    }

}