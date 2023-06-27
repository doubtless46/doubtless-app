package com.doubtless.doubtless.screens.common

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.doubtless.doubtless.R
import com.doubtless.doubtless.navigation.FragNavigator

class ExtraOptionsButtonHolder(val view: View, val interactionListener: InteractionListener)
    : RecyclerView.ViewHolder(view) {


    interface InteractionListener{
        fun onCreatePollClicked()
    }

    private val createPoll: ImageView


    init {
        createPoll = view.findViewById(R.id.ic_poll)

        createPoll.setOnClickListener {
            interactionListener.onCreatePollClicked()
        }
    }
}