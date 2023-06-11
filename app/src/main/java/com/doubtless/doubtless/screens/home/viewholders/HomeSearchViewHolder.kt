package com.doubtless.doubtless.screens.home.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class HomeSearchViewHolder(itemView: View, private val interactionListener: InteractionListener): ViewHolder(itemView) {

    interface InteractionListener {
        fun onLayoutClicked()
    }

    init {
        itemView.setOnClickListener {
            interactionListener.onLayoutClicked()
        }
    }

}