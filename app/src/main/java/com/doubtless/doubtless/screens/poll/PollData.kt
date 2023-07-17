package com.doubtless.doubtless.screens.poll

data class PollData(
    val id: String? = null,
    val pollOptions: List<String>? = null,
    val pollOptionVotes: List<String>? = null,
    val totalVotes: String? = null,

    )