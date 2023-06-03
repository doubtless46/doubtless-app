package com.doubtless.doubtless.screens.search

import com.doubtless.doubtless.screens.doubt.DoubtData

data class SearchRequest(
    val query: String
)

data class SearchResult(
    val results: List<DoubtData>
)