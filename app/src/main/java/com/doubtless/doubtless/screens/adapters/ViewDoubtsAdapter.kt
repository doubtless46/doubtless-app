package com.doubtless.doubtless.screens.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.doubtless.doubtless.R
import com.doubtless.doubtless.screens.auth.User
import com.doubtless.doubtless.screens.doubt.DoubtData
import com.doubtless.doubtless.screens.doubt.view.viewholder.DoubtPreviewViewHolder
import com.doubtless.doubtless.screens.home.HomeEntity
import com.doubtless.doubtless.screens.home.viewholders.HomeSearchViewHolder

class ViewDoubtsAdapter(
    private val homeEntities: MutableList<HomeEntity>,
    private val onLastItemReached: () -> Unit,
    private val interactionListener: InteractionListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface InteractionListener {
        fun onSearchBarClicked()
        fun onDoubtClicked(doubtData: DoubtData, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when (viewType) {
            HomeEntity.TYPE_DOUBT -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.doubt_layout, parent, false)
                return DoubtPreviewViewHolder(view, object: DoubtPreviewViewHolder.InteractionListener {
                    override fun onDoubtClicked(doubtData: DoubtData, position: Int) {
                        interactionListener.onDoubtClicked(doubtData, position)
                    }
                })
            }

            HomeEntity.TYPE_SEARCH -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_home_search, parent, false)
                return HomeSearchViewHolder(view, object : HomeSearchViewHolder.InteractionListener {
                    override fun onLayoutClicked() {
                        interactionListener.onSearchBarClicked()
                    }
                })
            }
        }

         throw Exception("type is not defined")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is DoubtPreviewViewHolder)
            holder.setData(homeEntities[position].doubt!!)

        if (position == itemCount - 1) {
            onLastItemReached.invoke()
        }
    }

    fun clearCurrentList() {
        homeEntities.clear()
        notifyDataSetChanged()
    }

    fun appendDoubts(doubts: List<HomeEntity>) {
        val offset = homeEntities.size
        homeEntities.addAll(doubts)
        notifyItemRangeChanged(offset, doubts.size)
    }

    override fun getItemCount(): Int {
        return homeEntities.size
    }

    override fun getItemViewType(position: Int): Int {
        return homeEntities[position].type
    }
}