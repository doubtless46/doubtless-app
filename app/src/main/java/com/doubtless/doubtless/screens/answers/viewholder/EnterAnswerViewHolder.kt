package com.doubtless.doubtless.screens.answers.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView

class EnterAnswerViewHolder (itemView: View, private val interactionListener: InteractionListener) :
    RecyclerView.ViewHolder(itemView) {

    interface InteractionListener {
        fun onLayoutClicked()
    }

    init {
        itemView.setOnClickListener {
            interactionListener.onLayoutClicked()
        }
    }

}