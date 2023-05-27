package com.doubtless.doubtless.screens.home

import com.doubtless.doubtless.screens.doubt.DoubtData

data class HomeEntity(
    val type: Int,
    val doubt: DoubtData? = null
) {
    companion object {
        const val TYPE_DOUBT = 1
        const val TYPE_SEARCH = 2

        fun getSearchEntity(): HomeEntity {
            return HomeEntity(HomeEntity.TYPE_SEARCH, null)
        }
    }
}