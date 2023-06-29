package com.doubtless.doubtless.screens.doubt

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.doubtless.doubtless.screens.doubt.view.ViewDoubtsFragment

class FilterTagsViewPagerAdapter(
    homeMainScreenFragment: HomeMainScreenFragment, private val tagsList: List<String>
) : FragmentStateAdapter(
    homeMainScreenFragment
) {

    override fun getItemCount(): Int {
        return tagsList.size
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = ViewDoubtsFragment()
        val args = Bundle()
        args.putString("tag", tagsList[position])
        fragment.arguments = args
        return fragment

    }
}
