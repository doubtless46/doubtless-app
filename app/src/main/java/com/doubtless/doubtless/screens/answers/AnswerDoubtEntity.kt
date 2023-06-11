package com.doubtless.doubtless.screens.answers

import com.doubtless.doubtless.screens.doubt.DoubtData

data class AnswerDoubtEntity(
    val type: Int,
    val doubt: DoubtData? = null,
    val answer: AnswerData? = null
) {
    companion object {
        const val TYPE_DOUBT = 1
        const val TYPE_ANSWER_ENTER = 2
        const val TYPE_ANSWER = 3

        fun getEnterAnswerEntity(): AnswerDoubtEntity {
            return AnswerDoubtEntity(AnswerDoubtEntity.TYPE_ANSWER_ENTER, null, null)
        }
    }


}
